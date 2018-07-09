package dikanev.nikita.bot.api.objects;

import dikanev.nikita.bot.api.objects.ApiObject;

import java.util.ArrayList;
import java.util.List;

public class ArrayObject extends ApiObject {

    private String typeObjects;

    private List<ApiObject> objects = new ArrayList<>();

    public ArrayObject(String typeObjects, List<ApiObject> objects) {
        super("array");

        this.typeObjects = typeObjects;
        this.objects = objects;
    }

    public ArrayObject(String typeObjects) {
        super("array");

        this.typeObjects = typeObjects;
    }

    public String getTypeObjects() {
        return typeObjects;
    }

    public List<ApiObject> getObjects() {
        return objects;
    }

    public void setObjects(List<ApiObject> objects) {
        this.objects = objects;
    }

    public void add(ApiObject object) {
        objects.add(object);
    }

    public void add(List<ApiObject> objects) {
        objects.addAll(objects);
    }

    public ApiObject get(int index) {
        return objects.get(index);
    }
}
