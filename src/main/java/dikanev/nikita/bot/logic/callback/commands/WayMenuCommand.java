package dikanev.nikita.bot.logic.callback.commands;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import dikanev.nikita.bot.controller.groups.AccessGroupController;
import dikanev.nikita.bot.logic.callback.CommandResponse;
import dikanev.nikita.bot.service.client.parameter.Parameter;
import dikanev.nikita.bot.service.item.Menu.*;
import dikanev.nikita.bot.service.item.Menu.Menu.*;
import dikanev.nikita.bot.service.storage.clients.CoreClientStorage;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class WayMenuCommand extends VkCommand {

    private static Menu menu;

    static {
        setMenu(new Menu())
                .way(getHelpWay());
    }

    @Override
    public CommandResponse init(CommandResponse resp, Parameter args) throws Exception {
        Map<String, Boolean> accesses = getAccesses(resp);
        sendStartMessage(resp, accesses);
        return resp.finish();
    }

    @Override
    public CommandResponse handle(CommandResponse resp, Parameter args) throws Exception {
        return menu.enter(resp);
    }

    protected static Map<String, Boolean> getAccesses(CommandResponse resp) throws SQLException, dikanev.nikita.bot.api.exceptions.ApiException {
        List<String> requiredAccessVerification = menu.getRequiredAccessVerification();
        return  requiredAccessVerification.isEmpty() ?
                new HashMap<>() :
                AccessGroupController.getAccessUser(CoreClientStorage.getInstance().getToken()
                , resp.getUserId()
                , requiredAccessVerification
        );
    }

    protected static WayData getHelpWay() {
        return new WayData()
                .loadAccess(false)
                .buttonText("help")
                .help("Информация по командам")
                .point(
                        (resp, param, bag) -> {
                            sendHelpMessage(resp);
                            return exitWay(resp);
                        },
                        (resp, param, bag) -> null
                );
    }

    protected static Point exitWay(CommandResponse resp) {
        resp.getState().remove("menu");
        return null;
    }

    protected static Menu setMenu(Menu menu) {
        WayMenuCommand.menu = menu;
        return menu;
    }

    protected static void sendStartMessage(CommandResponse resp, Map<String, Boolean> accesses) throws ClientException, ApiException {
        String startMessage = getStartMessage();
        Keyboard keyboard = getKeyboard(accesses);
        new SendMessage(resp.getUserId()).message(startMessage).button(keyboard).execute();
    }

    protected static Keyboard getKeyboard(Map<String, Boolean> accesses) {
        List<WayData.Button> buttons = menu.getButtons(accesses);
        Keyboard keyboard = new Keyboard();
        int[] countInRow = new int[]{0};
        buttons.forEach(it -> {
            keyboard.addButton(it.stringOfButton, it.typeButton, it.stringOfButton);
            countInRow[0]++;
            if (countInRow[0] == 3) {
                countInRow[0] = 0;
                keyboard.endl();
            }
        });

        return keyboard;
    }

    protected static String getStartMessage() {
        return menu.getStartMessage();
    }

    protected static void sendHelpMessage(CommandResponse resp) throws SQLException, dikanev.nikita.bot.api.exceptions.ApiException, ClientException, ApiException {
        Map<String, Boolean> accesses = getAccesses(resp);
        Keyboard keyboard = getKeyboard(accesses);
        Map<String, String> helpMessages = menu.getHelpMessages(accesses);

        StringBuilder text = new StringBuilder();
        helpMessages.forEach((k, v) -> text.append(k).append(" - ").append(v).append("\n"));

        new SendMessage(resp.getUserId()).message(text.toString()).button(keyboard).execute();
    }

}
