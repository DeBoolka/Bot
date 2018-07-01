package dikanev.nikita.bot.api.exceptions;

public class NoAccessException extends ApiException {
    public NoAccessException(String message) {
        super(403, "The user does not have access to this operation", message);
    }
}
