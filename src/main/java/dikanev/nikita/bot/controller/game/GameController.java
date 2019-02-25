package dikanev.nikita.bot.controller.game;

import com.google.gson.JsonArray;
import dikanev.nikita.bot.api.exceptions.ApiException;
import dikanev.nikita.bot.api.item.Game;
import dikanev.nikita.bot.api.item.Gamer;
import dikanev.nikita.bot.api.item.RoleForGame;
import dikanev.nikita.bot.logic.connector.core.GameCoreConnector;
import dikanev.nikita.bot.logic.connector.db.users.UserDBConnector;

import java.sql.SQLException;
import java.util.List;

public class GameController {
    public static List<Game> getAllGames(String token, int indent, int count) throws ApiException {
        return GameCoreConnector.getAllGames(token, indent, count);
    }

    public static JsonArray getSignedUpGames(String token, int userId, int indent, int count) throws ApiException, SQLException {
        return GameCoreConnector.getSignedUpGames(token, UserDBConnector.getIdCore(userId), indent, count);
    }

    public static Game getGame(String token, int gameId) throws ApiException {
        return GameCoreConnector.getGame(token, gameId);
    }

    public static boolean isUserSignedUpToGame(String token, int gameId, int userId) throws ApiException, SQLException {
        return GameCoreConnector.isUserSignedUpToGame(token, gameId, UserDBConnector.getIdCore(userId));
    }

    public static RoleForGame[] getRolesFromTheGame(String token, int gameId) throws ApiException {
        return GameCoreConnector.getRolesFromTheGame(token, gameId);
    }

    public static RoleForGame getRoleFromTheGame(String token, int gameId, int roleId) throws ApiException {
        return GameCoreConnector.getRoleFromTheGame(token, gameId, roleId);
    }

    public static boolean isUserPassedTheTestOfGame(String token, int userId, int gameId) throws ApiException, SQLException {
        return GameCoreConnector.isUserPassedTheTestOfGame(token, UserDBConnector.getIdCore(userId), gameId);
    }

    public static Gamer registerUserToGame(String token, int userId, int gameId, int roleId) throws SQLException, ApiException {
        return GameCoreConnector.registerUserToGame(token, UserDBConnector.getIdCore(userId), gameId, roleId);
    }
}
