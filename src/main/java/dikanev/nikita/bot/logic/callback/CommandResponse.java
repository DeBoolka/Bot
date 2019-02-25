package dikanev.nikita.bot.logic.callback;

import com.google.gson.JsonObject;
import com.vk.api.sdk.objects.messages.Message;
import dikanev.nikita.bot.service.client.parameter.Parameter;
import org.checkerframework.checker.nullness.compatqual.NonNullType;

public class CommandResponse {

    private int userId;

    private int idCommand;

    private Parameter args;

    private JsonObject state;

    private Message message;

    private JsonObject requestObject;

    @NonNullType
    private String text = "";

    private boolean isInit = true;

    private boolean isHandle = true;

    public CommandResponse(int userId, int idCommand, Parameter args, Message message, JsonObject requestObject) {
        this.userId = userId;
        this.idCommand = idCommand;
        this.message = message;
        this.requestObject = requestObject;
        this.args = args;
        setDefault();
    }

    public boolean isInit() {
        return isInit;
    }

    public CommandResponse setDefault(){
        isInit = true;
        isHandle = true;
        text = "";

        return this;
    }

    public CommandResponse setInit() {
        isInit = true;
        isHandle = false;
        return this;
    }

    public boolean isHandle() {
        return isHandle;
    }

    public CommandResponse setHandle() {
        isHandle = true;
        isInit = false;
        return this;
    }

    public boolean isTransit() {
        return isHandle || isInit;
    }

    public CommandResponse full() {
        isHandle = true;
        isInit = true;
        return this;
    }

    public CommandResponse finish(){
        isInit = false;
        isHandle = false;
        return this;
    }

    public int getIdCommand() {
        return idCommand;
    }

    public CommandResponse setIdCommand(int idCommand) {
        this.idCommand = idCommand;
        return this;
    }

    public Parameter getArgs() {
        return args;
    }

    public CommandResponse setArgs(String args) {
        this.args.set(args);
        return this;
    }

    public CommandResponse setArgs(Parameter args) {
        this.args = args;
        return this;
    }

    public Message getMessage() {
        return message;
    }

    public CommandResponse setMessage(Message message) {
        this.message = message;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public CommandResponse setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public String getText() {
        return text;
    }

    public CommandResponse setText(@NonNullType String text) {
        this.text = text;
        return this;
    }

    public JsonObject getRequestObject() {
        return requestObject;
    }

    public JsonObject getState() {
        return state;
    }

    public void setState(JsonObject state) {
        this.state = state;
    }
}
