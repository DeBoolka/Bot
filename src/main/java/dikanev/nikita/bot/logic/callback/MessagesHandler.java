package dikanev.nikita.bot.logic.callback;

import com.google.gson.JsonObject;
import com.vk.api.sdk.objects.messages.Message;
import dikanev.nikita.bot.api.exceptions.NotFoundException;
import dikanev.nikita.bot.controller.commands.CommandController;
import dikanev.nikita.bot.controller.users.UserController;
import dikanev.nikita.bot.logic.callback.commands.VkCommand;
import dikanev.nikita.bot.service.client.parameter.HttpGetParameter;
import dikanev.nikita.bot.service.client.parameter.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Map;

public class MessagesHandler {

    private static final Logger LOG = LoggerFactory.getLogger(MessagesHandler.class);

    public static void parseMessage(Integer groupId, Message message, JsonObject requestObject) {
        try {
            Map<String, Object> currentDataCommand = getCurrentDataCommand(message);
            int currentIdCommand = (Integer) currentDataCommand.get("id_command");
            Parameter args = new HttpGetParameter((String) currentDataCommand.get("args"));
            JsonObject state = (JsonObject) currentDataCommand.get("args");

            CommandResponse resp = new CommandResponse(message.getUserId(), currentIdCommand, args, message, requestObject);
            resp.setText(message.getBody());
            resp.setState(state);

            handle(resp);
        } catch (SQLException e) {
            LOG.error("DB error in parseMessage: " + e.getMessage(), e);
        } catch (Exception e) {
            LOG.error("Unknown error in parseMessage: " + e.getMessage(), e);
        }
    }

    private static void handle(CommandResponse resp) throws Exception {
        //Обработчик текущей команды
        if (resp.isHandle()) {
            VkCommand currentCommand = getCommand(resp.getIdCommand());
            resp = currentCommand.handle(resp, resp.getArgs());
        }

        //Вход в следующую команду
        if (resp.isInit()) {
            VkCommand nextCommand = getCommand(resp.getIdCommand());
            resp = nextCommand.init(resp, resp.getArgs());
        }

        if (resp.isTransit()) {
            handle(resp);
        } else {
            setCurrentCommand(resp);
        }
    }

    private static void setCurrentCommand(CommandResponse resp) {
        try {
            CommandController.setCurrentCommand(resp.getUserId(), resp.getArgs().getContent(), resp.getState().toString(), resp.getIdCommand());
        } catch (SQLException e) {
            LOG.error("DB error: " + e.getMessage());
        }
    }

    private static Map<String, Object> getCurrentDataCommand(Message message) throws Exception{
        Map<String, Object> currentDataCommand;
        try {
            currentDataCommand = CommandController.getCurrentCommand(message.getUserId());
        } catch (NotFoundException e) {
            UserController.createUser(message.getUserId());
            currentDataCommand = CommandController.getCurrentCommand(message.getUserId());
        }

        return currentDataCommand;
    }

    private static VkCommand getCommand(int idCommand){
        return VkCommands.getCommand(idCommand);
    }
}
