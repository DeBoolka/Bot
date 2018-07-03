package dikanev.nikita.bot.model.callback.commands.menus;

import dikanev.nikita.bot.model.callback.CommandResponse;
import dikanev.nikita.bot.model.callback.VkCommands;
import dikanev.nikita.bot.model.callback.commands.VkCommand;

import java.util.ArrayList;
import java.util.List;

public class UnknownMenuCommand extends VkCommand {

    @Override
    public CommandResponse init(CommandResponse cmdResp) throws Exception {
        sendMessage("Вы в главном меню", cmdResp.getIdUser(), true);

        return cmdResp.setArgs("").finish();
    }

    @Override
    public CommandResponse handle(CommandResponse cmdResp) throws Exception {
        if (!cmdResp.getArgs().equals("")) {
            cmdResp.setArgs("");
        }

        String[] args = cmdResp.getMessage().getBody().toLowerCase().split(" ");
        String command = args[0];

        switch (command) {
            case "help":
                return helpCommand(cmdResp);

            case "Пригласительный":
                //todo: Сделать ссылку на ввод пригласительного
                return unrealizedOperation(cmdResp);

            default:
                sendMessage("Неизвестная команда\nВведите help, чтобы увидеть список команд", cmdResp.getIdUser());
                return cmdResp.finish();
        }

    }

    private CommandResponse helpCommand(CommandResponse cmdResp) throws Exception {
        sendMessage("Список доступных команд:\n\n" +
                        "*help - Получить список команд\n" +
                        "*Пригласительный - Ввод пригласительного кода",
                cmdResp.getIdUser(), true);

        return cmdResp.finish();
    }

    public UnknownMenuCommand() {
        super();
    }

    @Override
    public List<List<TK>> setDefaultKeyboardButtons() {
        return new ArrayList<>(List.of(
                List.of(TK.getDefault("Ввод пригласительного")),
                List.of(TK.getDefault("help"))
        ));
    }

    private CommandResponse unrealizedOperation(CommandResponse cmdResp) throws Exception {
        sendMessage("Извините команда временно недоступна", cmdResp.getIdUser(), true);
        return cmdResp.finish();
    }
}
