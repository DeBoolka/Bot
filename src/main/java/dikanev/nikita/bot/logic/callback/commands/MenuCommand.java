package dikanev.nikita.bot.logic.callback.commands;

import dikanev.nikita.bot.api.exceptions.InvalidParametersException;
import dikanev.nikita.bot.api.groups.Group;
import dikanev.nikita.bot.controller.users.UserController;
import dikanev.nikita.bot.logic.callback.CommandResponse;
import dikanev.nikita.bot.logic.callback.VkCommands;
import dikanev.nikita.bot.service.client.parameter.Parameter;
import dikanev.nikita.bot.service.storage.clients.CoreClientStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;

public class MenuCommand extends VkCommandHandler {

    private static final Logger LOG = LoggerFactory.getLogger(MenuCommand.class);

    @Override
    public CommandResponse init(CommandResponse cmdResp, Parameter args) throws Exception {
        Map<String, CommandData> commands = getCommands(cmdResp, cmdResp.getArgs());
        loadAccessCommands(cmdResp, args, commands);

        final List<List<TK>> buttons = getButtons(commands);

        messageHandle(cmdResp, args, buttons);

        return cmdResp.finish();
    }

    @Override
    public CommandResponse handle(CommandResponse cmdResp, Parameter args) throws Exception {
        workerHandel(cmdResp, args);
        if (args.get("menu-block") != null && args.get("menu-block").size() > 0) {
            return cmdResp.finish();
        }

        Map<String, CommandData> commands = getCommands(cmdResp, cmdResp.getArgs());
        loadAccessCommands(cmdResp, args, commands);

        String userMessage = cmdResp.getText();

        for (Map.Entry<String, CommandData> entry : commands.entrySet()) {
            CommandData cmdData = entry.getValue();
            if (userMessage.toLowerCase().indexOf(cmdData.name.toLowerCase()) == 0 && cmdData.isAccess()) {
                return cmdData.getCmdProcess().process(cmdResp, cmdData, commands);
            }
        }

        return cmdResp.setInit();
    }

    //Возврщает map с путем доступа в качестве ключа (bot/vk/...) и в качестве значения CommandData
    @Override
    protected Map<String, CommandData> getCommands(CommandResponse cmdResp, Parameter args) {
        Map<String, CommandData> res = new LinkedHashMap<>();

        res.put("bot/vk/admin/", new CommandData("Админ", " - Меню администратора", true, (resp, data, commands) ->
                cmdResp.setArgs("").setIdCommand(VkCommands.ADMIN_MENU.id()).setInit()
//                            unrealizedOperation(cmdResp)
        ));
        res.put("bot/vk/person", new CommandData("Аккаунт", " - Личный кабинет", true, (resp, data, commands) ->
                cmdResp.setArgs("").setIdCommand(VkCommands.PERSONAL_MENU_OF_USER.id()).setInit()
        ));
        res.put("bot/vk/invite/apply", new CommandData("Пригласительный", " - Ввод пригласительного", true, (resp, data, commands) -> {
            addWorker(args, "apply-invite");
            sendMessage("Введите ваш инвайт код.", cmdResp.getIdUser());
            return resp.finish();
        }));
        res.put("help", new CommandData("help",true, "- Выводит список команд", (resp, data, commands) -> {
            cmdResp.getArgs().set("message", helpCommand(commands));
            return cmdResp.setInit();
        }));

        return res;
    }

    protected void addWorker(Parameter args, String workerName) {
        args.add("worker", workerName);
    }

    protected void addWorker(Parameter args, String workerName, String nameBlock) {
        args.add("worker", workerName);
        if (nameBlock != null) {
            args.add("menu-block", nameBlock);
        }
    }

    private void workerHandel(CommandResponse resp, Parameter param) {
        List<Worker> workers = initWorkers(resp, param);
        List<String> workersParam = param.get("worker");
        if (workersParam == null) {
            return;
        }

        workers.forEach(it -> {
            if (workersParam.contains(it.name)) {
                it.work.accept(resp);
            }
        });
    }

    protected List<Worker> initWorkers(CommandResponse resp, Parameter param) {
        List<Worker> workers = new ArrayList<>();
        workers.add(new Worker("apply-invite", it -> applyInvite(resp, resp.getText())));

        return workers;
    }

    @Override
    protected String getHelloMessage(CommandResponse cmd) {
        return "Вы в главном меню";
    }

    @Override
    protected String helpCommand(Map<String, CommandData> commands) {
        final StringBuilder helpMessage = new StringBuilder("Список команд:\n");
        commands.forEach((key, val) -> {
            if (val.isAccess()) {
                helpMessage.append(val.getName()).append(" ").append(val.getHelpMessage()).append("\n");
            }
        });
        return helpMessage.toString();
    }

    private void applyInvite(CommandResponse resp, String invite) {
        resp.getArgs().remove("worker", "apply-invite");
        if (invite == null) {
            return;
        }

        int userId = resp.getIdUser();
        try {
            try {
                Group group = UserController.applyInvite(CoreClientStorage.getInstance().getToken(), userId, invite);
                sendMessage("Инвайт код успешно применен.\nВы теперь в группе: " + group.name, userId);
            } catch (InvalidParametersException e) {
                sendMessage("Инвайт код не найден.", userId);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                sendMessage("Команда временно недоступна.", userId);
            }
        } catch (Exception e) {
            LOG.error("Send message error: ", e);
        }

    }

    protected class Worker{
        String name;
        Consumer<CommandResponse> work;

        public Worker(String name, Consumer<CommandResponse> work) {
            this.name = name;
            this.work = work;
        }
    }
}
