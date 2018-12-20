package dikanev.nikita.bot.logic.connector.core;

import dikanev.nikita.bot.api.exceptions.ApiException;
import dikanev.nikita.bot.api.objects.AccessGroupObject;
import dikanev.nikita.bot.api.objects.ApiObject;
import dikanev.nikita.bot.api.objects.ArrayObject;
import dikanev.nikita.bot.api.objects.MessageObject;
import dikanev.nikita.bot.controller.core.CoreController;
import dikanev.nikita.bot.controller.objects.ObjectsController;
import dikanev.nikita.bot.service.client.parameter.HttpGetParameter;
import dikanev.nikita.bot.service.client.parameter.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccessGroupCoreConnector {
    private static final Logger LOG = LoggerFactory.getLogger(AccessGroupCoreConnector.class);

    private static AccessGroupCoreConnector ourInstance = new AccessGroupCoreConnector();

    private PreparedStatement prStatement;

    public static AccessGroupCoreConnector getInstance() {
        return ourInstance;
    }

    //Устанавливает доступ к командам для группы
    public boolean createAccess(String token,int idGroup, String[] nameCommands, boolean privilege) throws SQLException {
        Parameter args = new HttpGetParameter();
        args.set("token", token);
        args.set("id_group", String.valueOf(privilege));
        args.set("name", List.of(nameCommands));
        args.set("access", String.valueOf(privilege));

        MessageObject msg;
        try {
            ApiObject resp = CoreController.execute("group/access/create", args);
            ObjectsController.ifExceptionThrow(resp);
            msg = ObjectsController.castObject(resp, MessageObject.class);
        } catch (ApiException e) {
            LOG.warn("Could not create access group: ", e);
            return false;
        }

        return msg.getMessage().toLowerCase().equals("ok");
    }

    //Возвращает доступность команд для группы
    public Map<String, Boolean> getAccessGroup(String token, int idGroup, List<String> commandsName) throws ApiException {
        String[] commandsNameArray = new String[commandsName.size()];

        Parameter req = new HttpGetParameter();
        req.set("token", token);
        req.set("cmd", new ArrayList<>(commandsName));
        req.set("id_group", String.valueOf(idGroup));



        ApiObject resp =  CoreController.execute("group/access/get", req);
        ObjectsController.ifExceptionThrow(resp);

        ArrayObject arrayObject = ObjectsController.castObject(resp, ArrayObject.class);
        final Map<String, Boolean> accessCommandMap = new HashMap<>();
        arrayObject.getObjects().forEach(e -> {
            AccessGroupObject access = (AccessGroupObject) e;
            accessCommandMap.put(access.getCommand(), access.hasAccess());
        });

        return accessCommandMap;
    }

    //Изменяет доступ к команде для группы
    public boolean editAccess(int idGroup, int idCommand, boolean privilege) throws SQLException {
        return false;
    }

    //Удаляет из БД запись с доступом к команде
    public boolean deleteAccess(int idGroup, int idCommand) throws SQLException {
        //        todo: сделать
        return false;
    }
}
