package dikanev.nikita.bot.model.callback.commands;

import com.vk.api.sdk.objects.messages.Message;
import dikanev.nikita.bot.model.callback.CommandResponse;
import dikanev.nikita.bot.model.callback.VkCommands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CallbackTestCommand extends VkCommand {

    @Override
    public CommandResponse init(CommandResponse commandResponse) throws Exception {
        sendMessage("Вы в проверке аргументов\n" +
                        "Ввод аргумантов:\n" +
                        "[key1] [value1] [key2] [value2] ...\n" +
                        "Для выхода в меню введите команду \"Меню\""
                , commandResponse.getIdUser());

        return commandResponse.setHandle();
    }

    @Override
    public CommandResponse handle(CommandResponse commandResponse) throws Exception {
        if (commandResponse.getText().equals("Меню")) {
            return commandResponse.setIdCommand(VkCommands.HOME.id()).setInit();
        }

        Map<String, String> argsMap = getUrlParametr(commandResponse.getArgs());
        List<String> splitMessage = new ArrayList<>(List.of(commandResponse.getText().split(" ")));

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
            sendMessage("Введите имя", commandResponse.getIdUser());
        } else if (argsMap.get("s_name") == null) {
            sendMessage("Введите фамилию", commandResponse.getIdUser());
        } else {
            String args = mapToGetString(argsMap);
            sendMessage("Вот ваши текущие аргументы:\n" + args, commandResponse.getIdUser());
            sendMessage("Вся информация заполнена\nВы переходите в меню", commandResponse.getIdUser());
            return commandResponse.setIdCommand(VkCommands.HOME.id()).finish();
        }

        int splitSize = splitMessage.size();
        for (int i = 0; i < splitSize;) {
            if (i + 1 < splitSize) {
                argsMap.put(splitMessage.get(i), splitMessage.get(i + 1));
            }

            i += 2;
        }

        String args = mapToGetString(argsMap);
        sendMessage("Вот ваши текущие аргументы:\n" + args, commandResponse.getIdUser());

        return commandResponse.setIdCommand(VkCommands.CALLBACK_TEST.id()).setArgs(args).finish();
    }
}
