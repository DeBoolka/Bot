package dikanev.nikita.bot.controller.core;

import dikanev.nikita.bot.api.exceptions.*;
import dikanev.nikita.bot.api.objects.ApiObject;
import dikanev.nikita.bot.client.core.CoreResponseClient;
import dikanev.nikita.bot.controller.objects.ObjectsController;
import dikanev.nikita.bot.model.storage.clients.CoreClientStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class CoreController {

    private static final Logger LOG = LoggerFactory.getLogger(CoreController.class);

    public static ApiObject execute(String command, Map<String, String> args) throws ApiException {
        CoreResponseClient response;
        try {
            response = CoreClientStorage.getInstance().getClient().get(command, args);
            if (response.getStatusCode() == 200) {
                return ObjectsController.getApiObject(response.getContent());
            }
        } catch (IOException e) {
            LOG.warn("Server is not available: ", e);
            throw new ConnectException("Команда временно недостпна");
        } catch (NotFoundException e) {
            throw new NotFoundException("Команда временно недостпна");
        } catch (InvalidParametersException e) {
            throw new InvalidParametersException("Команда временно недостпна");
        }

        LOG.warn("Invalid response from the server: ", response.getStatusCode(), response.getHeaders(), response.getContent());
        throw new UnidentifiedException("Команда временно недостпна");
    }

}
