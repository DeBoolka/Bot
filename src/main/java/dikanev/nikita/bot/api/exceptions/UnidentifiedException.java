package dikanev.nikita.bot.api.exceptions;

public class UnidentifiedException extends ApiException {
    public UnidentifiedException(String message) {
        super(500, "Unknown error.", message);
    }
}
