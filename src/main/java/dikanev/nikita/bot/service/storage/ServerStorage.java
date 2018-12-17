package dikanev.nikita.bot.service.storage;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.objects.groups.CallbackServer;
import com.vk.api.sdk.objects.groups.responses.AddCallbackServerResponse;
import com.vk.api.sdk.objects.groups.responses.GetCallbackConfirmationCodeResponse;
import com.vk.api.sdk.objects.groups.responses.GetCallbackServersResponse;
import dikanev.nikita.bot.service.storage.clients.VkClientStorage;
import dikanev.nikita.bot.service.server.CallbackRequestHandler;
import dikanev.nikita.bot.service.server.ConfirmationCodeRequestHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

public class ServerStorage {

    private static final Logger LOG = LoggerFactory.getLogger(ServerStorage.class);

    private static ServerStorage ourInstance = new ServerStorage();

    private Server server = null;

    private String confirmationServer;

    private ServletContextHandler contextHandler = null;

    public static ServerStorage getInstance() {
        return ourInstance;
    }

    public void start(Properties properties) throws Exception {
        String enabled = properties.getProperty("server.enabled", "true");
        if (!enabled.equals("true")) {
            LOG.warn("Server is not included");
            return;
        }

        GroupActor actor = DataStorage.getInstance().getActor();

        Integer port = Integer.valueOf(properties.getProperty("server.port"));
        String host = properties.getProperty("server.host");

        confirmationServer = properties.getProperty("server.confirmation");

        HandlerCollection handlers = new HandlerCollection();
        ConfirmationCodeRequestHandler confirmationCodeRequestHandler = null;

        VkApiClient vk = VkClientStorage.getInstance().vk();
        GetCallbackServersResponse getCallbackServersResponse = vk.groups().getCallbackServers(actor).execute();
        CallbackServer callbackServer = isServerExist(getCallbackServersResponse.getItems(), host);

        if (callbackServer == null) {
            GetCallbackConfirmationCodeResponse getCallbackConfirmationCodeResponse = vk.groups().getCallbackConfirmationCode(actor).execute();
            String confirmationCode = getCallbackConfirmationCodeResponse.getCode();
//            String confirmationCode = getConfirmationServer();
            confirmationCodeRequestHandler = new ConfirmationCodeRequestHandler(confirmationCode);
        }

        CallbackRequestHandler callbackRequestHandler = new CallbackRequestHandler();

        if (callbackServer == null) {
            handlers.setHandlers(new Handler[]{confirmationCodeRequestHandler, callbackRequestHandler});
        } else {
            handlers.setHandlers(new Handler[]{callbackRequestHandler}); //temp solution
        }

        Server server = new Server(port);
        server.setHandler(handlers);

        server.start();

        if (callbackServer == null) {
            AddCallbackServerResponse addServerResponse = vk.groups().addCallbackServer(actor, host, "Strikeball Bot").execute();
            Integer serverId = addServerResponse.getServerId();
            vk.groups().setCallbackSettings(actor, serverId).messageNew(true).execute();
        }

//        server.join();
    }

    private static CallbackServer isServerExist(List<CallbackServer> items, String host) {
        for (CallbackServer callbackServer : items) {
            if (callbackServer.getUrl().equals(host)) {
                return callbackServer;
            }
        }

        return null;
    }

    //Возращает текущий сервер
    public Server getServer() {
        return server;
    }

    public String getConfirmationServer() {
        return confirmationServer;
    }

}
