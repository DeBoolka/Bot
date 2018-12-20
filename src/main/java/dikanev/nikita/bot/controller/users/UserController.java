package dikanev.nikita.bot.controller.users;

import dikanev.nikita.bot.api.exceptions.ApiException;
import dikanev.nikita.bot.api.exceptions.InvalidParametersException;
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

    private static UserController ourInstance = new UserController();

    private PreparedStatement prStatement;

    public static UserController getInstance() {
        return ourInstance;
    }

    //Создание человека
    public boolean createUser(int id) throws SQLException {
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
    public boolean deleteUser(int idUser) throws SQLException {
        return UserDBConnector.deleteUser(idUser);
    }

    //Получение информации о человеке.
    //Возвращает map с ключами: id, id_core, id_command, token, args
    public Map<String, Object> getData(int idUser) throws SQLException {
        return UserDBConnector.getData(idUser);
    }

    //Получает юзера из ядра
    public UserObject getUser(String token, int idUser) throws SQLException, ApiException {
        LOG.debug("UserController.getUser(" + token + ", " + idUser + ")");
        idUser = UserDBConnector.getIdCore(idUser);
        LOG.debug("UserController.getUser idCore: " + idUser);
        return UserCoreConnector.getUser(token, idUser);
    }

    //Получить токен
    public String getToken(int id) throws SQLException, ApiException {
        return UserDBConnector.getToken(id);
    }

    //Удалить токен
    private boolean deleteFromGraph(int idUser) throws SQLException {
        return UserDBConnector.deleteFromGraph(idUser);
    }

    //Применение инвайта от другого пользователя
    public UserObject inInvite(int idUser, String invite) throws InvalidParametersException {
        //todo: сделать
        throw new IllegalStateException("Todo: Доделать");
    }
}
