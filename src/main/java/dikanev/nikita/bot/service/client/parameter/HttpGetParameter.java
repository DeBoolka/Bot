package dikanev.nikita.bot.service.client.parameter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpGetParameter implements Parameter {

    private static final Logger LOG = LoggerFactory.getLogger(HttpGetParameter.class);

    private Map<String, List<String>> parameters;

    private String transData = null;

    public HttpGetParameter() {
        this.parameters = new HashMap<>();
    }

    public HttpGetParameter(String parameters) throws UnsupportedEncodingException {
        init(parameters);
    }

    public HttpGetParameter(String key, List<String> parameter) {
        if (parameter == null || parameter.isEmpty()) {
            parameters = new HashMap<>();
        } else {
            this.parameters = new HashMap<>(Map.of(key, parameter));
        }
    }

    public HttpGetParameter(Map<String, String[]> parameterMap) {
        parameters = new HashMap<>();
        parameterMap.forEach((k, v) -> parameters.put(k, new ArrayList<>(List.of(v))));
    }

    @Override
    public Parameter getParameter(String parameter) {
        return new HttpGetParameter(parameter, parameters.get(parameter));
    }

    @Override
    public String getContent() {
        return mapToGetString(parameters);
    }

    @Override
    public List<String> get(String parameter) {
        return parameters.get(parameter);
    }


    @Override
    public List<String> getOrDefault(String key, List<String> def) {
        List<String> lst = get(key);
        return lst != null ? lst : def;
    }

    @Override
    public String getF(String parameter) {
        List<String> lst = parameters.get(parameter);
        if (lst != null && lst.size() > 0) {
            return lst.get(0);
        }
        return null;
    }

    @Override
    public String getFOrDefault(String parameter, String defaultValue) {
        String param = getF(parameter);
        return param != null ? param : defaultValue;
    }

    @Override
    public List<Integer> getInt(String param) {
        List<String> strList = get(param);
        if (strList == null) {
            return null;
        }

        List<Integer> intList = new ArrayList<>();
        strList.forEach(it -> intList.add(Integer.valueOf(it)));

        return intList;
    }

    @Override
    public List<Integer> getIntOrDefault(String param, List<Integer> def) {
        List<Integer> lst = getInt(param);
        return lst != null ? lst : def;
    }

    @Override
    public int getIntF(String param) throws NoSuchFieldException {
        String val = getF(param);
        if (val == null) {
            throw new NoSuchFieldException();
        }

        return Integer.valueOf(val);
    }

    @Override
    public int getIntFOrDefault(String param, int def) {
        try {
            return getIntF(param);
        } catch (NoSuchFieldException e) {
            return def;
        }
    }

    @Override
    public Parameter clear() {
        parameters.clear();
        return this;
    }

    @Override
    public Parameter set(String parameters) {
        try {
            init(parameters);
        } catch (UnsupportedEncodingException e) {
            LOG.warn("Failed init parameters.", e);
            this.parameters.clear();
        }
        return this;
    }

    @Override
    public Parameter set(String param, String val) {
        parameters.put(param, new ArrayList<>(List.of(val)));
        return this;
    }

    @Override
    public Parameter set(String param, List<String> val) {
        parameters.put(param, val);
        return this;
    }

    @Override
    public Parameter add(String param, String val) {
        List<String> lst = parameters.computeIfAbsent(param, k -> new ArrayList<>());
        lst.add(val);
        return this;
    }

    @Override
    public Parameter add(String param, List<String> val) {
        List<String> lst = parameters.computeIfAbsent(param, k -> new ArrayList<>());
        lst.addAll(val);
        return this;
    }

    @Override
    public boolean contains(String param) {
        return parameters.containsKey(param);
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
            if (!parameters.containsKey(param)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsAll(String[] params) {
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
    public Parameter remove(String param) {
        parameters.remove(param);
        return this;
    }

    @Override
    public Parameter remove(String param, int index) {
        try {
            List<String> lst = get(param);
            if (lst != null && lst.size() <= 1 && index == 0) {
                parameters.remove(param);
            } else if (lst != null) {
                lst.remove(index);
            }
        } catch (Exception ignore) {
        }

        return this;
    }

    @Override
    public Parameter remove(String param, String val) {
        try {
            List<String> lst = get(param);
            if (lst != null) {
                lst.remove(val);

                if (lst.isEmpty()) {
                    parameters.remove(param);
                }
            }
        } catch (Exception ignore) {
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
        try {
            if (transData != null) {
                init(transData);
            }
        } catch (UnsupportedEncodingException e) {
            LOG.warn("Failed rollback transaction.", e);
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
        return parameters.isEmpty();
    }

    @Override
    public boolean isInt(String paramIn) {
        List<String> paramList = parameters.get(paramIn);
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

    private static Map<String, List<String>> getMapFromHttpGet(String query) throws UnsupportedEncodingException {
        final Map<String, List<String>> query_pairs = new HashMap<>();
        final String[] pairs = query.split("&");
        for (String pair : pairs) {
            final int idx = pair.indexOf("=");
            final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
            final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
            if (value != null) {
                List<String> parameter = query_pairs.getOrDefault(key, new ArrayList<>());
                parameter.add(value);
                query_pairs.put(key, parameter);
            }
        }

        return query_pairs;
    }

    protected void init(String params) throws UnsupportedEncodingException {
        this.parameters = getMapFromHttpGet(params);
    }

    private static String mapToGetString(Map<String, List<String>> params) {
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<String, List<String>> entry : params.entrySet()) {
            for (String val : entry.getValue()) {
                builder.append("&").append(entry.getKey()).append("=").append(entry.getValue() != null ? escape(val) : "");
            }
        }

        return builder.toString();
    }

    private static String escape(String urlData) {
        try {
            return URLEncoder.encode(urlData, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
