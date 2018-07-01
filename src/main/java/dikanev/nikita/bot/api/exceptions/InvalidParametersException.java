package dikanev.nikita.bot.api.exceptions;

public class InvalidParametersException extends ApiException {
    public InvalidParametersException(String message) {
        super(400, "Invalid parameters.", message);
    }
}
