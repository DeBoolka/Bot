package dikanev.nikita.bot.logic.callback;

import dikanev.nikita.bot.logic.callback.commands.*;
import dikanev.nikita.bot.logic.callback.commands.menus.AdminMenuCommand;
import dikanev.nikita.bot.logic.callback.commands.menus.TestMenuCommand;

public enum VkCommands {
    ENTRY_BOT(new EntryBotCommand()),

    MENU(new MenuCommand()),
    ADMIN_MENU(new AdminMenuCommand()),
    TEST_MENU(new TestMenuCommand()),

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
