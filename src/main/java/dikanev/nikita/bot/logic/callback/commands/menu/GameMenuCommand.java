package dikanev.nikita.bot.logic.callback.commands.menu;

import dikanev.nikita.bot.logic.callback.VkCommands;
import dikanev.nikita.bot.logic.callback.commands.WayMenuCommand;
import dikanev.nikita.bot.service.item.Menu.Menu;

public class GameMenuCommand extends WayMenuCommand {
    static {
        setMenu(new Menu())
                .way(getGamesWay())
                .way(getSignedUpForTheGameWay())
                .way(getSignUpForTheGameWay())
                .way(getHelpWay())
                .way(getMenuWay());
    }

    private static Menu.WayData getGamesWay() {
        return new Menu.WayData()
                .accessAddress("bot/vk/game/all.get")
                .button("Игры", Keyboard.PRIMARY)
                .help("Просмотр доступных игр.")
                .point(
                        (resp, param, bag) -> {
                            new SendMessage(resp.getUserId()).message("Пока недоступно.").execute();
                            resp.setInit();
                            return exitWay(resp);
                        },
                        (resp, param, bag) -> null
                );
    }

    private static Menu.WayData getSignedUpForTheGameWay() {
        return new Menu.WayData()
                .accessAddress("bot/vk/game/my.get")
                .button("Мои", Keyboard.PRIMARY)
                .help("Просмотр игр на которых вы уже записаны.")
                .point(
                        (resp, param, bag) -> {
                            new SendMessage(resp.getUserId()).message("Пока недоступно.").execute();
                            resp.setInit();
                            return exitWay(resp);
                        },
                        (resp, param, bag) -> null
                );

    }

    private static Menu.WayData getSignUpForTheGameWay() {
        return new Menu.WayData()
                .accessAddress("bot/vk/game.register")
                .button("Записаться", Keyboard.PRIMARY)
                .help("Запись на игру.")
                .point(
                        (resp, param, bag) -> {
                            new SendMessage(resp.getUserId()).message("Пока недоступно.").execute();
                            resp.setInit();
                            return exitWay(resp);
                        },
                        (resp, param, bag) -> null
                );
    }

    private static Menu.WayData getMenuWay() {
        return new Menu.WayData()
                .loadAccess(false)
                .buttonText("Меню")
                .stringsToEnter("Menu")
                .help("Возврщение в главное меню.")
                .point(
                        (resp, param, bag) -> {
                            resp.setIdCommand(VkCommands.MENU.id()).setInit();
                            return exitWay(resp);
                        },
                        (resp, param, bag) -> null
                );
    }

}
