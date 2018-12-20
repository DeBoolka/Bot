package dikanev.nikita.bot.controller.core;

import dikanev.nikita.bot.api.exceptions.*;
import dikanev.nikita.bot.api.objects.ApiObject;
import dikanev.nikita.bot.service.client.core.CoreResponseClient;
import dikanev.nikita.bot.controller.objects.ObjectsController;
import dikanev.nikita.bot.service.client.parameter.Parameter;
import dikanev.nikita.bot.service.storage.clients.CoreClientStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CoreController {

    private static final Logger LOG = LoggerFactory.getLogger(CoreController.class);

    public static ApiObject execute(String command, Parameter args) throws ApiException {
        try {
            return executeHandle(command, CoreClientStorage.getInstance().getClient().get(command, args));
        } catch (IOException e) {
            LOG.warn("Server is not available: ", e);
            throw new ConnectException("Команда временно недостпна");
        }
    }

    public static ApiObject executeHandle(String command, CoreResponseClient response) throws ApiException {
        try {
            LOG.debug("Response: " + response.getStatusCode() + " | " +
                    response.getContent() + " | " +
                    response.getHeaders());
            return ObjectsController.getApiObject(response.getContent());
        } catch (NotFoundException e) {
            LOG.error("Command not found.", e);
            throw new NotFoundException("Команда временно недостпна");
        } catch (InvalidParametersException e) {
            throw new InvalidParametersException("Команда временно недостпна");
        }
    }

}
