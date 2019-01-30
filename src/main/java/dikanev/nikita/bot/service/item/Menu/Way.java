package dikanev.nikita.bot.service.item.Menu;

import com.google.gson.JsonObject;
import dikanev.nikita.bot.logic.callback.CommandResponse;

import java.util.HashMap;
import java.util.Map;

public class Way {

    private Map<String, Point> points = new HashMap<>();

    public Way() {
    }

    public Way(Point startPoint) {
        putPoint(startPoint);
    }

    public CommandResponse enterToWay(CommandResponse resp, Bag bag) throws Exception {
        bag.setWay(this);
        Point point = this.getCurrentPoint(bag);
        resp.finish();

        while (point != null) {
            bag.setCurrentPointName(point.getName());
            point = point.work(resp, bag);
        }
        return resp;
    }

    public Point getCurrentPoint(Bag bag) {
        String currentPointName = bag.getCurrentPointName();
        if (currentPointName == null) {
            currentPointName = "start";
            bag.setCurrentPointName(currentPointName);
        }

        return this.getPointByName(currentPointName);
    }

    public Point getPointByName(String pointName) {
        return points.get(pointName);
    }

    public Way putPoint(Point point) {
        this.points.put(point.getName(), point);
        return this;
    }

}
