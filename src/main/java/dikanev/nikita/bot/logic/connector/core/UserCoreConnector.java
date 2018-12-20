package dikanev.nikita.bot.logic.connector.core;

import dikanev.nikita.bot.api.exceptions.*;
import dikanev.nikita.bot.api.objects.ApiObject;
import dikanev.nikita.bot.api.objects.MessageObject;
import dikanev.nikita.bot.api.objects.UserObject;
import dikanev.nikita.bot.controller.core.CoreController;
import dikanev.nikita.bot.controller.objects.ObjectsController;
import dikanev.nikita.bot.service.client.parameter.HttpGetParameter;
import dikanev.nikita.bot.service.client.parameter.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserCoreConnector {

    private static final Logger LOG = LoggerFactory.getLogger(UserCoreConnector.class);

    //Возвращает юзера.
    //Кидает исключения: NoAccessException, NotFoundException, UnidentifiedException
    public static UserObject getUser(String token, int id) throws ApiException {
        Parameter getParam = new HttpGetParameter()
                .add("token", token)
                .add("id", String.valueOf(id));

        ApiObject req = CoreController.execute("user/get"
                , new HttpGetParameter()
                        .add("token", token)
                        .add("id", String.valueOf(id))
        );
        ObjectsController.ifExceptionThrow(req);

        return ObjectsController.castObject(req, UserObject.class);
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
        ApiObject req = CoreController.execute("user/register",
                new HttpGetParameter()
                        .add("token", token)
                        .add("id_group", String.valueOf(idGroup))
                        .add("name", name)
                        .add("s_name", sName)
                        .add("email", email)
                        .add("login", login)
        );
        ObjectsController.ifExceptionThrow(req);

        return ObjectsController.castObject(req, UserObject.class);
    }

    //Удаляет юзера.
    public static boolean deleteUser(String token, int id) throws ApiException {
        ApiObject req = CoreController.execute("user/delete", new HttpGetParameter()
                .add("token", token)
                .add("id", String.valueOf(id)));

        ObjectsController.ifExceptionThrow(req);
        MessageObject message = ObjectsController.castObject(req, MessageObject.class);

        return message.getMessage().equals("Ok");
    }

    //Получает токен юзера.
    public static String getToken(String token, int id) throws ApiException {
        ApiObject req = CoreController.execute("user/create/token", new HttpGetParameter()
                .add("token", token)
                .add("id", String.valueOf(id)));

        ObjectsController.ifExceptionThrow(req);
        MessageObject message = ObjectsController.castObject(req, MessageObject.class);

        return message.getMessage();
    }

    public static boolean hasLogin(String token, String login) {
        try {
            ApiObject req = CoreController.execute("user/info/get", new HttpGetParameter()
                    .add("token", token)
                    .add("login", login));
            ObjectsController.ifExceptionThrow(req);
        } catch (Exception e) {
            LOG.error("Failed checked login.", e);
            return false;
        }

        return true;
    }

    public static boolean hasEmail(String token, String email) {
        try {
            ApiObject req = CoreController.execute("user/info/get", new HttpGetParameter()
                    .add("token", token)
                    .add("email", email));
            ObjectsController.ifExceptionThrow(req);
        } catch (Exception e) {
            LOG.error("Failed checked email.", e);
            return false;
        }

        return true;
    }
}
