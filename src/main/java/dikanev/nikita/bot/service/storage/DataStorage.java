package dikanev.nikita.bot.service.storage;

import com.vk.api.sdk.client.actors.GroupActor;

import java.util.Properties;

public class DataStorage {

    private static DataStorage ourInstance = new DataStorage();

    private GroupActor actor = null;

    private String version = "1.0";

    public static DataStorage getInstance() {
        return ourInstance;
    }

    public void init(Properties properties) {

        actor = new GroupActor(Integer.parseInt(properties.getProperty("vk.group.id")), properties.getProperty("vk.group.token"));
        version = properties.getProperty("version");
    }

    public GroupActor getActor(){
        return actor;
    }

    public String getVersion(){
        return version;
    }
}
