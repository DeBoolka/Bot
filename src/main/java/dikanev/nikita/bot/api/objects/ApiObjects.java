package dikanev.nikita.bot.api.objects;

import dikanev.nikita.bot.api.exceptions.NotFoundException;

public enum ApiObjects {
    //При добавлении сюда не забудте добавить в getObjectClass(String)
    EXCEPTION(ExceptionObject.class),
    GROUP(GroupObject.class),
    USER(UserObject.class),
    MESSAGE(MessageObject.class);

    private Class<? extends ApiObject> objectClass;

    ApiObjects(Class<? extends ApiObject> objectClass) {
        this.objectClass = objectClass;
    }

    public Class<? extends ApiObject> getObjectClass() {
        return objectClass;
    }

    public static Class<? extends ApiObject> getObjectClass(String name) throws NotFoundException {
        switch (name.toLowerCase()) {
            case "error":
                return EXCEPTION.getObjectClass();

            case "group":
                return GROUP.getObjectClass();

            case "user":
                return USER.getObjectClass();

            case "message":
                return MESSAGE.getObjectClass();

            default:
                throw new NotFoundException("Object not found");
        }
    }
}
