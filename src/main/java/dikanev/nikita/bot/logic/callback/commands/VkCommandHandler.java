package dikanev.nikita.bot.logic.callback.commands;

import dikanev.nikita.bot.controller.groups.AccessGroupController;
import dikanev.nikita.bot.logic.callback.CommandResponse;
import dikanev.nikita.bot.service.client.parameter.Parameter;
import dikanev.nikita.bot.service.storage.clients.CoreClientStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class VkCommandHandler extends VkCommand {

    private static final Logger LOG = LoggerFactory.getLogger(VkCommandHandler.class);

    //Загружает из ядра доступность команд. Проставляет достум в map-е commands
    protected void loadAccessCommands(CommandResponse cmdResp, Parameter args, Map<String, CommandData> commands) {
        final List<String> commandsName = new ArrayList<>();
        commands.forEach((key, val) -> {
            if (val.isLoadAccess()) {
                commandsName.add(key);
            }
        });

        Map<String, Boolean> cmdAccessMap;
        try {
            String token = CoreClientStorage.getInstance().getToken();
            cmdAccessMap = AccessGroupController.getInstance().getAccessUser(token, cmdResp.getIdUser(), commandsName);
        } catch (Exception e) {
            LOG.warn("Get access error: ", e);
            return;
        }

        cmdAccessMap.forEach((key, val) -> {
            CommandData commandData = commands.get(key);
            if (commandData != null) {
                commandData.setAccess(val);
            }
        });
    }

    //Возврщает map с путем доступа в качестве ключа (bot/vk/...) и в качестве значения CommandData
    protected abstract Map<String, CommandData> getCommands(CommandResponse cmdResp, Parameter args);

    //Метод обрабатывающий отправку сообщения при входе в команду
    protected void messageHandle(CommandResponse cmdResp, Parameter args, Keyboard buttons) throws Exception {
        String message = args.getF("message");
        if (message == null) {
            new SendMessage(cmdResp.getIdUser()).message(getHelloMessage(cmdResp)).button(buttons.setOneTime(true)).execute();
            args.set("message", "default");
        } else if(message.equals("default")) {
            new SendMessage(cmdResp.getIdUser()).message("Введите help для получения списка команд").button(buttons.setOneTime(true)).execute();
        } else {
            new SendMessage(cmdResp.getIdUser()).message(message).button(buttons.setOneTime(true)).execute();
            args.set("message", "default");
        }
    }

    protected abstract String getHelloMessage(CommandResponse cmd);

    //Метод возвращающий help текст
    protected String helpCommand(Map<String, CommandData> commands) {
        final StringBuilder helpMessage = new StringBuilder("Список команд:\n");
        commands.forEach((key, val) -> {
            if (val.isAccess()) {
                helpMessage.append(val.getName()).append(" - ").append(val.getHelpMessage()).append("\n");
            }
        });
        return helpMessage.toString();
    }

    //Получение доступных кнопок
    protected Keyboard getButtons(Map<String, CommandData> commands) {
        final Keyboard buttons = new Keyboard();
        int[] countInRow = new int[]{0};
        commands.forEach((key, val) -> {
            if (val.isAccess()) {
                buttons.addButton(val.getName(), val.getColor(), val.getName());
                countInRow[0]++;
                if (countInRow[0] == 3) {
                    buttons.endl();
                }
            }
        });
        return buttons;
    }

    //Заглушка для нереализованной функции
    protected CommandResponse unrealizedOperation(CommandResponse cmdResp) throws Exception {
        new SendMessage(cmdResp.getIdUser()).message("Извините команда временно недоступна").execute();
        return cmdResp.setInit();
    }

    //Данные о команде
    protected class CommandData{
        String name;

        boolean access = false;

        String helpMessage = "";

        boolean loadAccess = false;

        String color = Keyboard.PRIMARY;

        CommandProcess cmdProcess;

        public CommandData(String name, boolean loadAccess, CommandProcess cmdProcess) {
            init(name, false, null, loadAccess, null, cmdProcess);
        }

        public CommandData(String name, boolean access, String helpMessage, boolean loadAccess, CommandProcess cmdProcess) {
            init(name, access, helpMessage, loadAccess, null, cmdProcess);
        }

        public CommandData(String name, boolean access, String helpMessage, CommandProcess cmdProcess) {
            init(name, access, helpMessage, false, null, cmdProcess);
        }

        public CommandData(String name, String helpMessage, boolean loadAccess, CommandProcess cmdProcess) {
            init(name, false, helpMessage, loadAccess, null, cmdProcess);
        }

        public CommandData(String name, String helpMessage, CommandProcess cmdProcess) {
            init(name, false, helpMessage, false, null, cmdProcess);
        }

        public CommandData(String name, boolean access, String helpMessage, String color, CommandProcess cmdProcess) {
            init(name, access, helpMessage, false, color, cmdProcess);
        }

        public CommandData(String name, String helpMessage, boolean loadAccess, String color, CommandProcess cmdProcess) {
            init(name, false, helpMessage, loadAccess, color, cmdProcess);
        }

        private void init(String name, boolean access, String helpMessage, boolean loadAccess, String color, CommandProcess cmdProcess) {
            this.name = name != null ? name : this.name;
            this.access = access;
            this.helpMessage = helpMessage != null ? helpMessage : this.helpMessage;
            this.loadAccess = loadAccess;
            this.color = color != null ? color : this.color;
            this.cmdProcess = cmdProcess != null ? cmdProcess : this.cmdProcess;
        }

        public String getName() {
            return name;
        }

        public boolean isAccess() {
            return access;
        }

        public String getHelpMessage() {
            return helpMessage;
        }

        public boolean isLoadAccess() {
            return loadAccess;
        }

        public CommandProcess getCmdProcess() {
            return cmdProcess;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setAccess(boolean access) {
            this.access = access;
        }

        public void setHelpMessage(String helpMessage) {
            this.helpMessage = helpMessage;
        }

        public void setLoadAccess(boolean loadAccess) {
            this.loadAccess = loadAccess;
        }

        public String getColor() {
            return color;
        }
    }

    //Класс обрабатывающий действие команды
    protected interface CommandProcess{
        CommandResponse process(CommandResponse commandResponse, CommandData data, Map<String, CommandData> commands) throws Exception;
    }
}
