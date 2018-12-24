package dikanev.nikita.bot.controller.users;

import com.google.gson.JsonObject;
import dikanev.nikita.bot.api.exceptions.ApiException;
import dikanev.nikita.bot.api.groups.Group;
import dikanev.nikita.bot.api.objects.JObject;
import dikanev.nikita.bot.api.objects.UserObject;
import dikanev.nikita.bot.logic.connector.core.UserCoreConnector;
import dikanev.nikita.bot.logic.connector.db.users.UserDBConnector;
import dikanev.nikita.bot.service.storage.clients.CoreClientStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Map;

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
    public static Map<String, Object> getData(int idUser) throws SQLException {
        return UserDBConnector.getData(idUser);
    }

    //Получает юзера из ядра
    public static UserObject getUser(String token, int idUser) throws SQLException, ApiException {
        LOG.debug("UserController.getUser(" + token + ", " + idUser + ")");
        idUser = UserDBConnector.getIdCore(idUser);
        LOG.debug("UserController.getUser idCore: " + idUser);
        return UserCoreConnector.getUser(token, idUser);
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
}
