package dikanev.nikita.bot.logic.callback.commands.menu;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dikanev.nikita.bot.api.item.PhotoVk;
import dikanev.nikita.bot.controller.PhotoController;
import dikanev.nikita.bot.controller.users.UserController;
import dikanev.nikita.bot.logic.callback.CommandResponse;
import dikanev.nikita.bot.logic.callback.VkCommands;
import dikanev.nikita.bot.logic.callback.commands.MenuCommand;
import dikanev.nikita.bot.service.client.parameter.Parameter;
import dikanev.nikita.bot.service.storage.clients.CoreClientStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class PhotoMenuCommand extends MenuCommand {

    private static final Logger LOG = LoggerFactory.getLogger(PhotoMenuCommand.class);
    private static final int COUNT_GET_USER_PHOTO = 5;

    @Override
    protected Map<String, CommandData> getCommands(CommandResponse cmdResp, Parameter args) {
        Map<String, CommandData> res = new LinkedHashMap<>();

        res.put("bot/vk/person/photo.get", new CommandData("Посмотреть", "- Просмотр ваших фото", true, (resp, data, commands) -> {
            addWorker(args, "get-photo", "get-photo");
            new SendMessage(resp.getUserId()).message("Ваши фото").execute();
            cmdResp.setText("назад");
            getPhoto(resp);
            return cmdResp.finish();
        }));
        res.put("bot/vk/person/photo.add", new CommandData("Добавить", "- Добавление фотографий", true, (resp, data, commands) -> {
            addWorker(args, "add-photo", "add-photo");
            new SendMessage(resp.getUserId()).message("Отправьте фото, которые хотите добавить").execute();
            return cmdResp.finish();
        }));
        res.put("help", new CommandData("help", true, "- Выводит список команд", Keyboard.DEFAULT, (resp, data, commands) -> {
            args.set("message", helpCommand(commands));
            return cmdResp.setInit();
        }));
        res.put("back", new CommandData("back", true, "- Возврат в личный кабинет", Keyboard.DEFAULT, (resp, data, commands) ->
                cmdResp.setArgs("").setIdCommand(VkCommands.PERSONAL_MENU_OF_USER.id()).setInit()
        ));

        return res;
    }

    @Override
    protected List<Worker> initWorkers(CommandResponse resp, Parameter param) {
        return new ArrayList<>(List.of(
                new Worker("add-photo", it -> addPhoto(resp)),
                new Worker("get-photo", it -> getPhoto(resp))
        ));
    }

    private void getPhoto(CommandResponse resp){
        String text = resp.getText().trim().toLowerCase();
        Parameter params = resp.getArgs();
        int indent = params.getIntFOrDefault("indent", 0);
        int countPhoto = params.getIntFOrDefault("countPhoto", COUNT_GET_USER_PHOTO);

        if (text.equals("уменьшить") && countPhoto == COUNT_GET_USER_PHOTO) {
            countPhoto = 1;
            params.set("countPhoto", String.valueOf(countPhoto));
        } else if (text.equals("увеличить") && countPhoto == 1) {
            countPhoto = COUNT_GET_USER_PHOTO;
            params.set("countPhoto", String.valueOf(countPhoto));
        } else if (!text.equals("назад") && !text.equals("вперед") && !text.equals("удалить")) {
            resp.setArgs("");
            return;
        }

        if (text.equals("назад")) {
            indent -= (indent - countPhoto >= 0) ? countPhoto : indent;
        } else if (text.equals("вперед") && params.getFOrDefault("hasNextPhoto", "true").equals("true")) {
            indent += countPhoto;
        }
        params.set("indent", String.valueOf(indent))
                .set("countPhoto", String.valueOf(countPhoto));

        try {
            List<PhotoVk> photos = UserController.getPhotoByUser(CoreClientStorage.getInstance().getToken(), resp.getUserId(), indent, countPhoto);
            if (photos == null || photos.isEmpty()) {
                new SendMessage(resp.getUserId()).message("Фотографии отстуствуют").button(new Keyboard(true).prim("Назад")).execute();
                resp.setArgs("");
                return;
            } else if (countPhoto == 1 && text.equals("удалить")) {
                if (UserController.deletePhoto(CoreClientStorage.getInstance().getToken(), photos.toArray(new PhotoVk[0]))) {
                    new SendMessage(resp.getUserId()).message("Фото удалено").execute();
                    indent = (indent == 0) ? 0 : indent - 1;
                    photos = UserController.getPhotoByUser(CoreClientStorage.getInstance().getToken(), resp.getUserId(), indent, countPhoto);

                    if (photos == null || photos.isEmpty()) {
                        new SendMessage(resp.getUserId()).message("Фотографии отстуствуют").button(new Keyboard(true).prim("Назад")).execute();
                        resp.setArgs("");
                        return;
                    }
                } else {
                    new SendMessage(resp.getUserId()).message("Не удалось удалить фото").execute();
                }
            }
            params.set("hasNextPhoto", photos.size() < countPhoto ? "false" : "true");

            List<String> sendPhoto = new ArrayList<>(photos.size());
            photos.forEach(it -> sendPhoto.add("photo" + it.getConcatId()));

            Keyboard keyboard = new Keyboard(true).prim("Назад").positive(countPhoto == 1 ? "Увеличить" : "Уменьшить").prim("Вперед").endl();
            if (countPhoto == 1) {
                keyboard.negative("Удалить");
            }
            keyboard.def("Отмена");
            new SendMessage(resp.getUserId())
                    .attachment(sendPhoto)
                    .button(keyboard)
                    .execute();
        } catch (Exception e) {
            LOG.error("Failed get user photos", e);
        }
    }

    private void addPhoto(CommandResponse resp) {
        JsonArray attachments = getAttachmentsPhoto(resp.getRequestObject());
        try {
            if (attachments == null) {
                new SendMessage(resp.getUserId()).message("Фото не найдено").execute();
                resp.setArgs("");
                return;
            }

            Map<PhotoVk, String> photos = getUrlPhotoMaxSize(attachments);

            Map<String, Integer> addedPhotos = UserController.addPhoto(CoreClientStorage.getInstance().getToken()
                    , resp.getUserId()
                    , photos);

            if (addedPhotos != null) {
                new SendMessage(resp.getUserId()).message("Фото дабавлены").execute();
            } else {
                new SendMessage(resp.getUserId()).message("Ошибка добавления").execute();
                LOG.warn("Failed added photo: " + photos);
            }
        } catch (Exception e) {
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
