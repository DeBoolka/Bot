package dikanev.nikita.bot.logic.callback.commands.menu;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dikanev.nikita.bot.api.objects.AmmunitionObject;
import dikanev.nikita.bot.api.item.PhotoVk;
import dikanev.nikita.bot.controller.AmmunitionController;
import dikanev.nikita.bot.controller.PhotoController;
import dikanev.nikita.bot.logic.callback.CommandResponse;
import dikanev.nikita.bot.logic.callback.VkCommands;
import dikanev.nikita.bot.logic.callback.commands.MenuCommand;
import dikanev.nikita.bot.service.client.parameter.Parameter;
import dikanev.nikita.bot.service.storage.clients.CoreClientStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AmmunitionMenuCommand extends MenuCommand {
    private static final Logger LOG = LoggerFactory.getLogger(AmmunitionMenuCommand.class);

    @Override
    protected Map<String, CommandData> getCommands(CommandResponse cmdResp, Parameter args) {
        Map<String, CommandData> res = new LinkedHashMap<>();

        res.put("bot/vk/person/ammunition.get", new CommandData("Посмотреть", "- Просмотр вашиго снаряжения", true, (resp, data, commands) -> {
//            addWorker(args, "get-ammunition", "get-ammunition");
//            new SendMessage(resp.getIdUser()).message("Ваши фото").execute();
//            cmdResp.setText("назад");
//            getAmmunition(resp);
//            return cmdResp.finish();
            return unrealizedOperation(cmdResp);
        }));
        res.put("bot/vk/person/ammunition.add", new CommandData("Добавить", "- Добавление снаряжения", true, (resp, data, commands) -> {
            addWorker(args, "add-ammunition", "add-ammunition");
            new SendMessage(resp.getIdUser()).message("Введите название снаряжения и прикрепите его фотографии").button(new Keyboard(true).def("Отмена")).execute();
            return cmdResp.finish();
        }));
        res.put("help", new CommandData("help", true, "- Выводит список команд ", Keyboard.DEFAULT, (resp, data, commands) -> {
            args.set("message", helpCommand(commands));
            return cmdResp.setInit();
        }));
        res.put("back", new CommandData("back", true, "- Возврат в личный кабинет ", Keyboard.DEFAULT, (resp, data, commands) ->
                cmdResp.setArgs("").setIdCommand(VkCommands.PERSONAL_MENU_OF_USER.id()).setInit()
        ));

        return res;
    }

    @Override
    protected List<Worker> initWorkers(CommandResponse resp, Parameter param) {
        return new ArrayList<>(List.of(
                new Worker("add-ammunition", it -> addAmmunition(resp))
//                new Worker("get-ammunition", it -> getAmmunition(resp))
        ));
    }

    private void addAmmunition(CommandResponse resp) {
        String name = resp.getText();
        JsonArray attachments = getAttachmentsPhoto(resp.getRequestObject());
        Map<PhotoVk, String> photos = null;
        try {
            if (name == null || name.isEmpty()) {
                new SendMessage(resp.getIdUser()).message("Введите название снаряжения.").execute();
                return;
            } else if(name.equals("Отмена")){
                resp.setArgs("");
                return;
            } else if (attachments != null) {
                 photos = getUrlPhotoMaxSize(attachments);
            }

            AmmunitionObject ammunition = AmmunitionController.addAmmunition(CoreClientStorage.getInstance().getToken()
                    , resp.getIdUser()
                    , name
                    , photos != null ? photos.values().toArray(new String[0]) : null);

            if (ammunition == null) {
                new SendMessage(resp.getIdUser()).message("Не удалось добавить снаряжение").execute();
            } else {
                new SendMessage(resp.getIdUser()).message("Снаряжение добавленно.").execute();
            }

        } catch (Exception e) {
            new SendMessage(resp.getIdUser()).message("Не удалось добавить снаряжение").saveExecute();
            LOG.error("Failed add photo", e);
        }
        resp.setArgs("");
    }

    private Map<PhotoVk, String> getUrlPhotoMaxSize(JsonArray attachments) {
        return PhotoController.getUrlPhotoMaxSizeFromMessageVk(attachments);
    }

    private JsonArray getAttachmentsPhoto(JsonObject requestObject) {
        return PhotoController.getAttachmentsPhotoFromMessageVk(requestObject);
    }

    @Override
    protected String getHelloMessage(CommandResponse cmd) {
        return "Что хотите сделать?";
    }
}
