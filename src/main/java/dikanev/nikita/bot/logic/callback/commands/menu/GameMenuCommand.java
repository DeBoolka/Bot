package dikanev.nikita.bot.logic.callback.commands.menu;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import dikanev.nikita.bot.api.item.Game;
import dikanev.nikita.bot.api.item.Gamer;
import dikanev.nikita.bot.controller.game.GameController;
import dikanev.nikita.bot.logic.callback.CommandResponse;
import dikanev.nikita.bot.logic.callback.VkCommands;
import dikanev.nikita.bot.logic.callback.commands.WayMenuCommand;
import dikanev.nikita.bot.service.item.Menu.Menu;
import dikanev.nikita.bot.service.item.Menu.Point;
import dikanev.nikita.bot.service.storage.clients.CoreClientStorage;

import java.sql.Timestamp;
import java.util.List;

public class GameMenuCommand extends WayMenuCommand {
    private final static int COUNT_GAMES = 5;
    private final static String BACK_BUTTON = "Назад";
    private final static String NEXT_BUTTON = "Вперед";
    private final static String END_BUTTON = "Закончить";
    private final static String CANCEL_BUTTON = "Отмена";

    private final static Keyboard CANCEL_KEYBOARD = new Keyboard().def(CANCEL_BUTTON);

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
                            new SendMessage(resp.getUserId()).message("Список всех игр:\n" +
                                    "id\tНазвание\tГород\tДата\t").button(CANCEL_KEYBOARD).execute();
                            resp.setText("");
                            return bag.getWay().getCurrentPoint(bag);
                        },
                        allGamePoint()
                );
    }

    private static Menu.WayData getSignedUpForTheGameWay() {
        return new Menu.WayData()
                .accessAddress("bot/vk/game/my.get")
                .button("Мои", Keyboard.PRIMARY)
                .help("Просмотр игр на которых вы уже записаны.")
                .point(
                        (resp, param, bag) -> {
                            new SendMessage(resp.getUserId()).message("Список игр на которые вы уже записаны:\n" +
                                    "id\tНазвание\tСтатус\tГород\tДата\t").button(CANCEL_KEYBOARD).execute();
                            resp.setText("");
                            return bag.getWay().getCurrentPoint(bag);
                        },
                        signedUpGamePoint()
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

    private static Point.Work allGamePoint() {
        return (resp, param, bag) -> {
            int indent = param.getIntFOrDefault("indent", 0);
            int count = param.getIntFOrDefault("count", COUNT_GAMES);
            String text = resp.getText().trim().toLowerCase();

            if (BACK_BUTTON.toLowerCase().equals(text)) {
                indent = (indent >= COUNT_GAMES) ? indent - COUNT_GAMES : 0;
            } else if (NEXT_BUTTON.toLowerCase().equals(text)) {
                indent += (param.getFOrDefault("hasNext", "true").equals("false")) ? 0 : COUNT_GAMES;
            } else if (END_BUTTON.toLowerCase().equals(text)) {
                resp.setInit();
                return exitWay(resp);
            }

            List<Game> games = GameController.getAllGames(CoreClientStorage.getInstance().getToken(), indent, count);
            param.set("hasNext", (games.size() < COUNT_GAMES) ? "false" : "true");

            StringBuilder sendText = new StringBuilder();
            games.forEach(it -> sendText.append(it.id).append("\t").append(it.name).append("\t")
                    .append(it.city).append("\t").append(it.date).append("\n"));

            if (sendText.length() == 0) {
                sendText.append("Игры отсутствуют");
            }

            return sendMessageAndReturnNull(resp, sendText);
        };
    }

    private static Point.Work signedUpGamePoint() {
        return (resp, param, bag) -> {
            int indent = param.getIntFOrDefault("indent", 0);
            int count = param.getIntFOrDefault("count", COUNT_GAMES);
            String text = resp.getText().trim().toLowerCase();

            if (BACK_BUTTON.toLowerCase().equals(text)) {
                indent = (indent >= COUNT_GAMES) ? (indent - COUNT_GAMES) : 0;
            } else if (NEXT_BUTTON.toLowerCase().equals(text)) {
                indent += ("false".equals(param.getFOrDefault("hasNext", "true"))) ? 0 : COUNT_GAMES;
            } else if (END_BUTTON.toLowerCase().equals(text)) {
                resp.setInit();
                return exitWay(resp);
            }

            JsonArray jsGamesAndGamers = GameController.getSignedUpGames(CoreClientStorage.getInstance().getToken(), indent, count);
            param.set("hasNext", (jsGamesAndGamers.size() < COUNT_GAMES) ? "false" : "true");

            StringBuilder sendText = new StringBuilder();
            jsGamesAndGamers.forEach(it -> {
                JsonObject jsObjIt = it.getAsJsonObject();
                Game game = getGameFromJsonObject(jsObjIt.getAsJsonObject("game"));
                Gamer gamer = getGamerFromJsonObject(jsObjIt.getAsJsonObject("gamer"), game);

                sendText.append(game.id).append("\t").append(game.name).append("\t").append(gamer.getLocaleStatus()).append("\t")
                        .append(game.city).append("\t").append(game.date).append("\n");
            });

            if (sendText.length() == 0) {
                sendText.append("Игры отсутствуют");
            }

            return sendMessageAndReturnNull(resp, sendText);
        };

    }

    private static Game getGameFromJsonObject(JsonObject game) {
        int gameId = game.getAsJsonPrimitive("id").getAsInt();
        String name = game.getAsJsonPrimitive("name").getAsString();
        int organizerId = game.getAsJsonPrimitive("organizerId").getAsInt();
        String city = game.getAsJsonPrimitive("city").getAsString();
        Timestamp gameDate = Timestamp.valueOf(game.getAsJsonPrimitive("gameDate").getAsString());
        return new Game(gameId, name, organizerId, city, gameDate);
    }

    private static Gamer getGamerFromJsonObject(JsonObject gamer, Game game) {
        int userId = gamer.getAsJsonPrimitive("userId").getAsInt();
        String userGroup = gamer.getAsJsonPrimitive("userGroup").getAsString();
        String status = gamer.getAsJsonPrimitive("status").getAsString();
        int roleId = gamer.getAsJsonPrimitive("roleId").getAsInt();
        int money = gamer.getAsJsonPrimitive("money").getAsInt();
        Timestamp fillingTime = Timestamp.valueOf(gamer.getAsJsonPrimitive("fillingTime").getAsString());
        return new Gamer(game.id, userId, userGroup, status, roleId, money, fillingTime);
    }

    private static Point sendMessageAndReturnNull(CommandResponse resp, StringBuilder sendText) throws ClientException, ApiException {
        Keyboard keyboard = new Keyboard().prim(BACK_BUTTON).def(END_BUTTON).prim(NEXT_BUTTON);
        new SendMessage(resp.getUserId()).message(sendText.toString()).button(keyboard).execute();
        return null;
    }

}
