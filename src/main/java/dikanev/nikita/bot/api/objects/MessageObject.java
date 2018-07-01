package dikanev.nikita.bot.api.objects;

public class MessageObject extends ApiObject {

    private String message;

    public MessageObject(String message) {
        super("message");

        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
