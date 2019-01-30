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


    private Keyboard buttons = null;

    public VkCommand() {
    }

    public abstract CommandResponse init(CommandResponse resp, Parameter args) throws Exception;

    public abstract CommandResponse handle(CommandResponse resp, Parameter args) throws Exception;

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

    public static class SendMessage {
        private MessagesSendQuery message;

        public SendMessage() {
            message = VkClientStorage.getInstance().vk().messages()
                    .send(DataStorage.getInstance().getActor());
        }

        public SendMessage(int userId) {
            message = VkClientStorage.getInstance().vk().messages()
                    .send(DataStorage.getInstance().getActor())
                    .peerId(userId);
        }

        public SendMessage message(String message) {
            this.message.message(message);
            return this;
        }

        public SendMessage button(Keyboard keyboard) {
            message.unsafeParam("keyboard", keyboard.getKeyboard().toString());
            return this;
        }

        public SendMessage attachment(List<String> attachment) {
            message.attachment(attachment);
            return this;
        }

        public SendMessage peerId(int id) {
            message.peerId(id);
            return this;
        }

        public SendMessage execute() throws ClientException, ApiException {
            message.execute();
            return this;
        }

        public SendMessage saveExecute() {
            try {
                message.execute();
            } catch (ApiException | ClientException e) {
                LOG.warn("Failed send message.", e);
            }
            return this;
        }
    }

    public static class Keyboard {

        public static final String PRIMARY = "primary";
        public static final String POSITIVE = "positive";
        public static final String DEFAULT = "default";
        public static final String NEGATIVE = "negative";

        public boolean oneTime = true;

        private JsonArray rows = new JsonArray();

        private JsonArray currentRow = new JsonArray();

        public Keyboard() {
            rows.add(currentRow);
        }

        public Keyboard(boolean oneTime) {
            this.oneTime = oneTime;
            rows.add(currentRow);
        }

        public Keyboard setOneTime(boolean oneTime) {
            this.oneTime = oneTime;
            return this;
        }

        public JsonObject getKeyboard() {
            JsonObject keyboard = new JsonObject();
            keyboard.addProperty("one_time", oneTime);
            if (currentRow.size() == 0) {
                rows.remove(rows.size() - 1);
                keyboard.add("buttons", rows.deepCopy());
                rows.add(currentRow);
            } else {
                keyboard.add("buttons", rows);
            }

            return keyboard;
        }

        public Keyboard addButton(String label, String color, String payload) {
            if (currentRow.size() >= 4) {
                endl();
            }

            JsonObject action = new JsonObject();
            action.addProperty("type", "text");
            action.addProperty("label", label);
            if (payload != null) {
                JsonObject jsPayload = new JsonObject();
                jsPayload.addProperty("button", payload);
                action.addProperty("payload", jsPayload.toString());
            }

            JsonObject button = new JsonObject();
            button.add("action", action);
            button.addProperty("color", color);

            currentRow.add(button);
            return this;
        }

        public Keyboard endl() {
            if (rows.size() >= 10) {
                throw new IllegalStateException("Number of lines exceeded.");
            }

            currentRow = new JsonArray();
            rows.add(currentRow);
            return this;
        }

        public Keyboard prim(String message, String payload) {
            return addButton(message, PRIMARY, payload);
        }

        public Keyboard prim(String message) {
            return addButton(message, PRIMARY, null);
        }

        public Keyboard def(String message, String payload) {
            return addButton(message, DEFAULT, payload);
        }

        public Keyboard def(String message) {
            return addButton(message, DEFAULT, null);
        }

        public Keyboard positive(String message, String payload) {
            return addButton(message, POSITIVE, payload);
        }

        public Keyboard positive(String message) {
            return addButton(message, POSITIVE, null);
        }

        public Keyboard negative(String message, String payload) {
            return addButton(message, NEGATIVE, payload);
        }

        public Keyboard negative(String message) {
            return addButton(message, NEGATIVE, null);
        }
    }
}
