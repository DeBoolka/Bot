package dikanev.nikita.bot.model.callback;

import dikanev.nikita.bot.model.callback.commands.*;
import dikanev.nikita.bot.model.callback.commands.menus.AdminMenuCommand;

public enum VkCommands {
    ENTRY_BOT(new EntryBotCommand()),

    MENU(new MenuCommand()),
    ADMIN_MENU(new AdminMenuCommand()),

    CALLBACK_TEST(new CallbackTestCommand()),
    CREATE_USER(new CreateUserCommand()),
    GET_USER(new GetUserCommand());

    private VkCommand commandClass;

    VkCommands(VkCommand commandClass) {
        this.commandClass = commandClass;
    }

    public VkCommand getCommand() {
        return commandClass;
    }

    public int id(){
        return this.ordinal();
    }

    public static VkCommand getCommand(int idCommand) {
        return VkCommands.values()[idCommand].getCommand();
    }
}
