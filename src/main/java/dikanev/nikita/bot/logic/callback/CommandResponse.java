package dikanev.nikita.bot.logic.callback;

import com.vk.api.sdk.objects.messages.Message;
import dikanev.nikita.bot.service.client.parameter.HttpGetParameter;
import dikanev.nikita.bot.service.client.parameter.Parameter;
import org.checkerframework.checker.nullness.compatqual.NonNullType;

public class CommandResponse {

    private int idUser;

    private int idCommand;

    private Parameter args;

    private Message message;

    @NonNullType
    private String text = "";

    private boolean isInit = true;

    private boolean isHandle = true;

    public CommandResponse(int idUser, int idCommand, Parameter args, Message message) {
        this.idUser = idUser;
        this.idCommand = idCommand;
        this.message = message;
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

    public int getIdUser() {
        return idUser;
    }

    public CommandResponse setIdUser(int idUser) {
        this.idUser = idUser;
        return this;
    }

    public String getText() {
        return text;
    }

    public CommandResponse setText(@NonNullType String text) {
        this.text = text;
        return this;
    }
}
