package dikanev.nikita.bot.model.callback;

import dikanev.nikita.bot.model.callback.commands.*;
import dikanev.nikita.bot.model.callback.commands.menus.RootMenuCommand;
import dikanev.nikita.bot.model.callback.commands.menus.UnknownMenuCommand;
import dikanev.nikita.bot.model.callback.commands.menus.UserMenuCommand;

public enum VkCommands {
    ENTRY_BOT(new EntryBotCommand()),

    BROKER_MENU(new BrokerMenuCommand()),
    UNKNOWN_MENU(new UnknownMenuCommand()),
    USER_MENU(new UserMenuCommand()),
    ROOT_MENU(new RootMenuCommand()),

    HOME(new HomeCommand()),
    HELP(new HelpCommand()),
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
