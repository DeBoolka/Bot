package dikanev.nikita.bot.api.objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class UserObject extends ApiObject {

    private int id = 0;

    private int idGroup = 0;

    private String s_name = null;

    private String name = null;

    private UserObject() {
        super("user");
    }

    public UserObject(int id, int idGroup, String s_name, String name) {
        super("user");

        this.id = id;
        this.idGroup = idGroup;
        this.s_name = s_name;
        this.name = name;
    }

    public static UserObject empty() {
        return new UserObject();
    }

    public int getId() {
        return id;
    }

    public int getIdGroup() {
        return idGroup;
    }

    public String getS_name() {
        return s_name;
    }


    public String getName() {
        return name;
    }

    @Override
    public void init(JsonElement js) {
        if (js.isJsonObject()) {
            JsonObject root = js.getAsJsonObject();
            if (root.has("id")) {
                id = root.get("id").getAsInt();
            }
            if (root.has("idGroup")) {
                idGroup = root.get("idGroup").getAsInt();
            }
            if (root.has("s_name")) {
                s_name = root.get("s_name").getAsString();
            }
            if (root.has("name")) {
                name = root.get("name").getAsString();
            }
        }
    }
}
