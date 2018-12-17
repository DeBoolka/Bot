package dikanev.nikita.bot.logic.callback.commands;

import dikanev.nikita.bot.api.groups.Groups;
import dikanev.nikita.bot.api.objects.UserObject;
import dikanev.nikita.bot.controller.users.UserController;
import dikanev.nikita.bot.logic.callback.CommandResponse;
import dikanev.nikita.bot.logic.callback.VkCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//Узел который приветствует юзера, и обрабатывает первичные данные
public class EntryBotCommand extends VkCommand {

    private static final Logger LOG = LoggerFactory.getLogger(EntryBotCommand.class);

    @Override
    public CommandResponse init(CommandResponse cmdResp) throws Exception {
        Map<String, String> args = getUrlParameter(cmdResp.getArgs());
        if (args.size() == 0) {
            return welcomeMessage(cmdResp, args);
        } else if (args.containsKey("isInvite")) {
            return isInvite(cmdResp, args);
        } else if (args.containsKey("invite")) {
            return inviteEquipped(cmdResp, args);
        }

        return cmdResp.setArgs("").setInit();
    }

    @Override
    public CommandResponse handle(CommandResponse cmdResp) throws Exception {
        Map<String, String> args = getUrlParameter(cmdResp.getArgs());
        String message = cmdResp.getText();

        if (args.containsKey("isInvite")) {
            if (isTrue(message)) {
                args.put("isInvite", "true");
            } else {
                args.put("isInvite", "false");
            }

            return cmdResp.setArgs(mapToGetString(args)).setInit();
        } else if (args.containsKey("invite")) {
            args.put("invite", cmdResp.getText());
            return cmdResp.setArgs(mapToGetString(args)).setInit();
        }

        return cmdResp.setArgs("").setInit();
    }

    //Первое сообщение.
    private CommandResponse welcomeMessage(CommandResponse cmdResp, Map<String, String> args) throws Exception {
        List<List<TK>> buttons = new ArrayList<>(List.of(
                List.of(TK.getDefault("Да"), TK.getDefault("Нет"))
        ));

        sendMessage("Здаравствуйте.\nУ вас есть пригласительный код?", cmdResp.getIdUser(), true, buttons);
        args.put("isInvite", "null");
        return cmdResp.setArgs(mapToGetString(args)).finish();
    }

    //Обработка вопроса о наличии инвайта
    private CommandResponse isInvite(CommandResponse cmdResp, Map<String, String> args) throws Exception{
        if (args.getOrDefault("isInvite", "false").equals("false")) {
            sendMessage("Некоторые ваши возможности ограниченны.\n" +
                    "Вы в любое время можете ввести пригласительный в меню.", cmdResp.getIdUser());

            args.clear();
            return cmdResp.setIdCommand(VkCommands.MENU.id()).setArgs(mapToGetString(args)).setInit();
        }

        sendMessage("Введите ваш пригласительный", cmdResp.getIdUser());
        args.put("invite", "null");
        args.remove("isInvite");

        return cmdResp.setArgs(mapToGetString(args)).finish();
    }

    //Обработка инвайта
    private CommandResponse inviteEquipped(CommandResponse cmdResp, Map<String, String> args) throws Exception {
        String invite = args.getOrDefault("invite", "");

        UserObject user;
        try {
            user = UserController.getInstance().inInvite(cmdResp.getIdUser(), invite);
        } catch (Exception e) {
            List<List<TK>> buttons = new ArrayList<>(List.of(
                    List.of(TK.getDefault("Да"), TK.getDefault("Нет"))
            ));
            sendMessage("Инвайт код не действителен.\nХотите повторить ввод?", cmdResp.getIdUser(), true, buttons);

            args.remove("invite");
            args.put("isInvite", "null");
            return cmdResp.setArgs(mapToGetString(args)).finish();
        }

        String groupName = null;
        Groups[] groups = Groups.values();
        for (Groups group : groups) {
            if (group.getId() == user.getIdGroup()) {
                groupName = group.getName();
            }
        }
        if (groupName == null) {
            LOG.warn("Not found Group id: " + user.getIdGroup());
            groupName = "null";
        }

        sendMessage("Вам присвоена группа " + groupName, cmdResp.getIdUser());
        args.clear();
        return cmdResp.setArgs(mapToGetString(args)).setIdCommand(VkCommands.MENU.id()).setInit();
    }
}
