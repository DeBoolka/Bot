package dikanev.nikita.bot.model.storage.clients;

import dikanev.nikita.bot.api.exceptions.UnidentifiedException;
import dikanev.nikita.bot.client.core.CoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class CoreClientStorage {

    private static final Logger LOG = LoggerFactory.getLogger(CoreClientStorage.class);

    private static CoreClientStorage ourInstance = new CoreClientStorage();

    private CoreClient client;

    private String host;

    private String token;

    public static CoreClientStorage getInstance() {
        return ourInstance;
    }

    public static void init(Properties properties) throws Exception {
        String enabled = properties.getProperty("core.enabled", "true");
        if (!enabled.equals("true")) {
            LOG.warn("Core is not included");
            return;
        }

        String host = properties.getProperty("core.host");
        String pathApi = properties.getProperty("core.api.path");

        CoreClient client = new CoreClient(host, pathApi,
                properties.getProperty("http.keystore.type"), properties.getProperty("http.keystore.path"), properties.getProperty("http.keystore.password"), properties.getProperty("http.key.password"),
                properties.getProperty("http.truststore.type"), properties.getProperty("http.truststore.path"), properties.getProperty("http.truststore.password"));
        String token = properties.getProperty("core.token");

        getInstance().setHost(host);
        getInstance().setClient(client);
        getInstance().setToken(token);

        LOG.info("Create core client");
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public CoreClient getClient() {
        return client;
    }

    public void setClient(CoreClient client) {
        this.client = client;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
