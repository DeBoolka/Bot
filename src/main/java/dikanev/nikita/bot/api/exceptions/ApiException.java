package dikanev.nikita.bot.api.exceptions;


import dikanev.nikita.bot.api.objects.ApiObject;
import dikanev.nikita.bot.api.objects.ExceptionObject;

public class ApiException extends Exception {

    private String description;

    private String message;

    private Integer code;

    public ApiException(Integer code, String description, String message) {
        this.description = description;
        this.code = code;
        this.message = message;
    }

    public ApiException(Integer code, String message) {
        this(code, "Unknown", message);
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
