package dikanev.nikita.bot.logic.callback.commands.menu;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dikanev.nikita.bot.api.exceptions.ApiException;
import dikanev.nikita.bot.api.item.Ammunition;
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

import java.sql.SQLException;
import java.util.*;

public class AmmunitionMenuCommand extends MenuCommand {
    private static final Logger LOG = LoggerFactory.getLogger(AmmunitionMenuCommand.class);

    @Override
    protected Map<String, CommandData> getCommands(CommandResponse cmdResp, Parameter args) {
        Map<String, CommandData> res = new LinkedHashMap<>();

        res.put("bot/vk/person/ammunition.get", new CommandData("Посмотреть", "- Просмотр вашиго снаряжения", true, (resp, data, commands) -> {
            addWorker(args, "get-ammunition", "get-ammunition");
            new SendMessage(resp.getUserId()).message("Ваше снаряжение:\n" + getAllAmmunitionOfUser(resp.getUserId())).execute();
            cmdResp.setText("назад");
            getAmmunition(resp);
            return cmdResp.finish();
        }));
        res.put("bot/vk/person/ammunition.add", new CommandData("Добавить", "- Добавление снаряжения", true, (resp, data, commands) -> {
            addWorker(args, "add-ammunition", "add-ammunition");
            new SendMessage(resp.getUserId()).message("Введите название снаряжения и прикрепите его фотографии").button(new Keyboard(true).def("Отмена")).execute();
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
                new Worker("add-ammunition", it -> addAmmunition(resp)),
                new Worker("get-ammunition", it -> getAmmunition(resp))
        ));
    }

    private void getAmmunition(CommandResponse resp) {
        String text = resp.getText().trim().toLowerCase();
        Parameter params = resp.getArgs();
        int indent = params.getIntFOrDefault("indent", 0);

        if (!text.equals("назад") && !text.equals("вперед") && !text.equals("удалить")) {
            resp.setArgs("");
            return;
        }

        if (text.equals("назад")) {
            indent -= (indent > 0) ? 1 : 0;
        } else if (text.equals("вперед")) {
            indent++;
        }
        params.set("indent", String.valueOf(indent));

        try {
            List<Ammunition> ammunitionList = AmmunitionController.getAmmunitionByUser(CoreClientStorage.getInstance().getToken(), resp.getUserId(), indent, 1);
            if (ammunitionList.isEmpty()) {
                if (indent == 0) {
                    new SendMessage(resp.getUserId()).message("Снаряжение отстутствует").button(new Keyboard(true).prim("Назад")).execute();
                    resp.setArgs("");
                    return;
                }
                params.set("indent", String.valueOf(--indent));
                ammunitionList = AmmunitionController.getAmmunitionByUser(CoreClientStorage.getInstance().getToken(), resp.getUserId(), indent, 1);
            }
            if (text.equals("удалить")) {
                if (AmmunitionController.deleteAmmunition(CoreClientStorage.getInstance().getToken(), ammunitionList.get(0).id)) {
                    new SendMessage(resp.getUserId()).message("Снаряжение удалено").execute();
                    indent = (indent == 0) ? 0 : indent - 1;
                    params.set("indent", String.valueOf(indent));
                    ammunitionList = AmmunitionController.getAmmunitionByUser(CoreClientStorage.getInstance().getToken(), resp.getUserId(), indent, 1);

                    if (ammunitionList.isEmpty()) {
                        new SendMessage(resp.getUserId()).message("Снаряжение отсутствует").button(new Keyboard(true).prim("Назад")).execute();
                        resp.setArgs("");
                        return;
                    }
                } else {
                    new SendMessage(resp.getUserId()).message("Не удалось удалить снаряжение").execute();
                }
            }

            Ammunition ammunition = ammunitionList.get(0);
            List<PhotoVk> photos = PhotoController.getPhotoVk(resp.getUserId(), ammunition.photos);
            List<String> sendPhoto = new ArrayList<>(ammunitionList.size());
            photos.forEach(it -> sendPhoto.add("photo" + it.getConcatId()));

            LOG.info(ammunition.toString());

            Keyboard keyboard = new Keyboard(true).prim("Назад").prim("Вперед").endl().negative("Удалить").def("Отмена").endl();
            new SendMessage(resp.getUserId())
                    .message(ammunition.name)
                    .attachment(sendPhoto)
                    .button(keyboard)
                    .execute();
        } catch (Exception e) {
            LOG.error("Failed get user photos", e);
        }
    }

    private void addAmmunition(CommandResponse resp) {
        String name = resp.getText();
        JsonArray attachments = getAttachmentsPhoto(resp.getRequestObject());
        Map<PhotoVk, String> photos = null;
        try {
            if (name == null || name.isEmpty()) {
                new SendMessage(resp.getUserId()).message("Введите название снаряжения.").execute();
                return;
            } else if(name.equals("Отмена")){
                resp.setArgs("");
                return;
            } else if (attachments != null) {
                 photos = getUrlPhotoMaxSize(attachments);
            }

            Ammunition ammunition = AmmunitionController.addAmmunition(CoreClientStorage.getInstance().getToken()
                    , resp.getUserId()
                    , name
                    , photos != null ? photos.values().toArray(new String[0]) : null);

            if (ammunition == null) {
                new SendMessage(resp.getUserId()).message("Не удалось добавить снаряжение").execute();
            } else {
                new SendMessage(resp.getUserId()).message("Снаряжение добавленно.").execute();
            }

        } catch (Exception e) {
            new SendMessage(resp.getUserId()).message("Не удалось добавить снаряжение").saveExecute();
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

    private String getAllAmmunitionOfUser(int userId) {
        List<Ammunition> ammunition;
        try {
            ammunition = AmmunitionController.getAmmunitionByUser(CoreClientStorage.getInstance().getToken(), userId, 0, 999);
        } catch (ApiException | SQLException e) {
            LOG.error("Failed get all ammunition.", e);
            return "<error>";
        }

        StringBuilder str = new StringBuilder();
        ammunition.forEach(it -> {
            if (str.length() != 0) {
                str.append(", ");
            }
            str.append(it.name);
        });

        return str.toString();
    }

    @Override
    protected String getHelloMessage(CommandResponse cmd) {
        return "Что хотите сделать?";
    }
}
