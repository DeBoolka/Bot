package dikanev.nikita.bot.service.item.Menu;

import com.google.gson.JsonObject;
import dikanev.nikita.bot.service.item.JsUtils;

import java.util.Set;

public class Bag {

    private JsonObject parameter;

    private Way way = null;

    public Bag(JsonObject data) {
        this.parameter = data;
    }

    public Way getWay() {
        return way;
    }

    public Bag setWay(Way way) {
        this.way = way;
        return this;
    }

    public String getCurrentPointName() {
        return parameter.has("currentPointName") ? parameter.getAsJsonPrimitive("currentPointName").getAsString() : null;
    }

    public Bag setCurrentPointName(String currentPointName) {
        JsUtils.set(parameter, "currentPointName", currentPointName);
        if (!parameter.has(currentPointName)) {
            setBeen(currentPointName, false);
        }
        return this;
    }

    public JsonObject getDataOfPoint() {
        return parameter.has("currentPointName") ?
                parameter.getAsJsonObject(parameter.getAsJsonPrimitive("currentPointName").getAsString()) : null;
    }

    public Point clear() {
        Set<String> keys = parameter.keySet();
        keys.forEach(parameter::remove);
        return null;
    }

    public String getCurrentWayName() {
        return parameter.has("currentWayName") ? parameter.getAsJsonPrimitive("currentWayName").getAsString() : null;
    }

    public void setCurrentWayName(String name) {
        JsUtils.set(parameter, "currentWayName", name);
    }

    public Bag setBeen(String pointName, boolean isBeen) {
        if (!parameter.has(pointName)) {
            JsonObject jsPointData = new JsonObject();
            jsPointData.addProperty("isBeen", false);
            JsUtils.set(parameter, pointName, jsPointData);
        } else {
            JsUtils.set(parameter.getAsJsonObject(pointName), "isBeen", false);
        }
        return this;
    }
}
