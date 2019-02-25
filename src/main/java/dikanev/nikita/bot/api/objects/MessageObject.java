package dikanev.nikita.bot.api.objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MessageObject extends ApiObject {

    private String message;

    public MessageObject() {
        super("message");
    }

    public MessageObject(String message) {
        super("message");

        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public static MessageObject empty() {
        return new MessageObject();
    }

    @Override
    public void init(JsonElement js) {
        if (js.isJsonObject()) {
            JsonObject jo = js.getAsJsonObject();
            if (jo.has("message")) {
                message = jo.get("message").getAsString();
            } else {
                message = null;
            }
        }

    }
}
