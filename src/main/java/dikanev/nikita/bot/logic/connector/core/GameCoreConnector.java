package dikanev.nikita.bot.logic.connector.core;

import com.google.gson.*;
import dikanev.nikita.bot.api.exceptions.ApiException;
import dikanev.nikita.bot.api.item.Game;
import dikanev.nikita.bot.api.item.GameRole;
import dikanev.nikita.bot.api.item.Gamer;
import dikanev.nikita.bot.api.item.RoleForGame;
import dikanev.nikita.bot.api.objects.JObject;
import dikanev.nikita.bot.api.objects.MessageObject;
import dikanev.nikita.bot.api.objects.SimpleObject;
import dikanev.nikita.bot.controller.core.CoreController;
import dikanev.nikita.bot.controller.objects.ObjectsController;
import dikanev.nikita.bot.service.client.parameter.HttpGetParameter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GameCoreConnector {
    private static Gson gson = getGsonBuilder();

    public static List<Game> getAllGames(String token, int indent, int count) throws ApiException {
        JObject req = CoreController.execute("game/all.get", new HttpGetParameter()
                .add("token", token)
                .add("indent", String.valueOf(indent))
                .add("count", String.valueOf(count)));
        ObjectsController.ifExceptionThrow(req);

        Game[] games = gson.fromJson(req.getObj().getAsJsonArray("objects"), Game[].class);
        return new ArrayList<>(List.of(games));
    }

    public static JsonArray getSignedUpGames(String token, int userId, int indent, int count) throws ApiException {
        JObject req = CoreController.execute("game/my.get", new HttpGetParameter()
                .add("token", token)
                .add("userId", String.valueOf(userId))
                .add("indent", String.valueOf(indent))
                .add("count", String.valueOf(count)));
        ObjectsController.ifExceptionThrow(req);

        return req.getObj().getAsJsonArray("objects");

    }

    public static Game getGame(String token, int gameId) throws ApiException {
        JObject req = CoreController.execute("game.get", new HttpGetParameter()
                .add("token", token)
                .add("gameId", String.valueOf(gameId)));
        ObjectsController.ifExceptionThrow(req);

        return gson.fromJson(SimpleObject.getSimpleJsonObject(req), Game.class);
    }

    public static boolean isUserSignedUpToGame(String token, int gameId, int userId) throws ApiException {
        JObject req = CoreController.execute("game/user.issigned", new HttpGetParameter()
                .add("token", token)
                .add("gameId", String.valueOf(gameId))
                .add("userId", String.valueOf(userId)));
        ObjectsController.ifExceptionThrow(req);

        return req.cast(MessageObject.empty()).getMessage().equalsIgnoreCase("true");
    }

    public static RoleForGame[] getRolesFromTheGame(String token, int gameId) throws ApiException {
        JObject req = CoreController.execute("game/roles.get", new HttpGetParameter()
                .add("token", token)
                .add("gameId", String.valueOf(gameId)));
        ObjectsController.ifExceptionThrow(req);

        JsonArray roles = req.getObj().getAsJsonArray("objects");
        return roles == null ? null : gson.fromJson(roles, RoleForGame[].class);
    }

    public static RoleForGame getRoleFromTheGame(String token, int gameId, int roleId) throws ApiException {
        JObject req = CoreController.execute("game/role.get", new HttpGetParameter()
                .add("token", token)
                .add("gameId", String.valueOf(gameId))
                .add("roleId", String.valueOf(roleId)));
        ObjectsController.ifExceptionThrow(req);

        return req.isNull() ? null : gson.fromJson(SimpleObject.getSimpleJsonObject(req), RoleForGame.class);
    }

    public static boolean isUserPassedTheTestOfGame(String token, int userId, int gameId) throws ApiException {
        JObject req = CoreController.execute("game/test.check", new HttpGetParameter()
                .add("token", token)
                .add("userId", String.valueOf(userId))
                .add("gameId", String.valueOf(gameId)));
        ObjectsController.ifExceptionThrow(req);

        return req.cast(MessageObject.empty()).getMessage().equalsIgnoreCase("true");
    }

    public static Gamer registerUserToGame(String token, int userId, int gameId, int roleId) throws ApiException {
        JObject req = CoreController.execute("game.register", new HttpGetParameter()
                .add("token", token)
                .add("userId", String.valueOf(userId))
                .add("gameId", String.valueOf(gameId))
                .add("roleId", String.valueOf(roleId)));
        ObjectsController.ifExceptionThrow(req);

        return gson.fromJson(SimpleObject.getSimpleJsonObject(req), Gamer.class);
    }

    private static Gson getGsonBuilder() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(RoleForGame.class, new GsonConverter());
        return gsonBuilder.create();
    }

    private static class GsonConverter implements JsonDeserializer<RoleForGame>{

        @Override
        public RoleForGame deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonObject()) {
                return context.deserialize(json, typeOfT);
            }
            return getRoleForGameObject(json.getAsJsonObject());
        }

        private RoleForGame getRoleForGameObject(JsonObject jsonRoot) {
            JsonObject jsRoleObj = jsonRoot.get("role").getAsJsonObject();
            int roleId = jsRoleObj.getAsJsonPrimitive("id").getAsInt();
            String roleName = jsRoleObj.getAsJsonPrimitive("name").getAsString();
            String roleDescription = jsRoleObj.has("description") ? jsRoleObj.getAsJsonPrimitive("description").getAsString() : null;

            GameRole gameRole = new GameRole(roleId, roleName, roleDescription);
            int numberOfAvailableSeats = jsonRoot.getAsJsonPrimitive("numberOfAvailableSeats").getAsInt();
            int userMaxCount = jsonRoot.getAsJsonPrimitive("userMaxCount").getAsInt();
            int armoredMaxCount = jsonRoot.getAsJsonPrimitive("armoredMaxCount").getAsInt();
            return new RoleForGame(gameRole, numberOfAvailableSeats, userMaxCount, armoredMaxCount);
        }
    }
}
