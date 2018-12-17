package dikanev.nikita.bot.logic.callback.commands.helping;

import dikanev.nikita.bot.logic.callback.CommandResponse;
import dikanev.nikita.bot.logic.callback.VkCommands;
import dikanev.nikita.bot.logic.callback.commands.VkCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HelpFunctionCommand extends VkCommand {

    private static final Logger LOG = LoggerFactory.getLogger(HelpFunctionCommand.class);

    @Override
    public CommandResponse init(CommandResponse cmdResp) throws Exception {
        Map<String, String> args = getUrlParameter(cmdResp.getArgs());

        int nextCommand;
        String nextCommandEnter;
        String execute;
        try {
            nextCommand = Integer.valueOf(args.getOrDefault("nextCommand", String.valueOf(VkCommands.MENU.id())));
            nextCommandEnter = args.getOrDefault("nextCommandEnter", "init");
            execute = args.getOrDefault("execute", "exit").toLowerCase();
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
            return cmdResp.setArgs(mapToGetString(args)).setIdCommand(nextCommand).setInit();
        } else {
            return cmdResp.setArgs(mapToGetString(args)).setIdCommand(nextCommand).setHandle();
        }
    }

    @Override
    public CommandResponse handle(CommandResponse cmdResp) throws Exception {
        Map<String, String> args = getUrlParameter(cmdResp.getArgs());
        String execute = args.getOrDefault("execute", "exit").toLowerCase();
        String helpVal = args.getOrDefault("helpVal", "null");

        switch (execute) {
            case "askyesorno":
                args.put("helpVal", isTrue(cmdResp.getText()) ? "true" : "false");
                args.put("execute", "exit");
        }

        return cmdResp.setArgs(mapToGetString(args)).setInit();
    }

    private CommandResponse askYesOrNo(CommandResponse cmdResp, Map<String, String> args) throws Exception {
        String helpVal = args.getOrDefault("helpVal", "null");

        if (helpVal.equals("null")) {
            String ask = args.getOrDefault("helpAsk", "null");
            List<List<TK>> buttons = new ArrayList<>(List.of(
                    List.of(TK.getDefault("Да"), TK.getDefault("Нет"))
            ));

            sendMessage(ask, cmdResp.getIdUser(), buttons);
            args.remove("helpAsk");
            return cmdResp.setArgs(mapToGetString(args)).finish();
        }

        args.put("execute", "exit");
        return cmdResp.setArgs(mapToGetString(args)).setInit();
    }
}
