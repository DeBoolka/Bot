package dikanev.nikita.bot.model.callback;

import com.vk.api.sdk.callback.CallbackApi;
import com.vk.api.sdk.objects.messages.Message;
import dikanev.nikita.bot.model.storage.ServerStorage;
import dikanev.nikita.bot.server.CallbackRequestHandler;

public class CallbackApiHandler extends CallbackApi {

    @Override
    public void confirmation(Integer groupId) {
        CallbackRequestHandler.setRespMessage(ServerStorage.getInstance().getConfirmationServer());
    }

    @Override
    public void messageNew(Integer groupId, Message message) {
        MessagesHandler.parseMessage(groupId, message);
    }
}
