package dikanev.nikita.bot.model.callback.commands;

import dikanev.nikita.bot.model.callback.CommandResponse;
import dikanev.nikita.bot.model.callback.VkCommands;
import dikanev.nikita.bot.model.storage.DataStorage;
import dikanev.nikita.bot.model.storage.clients.VkClientStorage;

import java.util.Random;

public class HelpCommand extends VkCommand {

    @Override
    public CommandResponse init(CommandResponse commandResponse) throws Exception {
        sendMessage("Справка по командам\n" +
                        "* find user [id]\t-\tинформация о пользователе\n" +
                        "* callback\t-\tпроверка работоспособности обработки аргументов\n" +
                        "* create user -i [id группы] -g [id группы] -n [имя] -s [фамилия]\t-\tсоздание нового пользователя\n" +
                        "* help\t-\tсправка по командам"
                , commandResponse.getIdUser());

        return commandResponse.setIdCommand(VkCommands.HOME.id()).finish();
    }

    @Override
    public CommandResponse handle(CommandResponse commandResponse) throws Exception {
        return commandResponse.setIdCommand(VkCommands.HOME.id()).finish();    }
}
