package dikanev.nikita.bot.api.objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class SimpleObject extends ApiObject {
    JsonObject object;

    protected SimpleObject(String type) {
        super(type);
    }

    public static JsonObject getSimpleJsonObject(JObject req) {
        JsonObject js = req.getObj();
        if (!js.has("object")) {
            throw new IllegalStateException("This object is not SimpleObject");
        }
        return js.getAsJsonObject("object");
    }


    @Override
    public void init(JsonElement js) {
        if (!js.isJsonObject()) {
            return;
        }
        JsonObject jsObj = js.getAsJsonObject();
        if (jsObj.has("object")) {
            this.object = jsObj.getAsJsonObject("object");
        }
    }
}
