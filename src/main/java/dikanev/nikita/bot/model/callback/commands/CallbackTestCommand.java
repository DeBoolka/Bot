package dikanev.nikita.bot.model.callback.commands;

import dikanev.nikita.bot.model.callback.CommandResponse;
import dikanev.nikita.bot.model.callback.VkCommands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CallbackTestCommand extends VkCommand {

    @Override
    public CommandResponse init(CommandResponse cmdResp) throws Exception {
        sendMessage("Вы в проверке аргументов\n" +
                        "Ввод аргумантов:\n" +
                        "[key1] [value1] [key2] [value2] ...\n" +
                        "Для выхода в меню введите команду \"Меню\""
                , cmdResp.getIdUser());

        return cmdResp.setHandle();
    }

    @Override
    public CommandResponse handle(CommandResponse cmdResp) throws Exception {
        if (cmdResp.getText().equals("Меню")) {
            return cmdResp.setIdCommand(VkCommands.HOME.id()).setInit();
        }

        Map<String, String> argsMap = getUrlParameter(cmdResp.getArgs());
        List<String> splitMessage = new ArrayList<>(List.of(cmdResp.getText().split(" ")));

        int findIndex;
        if (argsMap.get("name") == null
                && (findIndex = splitMessage.indexOf("name")) != -1
                && findIndex + 1 < splitMessage.size()) {
            argsMap.put("name", splitMessage.get(findIndex + 1));

            splitMessage.remove(findIndex + 1);
            splitMessage.remove(findIndex);
        } else if (argsMap.get("s_name") == null
                && (findIndex = splitMessage.indexOf("s_name")) != -1
                && findIndex + 1 < splitMessage.size()) {
            argsMap.put("s_name", splitMessage.get(findIndex + 1));

            splitMessage.remove(findIndex + 1);
            splitMessage.remove(findIndex);
        }

        if (argsMap.get("name") == null) {
            sendMessage("Введите имя", cmdResp.getIdUser());
        } else if (argsMap.get("s_name") == null) {
            sendMessage("Введите фамилию", cmdResp.getIdUser());
        } else {
            String args = mapToGetString(argsMap);
            sendMessage("Вот ваши текущие аргументы:\n" + args, cmdResp.getIdUser());
            sendMessage("Вся информация заполнена\nВы переходите в меню", cmdResp.getIdUser());
            return cmdResp.setIdCommand(VkCommands.HOME.id()).finish();
        }

        int splitSize = splitMessage.size();
        for (int i = 0; i < splitSize;) {
            if (i + 1 < splitSize) {
                argsMap.put(splitMessage.get(i), splitMessage.get(i + 1));
            }

            i += 2;
        }

        String args = mapToGetString(argsMap);
        sendMessage("Вот ваши текущие аргументы:\n" + args, cmdResp.getIdUser());

        return cmdResp.setIdCommand(VkCommands.CALLBACK_TEST.id()).setArgs(args).finish();
    }
}
