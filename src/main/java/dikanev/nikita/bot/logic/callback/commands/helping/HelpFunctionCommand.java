package dikanev.nikita.bot.logic.callback.commands.helping;

import dikanev.nikita.bot.logic.callback.CommandResponse;
import dikanev.nikita.bot.logic.callback.VkCommands;
import dikanev.nikita.bot.logic.callback.commands.VkCommand;
import dikanev.nikita.bot.service.client.parameter.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelpFunctionCommand extends VkCommand {

    private static final Logger LOG = LoggerFactory.getLogger(HelpFunctionCommand.class);

    @Override
    public CommandResponse init(CommandResponse cmdResp, Parameter param) throws Exception {
        Parameter args = cmdResp.getArgs();

        int nextCommand;
        String nextCommandEnter;
        String execute;
        try {
            nextCommand = Integer.valueOf(args.getFOrDefault("nextCommand", String.valueOf(VkCommands.MENU.id())));
            nextCommandEnter = args.getFOrDefault("nextCommandEnter", "init");
            execute = args.getFOrDefault("execute", "exit").toLowerCase();
        } catch (NumberFormatException e) {
            LOG.warn("Cast error", e);
            return cmdResp.setIdCommand(VkCommands.MENU.id()).setArgs("").setInit();
        }

        switch (execute) {
            case "askyesorno":
                return askYesOrNo(cmdResp, args);
        }

        args.remove("nextCommand");
        args.remove("nextCommandEnter");
        args.remove("execute");

        if (nextCommandEnter.equals("init")) {
            return cmdResp.setIdCommand(nextCommand).setInit();
        } else {
            return cmdResp.setIdCommand(nextCommand).setHandle();
        }
    }

    @Override
    public CommandResponse handle(CommandResponse cmdResp, Parameter param) throws Exception {
        Parameter args = cmdResp.getArgs();
        String execute = args.getFOrDefault("execute", "exit").toLowerCase();
        String helpVal = args.getFOrDefault("helpVal", "null");

        switch (execute) {
            case "askyesorno":
                args.set("helpVal", isTrue(cmdResp.getText()) ? "true" : "false");
                args.set("execute", "exit");
        }

        return cmdResp.setInit();
    }

    private CommandResponse askYesOrNo(CommandResponse cmdResp, Parameter args) throws Exception {
        String helpVal = args.getFOrDefault("helpVal", "null");

        if (helpVal.equals("null")) {
            String ask = args.getFOrDefault("helpAsk", "null");
            Keyboard buttons = new Keyboard(false).prim("Да").prim("Нет");
            new SendMessage(cmdResp.getIdUser()).message(ask).button(buttons).execute();

            args.remove("helpAsk");
            return cmdResp.finish();
        }

        args.set("execute", "exit");
        return cmdResp.setInit();
    }
}
