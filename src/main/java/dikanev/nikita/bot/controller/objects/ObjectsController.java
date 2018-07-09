package dikanev.nikita.bot.controller.objects;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dikanev.nikita.bot.api.exceptions.*;
import dikanev.nikita.bot.api.objects.ApiObject;
import dikanev.nikita.bot.api.objects.ApiObjects;
import dikanev.nikita.bot.api.objects.ArrayObject;
import dikanev.nikita.bot.api.objects.ExceptionObject;
import dikanev.nikita.bot.api.objects.deserialization.ArrayJsonAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectsController {

    private static final Logger LOG = LoggerFactory.getLogger(ObjectsController.class);

    private static Gson gson = getGson();

    public static ApiObject getApiObject(String objectString) throws InvalidParametersException, NotFoundException {
        ApiObject object;
        try {
            object = gson.fromJson(objectString, ApiObject.class);
        } catch (JsonSyntaxException e) {
            LOG.warn("Not a valid ApiObject", e);
            throw new InvalidParametersException("Not a valid ApiObject");
        }

        return gson.fromJson(objectString, ApiObjects.getObjectClass(object.getType()));
    }

    private static Gson getGson() {
        return new Gson().newBuilder().registerTypeAdapter(ArrayObject.class, new ArrayJsonAdapter()).create();
    }

    //Если объект ошибка кидает исключение
    public static void ifExceptionThrow(ApiObject object) throws ApiException {
        if (!object.getType().equals("error")) {
            return;
        }

        ExceptionObject exObject = (ExceptionObject) object;
        int code = exObject.getCode();

        switch (code) {
            case 400:
                throw new InvalidParametersException(exObject.getMessage());

            case 403:
                throw new NoAccessException(exObject.getMessage());

            case 404:
                throw new NotFoundException(exObject.getMessage());

            case 409:
                throw new ConfirmationException(exObject.getMessage());

            case 500:
                throw new UnidentifiedException(exObject.getMessage());

            case 503:
                throw new ConnectException(exObject.getMessage());

            default:
                throw new ApiException(exObject.getCode(), exObject.getDescription(), exObject.getMessage());
        }

    }

    public static <T extends ApiObject> T castObject(ApiObject object, Class<T> classOf) throws ApiException {
        try {
            return (T) object;
        } catch (Exception e) {
            LOG.warn("Could not convert ApiObject to " + classOf.getName() + "object ", e);
            throw new UnidentifiedException("Could not convert ApiObject to UserObject");
        }
    }

}
