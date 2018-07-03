package dikanev.nikita.bot.model.callback.commands;

import dikanev.nikita.bot.controller.commands.CommandController;
import dikanev.nikita.bot.model.callback.CommandResponse;
import dikanev.nikita.bot.model.callback.VkCommands;

import java.util.ArrayList;
import java.util.List;

public class HomeCommand extends VkCommand {

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

        String[] args = cmdResp.getMessage().getBody().split(" ");
        String command = args[0];

        switch (command) {
            case "help":
                return cmdResp.setIdCommand(VkCommands.HELP.id()).setInit();

            case "callback":
                return cmdResp.setIdCommand(VkCommands.CALLBACK_TEST.id())
                        .setText(cmdResp.getMessage().getBody().substring(command.length()));

            case "create":
                if (args.length > 1 && args[1].equals("user")) {
                    return cmdResp.setIdCommand(VkCommands.CREATE_USER.id())
                            .setText(cmdResp.getMessage().getBody().substring("create user".length())).setHandle();
                }

            case "find":
                if (args.length > 1 && args[1].equals("user")) {
                    return cmdResp.setIdCommand(VkCommands.GET_USER.id())
                            .setText(cmdResp.getMessage().getBody().substring("find user".length())).setHandle();
                }

            default:
                sendMessage("Неизвестная команда\nВведите help, чтобы увидеть список команд", cmdResp.getIdUser());
                return cmdResp.finish();
        }

    }

    public HomeCommand() {
        super();
    }

    @Override
    public List<List<TK>> setDefaultKeyboardButtons() {
        return new ArrayList<>(List.of(
                List.of(VkCommand.TK.getDefault("create user")),
                List.of(VkCommand.TK.getDefault("find user")),
                List.of(VkCommand.TK.getDefault("callback")),
                List.of(VkCommand.TK.getDefault("help"))
        ));
    }
}
