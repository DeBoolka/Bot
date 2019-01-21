package dikanev.nikita.bot.api.objects;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dikanev.nikita.bot.api.item.Ammunition;
import dikanev.nikita.bot.api.item.PhotoCore;

public class AmmunitionObject extends ApiObject {

    private static Gson gson = new Gson();

    public static Ammunition object = new Ammunition();

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
                object.id = root.get("id").getAsInt();
            }
            if (root.has("ownerId")) {
                object.ownerId = root.get("ownerId").getAsInt();
            }
            if (root.has("name")) {
                object.name = root.get("name").getAsString();
            }
            if (root.has("photos")) {
                object.photos = gson.fromJson(root.get("photos"), PhotoCore[].class);
            }
        }
    }
}
