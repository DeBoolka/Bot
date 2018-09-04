package dikanev.nikita.bot.model.callback.commands.menus;

import dikanev.nikita.bot.model.callback.CommandResponse;
import dikanev.nikita.bot.model.callback.VkCommands;
import dikanev.nikita.bot.model.callback.commands.MenuCommand;
import org.apache.commons.collections4.map.LinkedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestMenuCommand extends MenuCommand {

    private static final Logger LOG = LoggerFactory.getLogger(TestMenuCommand.class);

    @Override
    protected Map<String, CommandData> getCommands(CommandResponse cmdResp) {
        return new LinkedMap<>(Map.of(
                "callback", new CommandData("callback", true, "- Проверка работоспособности обработки аргументов", (resp, data, commands) ->
                        cmdResp.setIdCommand(VkCommands.CALLBACK_TEST.id())
                                .setText(cmdResp.getText().substring(data.getName().length())).setHandle()
                ),
                "help", new CommandData("help", true, "- Выводит список команд", (resp, data, commands) -> {
                    Map<String, String> args = getUrlParameter(cmdResp.getArgs());
                    args.put("message", helpCommand(commands));
                    return cmdResp.setArgs(mapToGetString(args)).setInit();
                }),
                "menu", new CommandData("menu", true, "- Возврат в главное меню", (resp, data, commands) ->
                        cmdResp.setArgs("").setIdCommand(VkCommands.MENU.id()).setInit()
                ),
                "back", new CommandData("menu", true, "- Возврат в меню администратора", (resp, data, commands) ->
                        cmdResp.setArgs("").setIdCommand(VkCommands.ADMIN_MENU.id()).setInit()
                )
        ));

    }

    @Override
    protected List<List<TK>> getButtons(Map<String, CommandData> commands) {
        final List<List<TK>> buttons = new ArrayList<>();
        commands.forEach((key, val) -> {
            if (val.isAccess() && !val.getName().equals("menu") && !val.getName().equals("back")) {
                buttons.add(List.of(TK.getDefault(val.getName(), val.getName())));
            }
        });
        buttons.add(List.of(TK.getDefault("back", "back"), TK.getDefault("menu", "menu")));
        return buttons;
    }

    @Override
    protected String getHelloMessage(CommandResponse cmd) {
        return "Вы в тестовом меню";
    }
}
