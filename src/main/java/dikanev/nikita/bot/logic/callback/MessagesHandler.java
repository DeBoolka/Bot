package dikanev.nikita.bot.logic.callback;

import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import dikanev.nikita.bot.api.exceptions.NotFoundException;
import dikanev.nikita.bot.controller.commands.CommandController;
import dikanev.nikita.bot.controller.users.UserController;
import dikanev.nikita.bot.logic.callback.commands.VkCommand;
import dikanev.nikita.bot.service.storage.DataStorage;
import dikanev.nikita.bot.service.storage.clients.CoreClientStorage;
import dikanev.nikita.bot.service.storage.clients.VkClientStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class MessagesHandler {

    private static final Logger LOG = LoggerFactory.getLogger(MessagesHandler.class);

    public static void parseMessage(Integer groupId, Message message) {
        try {
            Map<String, Object> currentDataCommand = getCurrentDataCommand(message);

            int currentIdCommand = (Integer) currentDataCommand.get("id_command");
            String args = (String) currentDataCommand.get("args");
            CommandResponse commandResponse = new CommandResponse(message.getUserId(), currentIdCommand, args, message);
            commandResponse.setText(message.getBody());

            handle(commandResponse);
        } catch (SQLException e) {
            LOG.error("DB error in parseMessage: " + e.getMessage(), e);
        } catch (Exception e) {
            LOG.error("Unknown error in parseMessage: " + e.getMessage(), e);
        }
    }

    private static void handle(CommandResponse commandResponse) throws Exception {
        //Обработчик текущей команды
        if (commandResponse.isHandle()) {
            VkCommand currentCommand = getCommand(commandResponse.getIdCommand());
            commandResponse = currentCommand.handle(commandResponse);
        }

        //Вход в следующую команду
        if (commandResponse.isInit()) {
            VkCommand nextCommand = getCommand(commandResponse.getIdCommand());
            commandResponse = nextCommand.init(commandResponse);
        }

        if (commandResponse.isTransit()) {
            handle(commandResponse);
        } else {
            setCurrentCommand(commandResponse);
        }
    }

    private static void setCurrentCommand(CommandResponse commandResponse) {
        try {
            CommandController.getInstance().setCurrentCommand(commandResponse.getIdUser(), commandResponse.getArgs(), commandResponse.getIdCommand());
        } catch (SQLException e) {
            LOG.error("DB error: " + e.getMessage());
        }
    }

    private static Map<String, Object> getCurrentDataCommand(Message message) throws Exception{
        Map<String, Object> currentDataCommand = null;
        try {
            currentDataCommand = CommandController.getInstance().getCurrentCommand(message.getUserId());
        } catch (NotFoundException e) {
            List<UserXtrCounters> users = VkClientStorage.getInstance().vk().users()
                    .get(DataStorage.getInstance().getActor())
                    .userIds(String.valueOf(message.getUserId()))
                    .execute();
            if (users == null || users.size() == 0) {
                throw new NotFoundException("Users not found from vk");
            }

            UserXtrCounters user = users.get(0);
            UserController.getInstance().createUser(
                    CoreClientStorage.getInstance().getToken(), message.getUserId(), user.getFirstName(), user.getLastName()
            );

            currentDataCommand = CommandController.getInstance().getCurrentCommand(message.getUserId());
        }

        return currentDataCommand;
    }

    private static VkCommand getCommand(int idCommand){
        return VkCommands.getCommand(idCommand);
    }
}
