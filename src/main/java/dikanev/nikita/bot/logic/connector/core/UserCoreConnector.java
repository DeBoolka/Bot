package dikanev.nikita.bot.logic.connector.core;

import com.google.gson.JsonObject;
import dikanev.nikita.bot.api.exceptions.*;
import dikanev.nikita.bot.api.groups.Group;
import dikanev.nikita.bot.api.objects.JObject;
import dikanev.nikita.bot.api.objects.MessageObject;
import dikanev.nikita.bot.api.objects.UserInfoObject;
import dikanev.nikita.bot.api.objects.UserObject;
import dikanev.nikita.bot.controller.core.CoreController;
import dikanev.nikita.bot.controller.objects.ObjectsController;
import dikanev.nikita.bot.service.client.parameter.HttpGetParameter;
import dikanev.nikita.bot.service.client.parameter.Parameter;
import dikanev.nikita.bot.service.storage.clients.CoreClientStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.util.List;

public class UserCoreConnector {

    private static final Logger LOG = LoggerFactory.getLogger(UserCoreConnector.class);

    //Возвращает юзера.
    //Кидает исключения: NoAccessException, NotFoundException, UnidentifiedException
    public static UserObject getUser(String token, int id) throws ApiException {
        JObject req = CoreController.execute("user/get"
                , new HttpGetParameter()
                        .add("token", token)
                        .add("id", String.valueOf(id))
        );
        ObjectsController.ifExceptionThrow(req);

        return req.cast(UserObject.empty());
    }

    /**
     * Создает нового пользователя в ядре
     *
     * @param token токен в ядре
     * @param name имя
     * @param sName фамилия
     * @param email почта
     * @param login логин
     * @param idGroup новая группа
     * @return id в ядре
     *
     * @throws NoAccessException
     * @throws UnidentifiedException
     * @throws InvalidParametersException
     */
    public static UserObject register(String token, String name, String sName, String email, String login, int idGroup) throws ApiException {
        JObject req = CoreController.execute("user/register",
                new HttpGetParameter()
                        .add("token", token)
                        .add("id_group", String.valueOf(idGroup))
                        .add("name", name)
                        .add("s_name", sName)
                        .add("email", email)
                        .add("login", login)
        );
        ObjectsController.ifExceptionThrow(req);

        return req.cast(UserObject.empty());
    }

    //Удаляет юзера.
    public static boolean deleteUser(String token, int id) throws ApiException {
        JObject req = CoreController.execute("user/delete", new HttpGetParameter()
                .add("token", token)
                .add("id", String.valueOf(id)));

        ObjectsController.ifExceptionThrow(req);
        MessageObject message = req.cast(MessageObject.empty());

        return message.getMessage().equals("Ok");
    }

    //Получает токен юзера.
    public static String getToken(String token, int id) throws ApiException {
        JObject req = CoreController.execute("user/create/token", new HttpGetParameter()
                .add("token", token)
                .add("id", String.valueOf(id)));

        ObjectsController.ifExceptionThrow(req);
        MessageObject message = req.cast(MessageObject.empty());

        return message.getMessage();
    }

    public static boolean hasLogin(String token, String login) {
        try {
            JObject req = CoreController.execute("user/info/get", new HttpGetParameter()
                    .add("token", token)
                    .add("login", login));
            ObjectsController.ifExceptionThrow(req);
        } catch (InvalidParametersException e) {
            return false;
        } catch (Exception e) {
            LOG.error("Failed checked login.", e);
            return false;
        }

        return true;
    }

    public static boolean hasEmail(String token, String email) {
        try {
            JObject req = CoreController.execute("user/info/get", new HttpGetParameter()
                    .add("token", token)
                    .add("email", email));
            ObjectsController.ifExceptionThrow(req);
        } catch (InvalidParametersException e) {
            return false;
        } catch (Exception e) {
            LOG.error("Failed checked email.", e);
            return false;
        }

        return true;
    }

    public static JObject applyInvite(String token, int userId, String invite) throws ApiException {
        JObject req = CoreController.execute("user/invite/apply", new HttpGetParameter()
                .add("token", token)
                .add("userId", String.valueOf(userId))
                .add("invite", invite));
        ObjectsController.ifExceptionThrow(req);

        return req;
    }

    public static String createInvite(String token, int userId, int groupId) throws ApiException {
        JObject req = CoreController.execute("user/invite/create", new HttpGetParameter()
                .add("token", token)
                .add("userId", String.valueOf(userId))
                .add("groupId", String.valueOf(groupId)));
        ObjectsController.ifExceptionThrow(req);

        return req.getObj().get("message").getAsString();
    }

    public static Group setGroup(String token, int userId, int groupId) throws ApiException {
        JObject req = CoreController.execute("user/update", new HttpGetParameter()
                .add("token", token)
                .add("userId", String.valueOf(userId))
                .add("groupId", String.valueOf(groupId)));
        ObjectsController.ifExceptionThrow(req);

        JsonObject root = req.getObj();
        if (root.has("message") && root.get("message").getAsString().toLowerCase().equals("ok")) {
            return new Group(groupId, GroupCoreConnector.getGroupName(CoreClientStorage.getInstance().getToken(), groupId));
        }
        return null;
    }

    public static UserInfoObject getUserInfo(String token, int userId) throws ApiException {
        JObject req = CoreController.execute("user/info/get"
                , new HttpGetParameter()
                        .add("token", token)
                        .add("id", String.valueOf(userId))
        );
        ObjectsController.ifExceptionThrow(req);

        return req.cast(UserInfoObject.empty());
    }

    public static UserInfoObject getUserInfo(String token, boolean isLoginTrueElseEmail, String loginOrEmail) throws ApiException {
        JObject req = CoreController.execute("user/info/get"
                , new HttpGetParameter()
                        .add("token", token)
                        .add(isLoginTrueElseEmail ? "login" : "email", loginOrEmail)
        );
        ObjectsController.ifExceptionThrow(req);

        return req.cast(UserInfoObject.empty());
    }

    public static JObject getPersonalDataOfUser(String token, int userId, String... data) throws ApiException {
        JObject req = CoreController.execute("user/info/get", new HttpGetParameter()
                .add("token", token)
                .add("id", String.valueOf(userId))
                .add("col", List.of(data)));
        ObjectsController.ifExceptionThrow(req);

        JsonObject root = req.getObj();
        if (root.has("object")) {
            return new JObject(root.get("object"));
        }
        return null;
    }
}
