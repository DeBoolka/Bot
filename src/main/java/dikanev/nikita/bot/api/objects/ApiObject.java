package dikanev.nikita.bot.api.objects;

import com.google.gson.JsonElement;

public abstract class ApiObject {

    private String type;

    protected ApiObject(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public abstract void init(JsonElement js);
}
