package dikanev.nikita.bot.controller.groups;

import com.google.gson.Gson;
import dikanev.nikita.bot.api.exceptions.ApiException;
import dikanev.nikita.bot.api.objects.AccessGroupObject;
import dikanev.nikita.bot.api.objects.ApiObject;
import dikanev.nikita.bot.api.objects.ArrayObject;
import dikanev.nikita.bot.api.objects.MessageObject;
import dikanev.nikita.bot.controller.core.CoreController;
import dikanev.nikita.bot.controller.objects.ObjectsController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccessGroupCoreController {
    private static final Logger LOG = LoggerFactory.getLogger(AccessGroupCoreController.class);

    private static AccessGroupCoreController ourInstance = new AccessGroupCoreController();

    private PreparedStatement prStatement;

    public static AccessGroupCoreController getInstance() {
        return ourInstance;
    }

    //Устанавливает доступ к командам для группы
    public boolean createAccess(String token,int idGroup, String[] nameCommands, boolean privilege) throws SQLException {
        Map<String, String[]> args = new HashMap<>();
        args.put("token", new String[]{token});
        args.put("id_group", new String[]{String.valueOf(privilege)});
        args.put("name", nameCommands);
        args.put("access", new String[]{String.valueOf(privilege)});

        MessageObject msg;
        try {
            ApiObject resp = CoreController.executeArgsArray("group/access/create", args);
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

        Map<String, String[]> req = new HashMap<>();
        req.put("token", new String[]{token});
        req.put("cmd", commandsName.toArray(commandsNameArray));
        req.put("id_group", new String[]{String.valueOf(idGroup)});

        ApiObject resp =  CoreController.executeArgsArray("group/access/get", req);
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
