package dikanev.nikita.bot.api.exceptions;

public class ConfirmationException extends ApiException {
    public ConfirmationException(String message) {
        super(409, "Confirmation", message);
    }
}
