package dikanev.nikita.bot.model.callback.commands;

import dikanev.nikita.bot.api.exceptions.ApiException;
import dikanev.nikita.bot.api.exceptions.NoAccessException;
import dikanev.nikita.bot.api.exceptions.NotFoundException;
import dikanev.nikita.bot.api.objects.UserObject;
import dikanev.nikita.bot.controller.users.UserController;
import dikanev.nikita.bot.controller.users.UserCoreController;
import dikanev.nikita.bot.model.callback.CommandResponse;
import dikanev.nikita.bot.model.callback.VkCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class GetUserCommand extends VkCommand {

    private static final Logger LOG = LoggerFactory.getLogger(GetUserCommand.class);

    @Override
    public CommandResponse init(CommandResponse cmdResponse) throws Exception {
        if (cmdResponse.getMessage() != null
                && cmdResponse.getMessage().getBody().toLowerCase().equals("menu")) {
            sendMessage("Вы переходите в главное меню", cmdResponse.getIdUser());
            return cmdResponse.setIdCommand(VkCommands.HOME.id()).finish();
        }

        Map<String, String> argsMap = getUrlParametr(cmdResponse.getArgs());
        if (!argsMap.containsKey("id")) {
            sendMessage("Введите id пользователя", cmdResponse.getIdUser());
            return cmdResponse.finish();
        }

        Map<String, Object> data;
        int idCore;
        try {
            data = UserController.getInstance().getData(Integer.valueOf(argsMap.get("id")));
            idCore = (Integer) data.get("id_core");
        } catch (Exception e) {
            LOG.warn("Get data exception ", e);
            sendMessage("Проверьте правильность написания id", cmdResponse.getIdUser());
            return cmdResponse.finish();
        }

        UserObject user;
        String token;
        try {
            token = UserController.getInstance().getToken(cmdResponse.getIdUser());
            user = UserCoreController.getUser(token, idCore);
        } catch (NoAccessException e) {
            sendMessage("У вас нет доступа к этой команде", cmdResponse.getIdUser());
            return cmdResponse.setIdCommand(VkCommands.HOME.id()).setInit();
        } catch (NotFoundException e) {
            sendMessage("Пользователь не найден", cmdResponse.getIdUser());
            return cmdResponse.setIdCommand(VkCommands.HOME.id()).setInit();
        }

        sendMessage("Id в vk: " + data.get("id") +
                        "\nId в ядре: " + user.getId() +
                        "\nId группы: " + user.getIdGroup() +
                        "\nИмя: " + user.getName() +
                        "\nФамилия: " + user.getsName() +
                        "\nId текущей команды: " + data.get("id_command") +
                        "\nТекущие аргументы: " + data.get("args") +
                        "\nТокен: " + data.get("token") + "\n"
                , cmdResponse.getIdUser());

        return cmdResponse.setIdCommand(VkCommands.HOME.id()).setInit();

    }

    @Override
    public CommandResponse handle(CommandResponse commandResponse) throws Exception {
        List<String> args = List.of(commandResponse.getText().split(" "));
        Map<String, String> argsMap = getUrlParametr(commandResponse.getArgs());

        argsMap.put("id", commandResponse.getText().trim());
        commandResponse.setArgs(mapToGetString(argsMap));

        return commandResponse.setInit();
    }
}
