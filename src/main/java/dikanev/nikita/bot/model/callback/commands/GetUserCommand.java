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
    public CommandResponse init(CommandResponse cmdResp) throws Exception {
        if (cmdResp.getMessage() != null
                && cmdResp.getMessage().getBody().toLowerCase().equals("menu")) {
            sendMessage("Вы переходите в главное меню", cmdResp.getIdUser());
            return cmdResp.setIdCommand(VkCommands.HOME.id()).finish();
        }

        Map<String, String> argsMap = getUrlParametr(cmdResp.getArgs());
        if (!argsMap.containsKey("id")) {
            sendMessage("Введите id пользователя", cmdResp.getIdUser());
            return cmdResp.finish();
        }

        Map<String, Object> data;
        int idCore;
        try {
            data = UserController.getInstance().getData(Integer.valueOf(argsMap.get("id")));
            idCore = (Integer) data.get("id_core");
        } catch (Exception e) {
            LOG.warn("Get data exception ", e);
            sendMessage("Проверьте правильность написания id", cmdResp.getIdUser());
            return cmdResp.finish();
        }

        UserObject user;
        String token;
        try {
            token = UserController.getInstance().getToken(cmdResp.getIdUser());
            user = UserCoreController.getUser(token, idCore);
        } catch (NoAccessException e) {
            sendMessage("У вас нет доступа к этой команде", cmdResp.getIdUser());
            return cmdResp.setIdCommand(VkCommands.HOME.id()).setInit();
        } catch (NotFoundException e) {
            sendMessage("Пользователь не найден", cmdResp.getIdUser());
            return cmdResp.setIdCommand(VkCommands.HOME.id()).setInit();
        }

        sendMessage("Id в vk: " + data.get("id") +
                        "\nId в ядре: " + user.getId() +
                        "\nId группы: " + user.getIdGroup() +
                        "\nИмя: " + user.getName() +
                        "\nФамилия: " + user.getsName() +
                        "\nId текущей команды: " + data.get("id_command") +
                        "\nТекущие аргументы: " + data.get("args") +
                        "\nТокен: " + data.get("token") + "\n"
                , cmdResp.getIdUser());

        return cmdResp.setIdCommand(VkCommands.HOME.id()).setInit();

    }

    @Override
    public CommandResponse handle(CommandResponse cmdResp) throws Exception {
        List<String> args = List.of(cmdResp.getText().split(" "));
        Map<String, String> argsMap = getUrlParametr(cmdResp.getArgs());

        argsMap.put("id", cmdResp.getText().trim());
        cmdResp.setArgs(mapToGetString(argsMap));

        return cmdResp.setInit();
    }
}
