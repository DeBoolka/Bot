package dikanev.nikita.bot.logic.callback.commands.menu;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import dikanev.nikita.bot.api.exceptions.InvalidParametersException;
import dikanev.nikita.bot.api.item.Game;
import dikanev.nikita.bot.api.item.GameRole;
import dikanev.nikita.bot.api.item.Gamer;
import dikanev.nikita.bot.api.item.RoleForGame;
import dikanev.nikita.bot.controller.game.GameController;
import dikanev.nikita.bot.logic.callback.CommandResponse;
import dikanev.nikita.bot.logic.callback.VkCommands;
import dikanev.nikita.bot.logic.callback.commands.WayMenuCommand;
import dikanev.nikita.bot.service.item.Menu.Menu;
import dikanev.nikita.bot.service.item.Menu.Point;
import dikanev.nikita.bot.service.storage.clients.CoreClientStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

public class GameMenuCommand extends WayMenuCommand {
    private static final Logger LOG = LoggerFactory.getLogger(GameMenuCommand.class);

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
                        allGamePointWorker()
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
                        signedUpGamePointWorker()
                );

    }

    private static Menu.WayData getSignUpForTheGameWay() {
        return new Menu.WayData()
                .accessAddress("bot/vk/game.register")
                .button("Записаться", Keyboard.PRIMARY)
                .help("Запись на игру.")
                .point(
                        (resp, param, bag) -> {
                            new SendMessage(resp.getUserId()).message("Введите номер игры:").execute();
                            return null;
                        },
                        indexGamePointWorker()
                ).point(choiceRolePoint())
                .point("checkTest", checkTestIn(), null);
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

    private static Point.Work allGamePointWorker() {
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

    private static Point.Work signedUpGamePointWorker() {
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

            JsonArray jsGamesAndGamers = GameController.getSignedUpGames(CoreClientStorage.getInstance().getToken(), resp.getUserId(), indent, count);
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

    private static Point.Work indexGamePointWorker() {
        return (resp, param, bag) -> {
            try {
                int gameId = Integer.valueOf(resp.getText());
                String token = CoreClientStorage.getInstance().getToken();
                Game game = GameController.getGame(token, gameId);
                if (GameController.isUserSignedUpToGame(token, gameId, resp.getUserId())) {
                    new SendMessage(resp.getUserId()).message("Вы уже подали заявку на эту игру: " + game.name).execute();
                    return exitWay(resp.setInit());
                }

                param.set("gameId", String.valueOf(gameId));
                return bag.getWay().getPointByName("checkTest");
            } catch (NumberFormatException e) {
                new SendMessage(resp.getUserId()).message("Ошибка в номере игры.").execute();
                return exitWay(resp.setInit());
            } catch (InvalidParametersException e) {
                new SendMessage(resp.getUserId()).message("Такой игры не существует").execute();
                return exitWay(resp.setInit());
            }
        };
    }

    private static Point choiceRolePoint() {
        return new Point("choiceRole",
                (resp, param, bag) -> {
                    int gameId = param.getIntF("gameId");
                    String token = CoreClientStorage.getInstance().getToken();
                    RoleForGame[] roles = GameController.getRolesFromTheGame(token, gameId);
                    if (roles == null || roles.length == 0) {
                        new SendMessage(resp.getUserId()).message("Нет доступных игровых ролей.").execute();
                        return exitWay(resp.setInit());
                    }

                    StringBuilder rolesMessage = new StringBuilder();
                    Arrays.stream(roles).forEach(role ->
                            rolesMessage.append(role.role.id).append(". ").append(role.role.name)
                                    .append(" - ").append(role.numberOfAvailableSeats).append("\n")
                    );
                    new SendMessage(resp.getUserId()).message("Выберите номер роли:\n" + rolesMessage.toString()).execute();
                    return null;
                },
                (resp, param, bag) -> {
                    try {
                        int roleId = Integer.valueOf(resp.getText());
                        int gameId = param.getIntF("gameId");
                        String token = CoreClientStorage.getInstance().getToken();
                        RoleForGame role;
                        if ((role = GameController.getRoleFromTheGame(token, gameId, roleId)) == null) {
                            new SendMessage(resp.getUserId()).message("Такой роли не существует на игре").execute();
                        } else {
                            param.set("roleId", String.valueOf(roleId));
                            registerUserToGame(resp, gameId, roleId);
                        }
                    } catch (NumberFormatException e) {
                        new SendMessage(resp.getUserId()).message("Ошибка в номере роли.").execute();
                    }
                    return exitWay(resp.setInit());
                }
        );
    }

    private static void registerUserToGame(CommandResponse resp, int gameId, int roleId) throws ClientException, ApiException {
        try {
            String token = CoreClientStorage.getInstance().getToken();
            Gamer gamer = GameController.registerUserToGame(token, resp.getUserId(), gameId, roleId);
            new SendMessage(resp.getUserId()).message("Ваша заявка подана и ожидает одобрения администратора игры.").execute();
        } catch (Exception e) {
            LOG.error("Fatal error in registration user to game. Data:{userId: " + resp.getUserId()
                    + ", gameId: " + gameId + ", roleId: " + roleId + "}", e);
            new SendMessage(resp.getUserId()).message("Ошибка в регистрации на игру").execute();
        }
    }

    private static Point.Work checkTestIn() {
        return (resp, param, bag) -> {
            int gameId = param.getIntF("gameId");
            String token = CoreClientStorage.getInstance().getToken();
            if (!GameController.isUserPassedTheTestOfGame(token, resp.getUserId(), gameId)) {
                new SendMessage(resp.getUserId()).message("Вы не прошли тест на знание правил игры.").execute();
                return exitWay(resp.setInit());
            }
            return bag.getWay().getPointByName("choiceRole");
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
