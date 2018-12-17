package dikanev.nikita.bot.logic.callback.commands;

import dikanev.nikita.bot.logic.callback.CommandResponse;
import dikanev.nikita.bot.logic.callback.VkCommands;
import dikanev.nikita.bot.service.client.parameter.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class MenuCommand extends VkCommandHandler {

    private static final Logger LOG = LoggerFactory.getLogger(MenuCommand.class);

    @Override
    public CommandResponse init(CommandResponse cmdResp, Parameter args) throws Exception {
        Map<String, CommandData> commands = getCommands(cmdResp, cmdResp.getArgs());
        loadAccessCommands(cmdResp, args, commands);

        final List<List<TK>> buttons = getButtons(commands);

        messageHandle(cmdResp, args, buttons);

        return cmdResp.finish();
    }

    @Override
    public CommandResponse handle(CommandResponse cmdResp, Parameter args) throws Exception {
        Map<String, CommandData> commands = getCommands(cmdResp, cmdResp.getArgs());
        loadAccessCommands(cmdResp, args, commands);

        String userMessage = cmdResp.getText();

        for (Map.Entry<String, CommandData> entry : commands.entrySet()) {
            CommandData cmdData = entry.getValue();
            if (userMessage.toLowerCase().indexOf(cmdData.name.toLowerCase()) == 0 && cmdData.isAccess()) {
                return cmdData.getCmdProcess().process(cmdResp, cmdData, commands);
            }
        }

        return cmdResp.setInit();
    }

    //Возврщает map с путем доступа в качестве ключа (bot/vk/...) и в качестве значения CommandData
    @Override
    protected Map<String, CommandData> getCommands(CommandResponse cmdResp, Parameter args) {
            return new LinkedHashMap<>(Map.of(
                    "bot/vk/admin/", new CommandData("Админ", " - Меню администратора", true, (resp, data, commands) ->
                            cmdResp.setArgs("").setIdCommand(VkCommands.ADMIN_MENU.id()).setInit()
                    ),
                    "bot/vk/game/", new CommandData("Игры", " - Меню игр", true, (resp, data, commands) ->
                            unrealizedOperation(cmdResp)
                    ),
                    "bot/vk/team/", new CommandData("Команды", " - Меню команд (team)", true, (resp, data, commands) ->
                            unrealizedOperation(cmdResp)
                    ),
                    "bot/vk/invite/", new CommandData("Пригласительный", " - Ввод пригласительного", true, (resp, data, commands) ->
                            unrealizedOperation(cmdResp)
                    ),
                    "help", new CommandData("help",true, "- Выводит список команд", (resp, data, commands) -> {
                        cmdResp.getArgs().set("message", helpCommand(commands));
                        return cmdResp.setInit();
                    })
            ));
    }

    @Override
    protected String getHelloMessage(CommandResponse cmd) {
        return "Вы в главном меню";
    }

    @Override
    protected String helpCommand(Map<String, CommandData> commands) {
        final StringBuilder helpMessage = new StringBuilder("Список команд:\n");
        commands.forEach((key, val) -> {
            if (val.isAccess()) {
                helpMessage.append(val.getName()).append(" ").append(val.getHelpMessage()).append("\n");
            }
        });
        return helpMessage.toString();
    }
}
