package dikanev.nikita.bot.model.callback.commands;

import dikanev.nikita.bot.api.groups.Groups;
import dikanev.nikita.bot.api.objects.UserObject;
import dikanev.nikita.bot.controller.users.UserController;
import dikanev.nikita.bot.model.callback.CommandResponse;
import dikanev.nikita.bot.model.callback.VkCommands;
import dikanev.nikita.bot.model.storage.clients.CoreClientStorage;

public class BrokerMenuCommand extends VkCommand {
    @Override
    public CommandResponse init(CommandResponse cmdResp) throws Exception {
        UserObject user = UserController.getInstance().getUser(CoreClientStorage.getInstance().getToken(), cmdResp.getIdUser());
        switch (Groups.getName(user.getIdGroup())) {
            case "unknown":
                return cmdResp.setIdCommand(VkCommands.UNKNOWN_MENU.id()).setInit();

            case "user":
                return cmdResp.setIdCommand(VkCommands.USER_MENU.id()).setInit();

            case "root":
                return cmdResp.setIdCommand(VkCommands.ROOT_MENU.id()).setInit();

            default:
                    return cmdResp.setIdCommand(VkCommands.USER_MENU.id()).setInit();
        }
    }

    @Override
    public CommandResponse handle(CommandResponse cmdResp) throws Exception {
        return cmdResp.setInit();
    }
}
