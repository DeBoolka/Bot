package dikanev.nikita.bot.api.objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JsonObject extends ApiObject {

    private static JsonParser parser = new JsonParser();

    private JsonElement element;

    public JsonObject(JsonElement element) {
        super("unknown");

        this.element = element;
    }

    public JsonObject(String objectString) {
        super("unknown");
        element = parser.parse(objectString);
    }

    public JsonElement getElement() {
        return element;
    }
}
