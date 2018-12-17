package dikanev.nikita.bot.logic.callback.commands;

import dikanev.nikita.bot.api.groups.Groups;
import dikanev.nikita.bot.api.objects.UserObject;
import dikanev.nikita.bot.controller.users.UserController;
import dikanev.nikita.bot.logic.callback.CommandResponse;
import dikanev.nikita.bot.logic.callback.VkCommands;
import dikanev.nikita.bot.service.client.parameter.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//Узел который приветствует юзера, и обрабатывает первичные данные
public class EntryBotCommand extends VkCommand {

    private static final Logger LOG = LoggerFactory.getLogger(EntryBotCommand.class);

    @Override
    public CommandResponse init(CommandResponse cmdResp, Parameter args) throws Exception {
        if (args.isEmpty()) {
            return welcomeMessage(cmdResp, args);
        } else if (args.contains("isInvite")) {
            return isInvite(cmdResp, args);
        } else if (args.contains("invite")) {
            return inviteEquipped(cmdResp, args);
        }

        return cmdResp.setArgs("").setInit();
    }

    @Override
    public CommandResponse handle(CommandResponse cmdResp, Parameter args) throws Exception {
        String message = cmdResp.getText();

        if (args.contains("isInvite")) {
            if (isTrue(message)) {
                args.set("isInvite", "true");
            } else {
                args.set("isInvite", "false");
            }

            return cmdResp.setInit();
        } else if (args.contains("invite")) {
            args.set("invite", cmdResp.getText());
            return cmdResp.setInit();
        }

        return cmdResp.setArgs("").setInit();
    }

    //Первое сообщение.
    private CommandResponse welcomeMessage(CommandResponse cmdResp, Parameter args) throws Exception {
        List<List<TK>> buttons = new ArrayList<>(List.of(
                List.of(TK.getDefault("Да"), TK.getDefault("Нет"))
        ));

        sendMessage("Здаравствуйте.\nУ вас есть пригласительный код?", cmdResp.getIdUser(), true, buttons);
        args.set("isInvite", "null");
        return cmdResp.finish();
    }

    //Обработка вопроса о наличии инвайта
    private CommandResponse isInvite(CommandResponse cmdResp, Parameter args) throws Exception{
        if (args.getFOrDefault("isInvite", "false").equals("false")) {
            sendMessage("Некоторые ваши возможности ограниченны.\n" +
                    "Вы в любое время можете ввести пригласительный в меню.", cmdResp.getIdUser());

            args.clear();
            return cmdResp.setIdCommand(VkCommands.MENU.id()).setInit();
        }

        sendMessage("Введите ваш пригласительный", cmdResp.getIdUser());
        args.set("invite", "null");
        args.remove("isInvite");

        return cmdResp.finish();
    }

    //Обработка инвайта
    private CommandResponse inviteEquipped(CommandResponse cmdResp, Parameter args) throws Exception {
        String invite = args.getFOrDefault("invite", "");

        UserObject user;
        try {
            user = UserController.getInstance().inInvite(cmdResp.getIdUser(), invite);
        } catch (Exception e) {
            List<List<TK>> buttons = new ArrayList<>(List.of(
                    List.of(TK.getDefault("Да"), TK.getDefault("Нет"))
            ));
            sendMessage("Инвайт код не действителен.\nХотите повторить ввод?", cmdResp.getIdUser(), true, buttons);

            args.remove("invite");
            args.set("isInvite", "null");
            return cmdResp.finish();
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
        return cmdResp.setIdCommand(VkCommands.MENU.id()).setInit();
    }
}
