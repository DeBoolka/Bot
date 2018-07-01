package dikanev.nikita.bot.model.callback.commands;

import dikanev.nikita.bot.controller.commands.CommandController;
import dikanev.nikita.bot.model.callback.CommandResponse;
import dikanev.nikita.bot.model.callback.VkCommands;

import java.util.List;

public class HomeCommand extends VkCommand {

    @Override
    public CommandResponse init(CommandResponse cmdResponse) throws Exception {
        sendMessage("Вы в главном меню", cmdResponse.getIdUser(), true);

        return cmdResponse.setArgs("").finish();
    }

    @Override
    public CommandResponse handle(CommandResponse cmdResponse) throws Exception {
        if (!cmdResponse.getArgs().equals("")) {
            cmdResponse.setArgs("");
        }

        String[] args = cmdResponse.getMessage().getBody().split(" ");
        String command = args[0];

        switch (command) {
            case "help":
                return cmdResponse.setIdCommand(VkCommands.HELP.id()).setInit();

            case "callback":
                return cmdResponse.setIdCommand(VkCommands.CALLBACK_TEST.id())
                        .setText(cmdResponse.getMessage().getBody().substring(command.length()));

            case "create":
                if (args.length > 1 && args[1].equals("user")) {
                    return cmdResponse.setIdCommand(VkCommands.CREATE_USER.id())
                            .setText(cmdResponse.getMessage().getBody().substring("create user".length())).setHandle();
                }

            case "find":
                if (args.length > 1 && args[1].equals("user")) {
                    return cmdResponse.setIdCommand(VkCommands.GET_USER.id())
                            .setText(cmdResponse.getMessage().getBody().substring("find user".length())).setHandle();
                }

            default:
                sendMessage("Неизвестная команда\nВведите help, чтобы увидеть список команд", cmdResponse.getIdUser());
                return cmdResponse.finish();
        }

    }

    public HomeCommand() {
        super();
    }

    @Override
    public List<TK>[] setDefaultKeyboardButtons() {
        return new List[]{
                List.of(VkCommand.TK.getDefault("create user")),
                List.of(VkCommand.TK.getDefault("find user")),
                List.of(VkCommand.TK.getDefault("callback")),
                List.of(VkCommand.TK.getDefault("help"))
        };
    }
}
