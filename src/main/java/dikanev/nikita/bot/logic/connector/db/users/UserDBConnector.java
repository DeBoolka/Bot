package dikanev.nikita.bot.logic.connector.db.users;

import dikanev.nikita.bot.api.exceptions.ApiException;
import dikanev.nikita.bot.logic.connector.db.commands.CommandDBConnector;
import dikanev.nikita.bot.logic.connector.core.UserCoreConnector;
import dikanev.nikita.bot.logic.callback.VkCommands;
import dikanev.nikita.bot.service.storage.DBStorage;
import dikanev.nikita.bot.service.storage.clients.CoreClientStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class UserDBConnector {

    private static final Logger LOG = LoggerFactory.getLogger(UserDBConnector.class);

    public static boolean setCoreIdAndToken(int userIdInBot, int id, String userToken) throws SQLException {
        String sql = "UPDATE users SET id_core = ?, token = ? WHERE id = ?";

        PreparedStatement prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, id);
        prStatement.setString(2, userToken);
        prStatement.setInt(3, userIdInBot);
        int countUpdate = prStatement.executeUpdate();
        prStatement.close();

        if (countUpdate == 0) {
            LOG.warn("Failed to create coreId (" + id + ") and token (" + userToken + "). ");
            return false;
        }

        CommandDBConnector.createCurrentCommand(id, "", VkCommands.ENTRY_BOT.ordinal());

        return true;
    }

    //Создание человека
    public static boolean createUser(int id) throws SQLException {
        String sql = "INSERT INTO users(id, id_core, token) " +
                "VALUES (?, ?, ?)";

        PreparedStatement prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, id);
        prStatement.setNull(2, Types.INTEGER);
        prStatement.setNull(3, Types.CHAR);
        int countUpdate = prStatement.executeUpdate();
        prStatement.close();

        if (countUpdate == 0) {
            LOG.warn("Failed to create a user with id: " + id);
            return false;
        }

        CommandDBConnector.createCurrentCommand(id, "", VkCommands.ENTRY_BOT.ordinal());

        return true;
    }

    //Удаление человека
    public static boolean deleteUser(int idUser) throws SQLException {
        String sql = "DELETE FROM users " +
                "WHERE id = ? " +
                "LIMIT 1";

        PreparedStatement prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, idUser);
        int countDelete = prStatement.executeUpdate();
        prStatement.close();

        if (countDelete == 0) {
            LOG.warn("Failed to delete user with id: " + idUser);
            return false;
        }

        UserDBConnector.deleteFromGraph(idUser);

        return true;
    }

    //Удаляет человека из графа
    public static boolean deleteFromGraph(int idUser) throws SQLException {
        String sql = "DELETE FROM graph " +
                "WHERE id_user = ? " +
                "LIMIT 1";

        PreparedStatement prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, idUser);
        prStatement.executeUpdate();
        prStatement.close();

        return true;
    }

    //Получение информации о человеке.
    //Возвращает map с ключами: id, id_core, id_command, token, args
    public static Map<String, Object> getData(int id) throws SQLException {
        String sql = "SELECT usr.id, usr.id_core, usr.token, graph.args, graph.id_command " +
                "FROM users AS usr " +
                "   LEFT JOIN graph ON usr.id = graph.id_user WHERE usr.id = ? " +
                "LIMIT 1";

        PreparedStatement prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
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

    public static int getIdCore(int id) throws SQLException {
        String sql = "SELECT id_core " +
                "FROM users " +
                "WHERE id = ? " +
                "LIMIT 1;";

        PreparedStatement prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, id);
        ResultSet res = prStatement.executeQuery();

        int idCore = -1;
        while (res.next()) {
            idCore = res.getInt("id_core");
        }

        res.close();
        return idCore;
    }

    //Получение токена
    public static String getToken(int id) throws SQLException, ApiException {
        String sql = "SELECT token, id_core " +
                "FROM users " +
                "WHERE id = ? " +
                "LIMIT 1;";

        PreparedStatement prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, id);
        ResultSet res = prStatement.executeQuery();

        String token = null;
        int idCore = -1;
        while (res.next()) {
            token = res.getString("token");
            idCore = res.getInt("id_core");
        }
        res.close();

        if (token != null && idCore > 0 && token.equals("")) {
            UserCoreConnector.getToken(CoreClientStorage.getInstance().getToken(), idCore);
        }

        return token;
    }

}
