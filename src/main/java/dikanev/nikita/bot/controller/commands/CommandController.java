package dikanev.nikita.bot.controller.commands;

import dikanev.nikita.bot.api.exceptions.ApiException;
import dikanev.nikita.bot.logic.connector.db.commands.CommandDBConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Map;

public class CommandController {

    private static final Logger LOG = LoggerFactory.getLogger(CommandController.class);

    private static CommandController ourInstance = new CommandController();

    public static CommandController getInstance() {
        return ourInstance;
    }

    //Создание команды
    public int createCurrentCommand(int idUser, String args, int idCommand) throws SQLException{
        return CommandDBConnector.createCurrentCommand(idUser, args, idCommand);
    }

    //Возвращает текущий id команды (id_command) и текущие аргументы (args)
    public Map<String, Object> getCurrentCommand(int userId) throws SQLException, ApiException {
        return CommandDBConnector.getCurrentCommand(userId);
    }

    public boolean setArgs(int userId, String args) throws SQLException {
        return CommandDBConnector.setArgs(userId, args);
    }

    public boolean setCurrentCommand(int idUser, String args, int idCommand) throws SQLException {
        return CommandDBConnector.setCurrentCommand(idUser, args, idCommand);
    }
}
