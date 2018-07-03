package dikanev.nikita.bot.controller.users;

import dikanev.nikita.bot.api.exceptions.ApiException;
import dikanev.nikita.bot.api.exceptions.InvalidParametersException;
import dikanev.nikita.bot.api.objects.UserObject;
import dikanev.nikita.bot.controller.db.users.UserDBController;
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
    public UserObject createUser(String token, int id, String name, String sName) throws SQLException, ApiException {
        return UserDBController.getInstance().createUser(token, id, name, sName);
    }

    //Создание человека
    public UserObject createUser(String token, int id, int idGroup, String name, String sName) throws SQLException, ApiException {
        return UserDBController.getInstance().createUser(token, id, idGroup, name, sName);
    }

    //Удаление человека
    public boolean deleteUser(int idUser) throws SQLException {
        return UserDBController.getInstance().deleteUser(idUser);
    }

    //Получение информации о человеке.
    //Возвращает map с ключами: id, id_core, id_command, token, args
    public Map<String, Object> getData(int idUser) throws SQLException {
        return UserDBController.getInstance().getData(idUser);
    }

    //Получает юзера из ядра
    public UserObject getUser(String token, int idUser) throws SQLException, ApiException {
        return UserCoreController.getUser(token, idUser);
    }

    //Получить токен
    public String getToken(int id) throws SQLException, ApiException {
        return UserDBController.getInstance().getToken(id);
    }

    //Удалить токен
    private boolean deleteFromGraph(int idUser) throws SQLException {
        return UserDBController.getInstance().deleteFromGraph(idUser);
    }

    //Применение инвайта от другого пользователя
    public UserObject inInvite(int idUser, String invite) throws InvalidParametersException {
        //todo: Сделать ввод инвайт кода
        return null;
    }

    //Получение хеша строки
    public String getHash(String text) {
        return UserDBController.getInstance().getHash(text);
    }
}
