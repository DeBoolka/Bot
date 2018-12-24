package dikanev.nikita.bot.logic.connector.core;

import com.google.gson.JsonObject;
import dikanev.nikita.bot.api.exceptions.ApiException;
import dikanev.nikita.bot.api.objects.JObject;
import dikanev.nikita.bot.api.objects.ArrayObject;
import dikanev.nikita.bot.api.objects.MessageObject;
import dikanev.nikita.bot.controller.core.CoreController;
import dikanev.nikita.bot.controller.objects.ObjectsController;
import dikanev.nikita.bot.service.client.parameter.HttpGetParameter;
import dikanev.nikita.bot.service.client.parameter.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccessGroupCoreConnector {
    private static final Logger LOG = LoggerFactory.getLogger(AccessGroupCoreConnector.class);

    //Устанавливает доступ к командам для группы
    public static boolean createAccess(String token,int idGroup, String[] nameCommands, boolean privilege) {
        Parameter args = new HttpGetParameter();
        args.set("token", token);
        args.set("id_group", String.valueOf(privilege));
        args.set("name", List.of(nameCommands));
        args.set("access", String.valueOf(privilege));

        MessageObject msg;
        try {
            JObject resp = CoreController.execute("group/access/create", args);
            ObjectsController.ifExceptionThrow(resp);
            msg = resp.cast(MessageObject.empty());
        } catch (ApiException e) {
            LOG.warn("Could not create access group: ", e);
            return false;
        }

        return msg.getMessage().toLowerCase().equals("ok");
    }

    //Возвращает доступность команд для группы
    public static Map<String, Boolean> getAccessGroup(String token, int idGroup, List<String> commandsName) throws ApiException {
        Parameter req = new HttpGetParameter();
        req.set("token", token);
        req.set("cmd", new ArrayList<>(commandsName));
        req.set("id_group", String.valueOf(idGroup));



        JObject resp =  CoreController.execute("group/access/get", req);
        ObjectsController.ifExceptionThrow(resp);

        ArrayObject arrayObject = resp.cast(ArrayObject.empty());
        final Map<String, Boolean> accessCommandMap = new HashMap<>();
        arrayObject.getObjects().forEach(e -> {
            JsonObject obj = e.getAsJsonObject();
            String command = obj.get("command").getAsString();

            boolean access = false;
            if (obj.has("access")) {
                access = obj.get("access").getAsBoolean();
            }
            accessCommandMap.put(command, access);
        });

        return accessCommandMap;
    }
}
