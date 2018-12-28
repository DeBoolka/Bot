package dikanev.nikita.bot.logic.callback;

import dikanev.nikita.bot.logic.callback.commands.*;
import dikanev.nikita.bot.logic.callback.commands.menu.AdminMenuCommand;
import dikanev.nikita.bot.logic.callback.commands.menu.PersonMenuCommand;

public enum VkCommands {
    ENTRY_BOT(new EntryBotCommand()),

    MENU(new MenuCommand()),
    ADMIN_MENU(new AdminMenuCommand()),
    PERSONAL_MENU_OF_USER(new PersonMenuCommand()),

    CALLBACK_TEST(new CallbackTestCommand());

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
