package dikanev.nikita.bot.api.exceptions;

public class NotFoundException extends ApiException {
    public NotFoundException(String message) {
        super(404, "Not Found", message);
    }
}
