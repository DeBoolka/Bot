package dikanev.nikita.bot.logic.callback.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.queries.messages.MessagesSendQuery;
import dikanev.nikita.bot.logic.callback.CommandResponse;
import dikanev.nikita.bot.service.client.parameter.Parameter;
import dikanev.nikita.bot.service.storage.DataStorage;
import dikanev.nikita.bot.service.storage.clients.VkClientStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public abstract class VkCommand {

    private static final Logger LOG = LoggerFactory.getLogger(VkCommand.class);

    private boolean isOneTime = true;

    private List<List<TK>> buttons = null;

    public VkCommand() {
        buttons = setDefaultKeyboardButtons();
    }

    public abstract CommandResponse init(CommandResponse cmdResp, Parameter args) throws Exception;

    public abstract CommandResponse handle(CommandResponse cmdResp, Parameter args) throws Exception;

    public static void sendMessage(String msg, int vkId) throws ClientException, ApiException{
        VkClientStorage.getInstance().vk().messages()
                .send(DataStorage.getInstance().getActor())
                .randomId(new Random().nextInt(10000))
                .message(msg)
                .peerId(vkId).execute();
    }

    public void sendMessage(String msg, int vkId, boolean defKeyboard) throws ClientException, ApiException {

        if (defKeyboard && buttons != null) {
            sendMessage(msg, vkId, isOneTime, buttons);
            return;
        }

        VkClientStorage.getInstance().vk().messages()
                .send(DataStorage.getInstance().getActor())
                .randomId(new Random().nextInt(10000))
                .message(msg)
                .peerId(vkId).execute();
    }

    public static void sendMessage(String msg, int vkId, boolean one_time, List<List<TK>> buttons) throws ClientException, ApiException {
        JsonObject jsButtons = buildButtons(one_time, buttons);

        VkClientStorage.getInstance().vk().messages()
                .send(DataStorage.getInstance().getActor())
                .randomId(new Random().nextInt(10000))
                .message(msg)
                .unsafeParam("keyboard", jsButtons)
                .peerId(vkId).execute();
    }

    private static JsonObject buildButtons(boolean one_time, List<List<TK>> buttons) {
        JsonArray columnButton = new JsonArray();
        buttons.forEach(c -> {
            JsonArray rowButton = new JsonArray();
            c.forEach(r -> {
                JsonObject payload = new JsonObject();
                payload.addProperty("button", r.getPayload());

                JsonObject action = new JsonObject();
                action.addProperty("type", "text");
                action.addProperty("payload", payload.toString());
                action.addProperty("label", r.getMsg());

                JsonObject button = new JsonObject();
                button.add("action", action);
                button.addProperty("color", "primary");

                rowButton.add(button);
            });
            columnButton.add(rowButton);
        });

        JsonObject messageObject = new JsonObject();
        messageObject.addProperty("one_time", one_time);
        messageObject.add("buttons", columnButton);

        return messageObject;
    }

    public static void sendMessage(String msg, int vkId, List<List<TK>> buttons) throws ClientException, ApiException {
        sendMessage(msg, vkId, false, buttons);
    }

    @SafeVarargs
    public static void sendMessage(String msg, int vkId, boolean one_time, List<TK>... buttons) throws ClientException, ApiException {
        List<List<TK>> btns = new ArrayList<>(List.of(buttons));
        sendMessage(msg, vkId, one_time, btns);
    }

    protected static boolean isTrue(String message) {
        message = message.trim().toLowerCase();
        switch (message) {
            case "yes":
            case "y":
            case "ys":
            case "true":
            case "да":
            case "ага":
            case "конечно":
            case "так точно":
                return true;
            default:
                return false;
        }
    }

    public List<List<TK>> setDefaultKeyboardButtons() {
        return null;
    }

    public static class MessageSend {
        private MessagesSendQuery message;

        public MessageSend(int userId) {
            message = VkClientStorage.getInstance().vk().messages()
                    .send(DataStorage.getInstance().getActor())
                    .peerId(userId);
        }

        public MessageSend message(String message) {
            this.message.message(message);
            return this;
        }

        public MessageSend button(boolean one_time, List<List<TK>> buttons) {
            message.unsafeParam("keyboard", buildButtons(one_time, buttons));
            return this;
        }

        public MessageSend attachment(List<String> attachment) {
            message.attachment(attachment);
            return this;
        }

        public MessageSend peerId(int id) {
            message.peerId(id);
            return this;
        }

        public MessageSend execute() throws ClientException, ApiException {
            message.execute();
            return this;
        }
    }

    public static class TK {

        private String msg;

        private String color;

        private String payload;

        public TK(String color, String text, String payload) {
            this.color = color;
            this.msg = text;
            this.payload = payload;
        }

        public static TK getPrimary() {
            return new TK("primary", "null", "unknown");
        }

        public static TK getPrimary(String text) {
            return new TK("primary", text, "unknown");
        }

        public static TK getPrimary(String text, String payload) {
            return new TK("primary", text, payload);
        }

        public static TK getDefault() {
            return new TK("default", "null", "unknown");
        }

        public static TK getDefault(String text) {
            return new TK("default", text, "unknown");
        }

        public static TK getDefault(String text, String payload) {
            return new TK("default", text, payload);
        }

        public static TK getNegative() {
            return new TK("negative", "null", "unknown");
        }

        public static TK getNegative(String text) {
            return new TK("negative", text, "unknown");
        }

        public static TK getNegative(String text, String payload) {
            return new TK("negative", text, payload);
        }

        public static TK getPositive() {
            return new TK("positive", "null", "unknown");
        }

        public static TK getPositive(String text) {
            return new TK("positive", text, "unknown");
        }

        public static TK getPositive(String text, String payload) {
            return new TK("positive", text, payload);
        }

        public TK text(String msg) {
            this.msg = msg;
            return this;
        }

        public TK text(String msg, String payload) {
            this.msg = msg;
            this.payload = payload;
            return this;
        }

        public String getMsg() {
            return msg != null ? msg : "null";
        }

        public String getColor() {
            return color;
        }

        public String getPayload() {
            if (payload == null) {
                return getMsg();
            }

            return payload;
        }
    }
}
