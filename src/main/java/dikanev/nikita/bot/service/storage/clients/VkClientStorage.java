package dikanev.nikita.bot.service.storage.clients;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VkClientStorage {

    private static final Logger LOG = LoggerFactory.getLogger(VkClientStorage.class);

    private VkApiClient vk;

    private static VkClientStorage ourInstance = new VkClientStorage();

    public static VkClientStorage getInstance() {
        return ourInstance;
    }

    private VkClientStorage() {
        TransportClient client = HttpTransportClient.getInstance();
        vk = new VkApiClient(client);
    }

    public VkApiClient vk(){
        return vk;
    }
}
