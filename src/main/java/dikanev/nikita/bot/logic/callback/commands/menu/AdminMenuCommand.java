package dikanev.nikita.bot.logic.callback.commands.menu;

import dikanev.nikita.bot.api.groups.Group;
import dikanev.nikita.bot.controller.users.UserController;
import dikanev.nikita.bot.logic.callback.CommandResponse;
import dikanev.nikita.bot.logic.callback.VkCommands;
import dikanev.nikita.bot.logic.callback.commands.MenuCommand;
import dikanev.nikita.bot.service.client.parameter.Parameter;
import dikanev.nikita.bot.service.storage.clients.CoreClientStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AdminMenuCommand extends MenuCommand {

    private static final Logger LOG = LoggerFactory.getLogger(AdminMenuCommand.class);

    @Override
    protected Map<String, CommandData> getCommands(CommandResponse cmdResp, Parameter args) {
        Map<String, CommandData> res = new LinkedHashMap<>();

        res.put("callback", new CommandData("callback", true, "- Callback menu", (resp, data, commands) ->
                cmdResp.setArgs("").setIdCommand(VkCommands.CALLBACK_TEST.id()).setInit()
        ));
        res.put("bot/vk/invite/create", new CommandData("create invite", "- Создает инвайт код", true, (resp, data, commands) -> {
            addWorker(args, "invite-create", "invite-create");
            new SendMessage(cmdResp.getIdUser()).message("Введите id группы.").execute();
            return cmdResp.finish();
        }));
        res.put("bot/vk/group/update", new CommandData("change group", "- Изменяет группу пользователя", true, (resp, data, commands) -> {
            addWorker(args, "change-group", "change-group");
            new SendMessage(cmdResp.getIdUser()).message("Введите id пользователя.").execute();
            return cmdResp.finish();
        }));
        res.put("help", new CommandData("help", true, "- Выводит список команд", Keyboard.DEFAULT, (resp, data, commands) -> {
            args.set("message", helpCommand(commands));
            return cmdResp.setInit();
        }));
        res.put("menu", new CommandData("menu", true, "- Возврат в главное меню", Keyboard.DEFAULT, (resp, data, commands) ->
                cmdResp.setArgs("").setIdCommand(VkCommands.MENU.id()).setInit()
        ));

        return res;
    }

    @Override
    protected List<Worker> initWorkers(CommandResponse resp, Parameter param) {
        return new ArrayList<>(List.of(
                new Worker("change-group", it -> changeGroup(resp, resp.getText())),
                new Worker("invite-create", it -> createInvite(resp, resp.getText()))
        ));
    }

    private void createInvite(CommandResponse resp, String text) {
        Parameter param = resp.getArgs();
        if (text == null) {
            return;
        }

        try {
            try {
                String invite = UserController.createInvite(CoreClientStorage.getInstance().getToken()
                        , resp.getIdUser()
                        , Integer.valueOf(text));
                if (invite != null && !invite.isEmpty()) {
                    new SendMessage(resp.getIdUser()).message("Инвайт создан: " + invite).execute();
                    param.remove("menu-block", "invite-create");
                    param.remove("worker", "invite-create");
                    return;
                }
                new SendMessage(resp.getIdUser()).message("Не удалось создать инвайт код.").execute();
            } catch (NumberFormatException e) {
                new SendMessage(resp.getIdUser()).message("Не корректный id.").execute();
            }
        } catch (Exception e) {
            LOG.error("Failed create invite code: ", e);
        }
        param.remove("menu-block", "invite-create");
        param.remove("worker", "invite-create");
    }

    private void changeGroup(CommandResponse resp, String text) {
        Parameter param = resp.getArgs();
        if (text == null) {
            return;
        }

        try {
            try {
                if (!param.contains("change-userId")) {
                    param.set("change-userId", String.valueOf(Integer.valueOf(text)));
                    new SendMessage(resp.getIdUser()).message("Введите id группы которую хотите ему присвоить.").execute();
                    return;
                }

                Group group = UserController.setGroup(CoreClientStorage.getInstance().getToken()
                        , param.getIntF("change-userId")
                        , Integer.valueOf(text));
                if (group != null) {
                    new SendMessage(resp.getIdUser()).message("Пользователю присвоена группа: " + group.name).execute();
                    param.remove("menu-block", "change-group");
                    param.remove("worker", "change-group");
                    param.remove("change-userId");
                    return;
                }
                new SendMessage(resp.getIdUser()).message("Не удалось создать инвайт код.").execute();
            } catch (NumberFormatException e) {
                new SendMessage(resp.getIdUser()).message("Не корректный id.").execute();
            }
        } catch (Exception e) {
            LOG.error("Failed change group: ", e);
        }
        param.remove("menu-block", "change-group");
        param.remove("worker", "change-group");
        param.remove("change-userId");
    }

    @Override
    protected String getHelloMessage(CommandResponse cmd) {
        return "Вы в меню администратора";
    }
}
