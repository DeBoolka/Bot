package dikanev.nikita.bot.service.server;

import com.vk.api.sdk.callback.CallbackApi;
import com.vk.api.sdk.objects.messages.Message;
import dikanev.nikita.bot.logic.callback.MessagesHandler;
import dikanev.nikita.bot.service.storage.ServerStorage;
import dikanev.nikita.bot.service.server.CallbackRequestHandler;

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
