package dikanev.nikita.bot.controller.db.users;

import com.google.common.hash.Hashing;
import dikanev.nikita.bot.api.exceptions.ApiException;
import dikanev.nikita.bot.api.exceptions.NotFoundException;
import dikanev.nikita.bot.api.objects.UserObject;
import dikanev.nikita.bot.controller.db.commands.CommandDBController;
import dikanev.nikita.bot.controller.users.UserController;
import dikanev.nikita.bot.controller.users.UserCoreController;
import dikanev.nikita.bot.model.callback.VkCommands;
import dikanev.nikita.bot.model.storage.DBStorage;
import dikanev.nikita.bot.model.storage.DataStorage;
import dikanev.nikita.bot.model.storage.clients.CoreClientStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class UserDBController {

    private static final Logger LOG = LoggerFactory.getLogger(UserDBController.class);

    private static UserDBController ourInstance = new UserDBController();

    private PreparedStatement prStatement;

    public static UserDBController getInstance() {
        return ourInstance;
    }

    //Создание человека
    public UserObject createUser(String token, int id, String name, String sName) throws SQLException, ApiException {
        return createUser(token, id, 3, name, sName);
    }

    //Создание человека
    public UserObject createUser(String token, int id, int idGroup, String name, String sName) throws SQLException, ApiException {

        UserObject user = UserCoreController.createUser(token, idGroup, name, sName);
        String receivedToken = UserCoreController.getToken(token, user.getId());

        String sql = "INSERT INTO users(id, id_core, token) " +
                "VALUES (?, ?, ?)";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, id);
        prStatement.setInt(2, user.getId());
        prStatement.setString(3, receivedToken);
        int countUpdate = prStatement.executeUpdate();
        prStatement.close();

        if (countUpdate == 0) {
            LOG.warn("Failed to create a user with the data: (" + sName + ", " + name + ", " + idGroup + " )");
            throw new IllegalStateException("Failed to create a user");
        }

        CommandDBController.getInstance().createCurrentCommand(id, "", VkCommands.HOME.ordinal());

        return user;
    }

    //Удаление человека
    public boolean deleteUser(int idUser) throws SQLException {
        String sql = "DELETE FROM users " +
                "WHERE id = ? " +
                "LIMIT 1";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, idUser);
        int countDelete = prStatement.executeUpdate();
        prStatement.close();

        if (countDelete == 0) {
            LOG.warn("Failed to delete user with id: " + idUser);
            return false;
        }

        UserDBController.getInstance().deleteFromGraph(idUser);

        return true;
    }

    //Удаляет человека из графа
    public boolean deleteFromGraph(int idUser) throws SQLException {
        String sql = "DELETE FROM graph " +
                "WHERE id_user = ? " +
                "LIMIT 1";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, idUser);
        prStatement.executeUpdate();
        prStatement.close();

        return true;
    }

    //Получение информации о человеке.
    //Возвращает map с ключами: id, id_core, id_command, token, args
    public Map<String, Object> getData(int id) throws SQLException {
        String sql = "SELECT usr.id, usr.id_core, usr.token, graph.args, graph.id_command " +
                "FROM (SELECT users.id, users.id_core, users.token FROM users WHERE id = ? LIMIT 1) AS usr " +
                "   LEFT JOIN graph ON usr.id = graph.id_user WHERE usr.id = ? " +
                "LIMIT 1;";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, id);
        prStatement.setInt(2, id);
        ResultSet res = prStatement.executeQuery();

        Map<String, Object> resMap = new HashMap<>();
        while (res.next()) {
            resMap.put("id", res.getInt("id"));
            resMap.put("id_core", res.getInt("id_core"));
            resMap.put("id_command", res.getInt("id_command"));
            resMap.put("token", res.getString("token"));
            resMap.put("args", res.getString("args"));
        }

        res.close();
        return resMap.size() > 0 ? resMap : null;
    }

    //Получение токена
    public String getToken(int id) throws SQLException, ApiException {
        String sql = "SELECT token, id_core " +
                "FROM users " +
                "WHERE id = ? " +
                "LIMIT 1;";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, id);
        ResultSet res = prStatement.executeQuery();

        String token = null;
        int idCore = -1;
        while (res.next()) {
            token = res.getString("token");
            idCore = res.getInt("id_core");
        }
        res.close();

        if (token == null) {
            throw new NotFoundException("User not found");
        } else if (token.equals("")) {
            UserCoreController.getToken(CoreClientStorage.getInstance().getToken(), id);
        }

        return token;
    }

    //Получение хеша строки
    public String getHash(String text) {
        return Hashing.sha256().hashString(text, StandardCharsets.UTF_8).toString();
    }
}
