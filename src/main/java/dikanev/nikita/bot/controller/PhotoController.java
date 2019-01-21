package dikanev.nikita.bot.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.photos.PhotoUpload;
import com.vk.api.sdk.objects.photos.responses.MessageUploadResponse;
import com.vk.api.sdk.queries.photos.PhotosGetMessagesUploadServerQuery;
import dikanev.nikita.bot.api.item.PhotoCore;
import dikanev.nikita.bot.api.item.PhotoVk;
import dikanev.nikita.bot.logic.connector.db.PhotoDBConnector;
import dikanev.nikita.bot.service.storage.DataStorage;
import dikanev.nikita.bot.service.storage.clients.VkClientStorage;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class PhotoController {

    private static final Logger LOG = LoggerFactory.getLogger(PhotoController.class);

    public static List<PhotoVk> loadInVk(int userId, Map<Integer, String> notLoadInVk) {
        List<PhotoVk> loaded = new ArrayList<>(notLoadInVk.size());

        for (Map.Entry<Integer, String> entry : notLoadInVk.entrySet()) {
            Integer k = entry.getKey();
            String v = entry.getValue();
            File file = saveFiles(v);
            try {
                PhotosGetMessagesUploadServerQuery uploadServer = new PhotosGetMessagesUploadServerQuery(VkClientStorage.getInstance().vk(), DataStorage.getInstance().getActor());
                uploadServer.peerId(userId);

                PhotoUpload photoUpload = uploadServer.execute();
                MessageUploadResponse uploadResponse = VkClientStorage.getInstance().vk().upload().photoMessage(photoUpload.getUploadUrl(), file).execute();

                LOG.info("Save photo: [upload server: " + uploadResponse.getServer()
                        + ", hash: " + uploadResponse.getHash()
                        + ", photo: " + uploadResponse.getPhoto()
                        + ", upload url: " + photoUpload.getUploadUrl());
                Photo photo = VkClientStorage.getInstance().vk().photos().saveMessagesPhoto(DataStorage.getInstance().getActor(), uploadResponse.getPhoto())
                        .server(uploadResponse.getServer())
                        .hash(uploadResponse.getHash())
                        .execute().get(0);


                loaded.add(new PhotoVk(photo.getId(), photo.getOwnerId(), photo.getAccessKey(), k));
            } catch (Exception ex) {
                LOG.error("Failed photo upload on server", ex);
            } finally {
                if (file != null && !file.delete()) {
                    file.deleteOnExit();
                }
                LOG.info("Delete file: " + (file != null ? file.getPath() : null));
            }
        }

        try {
            PhotoDBConnector.addPhoto(loaded);
        } catch (SQLException e) {
            LOG.warn("Failed save to DB photos", e);
        }

        return loaded;
    }

    public static List<PhotoVk> getPhotoVk(int userId, PhotoCore[] photos) throws SQLException {
        Map<Integer, String> photosCore = new HashMap<>(photos.length);
        Arrays.stream(photos).forEach(it -> photosCore.put(it.id, it.link));

        List<PhotoVk> photosCoreAndVk = PhotoDBConnector.getPhotoFromCore(photosCore.keySet().toArray(new Integer[0]));
        photosCoreAndVk.forEach(it -> photosCore.remove(it.coreId));

        if (!photosCore.isEmpty()) {
            LOG.info("Not load photo in vk: " + photosCore);
            photosCoreAndVk.addAll(PhotoController.loadInVk(userId, photosCore));
        }

        return photosCoreAndVk;
    }

    private static File saveFiles(String filePath) {
            File file = new File(filePath.replace('/', '.')/*String.valueOf(Objects.hash(System.nanoTime()))*/);
            try {
                FileUtils.copyURLToFile(new URL(filePath), file);
                LOG.info("Save file: " + file.getPath());
            } catch (IOException e) {
                LOG.error("Failed load file: " + filePath, e);
            }

        return file;
    }

    public static JsonArray getAttachmentsPhotoFromMessageVk(JsonObject requestObject) {
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

    public static Map<PhotoVk, String> getUrlPhotoMaxSizeFromMessageVk(JsonArray attachments) {
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
}
