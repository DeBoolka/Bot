package dikanev.nikita.bot.api.exceptions;

public class ConnectException extends ApiException {
    public ConnectException(String message) {
        super(503, "Service Unavailable", message);
    }
}
