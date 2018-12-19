package dikanev.nikita.bot.service.client.parameter;

import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class JsonParameter implements Parameter {

    private static final Logger LOG = LoggerFactory.getLogger(JsonParameter.class);

    private static JsonParser parser = new JsonParser();
    private static Gson gson = new Gson();

    private JsonObject parameters;
    private String transData = null;

    public JsonParameter(){
        parameters = new JsonObject();
    }

    public JsonParameter(String parameters) {
        this.parameters = parser.parse(parameters).getAsJsonObject();
    }

    public JsonParameter(JsonElement parameters) {
        this.parameters = parameters.getAsJsonObject();
    }

    public <T> JsonParameter(T parameters) {
        this.parameters = parser.parse(gson.toJson(parameters)).getAsJsonObject();
    }

    @Override
    public Parameter getParameter(String parameter) {
        JsonElement je = this.parameters.get(parameter);
        if (je == null || !je.isJsonObject()) {
            return this;
        }

        return new JsonParameter(parameters.get(parameter));
    }

    @Override
    public String getContent() {
        return parameters.toString();
    }

    @Override
    public List<String> get(String param) {
        return  getOrDefault(param, null);
    }

    @Override
    public List<String> getOrDefault(String param, List<String> def) {
        JsonElement je = parameters.get(param);
        if (je == null) {
            return def;
        }

        if (je.isJsonPrimitive()) {
            return new ArrayList<>(List.of(je.getAsString()));
        } else if (je.isJsonArray()) {
            List<String> arr = new ArrayList<>();
            je.getAsJsonArray().forEach(it -> arr.add(it.getAsString()));
            return arr;
        }

        return def;
    }

    @Override
    public List<Integer> getInt(String param) {
        return getIntOrDefault(param, null);
    }

    @Override
    public List<Integer> getIntOrDefault(String param, List<Integer> def) {
        JsonElement je = parameters.get(param);
        if (je == null) {
            return def;
        }

        if (je.isJsonPrimitive()) {
            return new ArrayList<>(List.of(Integer.valueOf(je.getAsString())));
        } else if (je.isJsonArray()) {
            List<Integer> arr = new ArrayList<>();
            je.getAsJsonArray().forEach(it -> arr.add(Integer.valueOf(it.getAsString())));
            return arr;
        }

        return def;
    }

    @Override
    public String getF(String param) {
        return getFOrDefault(param, null);
    }

    @Override
    public String getFOrDefault(String param, String def) {
        JsonElement je = parameters.get(param);
        if (je == null) {
            return def;
        }

        if (je.isJsonPrimitive()) {
            return je.getAsString();
        } else if (je.isJsonArray()) {
            je = je.getAsJsonArray().get(0);
            return je.getAsString();
        }

        return def;
    }

    @Override
    public int getIntF(String param) throws NoSuchFieldException {
        JsonElement je = parameters.get(param);
        if (je == null) {
            throw new NoSuchFieldException("Parameter not found.");
        }

        if (je.isJsonPrimitive()) {
            return Integer.valueOf(je.getAsString());
        } else if (je.isJsonArray()) {
            je = je.getAsJsonArray().get(0);
            return Integer.valueOf(je.getAsString());
        }

        throw new NoSuchFieldException("Parameter not found.");
    }

    @Override
    public int getIntFOrDefault(String param, int def) {
        JsonElement je = parameters.get(param);
        if (je == null) {
            return def;
        }

        if (je.isJsonPrimitive()) {
            return Integer.valueOf(je.getAsString());
        } else if (je.isJsonArray()) {
            je = je.getAsJsonArray().get(0);
            return Integer.valueOf(je.getAsString());
        }

        return def;
    }

    @Override
    public Parameter set(String parameters) {
        this.parameters = parser.parse(parameters).getAsJsonObject();
        return this;
    }

    @Override
    public Parameter set(String param, String val) {
        parameters.remove(param);
        JsonArray ja = new JsonArray();
        ja.add(val);
        parameters.add(param, ja);
        return this;
    }

    @Override
    public Parameter set(String param, List<String> val) {
        parameters.remove(param);
        JsonArray ja = new JsonArray();
        val.forEach(ja::add);
        parameters.add(param, ja);
        return this;
    }

    @Override
    public Parameter add(String param, String val) {
        JsonElement je = parameters.remove(param);
        if (je == null) {
            je = new JsonArray();
        }

        if (je.isJsonArray()) {
            je.getAsJsonArray().add(val);
        } else{
            JsonArray ja = new JsonArray();
            ja.add(je);
            ja.add(val);
            je = ja;
        }

        parameters.add(param, je);
        return this;
    }

    @Override
    public Parameter add(String param, List<String> val) {
        JsonElement je = parameters.remove(param);
        if (je == null) {
            je = new JsonArray();
        }

        if (je.isJsonArray()) {
            JsonArray ja = je.getAsJsonArray();
            val.forEach(ja::add);
        } else{
            JsonArray ja = new JsonArray();
            ja.add(je);

            JsonArray jb = je.getAsJsonArray();
            val.forEach(jb::add);

            ja.add(jb);
            je = ja;
        }

        parameters.add(param, je);
        return this;
    }

    @Override
    public boolean contains(String param) {
        return parameters.get(param) != null;
    }

    @Override
    public boolean contains(String... params) {
        for (String param : params) {
            if (parameters.get(param) != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(List<String> params) {
        for (String param : params) {
            if (parameters.get(param) == null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsAll(String... params) {
        return containsAll(List.of(params));
    }

    @Override
    public boolean containsAllVal(String param, List<String> vals) {
        List<String> lst = get(param);
        if (lst == null || vals == null) {
            return false;
        }

        for (String temp : vals) {
            if (!lst.contains(temp)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsVal(String param, String val) {
        List<String> lst = get(param);
        if (lst == null) {
            return false;
        }

        return lst.contains(val);
    }

    @Override
    public boolean containsVal(String param, int val) {
        return containsVal(param, String.valueOf(val));
    }

    @Override
    public Parameter clear() {
        parameters = new JsonObject();
        return this;
    }

    @Override
    public Parameter remove(String param) {
        parameters.remove(param);
        return this;
    }

    @Override
    public Parameter remove(String param, int index) {
        List<String> arr = get(param);
        if (arr != null) {
            arr.remove(index);
            if (!arr.isEmpty()) {
                set(param, arr);
            } else {
                parameters.remove(param);
            }
        }
        return this;
    }

    @Override
    public Parameter remove(String param, String val) {
        List<String> arr = get(param);
        if (arr != null) {
            arr.remove(val);
            if (!arr.isEmpty()) {
                set(param, arr);
            } else {
                parameters.remove(param);
            }
        }
        return this;
    }

    @Override
    public Parameter transaction() {
        transData = getContent();
        return this;
    }

    @Override
    public Parameter rollback() {
        if (transData != null) {
            set(transData);
        }

        return this;
    }

    @Override
    public Parameter endTransaction() {
        transData = null;
        return this;
    }

    @Override
    public boolean isEmpty() {
        return parameters.keySet().isEmpty();
    }

    @Override
    public boolean isInt(String paramIn) {
        List<String> paramList = get(paramIn);
        if (paramList == null || paramList.isEmpty()) {
            return false;
        }

        for (String param : paramList) {
            if (param.isEmpty()) {
                return false;
            }

            for (char ch : param.toCharArray()) {
                if (ch < '0' || ch > '9') {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public boolean isIntF(String paramIn) {
        String param = getF(paramIn);
        if (param == null || param.isEmpty()) {
            return false;
        }

        for (char ch : param.toCharArray()) {
            if (ch < '0' || ch > '9') {
                return false;
            }
        }

        return true;
    }
}
