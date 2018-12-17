package dikanev.nikita.bot.logic.callback.commands.menus;

import dikanev.nikita.bot.logic.callback.CommandResponse;
import dikanev.nikita.bot.logic.callback.VkCommands;
import dikanev.nikita.bot.logic.callback.commands.MenuCommand;
import dikanev.nikita.bot.service.client.parameter.Parameter;
import org.apache.commons.collections4.map.LinkedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class AdminMenuCommand extends MenuCommand {

    private static final Logger LOG = LoggerFactory.getLogger(AdminMenuCommand.class);

    @Override
    protected Map<String, CommandData> getCommands(CommandResponse cmdResp, Parameter args) {
        return new LinkedMap<>(Map.of(
                "help", new CommandData("help", true, "- Выводит список команд", (resp, data, commands) -> {
                    args.set("message", helpCommand(commands));
                    return cmdResp.setInit();
                }),
                "menu", new CommandData("menu", true, "- Возврат в главное меню", (resp, data, commands) ->
                        cmdResp.setArgs("").setIdCommand(VkCommands.MENU.id()).setInit()
                )
        ));

    }

    @Override
    protected String getHelloMessage(CommandResponse cmd) {
        return "Вы в меню администратора";
    }
}
