package dikanev.nikita.bot.controller;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.photos.PhotoUpload;
import com.vk.api.sdk.objects.photos.responses.MessageUploadResponse;
import com.vk.api.sdk.queries.photos.PhotosGetMessagesUploadServerQuery;
import dikanev.nikita.bot.api.PhotoVk;
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
}
