package dikanev.nikita.bot.controller.users;

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

import java.util.Map;

public class UserCoreController {

    private static final Logger LOG = LoggerFactory.getLogger(UserCoreController.class);

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

    //Возвращает вновь созданного юзера.
    //Кидает исключения: NoAccessException, InvalidParametersException, UnidentifiedException
    public static UserObject createUser(String token, int idGroup, String name, String sName) throws ApiException {
        ApiObject req = CoreController.execute("user/register",
                new HttpGetParameter()
                        .add("token", token)
                        .add("id_group", String.valueOf(idGroup))
                        .add("name", name)
                        .add("s_name", sName)
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

}
