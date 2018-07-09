package dikanev.nikita.bot.api.objects;

import dikanev.nikita.bot.api.exceptions.NotFoundException;

public enum ApiObjects {
    //При добавлении сюда не забудте добавить в getObjectClass(String)
    ACCESS_GROUP(AccessGroupObject.class, "accessgroup"),

    ARRAY(ArrayObject.class, "array"),
    ARRAY_ACCESS_OBJECT(ArrayObject.class, "array"),

    EXCEPTION(ExceptionObject.class, "error"),
    GROUP(GroupObject.class, "group"),
    MESSAGE(MessageObject.class, "message"),
    USER(UserObject.class, "user"),;

    private Class<? extends ApiObject> objectClass;

    private String objectTypeName;

    ApiObjects(Class<? extends ApiObject> objectClass, String objectTypeName) {
        this.objectClass = objectClass;
        this.objectTypeName = objectTypeName;
    }

    public Class<? extends ApiObject> getObjectClass() {
        return objectClass;
    }

    public String getObjectTypeName() {
        return objectTypeName;
    }

    public static Class<? extends ApiObject> getObjectClass(String name) throws NotFoundException {
        name = name.toLowerCase();
        for (ApiObjects object : ApiObjects.values()) {
            if (object.getObjectTypeName().equals(name)) {
                return object.getObjectClass();
            }
        }
        throw new NotFoundException("Object not found");
    }
}
