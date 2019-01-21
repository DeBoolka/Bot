package dikanev.nikita.bot.logic.connector.core;

import com.google.gson.Gson;
import dikanev.nikita.bot.api.exceptions.ApiException;
import dikanev.nikita.bot.api.item.Ammunition;
import dikanev.nikita.bot.api.objects.JObject;
import dikanev.nikita.bot.controller.core.CoreController;
import dikanev.nikita.bot.controller.objects.ObjectsController;
import dikanev.nikita.bot.service.client.parameter.HttpGetParameter;
import dikanev.nikita.bot.service.client.parameter.Parameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AmmunitionCoreConnector {

    private static Gson gson = new Gson();

    public static Ammunition addAmmunition(String token, int userId, String name, String[] photoLinks) throws ApiException {
        Parameter params = new HttpGetParameter()
                .add("token", token)
                .add("userId", String.valueOf(userId))
                .add("name", name);
        if (photoLinks != null) {
                params.set("link", Arrays.asList(photoLinks));
        }

        JObject req = CoreController.execute("user/ammunition.add", params);
        ObjectsController.ifExceptionThrow(req);

        return gson.fromJson(req.getObj().getAsJsonObject("object"), Ammunition.class);
    }

    public static List<Ammunition> getAmmunition(String token, int userId, int indent, int count) throws ApiException {
        JObject req = CoreController.execute("user/ammunition.get", new HttpGetParameter()
                .add("token", token)
                .add("userId", String.valueOf(userId))
                .add("indent", String.valueOf(indent))
                .add("count", String.valueOf(count)));
        ObjectsController.ifExceptionThrow(req);

        Ammunition[] ammunition = gson.fromJson(req.getObj().getAsJsonArray("objects"), Ammunition[].class);
        return new ArrayList<>(List.of(ammunition));
    }

    public static boolean deleteAmmunition(String token, int ammunitionId) throws ApiException {
        JObject req = CoreController.execute("user/ammunition.delete", new HttpGetParameter()
                .add("token", token)
                .add("ammunitionId", String.valueOf(ammunitionId)));
        ObjectsController.ifExceptionThrow(req);

        return req.getObj().getAsJsonPrimitive("message").getAsString().toLowerCase().equals("ok");
    }
}
