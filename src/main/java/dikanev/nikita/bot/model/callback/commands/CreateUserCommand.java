package dikanev.nikita.bot.model.callback.commands;

import dikanev.nikita.bot.api.exceptions.ApiException;
import dikanev.nikita.bot.api.exceptions.NoAccessException;
import dikanev.nikita.bot.api.objects.UserObject;
import dikanev.nikita.bot.controller.users.UserController;
import dikanev.nikita.bot.model.callback.CommandResponse;
import dikanev.nikita.bot.model.callback.VkCommands;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class CreateUserCommand extends VkCommand {

    @Override
    public CommandResponse init(CommandResponse cmdResponse) throws Exception {
        if (cmdResponse.getMessage() != null
                && cmdResponse.getMessage().getBody().toLowerCase().equals("menu")) {
            sendMessage("Вы переходите в главное меню", cmdResponse.getIdUser());
            return cmdResponse.setIdCommand(VkCommands.HOME.id()).finish();
        }

        Map<String, String> argsMap = getUrlParametr(cmdResponse.getArgs());
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
                sendMessage("Хотите создать пользователя с группой по умолчанию?", cmdResponse.getIdUser());
                argsMap.put("default_group", "true");
                return cmdResponse.setArgs(mapToGetString(argsMap)).finish();
            } else if (!argsMap.get("default_group").equals("true")) {
                sendMessage("Введите id группы", cmdResponse.getIdUser());
                return cmdResponse.finish();
            }
            createUserDefGroup = true;
        } else if (indexArg > 0) {
            sendMessage("Введите:\n" + resp.toString() +
                            "\nДля выхода в меню воспользуйтесь командой \"menu\"\n"
                    , cmdResponse.getIdUser());
            return cmdResponse.finish();
        }

        try {
            UserObject user;
            if (createUserDefGroup) {
                user = UserController.getInstance().createUser(
                        UserController.getInstance().getToken(cmdResponse.getIdUser())
                        , Integer.valueOf(argsMap.get("id_user")), argsMap.get("name"), argsMap.get("s_name")
                );
            } else {
                user = UserController.getInstance().createUser(
                        UserController.getInstance().getToken(cmdResponse.getIdUser()), Integer.valueOf(argsMap.get("id_user")),
                        Integer.valueOf(argsMap.get("id_group")), argsMap.get("name"), argsMap.get("s_name")
                );
            }
            sendMessage("Пользователь создан:" +
                            "\nId вк: " + argsMap.get("id_user") +
                            "\nId в ядре: " + user.getId() +
                            "\nId группы: " + user.getIdGroup() +
                            "\nИмя: " + user.getName() +
                            "\nФамилия: " + user.getsName()
                    , cmdResponse.getIdUser()
            );
        } catch (NoAccessException e) {
            sendMessage("У вас нет доступа к данной комнде!" + resp.toString(), cmdResponse.getIdUser());
        } catch (SQLException | NumberFormatException | ApiException e) {
            sendMessage("Ошибка при создании пользователя.\n" +
                    "Проверьте правильность введенных данных и попробуйте еще раз.", cmdResponse.getIdUser());
            return cmdResponse.finish();
        }

        return cmdResponse.setIdCommand(VkCommands.HOME.id()).setInit();
    }

    @Override
    public CommandResponse handle(CommandResponse commandResponse) throws Exception {
        List<String> args = List.of(commandResponse.getText().split(" "));
        Map<String, String> argsMap = getUrlParametr(commandResponse.getArgs());

        if (argsMap.containsKey("default_group")) {
            String defGroup = argsMap.get("default_group");
            if (defGroup.equals("true") && !isTrue(commandResponse.getText())) {
                argsMap.put("default_group", "false");
                return commandResponse.setArgs(mapToGetString(argsMap)).setInit();
            } else if (defGroup.equals("true")) {
                return commandResponse.setInit();
            }

            argsMap.put("id_group", commandResponse.getText());
            return commandResponse.setArgs(mapToGetString(argsMap)).setInit();
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

        commandResponse.setArgs(mapToGetString(argsMap));
        return commandResponse.setIdCommand(VkCommands.CREATE_USER.id()).setInit();
    }
}
