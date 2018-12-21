package dikanev.nikita.bot.controller.core;

import dikanev.nikita.bot.api.exceptions.*;
import dikanev.nikita.bot.api.objects.JObject;
import dikanev.nikita.bot.api.objects.ExceptionObject;
import dikanev.nikita.bot.service.client.core.CoreResponseClient;
import dikanev.nikita.bot.controller.objects.ObjectsController;
import dikanev.nikita.bot.service.client.parameter.Parameter;
import dikanev.nikita.bot.service.storage.clients.CoreClientStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CoreController {

    private static final Logger LOG = LoggerFactory.getLogger(CoreController.class);

    public static JObject execute(String command, Parameter args) throws ApiException {
        try {
            CoreResponseClient response = CoreClientStorage.getInstance().getClient().get(command, args);
            LOG.debug("Response: " + response.getStatusCode() + " | " +
                    response.getContent() + " | " +
                    response.getHeaders());

            return ObjectsController.getApiObject(response.getContent());
        } catch (IOException e) {
            LOG.warn("Server is not available: ", e);
            throw new ConnectException("Команда временно недостпна");
        }
    }

}
