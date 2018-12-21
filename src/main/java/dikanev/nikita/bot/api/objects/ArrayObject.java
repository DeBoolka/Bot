package dikanev.nikita.bot.api.objects;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class ArrayObject extends ApiObject {

    private Gson gson = new Gson();

    private String typeObjects = null;

    private JsonArray objects;

    private ArrayObject() {
        super("array");
    }

    public static ArrayObject empty(){
        return new ArrayObject();
    }

    @Override
    public void init(JsonElement js) {
        if (js.isJsonObject()) {
            JsonObject obj = js.getAsJsonObject();
            if (obj.has("typeObjects")) {
                typeObjects = obj.get("typeObjects").getAsString();
            }

            if (obj.has("objects")) {
                objects = obj.get("objects").getAsJsonArray();
            }
        }
    }

    public String getTypeObjects() {
        return typeObjects;
    }

    public JsonArray getObjects() {
        return objects;
    }

    public List<JsonElement> toList() {
        List<JsonElement> lst = new ArrayList<>();
        objects.forEach(lst::add);
        return lst;
    }

    public <T> List<T> toList(Class<T> clazz) {
        List<T> lst = new ArrayList<>();
        objects.forEach(it -> lst.add(gson.fromJson(it, clazz)));
        return lst;
    }
}
