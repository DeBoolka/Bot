package dikanev.nikita.bot.controller.db.commands;

import dikanev.nikita.bot.api.exceptions.NotFoundException;
import dikanev.nikita.bot.model.storage.DBStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class CommandDBController {

    private static final Logger LOG = LoggerFactory.getLogger(CommandDBController.class);

    private static CommandDBController ourInstance = new CommandDBController();

    private PreparedStatement prStatement;

    public static CommandDBController getInstance() {
        return ourInstance;
    }

    //Создание команды
    public int createCurrentCommand(int idUser, String args, int idCommand) throws SQLException {
        String sql = "INSERT graph(id_user, args, id_command) VALUES (?, ?, ?)";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
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
    public Map<String, Object> getCurrentCommand(int userId) throws SQLException, NotFoundException {
        String sql = "SELECT id_command, args " +
                "FROM graph " +
                "WHERE id_user = ? " +
                "LIMIT 1";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
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

        Map<String, Object> resp = new HashMap<>(Map.of("id_command", id, "args", args));

        return resp;
    }

    //Устанавливает текущие аргументы
    public boolean setArgs(int userId, String args) throws SQLException {
        String sql = "UPDATE graph " +
                "SET args = ? " +
                "WHERE id_user = ? " +
                "LIMIT 1";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
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
    public boolean setCurrentCommand(int idUser, String args, int idCommand) throws SQLException {
        String sql = "UPDATE graph " +
                "SET args = ?, id_command = ? " +
                "WHERE id_user = ? " +
                "LIMIT 1";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
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
