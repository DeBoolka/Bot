package dikanev.nikita.bot.logic.connector.core;

import dikanev.nikita.bot.api.exceptions.ApiException;
import dikanev.nikita.bot.api.objects.AmmunitionObject;
import dikanev.nikita.bot.api.objects.JObject;
import dikanev.nikita.bot.controller.core.CoreController;
import dikanev.nikita.bot.controller.objects.ObjectsController;
import dikanev.nikita.bot.service.client.parameter.HttpGetParameter;
import dikanev.nikita.bot.service.client.parameter.Parameter;

import java.util.Arrays;

public class AmmunitionCoreConnector {

    public static AmmunitionObject addAmmunition(String token, int userId, String name, String[] photoLinks) throws ApiException {
        Parameter params = new HttpGetParameter()
                .add("token", token)
                .add("userId", String.valueOf(userId))
                .add("name", name);
        if (photoLinks != null) {
                params.set("link", Arrays.asList(photoLinks));
        }

        JObject req = CoreController.execute("user/ammunition.add", params);
        ObjectsController.ifExceptionThrow(req);

        return req.cast(AmmunitionObject.empty());
    }
}
