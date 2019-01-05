package dikanev.nikita.bot.logic.callback.commands.menu;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dikanev.nikita.bot.api.PhotoVk;
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
            sendMessage("Ваши фото", resp.getIdUser());
            cmdResp.setText("назад");
            getPhoto(resp);
            return cmdResp.finish();
        }));
        res.put("bot/vk/person/photo.add", new CommandData("Добавить", "- Добавление фотографий", true, (resp, data, commands) -> {
            addWorker(args, "add-photo", "add-photo");
            sendMessage("Отправьте фото, которые хотите добавить", resp.getIdUser());
            return cmdResp.finish();
        }));
        res.put("bot/vk/person/photo.delete", new CommandData("Удалить", "- Удаление фотографии", true, (resp, data, commands) ->
                unrealizedOperation(cmdResp)
        ));
        res.put("help", new CommandData("help", true, "- Выводит список команд", (resp, data, commands) -> {
            args.set("message", helpCommand(commands));
            return cmdResp.setInit();
        }));
        res.put("back", new CommandData("back", true, "- Возврат в личный кабинет", (resp, data, commands) ->
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
        if (!text.equals("назад") && !text.equals("вперед")) {
            resp.setArgs("");
            return;
        }

        Parameter params = resp.getArgs();
        int indent = params.getIntFOrDefault("indent", 0);

        if (text.equals("назад") && indent >= COUNT_GET_USER_PHOTO) {
            indent -= COUNT_GET_USER_PHOTO;
        } else if (text.equals("вперед") && params.getFOrDefault("hasNextPhoto", "true").equals("true")) {
            indent += COUNT_GET_USER_PHOTO;
        }
        params.set("indent", String.valueOf(indent));

        try {
            List<PhotoVk> photos = UserController.getPhotoByUser(CoreClientStorage.getInstance().getToken(), resp.getIdUser(), indent, COUNT_GET_USER_PHOTO);
            if (photos == null || photos.isEmpty()) {
                new MessageSend(resp.getIdUser()).message("Фотографии отстуствуют").button(true, List.of(List.of(TK.getDefault("Назад")))).execute();
                resp.setArgs("");
                return;
            }
            params.set("hasNextPhoto", photos.size() < COUNT_GET_USER_PHOTO ? "false" : "true");

            List<String> sendPhoto = new ArrayList<>(photos.size());
            photos.forEach(it -> sendPhoto.add("photo" + it.getConcatId()));
            new MessageSend(resp.getIdUser())
                    .attachment(sendPhoto)
                    .button(true, List.of(List.of(TK.getDefault("Назад"), TK.getDefault("Отмена"), TK.getDefault("Вперед"))))
                    .execute();
        } catch (Exception e) {
            LOG.error("Failed get user photos", e);
        }
    }

    private void addPhoto(CommandResponse resp) {
        JsonArray attachments = getAttachmentsPhoto(resp.getRequestObject());
        try {
            if (attachments == null) {
                sendMessage("Фото не найдено", resp.getIdUser());
                resp.setArgs("");
                return;
            }

            Map<PhotoVk, String> photos = getUrlPhotoMaxSize(attachments);

            Map<String, Integer> addedPhotos = UserController.addPhoto(CoreClientStorage.getInstance().getToken()
                    , resp.getIdUser()
                    , photos);

            if (addedPhotos != null) {
                sendMessage("Фото дабавлены", resp.getIdUser());
            } else {
                sendMessage("Ошибка добавления", resp.getIdUser());
                LOG.warn("Failed added photo: " + photos);
            }
        } catch (Exception e) {
            LOG.error("Failed add photo", e);
        }
        resp.setArgs("");
    }

    private Map<PhotoVk, String> getUrlPhotoMaxSize(JsonArray attachments) {
        Map<PhotoVk, String> photos = new HashMap<>(attachments.size());
        attachments.forEach(it -> {
            JsonObject jsPhoto = it.getAsJsonObject().getAsJsonObject("photo");
            JsonArray sizes = jsPhoto.getAsJsonArray("sizes");
            if (sizes.size() == 0) {
                return;
            }

            final JsonObject[] maxPhoto = {sizes.get(sizes.size() - 1).getAsJsonObject()};
            int w = maxPhoto[0].getAsJsonPrimitive("width").getAsInt();
            int h = maxPhoto[0].getAsJsonPrimitive("height").getAsInt();
            final int[] maxSize = {w * h};

            sizes.forEach(ph -> {
                JsonObject photo = ph.getAsJsonObject();
                int width = photo.getAsJsonPrimitive("width").getAsInt();
                int height = photo.getAsJsonPrimitive("height").getAsInt();
                if (width * height > maxSize[0]) {
                    maxPhoto[0] = photo;
                    maxSize[0] = width * height;
                }
            });

            PhotoVk photo = new PhotoVk();
            photo.id = jsPhoto.getAsJsonPrimitive("id").getAsInt();
            photo.ownerId = jsPhoto.getAsJsonPrimitive("owner_id").getAsInt();
            if (jsPhoto.has("access_key")) {
                photo.accessKey = jsPhoto.getAsJsonPrimitive("access_key").getAsString();
            }
            photos.put(photo , maxPhoto[0].get("url").getAsString());
        });

        return photos;
    }

    private JsonArray getAttachmentsPhoto(JsonObject requestObject) {
        if (!requestObject.has("object")) {
            return null;
        }

        requestObject = requestObject.getAsJsonObject("object");
        if (!requestObject.has("attachments")) {
            return null;
        }

        JsonArray attachments = requestObject.getAsJsonArray("attachments");
        for (int i = attachments.size() - 1; i >= 0; i--) {
            if (!attachments.get(i).getAsJsonObject().getAsJsonPrimitive("type").getAsString().equals("photo")) {
                attachments.remove(i);
            }
        }

        return attachments.size() > 0 ? attachments : null;
    }

    @Override
    protected String getHelloMessage(CommandResponse cmd) {
        return "Что хотите сделать?";
    }
}
