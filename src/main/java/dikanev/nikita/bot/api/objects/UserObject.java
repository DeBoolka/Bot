package dikanev.nikita.bot.api.objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class UserObject extends ApiObject {

    private int id = 0;

    private int idGroup = 0;

    private String sName = null;

    private String name = null;

    private UserObject() {
        super("user");
    }

    public UserObject(int id, int idGroup, String sName, String name) {
        super("user");

        this.id = id;
        this.idGroup = idGroup;
        this.sName = sName;
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

    public String getsName() {
        return sName;
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
            if (root.has("sName")) {
                sName = root.get("sName").getAsString();
            }
            if (root.has("name")) {
                name = root.get("name").getAsString();
            }
        }
    }
}
