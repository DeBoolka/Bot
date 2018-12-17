package dikanev.nikita.bot.service.server;

import com.google.gson.Gson;
import dikanev.nikita.bot.api.objects.ArrayObject;
import dikanev.nikita.bot.api.objects.deserialization.ArrayJsonAdapter;
import org.junit.jupiter.api.Test;

class CallbackRequestHandlerTest {

    @Test
    void handle() {
        String body = "{\"type\":\"message_new\",\"object\":{\"date\":1529438942,\"from_id\":147952026,\"id\":3,\"out\":1,\"peer_id\":147952026,\"text\":\"Hey\",\"conversation_message_id\":3,\"fwd_messages\":[],\"important\":false,\"random_id\":0,\"attachments\":[],\"is_hidden\":false},\"group_id\":167918981}\n";
        body = "{\"typeObjects\":\"accessGroup\",\"objects\":[{\"idGroup\":2,\"command\":\"group/create\",\"access\":true,\"type\":\"accessGroup\"},{\"idGroup\":2,\"command\":\"group/delete\",\"access\":true,\"type\":\"accessGroup\"},{\"idGroup\":2,\"command\":\"group/access/\",\"access\":false,\"type\":\"accessGroup\"}],\"type\":\"array\"}";
        Gson gson = new Gson().newBuilder().registerTypeAdapter(ArrayObject.class, new ArrayJsonAdapter()).create();

        ArrayObject arrayObject = gson.fromJson(body, ArrayObject.class);

    }
}