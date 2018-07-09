package dikanev.nikita.bot.model.callback.commands;

import com.google.gson.Gson;
import dikanev.nikita.bot.model.callback.CommandResponse;
import dikanev.nikita.bot.model.storage.clients.CoreClientStorage;
import dikanev.nikita.bot.controller.groups.AccessGroupController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MenuCommand extends VkCommand {

    private static final Logger LOG = LoggerFactory.getLogger(MenuCommand.class);

    @Override
    public CommandResponse init(CommandResponse cmdResp) throws Exception {
        Map<String, String> args = getUrlParameter(cmdResp.getArgs());

        List<String> commands = getAccessCommand(cmdResp, args);
        List<List<TK>> buttons = new ArrayList<>();
        commands.forEach(command -> buttons.add(List.of(TK.getDefault(command, command))));

        String homeMessage = args.get("homeMessage");
        if (homeMessage == null) {
            sendMessage("Вы в главном меню", cmdResp.getIdUser(), true, buttons);
            args.put("homeMessage", "default");
        } else if(homeMessage.equals("default")) {
            sendMessage("Введите help для получения списка команд", cmdResp.getIdUser(), true, buttons);
        } else {
            sendMessage(homeMessage, cmdResp.getIdUser(), true, buttons);
            args.put("homeMessage", "default");
        }

        return cmdResp.setArgs(mapToGetString(args)).finish();
    }

    @Override
    public CommandResponse handle(CommandResponse cmdResp) throws Exception {
        Map<String, String> args = getUrlParameter(cmdResp.getArgs());

        List<String> commands = getAccessCommand(cmdResp, args);
        List<List<TK>> buttons = new ArrayList<>();
        commands.forEach(command -> buttons.add(List.of(TK.getDefault(command, command))));

        return getNextCommand(cmdResp, args, commands, buttons).setArgs(mapToGetString(args));
    }

    private CommandResponse getNextCommand(CommandResponse cmdResp, Map<String, String> args, List<String> commands, List<List<TK>> buttons) throws Exception {
        List<String> commandsLower = new ArrayList<>();
        commands.forEach(e -> commandsLower.add(e.toLowerCase()));

        String userGetCommand = cmdResp.getText().toLowerCase();
        switch (userGetCommand) {
            case "админ":
                if (commandsLower.contains(userGetCommand)) {
                    return unrealizedOperation(cmdResp);
                }
                break;

            case "игры":
                if (commandsLower.contains(userGetCommand)) {
                    return unrealizedOperation(cmdResp);
                }
                break;

            case "команды":
                if (commandsLower.contains(userGetCommand)) {
                    return unrealizedOperation(cmdResp);
                }
                break;

            case "пригласительный":
                if (commandsLower.contains(userGetCommand)) {
                    return unrealizedOperation(cmdResp);
                }
                break;

            case "help":
                args.put("homeMessage", helpCommand(commands));
                break;
        }

        return cmdResp.setInit();
    }

    private List<String> getAccessCommand(CommandResponse cmdResp, Map<String, String> args) {
        List<String> getAccess = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        /*Menu для unknown*/
        //todo: доделать проверку access
        checkAccess("bot/vk/invite/", args, getAccess, commands);

        /*Menu для user*/
        //todo: доделать проверку access
        checkAccess("bot/vk/game/", args, getAccess, commands);

        //todo: доделать проверку access
        checkAccess("bot/vk/team/", args, getAccess, commands);

        /*Admin команды*/
        //todo: доделать проверку access
        checkAccess("bot/vk/admin/", args, getAccess, commands);

        commands.add("help");

        if (getAccess.size() == 0) {
            return commands;
        }

        Map<String, Boolean> cmdAccess;
        try {
            String token = CoreClientStorage.getInstance().getToken();
            cmdAccess = AccessGroupController.getInstance().getAccessUser(token, cmdResp.getIdUser(), getAccess);
        } catch (Exception e) {
            LOG.warn("Get access error: ", e);
            commands.clear();
            return commands;
        }

        LOG.debug("Get map: " + new Gson().toJson(cmdAccess, cmdAccess.getClass()));

        cmdAccess.forEach((key, value) -> {
            args.put(key, String.valueOf(value));
            if (value) {
                commands.add(getTextCommand(key));
            }
        });

        return commands;
    }

    private CommandResponse unrealizedOperation(CommandResponse cmdResp) throws Exception {
        sendMessage("Извините команда временно недоступна", cmdResp.getIdUser(), false);
        return cmdResp.setInit();
    }

    private void checkAccess(String pathCommand, Map<String, String> args, List<String> getAccess, List<String> commands) {
        if (!args.containsKey(pathCommand)) {
            getAccess.add(pathCommand);
        } else if (args.get(pathCommand).equals("true")) {
            commands.add(getTextCommand(pathCommand));
        }
    }

    private String getTextCommand(String pathCommand) {
        switch (pathCommand) {
            case "bot/vk/admin/":
                return "Админ";

            case "bot/vk/game/":
                return "Игры";

            case "bot/vk/team/":
                return "Команды";

            case "bot/vk/invite/":
                return "Пригласительный";

            default:
                return pathCommand;
        }
    }

    private String helpCommand(List<String> commands) {
        StringBuilder helpMessage = new StringBuilder("Список команд:\n");
        commands.forEach(command -> {
            helpMessage.append(command);

            switch (command.toLowerCase()) {
                case "админ":
                    helpMessage.append(" - Меню администратора\n");
                    break;

                case "игры":
                    helpMessage.append(" - Операции с играми\n");
                    break;

                case "команды":
                    helpMessage.append(" - Операции с командами(team)\n");
                    break;

                case "пригласительный":
                    helpMessage.append(" - Ввести пригласительный код\n");
                    break;

                case "help":
                    helpMessage.append(" - Выводит список команд\n");
                    break;

                default:
                    helpMessage.append("\n");
            }
        });

        return helpMessage.toString();
    }
}
