package dikanev.nikita.bot.logic.callback.commands.menus;

import dikanev.nikita.bot.logic.callback.CommandResponse;
import dikanev.nikita.bot.logic.callback.VkCommands;
import dikanev.nikita.bot.logic.callback.commands.MenuCommand;
import org.apache.commons.collections4.map.LinkedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class AdminMenuCommand extends MenuCommand {

    private static final Logger LOG = LoggerFactory.getLogger(AdminMenuCommand.class);

    @Override
    protected Map<String, CommandData> getCommands(CommandResponse cmdResp) {
        return new LinkedMap<>(Map.of(
                "test", new CommandData("test", true, "- Тестовое меню", (resp, data, commands) ->
                        cmdResp.setIdCommand(VkCommands.TEST_MENU.id()).setInit()
                ),
                "bot/vk/user/create", new CommandData("create user", "-i [id группы] -g [id группы] -n [имя] -s [фамилия] - Создает нового пользователя", true, (resp, data, commands) ->
                        cmdResp.setIdCommand(VkCommands.CREATE_USER.id())
                                .setText(cmdResp.getText().substring(data.getName().length())).setHandle()
                ),
                "bot/vk/user/get", new CommandData("find user", "[id] - Возвращает информацию о пользователе", true, (resp, data, commands) ->
                        cmdResp.setIdCommand(VkCommands.GET_USER.id())
                                .setText(cmdResp.getText().substring(data.getName().length())).setHandle()
                ),
                "help", new CommandData("help", true, "- Выводит список команд", (resp, data, commands) -> {
                    Map<String, String> args = getUrlParameter(cmdResp.getArgs());
                    args.put("message", helpCommand(commands));
                    return cmdResp.setArgs(mapToGetString(args)).setInit();
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
