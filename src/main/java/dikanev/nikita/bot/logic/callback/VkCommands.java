package dikanev.nikita.bot.logic.callback;

import dikanev.nikita.bot.logic.callback.commands.*;
import dikanev.nikita.bot.logic.callback.commands.menu.*;

public enum VkCommands {
    LOGIN_USER(new LoginBotCommand()),

    MENU(new MenuCommand()),
    ADMIN_MENU(new AdminMenuCommand()),
    GAME_MENU(new GameMenuCommand()),

    PERSONAL_MENU_OF_USER(new PersonMenuCommand()),
    PHOTO_OF_USER(new PhotoMenuCommand()),
    AMMUNITION_OF_USER(new AmmunitionMenuCommand()),

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
