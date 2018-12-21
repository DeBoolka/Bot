package dikanev.nikita.bot.controller.objects;

import com.google.gson.JsonSyntaxException;
import dikanev.nikita.bot.api.exceptions.*;
import dikanev.nikita.bot.api.objects.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectsController {

    private static final Logger LOG = LoggerFactory.getLogger(ObjectsController.class);

    public static JObject getApiObject(String objectString) throws UnidentifiedException {
        JObject object;
        try {
            object = new JObject(objectString);
            return object;
        } catch (JsonSyntaxException e) {
            LOG.warn("Not a valid JObject", e);
            throw new UnidentifiedException("Команда временно недостпна");
        }
    }

    //Если объект исключение, кидает его
    public static void ifExceptionThrow(JObject object) throws ApiException {
        if (!object.getType().equals("error")) {
            return;
        }

        ExceptionObject exObject = object.build(ExceptionObject.empty());
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

}
