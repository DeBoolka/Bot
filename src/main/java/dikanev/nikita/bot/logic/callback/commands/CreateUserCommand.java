package dikanev.nikita.bot.logic.callback.commands;

import dikanev.nikita.bot.api.exceptions.ApiException;
import dikanev.nikita.bot.api.exceptions.NoAccessException;
import dikanev.nikita.bot.api.objects.UserObject;
import dikanev.nikita.bot.controller.users.UserController;
import dikanev.nikita.bot.logic.callback.CommandResponse;
import dikanev.nikita.bot.logic.callback.VkCommands;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class CreateUserCommand extends VkCommand {

    @Override
    public CommandResponse init(CommandResponse cmdResp) throws Exception {
        if (cmdResp.getMessage() != null
                && cmdResp.getMessage().getBody().toLowerCase().equals("menu")) {
            sendMessage("Вы переходите в главное меню", cmdResp.getIdUser());
            return cmdResp.setIdCommand(VkCommands.MENU.id()).finish();
        }

        Map<String, String> argsMap = getUrlParameter(cmdResp.getArgs());
        StringBuilder resp = new StringBuilder();
        int indexArg = 0;

        if(!argsMap.containsKey("id_user")){
            resp.append(++indexArg).append(". Id вк\n");
        }
        if(!argsMap.containsKey("id_group")){
            resp.append(++indexArg).append(". Id группы\n");
        }
        if(!argsMap.containsKey("name")){
            resp.append(++indexArg).append(". Имя человека\n");
        }
        if(!argsMap.containsKey("s_name")){
            resp.append(++indexArg).append(". Фамилию человека\n");
        }

        boolean createUserDefGroup = false;
        if (indexArg == 1 && !argsMap.containsKey("id_group")) {
            if (!argsMap.containsKey("default_group")) {
                sendMessage("Хотите создать пользователя с группой по умолчанию?", cmdResp.getIdUser());
                argsMap.put("default_group", "true");
                return cmdResp.setArgs(mapToGetString(argsMap)).finish();
            } else if (!argsMap.get("default_group").equals("true")) {
                sendMessage("Введите id группы", cmdResp.getIdUser());
                return cmdResp.finish();
            }
            createUserDefGroup = true;
        } else if (indexArg > 0) {
            sendMessage("Введите:\n" + resp.toString() +
                            "\nДля выхода в меню воспользуйтесь командой \"menu\"\n"
                    , cmdResp.getIdUser());
            return cmdResp.finish();
        }

        try {
            UserObject user;
            if (createUserDefGroup) {
                user = UserController.getInstance().createUser(
                        UserController.getInstance().getToken(cmdResp.getIdUser())
                        , Integer.valueOf(argsMap.get("id_user")), argsMap.get("name"), argsMap.get("s_name")
                );
            } else {
                user = UserController.getInstance().createUser(
                        UserController.getInstance().getToken(cmdResp.getIdUser()), Integer.valueOf(argsMap.get("id_user")),
                        Integer.valueOf(argsMap.get("id_group")), argsMap.get("name"), argsMap.get("s_name")
                );
            }
            sendMessage("Пользователь создан:" +
                            "\nId вк: " + argsMap.get("id_user") +
                            "\nId в ядре: " + user.getId() +
                            "\nId группы: " + user.getIdGroup() +
                            "\nИмя: " + user.getName() +
                            "\nФамилия: " + user.getsName()
                    , cmdResp.getIdUser()
            );
        } catch (NoAccessException e) {
            sendMessage("У вас нет доступа к данной комнде!" + resp.toString(), cmdResp.getIdUser());
        } catch (SQLException | NumberFormatException | ApiException e) {
            sendMessage("Ошибка при создании пользователя.\n" +
                    "Проверьте правильность введенных данных и попробуйте еще раз.", cmdResp.getIdUser());
            return cmdResp.finish();
        }

        return cmdResp.setIdCommand(VkCommands.MENU.id()).setInit();
    }

    @Override
    public CommandResponse handle(CommandResponse cmdResp) throws Exception {
        List<String> args = List.of(cmdResp.getText().split(" "));
        Map<String, String> argsMap = getUrlParameter(cmdResp.getArgs());

        if (argsMap.containsKey("default_group")) {
            String defGroup = argsMap.get("default_group");
            if (defGroup.equals("true") && !isTrue(cmdResp.getText())) {
                argsMap.put("default_group", "false");
                return cmdResp.setArgs(mapToGetString(argsMap)).setInit();
            } else if (defGroup.equals("true")) {
                return cmdResp.setInit();
            }

            argsMap.put("id_group", cmdResp.getText());
            return cmdResp.setArgs(mapToGetString(argsMap)).setInit();
        }

        int findIndex = -1;

        if(!argsMap.containsKey("id_user")){
            findIndex = args.indexOf("-i");
            if (findIndex >= 0 && args.size() > findIndex + 1) {
                argsMap.put("id_user", args.get(findIndex + 1));
            }
        }
        if(!argsMap.containsKey("id_group")){
            findIndex = args.indexOf("-g");
            if (findIndex >= 0 && args.size() > findIndex + 1) {
                argsMap.put("id_group", args.get(findIndex + 1));
            }
        }
        if(!argsMap.containsKey("name")){
            findIndex = args.indexOf("-n");
            if (findIndex >= 0 && args.size() > findIndex + 1) {
                argsMap.put("name", args.get(findIndex + 1));
            }
        }
        if(!argsMap.containsKey("s_name")){
            findIndex = args.indexOf("-s");
            if (findIndex >= 0 && args.size() > findIndex + 1) {
                argsMap.put("s_name", args.get(findIndex + 1));
            }
        }

        cmdResp.setArgs(mapToGetString(argsMap));
        return cmdResp.setIdCommand(VkCommands.CREATE_USER.id()).setInit();
    }
}
