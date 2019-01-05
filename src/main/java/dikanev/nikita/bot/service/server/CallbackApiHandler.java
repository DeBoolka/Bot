package dikanev.nikita.bot.service.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vk.api.sdk.callback.CallbackApi;
import com.vk.api.sdk.objects.messages.Message;
import dikanev.nikita.bot.logic.callback.MessagesHandler;
import dikanev.nikita.bot.service.storage.ServerStorage;

public class CallbackApiHandler extends CallbackApi {

    private static JsonParser jsParser = new JsonParser();

    private JsonObject requestObject = null;

    @Override
    public boolean parse(String json) {
        JsonElement el = jsParser.parse(json);
        if (el.isJsonObject()) {
            requestObject = el.getAsJsonObject();
        }

        return super.parse(json);
    }

    @Override
    public boolean parse(JsonObject json) {
        requestObject = json;
        return super.parse(json);
    }

    @Override
    public void confirmation(Integer groupId) {
        CallbackRequestHandler.setRespMessage(ServerStorage.getInstance().getConfirmationServer());
    }

    @Override
    public void messageNew(Integer groupId, Message message) {
        MessagesHandler.parseMessage(groupId, message, requestObject);
    }
}
