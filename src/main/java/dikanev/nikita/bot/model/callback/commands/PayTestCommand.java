package dikanev.nikita.bot.model.callback.commands;

import dikanev.nikita.bot.model.callback.CommandResponse;
import dikanev.nikita.bot.model.callback.VkCommands;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PayTestCommand extends VkCommandHandler {
    @Override
    public CommandResponse init(CommandResponse cmdResp) throws Exception {
        Map<String, String> args = getUrlParameter(cmdResp.getArgs());

        Map<String, CommandData> commands = getCommands(cmdResp);
        final List<List<TK>> buttons = getButtons(commands);
        messageHandle(cmdResp, args, buttons);

        if (!args.containsKey("id_game")) {
            pay(cmdResp);
        } else {
            try {
                int idGame = Integer.valueOf(cmdResp.getText());
                if (idGame < 1 || idGame > 3) {
                    throw new NumberFormatException("Exp");
                }

                args.put("id_game", String.valueOf(idGame));
                args.put("message", "Оплата");
            } catch (NumberFormatException e) {
                args.put("message", getHelloMessage(cmdResp));
            }
            return cmdResp.setArgs(mapToGetString(args)).finish();
        }

        return cmdResp.setArgs(mapToGetString(args)).finish();
    }

    private void pay(CommandResponse cmdResp) {

    }

    @Override
    public CommandResponse handle(CommandResponse cmdResp) throws Exception {
        return null;
    }

    @Override
    protected Map<String, CommandData> getCommands(CommandResponse cmdResp) {
        return new LinkedHashMap<>(Map.of(
                "menu", new CommandData("menu", true, "- Возврат в главное меню", (resp, data, commands) ->
                        cmdResp.setArgs("").setIdCommand(VkCommands.MENU.id()).setInit()
                ),
                "help", new CommandData("help",true, "- Выводит список команд", (resp, data, commands) -> {
                    Map<String, String> args = getUrlParameter(cmdResp.getArgs());
                    args.put("message", helpCommand(commands));
                    return cmdResp.setArgs(mapToGetString(args)).setInit();
                })
        ));

    }

    @Override
    protected String getHelloMessage(CommandResponse cmd) {
        return "Введите номер игры, которую хотите оплатить:\n" +
                "1. Game 1\n" +
                "2. Game 2\n" +
                "3. Game 3";
    }
}
