package dikanev.nikita.bot.controller.game;

import com.google.gson.JsonArray;
import dikanev.nikita.bot.api.exceptions.ApiException;
import dikanev.nikita.bot.api.item.Game;
import dikanev.nikita.bot.logic.connector.core.GameCoreConnector;

import java.util.List;

public class GameController {
    public static List<Game> getAllGames(String token, int indent, int count) throws ApiException {
        return GameCoreConnector.getAllGames(token, indent, count);
    }

    public static JsonArray getSignedUpGames(String token, int indent, int count) throws ApiException {
        return GameCoreConnector.getSignedUpGames(token, indent, count);
    }
}
