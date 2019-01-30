package dikanev.nikita.bot.service.item.Menu;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dikanev.nikita.bot.logic.callback.CommandResponse;
import dikanev.nikita.bot.service.client.parameter.Parameter;
import dikanev.nikita.bot.service.item.JsUtils;
import org.apache.logging.log4j.core.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class Point {

    private static final Logger LOG = LoggerFactory.getLogger(Point.class);

    private String name;
    private Work in;
    private Work payload;

    public Point() {
    }

    public Point(String name, Work in, Work payload) {
        this.name = name;
        this.in = in;
        this.payload = payload;
    }

    public void in(Work in) {
        this.in = in;
    }

    public void payload(Work payload) {
        this.payload = payload;
    }

    public Point work(CommandResponse resp, Bag bag) throws Exception {
        JsonObject jsDataOfPoint = bag.getDataOfPoint();
        if (jsDataOfPoint == null) {
            LOG.error("Not found data of point: " + bag.getCurrentPointName());
            throw new IllegalStateException("Not found data of point: " + bag.getCurrentPointName());
        }

        JsonPrimitive jsIsBeen = jsDataOfPoint.getAsJsonPrimitive("isBeen");
        if (jsIsBeen == null) {
            JsUtils.set(jsDataOfPoint, "isBeen", false);
        }

        if (!Objects.requireNonNull(jsIsBeen).getAsBoolean()) {
            JsUtils.set(jsDataOfPoint, "isBeen", true);
            return in.work(resp, resp.getArgs(), bag);
        }
        return payload.work(resp, resp.getArgs(), bag);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public interface Work {
        Point work(CommandResponse resp, Parameter param, Bag bag) throws Exception;
    }
}
