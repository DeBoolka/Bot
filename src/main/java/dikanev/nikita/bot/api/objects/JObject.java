package dikanev.nikita.bot.api.objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.checkerframework.checker.nullness.compatqual.NonNullType;

public class JObject {

    private static JsonParser parser = new JsonParser();

    private JsonElement element;

    private String type;

    public JObject(String type, boolean flag) {
        element = new JsonObject();
        this.type = type;
    }

    public JObject(JsonElement element) {
        this.element = element;
        setType();
    }

    public JObject(String jsonStr) {
        element = parser.parse(jsonStr);
        setType();
    }

    private void setType() {
        if (element.isJsonObject()) {
            JsonObject root = element.getAsJsonObject();
            if (root.has("type") && root.get("type").isJsonPrimitive()) {
                type = root.get("type").getAsString();
                return;
            }
        }
        type = "";
    }

    public JsonObject getObj() {
        if (element.isJsonObject()) {
            return element.getAsJsonObject();
        }
        throw new IllegalStateException("Json element is not object. Element: " + element.toString());
    }

    public JsonElement getElement() {
        return element;
    }

    public String getType(){
        return type;
    }

    public <T extends ApiObject> T cast(@NonNullType T object) {
        object.init(element);
        return object;
    }
}
