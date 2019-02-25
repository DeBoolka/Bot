package dikanev.nikita.bot.api.exceptions;


public class ApiException extends Exception {

    private String description;

    private String message;

    private Integer code;

    public int serverCode = -1;

    public ApiException(Integer code, String description, String message) {
        this.description = description;
        this.code = code;
        this.message = message;
    }

    public ApiException(Integer code, String message) {
        this(code, "Unknown", message);
    }

    public ApiException(int code, String description, String message, int serverCode) {
        this(code, description, message);
        this.serverCode = serverCode;
    }

    public String getDescription() {
        return description;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return description + " (" + code + "): " + message;
    }
}
