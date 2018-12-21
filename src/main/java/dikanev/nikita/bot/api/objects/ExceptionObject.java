package dikanev.nikita.bot.api.objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dikanev.nikita.bot.api.exceptions.ApiException;

public class ExceptionObject extends ApiObject {

    private String description;

    private String message;

    private Integer code;

    public ExceptionObject() {
        super("error");
    }

    public ExceptionObject(ApiException ex) {
        super("error");

        this.code = ex.getCode();
        this.message = ex.getMessage();
        this.description = ex.getDescription();
    }

    public static ExceptionObject empty() {
        return new ExceptionObject();
    }

    public String getDescription() {
        return description;
    }

    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public void init(JsonElement js) {
        if (js.isJsonObject()) {
            JsonObject jo = js.getAsJsonObject();
            if (jo.has("description")) {
                description = jo.get("description").getAsString();
            } else {
                description = null;
            }

            if (jo.has("message")) {
                message = jo.get("message").getAsString();
            } else {
                message = null;
            }

            if (jo.has("code")) {
                code = jo.get("code").getAsInt();
            } else {
                code = 0;
            }
        }
    }
}
