package dikanev.nikita.bot.controller.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dikanev.nikita.bot.api.exceptions.ApiException;
import dikanev.nikita.bot.logic.connector.db.commands.CommandDBConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Map;

public class CommandController {

    private static final Logger LOG = LoggerFactory.getLogger(CommandController.class);
    private static final JsonParser jsParser = new JsonParser();

    //Создание команды
    public static int createCurrentCommand(int idUser, String args, String state, int idCommand) throws SQLException{
        return CommandDBConnector.createCurrentCommand(idUser, args, state, idCommand);
    }

    //Возвращает текущий id команды (id_command) и текущие аргументы (args)
    public static Map<String, Object> getCurrentCommand(int userId) throws SQLException, ApiException {
        Map<String, Object> commandData = CommandDBConnector.getCurrentCommand(userId);
        JsonElement state;
        try {
            state = jsParser.parse((String) commandData.get("state"));
            if (!state.isJsonObject()) {
                state = new JsonObject();
            }
        } catch (Exception e) {
            state = new JsonObject();
        }

        commandData.put("state", state);
        return commandData;
    }

    public static boolean setArgs(int userId, String args) throws SQLException {
        return CommandDBConnector.setArgs(userId, args);
    }

    public static boolean setCurrentCommand(int idUser, String args, String state, int idCommand) throws SQLException {
        return CommandDBConnector.setCurrentCommand(idUser, args, state, idCommand);
    }
}
