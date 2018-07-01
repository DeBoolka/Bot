package dikanev.nikita.bot.model.callback;

import com.sun.istack.NotNull;
import com.vk.api.sdk.objects.messages.Message;

public class CommandResponse {

    private int idUser;

    private int idCommand;

    private String args;

    private Message message;

    @NotNull
    private String text = "";

    private boolean isInit = true;

    private boolean isHandle = true;

    public CommandResponse(int idUser, int idCommand, String args, Message message) {
        this.idUser = idUser;
        this.idCommand = idCommand;
        this.args = args;
        this.message = message;
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

    public String getArgs() {
        return args;
    }

    public CommandResponse setArgs(String args) {
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

    public CommandResponse setText(@NotNull String text) {
        this.text = text;
        return this;
    }
}
