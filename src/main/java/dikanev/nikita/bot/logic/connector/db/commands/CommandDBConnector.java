package dikanev.nikita.bot.logic.connector.db.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import dikanev.nikita.bot.api.exceptions.NotFoundException;
import dikanev.nikita.bot.service.client.SQLRequest;
import dikanev.nikita.bot.service.storage.DBStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CommandDBConnector {

    private static final Logger LOG = LoggerFactory.getLogger(CommandDBConnector.class);
    private final static JsonParser jsParser = new JsonParser();

    //Создание команды
    public static int createCurrentCommand(int idUser, String args, String state, int idCommand) throws SQLException {
        SQLRequest req = new SQLRequest(DBStorage.getInstance().getConnection())
                .build("INSERT graph(id_user, args, id_command, state) VALUES (?, ?, ?, ?)")
                .set(
                        p -> p.setInt(1, idUser),
                        p -> p.setString(2, args),
                        p -> p.setInt(3, idCommand),
                        p -> p.setString(4, state)
                        );
        int res = req.executeUpdate();
        req.close();

        if (res == 0) {
            LOG.warn("Failed to create a command");
            throw new IllegalStateException("Failed to create a command ");
        }

        return idUser;
    }

    //Получение текущей позиции на графе
    public static Map<String, Object> getCurrentCommand(int userId) throws SQLException, NotFoundException {
        ResultSet res = new SQLRequest(DBStorage.getInstance().getConnection())
                .build("SELECT id_command, args, state " +
                        "FROM graph " +
                        "WHERE id_user = ? " +
                        "LIMIT 1")
                .set(p -> p.setInt(1, userId))
                .executeQuery();

        int id = -1;
        String args = null;
        String state = null;
        while (res.next()) {
            id = res.getInt("id_command");
            args = res.getString("args");
            state = res.getString("state");
        }

        res.close();
        if (id < 0 || args == null) {
            throw new NotFoundException("User not found");
        }

        return new HashMap<>(Map.of("id_command", id, "args", args, "state", state == null ? "" : state));
    }

    //Устанавливает текущие аргументы
    public static boolean setArgs(int userId, String args) throws SQLException {
        SQLRequest req = new SQLRequest(DBStorage.getInstance().getConnection())
                .build("UPDATE graph " +
                        "SET args = ? " +
                        "WHERE id_user = ? " +
                        "LIMIT 1")
                .set(
                        p -> p.setString(1, args),
                        p -> p.setInt(2, userId));

        int res = req.executeUpdate();
        req.close();

        if (res == 0) {
            createCurrentCommand(userId, args, "{}", 0);
        }

        return true;
    }

    //Устанавливает текущую команду
    public static boolean setCurrentCommand(int idUser, String args, String state, int idCommand) throws SQLException {
        SQLRequest req = new SQLRequest(DBStorage.getInstance().getConnection())
                .build("UPDATE graph " +
                        "SET args = ?, id_command = ?, state = ? " +
                        "WHERE id_user = ? " +
                        "LIMIT 1")
                .set(
                        p -> p.setString(1, args),
                        p -> p.setInt(2, idCommand),
                        p -> p.setString(3, state),
                        p -> p.setInt(4, idUser)
                        );

        int res = req.executeUpdate();
        req.close();

        if (res == 0) {
            createCurrentCommand(idUser, args, state, idCommand);
        }

        return true;
    }
}
