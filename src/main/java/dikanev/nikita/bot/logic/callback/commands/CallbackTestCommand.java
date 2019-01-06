package dikanev.nikita.bot.logic.callback.commands;

import dikanev.nikita.bot.logic.callback.CommandResponse;
import dikanev.nikita.bot.logic.callback.VkCommands;
import dikanev.nikita.bot.service.client.parameter.Parameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CallbackTestCommand extends VkCommand {

    @Override
    public CommandResponse init(CommandResponse cmdResp, Parameter args) throws Exception {
        new SendMessage(cmdResp.getIdUser()).message("Вы в проверке аргументов\n" +
                "Ввод аргумантов:\n" +
                "[key1] [value1] [key2] [value2] ...\n" +
                "Для выхода в меню введите команду \"Меню\""
        ).execute();

        return cmdResp.setHandle();
    }

    @Override
    public CommandResponse handle(CommandResponse cmdResp, Parameter argsMap) throws Exception {
        if (cmdResp.getText().equals("Меню")) {
            return cmdResp.setIdCommand(VkCommands.MENU.id()).setInit();
        }

        List<String> splitMessage = new ArrayList<>(List.of(cmdResp.getText().split(" ")));

        int findIndex;
        if (argsMap.get("name") == null
                && (findIndex = splitMessage.indexOf("name")) != -1
                && findIndex + 1 < splitMessage.size()
        ) {
            argsMap.set("name", splitMessage.get(findIndex + 1));

            splitMessage.remove(findIndex + 1);
            splitMessage.remove(findIndex);
        } else if (argsMap.get("s_name") == null
                && (findIndex = splitMessage.indexOf("s_name")) != -1
                && findIndex + 1 < splitMessage.size()) {
            argsMap.set("s_name", splitMessage.get(findIndex + 1));

            splitMessage.remove(findIndex + 1);
            splitMessage.remove(findIndex);
        }

        if (argsMap.get("name") == null) {
            new SendMessage(cmdResp.getIdUser()).message("Введите имя").execute();
        } else if (argsMap.get("s_name") == null) {
            new SendMessage(cmdResp.getIdUser()).message("Введите фамилию").execute();
        } else {
            String args = argsMap.getContent();
            new SendMessage(cmdResp.getIdUser()).message("Вот ваши текущие аргументы:\n" + args).execute();
            new SendMessage(cmdResp.getIdUser()).message("Вся информация заполнена\nВы переходите в меню").execute();
            return cmdResp.setIdCommand(VkCommands.MENU.id()).finish();
        }

        int splitSize = splitMessage.size();
        for (int i = 0; i < splitSize;) {
            if (i + 1 < splitSize) {
                argsMap.set(splitMessage.get(i), splitMessage.get(i + 1));
            }

            i += 2;
        }

        String args = argsMap.getContent();
        new SendMessage(cmdResp.getIdUser()).message("Вот ваши текущие аргументы:\n" + args).execute();

        return cmdResp.setIdCommand(VkCommands.CALLBACK_TEST.id()).finish();
    }
}
