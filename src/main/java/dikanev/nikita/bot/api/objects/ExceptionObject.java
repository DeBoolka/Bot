package dikanev.nikita.bot.api.objects;

import dikanev.nikita.bot.api.exceptions.ApiException;

public class ExceptionObject extends ApiObject {

    private String description;

    private String message;

    private Integer code;

    public ExceptionObject(ApiException ex) {
        super("error");

        this.code = ex.getCode();
        this.message = ex.getMessage();
        this.description = ex.getDescription();
    }

    public String getDescription() {
        return description;
    }

    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }
}
