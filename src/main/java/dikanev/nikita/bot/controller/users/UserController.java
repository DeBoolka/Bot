package dikanev.nikita.bot.controller.users;

import com.google.gson.JsonObject;
import com.vk.api.sdk.exceptions.ClientException;
import dikanev.nikita.bot.api.PhotoVk;
import dikanev.nikita.bot.api.exceptions.ApiException;
import dikanev.nikita.bot.api.exceptions.InvalidParametersException;
import dikanev.nikita.bot.api.groups.Group;
import dikanev.nikita.bot.api.objects.JObject;
import dikanev.nikita.bot.api.objects.UserInfoObject;
import dikanev.nikita.bot.api.objects.UserObject;
import dikanev.nikita.bot.controller.PhotoController;
import dikanev.nikita.bot.logic.connector.core.UserCoreConnector;
import dikanev.nikita.bot.logic.connector.db.PhotoDBConnector;
import dikanev.nikita.bot.logic.connector.db.users.UserDBConnector;
import dikanev.nikita.bot.service.storage.clients.CoreClientStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    //Создание человека
    public static boolean createUser(int id) throws SQLException {
        return UserDBConnector.createUser(id);
    }

    //регистрация юзера в ядре
    public static UserObject register(String token, int userIdInBot, String name, String sName, String email, String login, int idGroup) throws ApiException, SQLException {
        UserObject userFromCore = UserCoreConnector.register(token, name, sName, email, login, idGroup);
        String userToken = UserCoreConnector.getToken(CoreClientStorage.getInstance().getToken(), userFromCore.getId());
        UserDBConnector.setCoreIdAndToken(userIdInBot, userFromCore.getId(), userToken);
        return userFromCore;
    }

    //Удаление человека
    public static boolean deleteUser(int idUser) throws SQLException {
        return UserDBConnector.deleteUser(idUser);
    }

    //Получение информации о человеке.
    //Возвращает map с ключами: id, id_core, id_command, token, args
    public static Map<String, Object> getSystemData(int idUser) throws SQLException {
        return UserDBConnector.getSystemData(idUser);
    }

    //Получает юзера из ядра
    public static UserObject getUser(String token, int idUser) throws SQLException, ApiException {
        return UserCoreConnector.getUser(token, UserDBConnector.getIdCore(idUser));
    }

    //Получить токен
    public static String getToken(int id) throws SQLException, ApiException {
        return UserDBConnector.getToken(id);
    }

    //Удалить токен
    private static boolean deleteFromGraph(int idUser) throws SQLException {
        return UserDBConnector.deleteFromGraph(idUser);
    }

    //Применение инвайта от другого пользователя
    public static Group applyInvite(String token, int userIdInCore, String invite) throws ApiException, SQLException {
        JObject resp = UserCoreConnector.applyInvite(token,  UserDBConnector.getIdCore(userIdInCore), invite);

        JsonObject rootObj = resp.getObj();
        int id = rootObj.get("id").getAsInt();
        String name = rootObj.get("name").getAsString();

        return new Group(id, name);
    }

    public static String createInvite(String token, int userId, Integer groupId) throws ApiException {
        try {
            return UserCoreConnector.createInvite(token, UserDBConnector.getIdCore(userId), groupId);
        } catch (SQLException e) {
            LOG.error("Failed get userId", e);
            return null;
        }
    }

    public static Group setGroup(String token, int userId, Integer groupId) {
        try {
            return UserCoreConnector.setGroup(token, UserDBConnector.getIdCore(userId), groupId);
        } catch (Exception e) {
            LOG.error("Failed get userId in core.", e);
            return null;
        }
    }

    public static UserInfoObject getUserInfo(String token, int userId) throws SQLException, ApiException {
        return UserCoreConnector.getUserInfo(token, UserDBConnector.getIdCore(userId));
    }

    public static JObject getPersonalDataOfUser(String token, int userId, String... data) throws ApiException, SQLException {
        return UserCoreConnector.getPersonalDataOfUser(token, UserDBConnector.getIdCore(userId), data);
    }

    public static boolean updateUserInfo(String token, int userId, String updateData, String newValues) throws SQLException, ApiException {
        return UserCoreConnector.updateUserInfo(token, UserDBConnector.getIdCore(userId), updateData, newValues);
    }

    public static Map<String, Integer> addPhoto(String token, int userId, Map<PhotoVk, String> photosInVk) throws SQLException, ApiException {
        String[] links = photosInVk.values().toArray(new String[0]);
        Map<String, Integer> photosInCore = UserCoreConnector.addPhoto(token, UserDBConnector.getIdCore(userId), links);

        List<PhotoVk> photoInVkAndCore = new ArrayList<>(photosInCore.size());
        photosInVk.forEach((phVk, link) -> {
            Integer coreId = photosInCore.get(link);
            if (coreId != null) {
                phVk.coreId = coreId;
                photoInVkAndCore.add(phVk);
            }
        });
        PhotoDBConnector.addPhoto(photoInVkAndCore);

        return photosInCore;
    }

    public static List<PhotoVk> getPhotoByUser(String token, int userId, int indent, int count) throws ApiException, ClientException, com.vk.api.sdk.exceptions.ApiException, SQLException {
        Map<Integer, String> photosCore = UserCoreConnector.getPhotoByUser(token, UserDBConnector.getIdCore(userId), indent, count);
        if (photosCore == null) {
            throw new InvalidParametersException("Incorrect count or indent parameter.");
        } else if (photosCore.isEmpty()) {
            return null;
        }

        List<PhotoVk> photosCoreAndVk = PhotoDBConnector.getPhotoFromCore(photosCore.keySet().toArray(new Integer[0]));
        Map<Integer, String> notLoadInVk = new HashMap<>(photosCore);
        photosCoreAndVk.forEach(it -> notLoadInVk.remove(it.coreId));

        if (!notLoadInVk.isEmpty()) {
            LOG.info("Not load in vk: " + notLoadInVk);
            photosCoreAndVk.addAll(PhotoController.loadInVk(userId, notLoadInVk));
        }

        return photosCoreAndVk;
    }

    public static boolean deletePhoto(String token, PhotoVk[] photos) throws ApiException, SQLException {
        Integer[] photosId = new Integer[photos.length];
        for (int i = 0; i < photos.length; i++) {
            photosId[i] = photos[i].coreId;
        }

        return UserCoreConnector.deletePhoto(token, photosId) && PhotoDBConnector.deletePhoto(photosId);
    }
}
