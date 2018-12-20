package dikanev.nikita.bot.logic.connector.db.commands;

import dikanev.nikita.bot.api.exceptions.NotFoundException;
import dikanev.nikita.bot.service.storage.DBStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CommandDBConnector {

    private static final Logger LOG = LoggerFactory.getLogger(CommandDBConnector.class);

    //Создание команды
    public static int createCurrentCommand(int idUser, String args, int idCommand) throws SQLException {
        String sql = "INSERT graph(id_user, args, id_command) VALUES (?, ?, ?)";

        PreparedStatement prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, idUser);
        prStatement.setString(2, args);
        prStatement.setInt(3, idCommand);
        int res = prStatement.executeUpdate();
        prStatement.close();

        if (res == 0) {
            LOG.warn("Failed to create a command");
            throw new IllegalStateException("Failed to create a command ");
        }

        return idUser;
    }

    //Получение текущей позиции на графе
    public static Map<String, Object> getCurrentCommand(int userId) throws SQLException, NotFoundException {
        String sql = "SELECT id_command, args " +
                "FROM graph " +
                "WHERE id_user = ? " +
                "LIMIT 1";

        PreparedStatement prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, userId);
        ResultSet res = prStatement.executeQuery();

        int id = -1;
        String args = null;
        while (res.next()) {
            id = res.getInt("id_command");
            args = res.getString("args");
        }

        res.close();
        if (id < 0 || args == null) {
            throw new NotFoundException("User not found");
        }

        return new HashMap<>(Map.of("id_command", id, "args", args));
    }

    //Устанавливает текущие аргументы
    public static boolean setArgs(int userId, String args) throws SQLException {
        String sql = "UPDATE graph " +
                "SET args = ? " +
                "WHERE id_user = ? " +
                "LIMIT 1";

        PreparedStatement prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setString(1, args);
        prStatement.setInt(2, userId);
        int res = prStatement.executeUpdate();
        prStatement.close();

        if (res == 0) {
            createCurrentCommand(userId, args, 0);
        }

        return true;
    }

    //Устанавливает текущую команду
    public static boolean setCurrentCommand(int idUser, String args, int idCommand) throws SQLException {
        String sql = "UPDATE graph " +
                "SET args = ?, id_command = ? " +
                "WHERE id_user = ? " +
                "LIMIT 1";

        PreparedStatement prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setString(1, args);
        prStatement.setInt(2, idCommand);
        prStatement.setInt(3, idUser);
        int res = prStatement.executeUpdate();
        prStatement.close();

        if (res == 0) {
            createCurrentCommand(idUser, args, idCommand);
        }

        return true;
    }
}
