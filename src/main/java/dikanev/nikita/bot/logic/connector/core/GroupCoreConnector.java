package dikanev.nikita.bot.logic.connector.core;

import com.google.gson.JsonObject;
import dikanev.nikita.bot.api.exceptions.ApiException;
import dikanev.nikita.bot.api.groups.Group;
import dikanev.nikita.bot.api.objects.JObject;
import dikanev.nikita.bot.controller.core.CoreController;
import dikanev.nikita.bot.controller.objects.ObjectsController;
import dikanev.nikita.bot.service.client.parameter.HttpGetParameter;

public class GroupCoreConnector {
    public static String getGroupName(String token, int groupId) throws ApiException {
        JObject req = CoreController.execute("group/get", new HttpGetParameter()
                .add("token", token)
                .add("id", String.valueOf(groupId)));
        ObjectsController.ifExceptionThrow(req);

        return req.getObj().get("name").getAsString();
    }
}
