package dikanev.nikita.bot.model.callback.commands;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import dikanev.nikita.bot.model.callback.CommandResponse;
import dikanev.nikita.bot.model.storage.DataStorage;
import dikanev.nikita.bot.model.storage.clients.VkClientStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

public abstract class VkCommand {

    private static final Logger LOG = LoggerFactory.getLogger(VkCommand.class);

    private boolean isOneTime = true;

    private List<TK>[] buttons = null;

    public VkCommand() {
        buttons = setDefaultKeyboardButtons();
    }

    public abstract CommandResponse init(CommandResponse commandResponse) throws Exception;

    public abstract CommandResponse handle(CommandResponse commandResponse) throws Exception;

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

    public static void sendMessage(String msg, int vkId, boolean one_time, List<List<String>> buttons) throws ClientException, ApiException {
        JsonArray columnButton = new JsonArray();
        buttons.forEach(c -> {
            JsonArray rowButton = new JsonArray();
            c.forEach(r -> {
                JsonObject payload = new JsonObject();
                payload.addProperty("button", r);

                JsonObject action = new JsonObject();
                action.addProperty("type", "text");
                action.addProperty("payload", payload.toString());
                action.addProperty("label", r);

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


        System.out.println(messageObject);
        VkClientStorage.getInstance().vk().messages()
                .send(DataStorage.getInstance().getActor())
                .randomId(new Random().nextInt(10000))
                .message(msg)
                .unsafeParam("keyboard", messageObject)
                .peerId(vkId).execute();
    }

    public static void sendMessage(String msg, int vkId, List<List<String>> buttons) throws ClientException, ApiException {
        sendMessage(msg, vkId, false, buttons);
    }

    @SafeVarargs
    public static void sendMessage(String msg, int vkId, boolean one_time, List<TK>... buttons) throws ClientException, ApiException {
        JsonArray columnButton = new JsonArray();
        for (List<TK> c : buttons) {
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
                button.addProperty("color", r.getColor());

                rowButton.add(button);
            });
            columnButton.add(rowButton);
        }

        JsonObject messageObject = new JsonObject();
        messageObject.addProperty("one_time", one_time);
        messageObject.add("buttons", columnButton);


        VkClientStorage.getInstance().vk().messages()
                .send(DataStorage.getInstance().getActor())
                .randomId(new Random().nextInt(10000))
                .message(msg)
                .unsafeParam("keyboard", messageObject)
                .peerId(vkId).execute();
    }

    public static Map<String, String> getUrlParametr(String query) throws UnsupportedEncodingException {
        final Map<String, String> query_pairs = new LinkedHashMap<>();
        final String[] pairs = query.split("&");
        for (String pair : pairs) {
            final int idx = pair.indexOf("=");
            final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
            final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
            if (value != null) {
                query_pairs.put(key, value);
            }
        }

        return query_pairs;
    }

    protected static String mapToGetString(Map<String, String> params) {
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.append("&").append(entry.getKey()).append("=").append(entry.getValue() != null ? escape(entry.getValue()) : "");
        }

        return builder.toString();
    }

    private static String escape(String urlData) {
        try {
            return URLEncoder.encode(urlData, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
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

    public List<TK>[] setDefaultKeyboardButtons() {
        return null;
    }

    public static class TK {

        private String msg;

        private String color;

        private String payload = null;

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
