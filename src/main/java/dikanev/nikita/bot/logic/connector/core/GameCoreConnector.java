package dikanev.nikita.bot.logic.connector.core;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import dikanev.nikita.bot.api.exceptions.ApiException;
import dikanev.nikita.bot.api.item.Game;
import dikanev.nikita.bot.api.objects.JObject;
import dikanev.nikita.bot.controller.core.CoreController;
import dikanev.nikita.bot.controller.objects.ObjectsController;
import dikanev.nikita.bot.service.client.parameter.HttpGetParameter;

import java.util.ArrayList;
import java.util.List;

public class GameCoreConnector {
    private static Gson gson = new Gson();

    public static List<Game> getAllGames(String token, int indent, int count) throws ApiException {
        JObject req = CoreController.execute("game/all.get", new HttpGetParameter()
                .add("token", token)
                .add("indent", String.valueOf(indent))
                .add("count", String.valueOf(count)));
        ObjectsController.ifExceptionThrow(req);

        Game[] games = gson.fromJson(req.getObj().getAsJsonArray("objects"), Game[].class);
        return new ArrayList<>(List.of(games));
    }

    public static JsonArray getSignedUpGames(String token, int indent, int count) throws ApiException {
        JObject req = CoreController.execute("game/my.get", new HttpGetParameter()
                .add("token", token)
                .add("indent", String.valueOf(indent))
                .add("count", String.valueOf(count)));
        ObjectsController.ifExceptionThrow(req);

        return req.getObj().getAsJsonArray("objects");

    }
}
