package dikanev.nikita.bot.controller.groups;

import dikanev.nikita.bot.api.exceptions.ApiException;
import dikanev.nikita.bot.api.objects.UserObject;
import dikanev.nikita.bot.controller.users.UserController;
import dikanev.nikita.bot.logic.connector.core.AccessGroupCoreConnector;
import dikanev.nikita.bot.service.storage.clients.CoreClientStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class AccessGroupController {
    private static final Logger LOG = LoggerFactory.getLogger(AccessGroupController.class);

    private static AccessGroupController ourInstance = new AccessGroupController();

    private PreparedStatement prStatement;

    public static AccessGroupController getInstance() {
        return ourInstance;
    }

    //Проверяет доступна ли комманда пользователю
    public boolean hasAccessUser(String token, int idUser, String commandName) throws ApiException, SQLException {
        UserObject user;
        try {
            user = UserController.getInstance().getUser(CoreClientStorage.getInstance().getToken(), idUser);
        } catch (ApiException | SQLException e) {
            LOG.warn("Could not get user.", e);
            throw e;
        }

        return hasAccessGroup(token, user.getIdGroup(), commandName);
    }

    //Проверяет доступна ли комманда группе
    public boolean hasAccessGroup(String token, int idGroup, String commandName) throws ApiException {
        return getAccessGroup(token, idGroup, List.of(commandName))
                .getOrDefault(commandName, false);
    }

    //Возвращает доступность команд для группы
    public Map<String, Boolean> getAccessUser(String token, int idUser, List<String> commandsName) throws ApiException, SQLException{
        UserObject user;
        try {
            user = UserController.getInstance().getUser(CoreClientStorage.getInstance().getToken(), idUser);
        } catch (ApiException | SQLException e) {
            LOG.warn("Could not get user.", e);
            throw e;
        }

        return getAccessGroup(token, user.getIdGroup(), commandsName);
    }

    //Возвращает доступность команд для группы
    public Map<String, Boolean> getAccessGroup(String token, int idGroup, List<String> commandsName) throws ApiException{
        return AccessGroupCoreConnector.getAccessGroup(token, idGroup, commandsName);
    }
}
