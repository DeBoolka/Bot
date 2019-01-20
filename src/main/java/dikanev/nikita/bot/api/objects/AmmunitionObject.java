package dikanev.nikita.bot.api.objects;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dikanev.nikita.bot.api.item.PhotoCore;
import dikanev.nikita.bot.api.objects.ApiObject;

public class AmmunitionObject extends ApiObject {

    private static Gson gson = new Gson();

    public int id;

    public int ownerId;

    public String name;

    public PhotoCore[] photos;

    protected AmmunitionObject(String type) {
        super(type);
    }

    public AmmunitionObject() {
        super("ammunition");
    }

    public static AmmunitionObject empty() {
        return new AmmunitionObject();
    }

    @Override
    public void init(JsonElement js) {
        if (js.isJsonObject()) {
            JsonObject root = js.getAsJsonObject();
            if (root.has("id")) {
                id = root.get("id").getAsInt();
            }
            if (root.has("ownerId")) {
                ownerId = root.get("ownerId").getAsInt();
            }
            if (root.has("name")) {
                name = root.get("name").getAsString();
            }
            if (root.has("photos")) {
                photos = gson.fromJson(root.get("photos"), PhotoCore[].class);
            }
        }
    }
}
