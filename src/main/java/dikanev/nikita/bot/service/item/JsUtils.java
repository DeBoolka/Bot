package dikanev.nikita.bot.service.item;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class JsUtils {
    public static JsonObject set(JsonObject js, String param, JsonElement val) {
        if (js.has(param)) {
            js.remove(param);
        }

        js.add(param, val);
        return js;
    }

    public static JsonObject set(JsonObject js, String param, boolean val) {
        return set(js, param, new JsonPrimitive(val));
    }

    public static JsonObject set(JsonObject js, String param, Number val) {
        return set(js, param, new JsonPrimitive(val));
    }

    public static JsonObject set(JsonObject js, String param, String val) {
        return set(js, param, new JsonPrimitive(val));
    }

    public static JsonObject set(JsonObject js, String param, Character val) {
        return set(js, param, new JsonPrimitive(val));
    }

}
