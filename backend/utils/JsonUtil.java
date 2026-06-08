package utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Minimal JSON helper so the project stays dependency-free. It covers exactly
 * what the dorm_LINK REST endpoints need:
 *   - building objects / arrays for responses (JsonUtil.obj()/arr())
 *   - parsing a flat JSON request body into a Map (JsonUtil.parse())
 *
 * For richer needs, drop Gson or Jackson into WEB-INF/lib and swap this out;
 * the DAO/service layers do not depend on the JSON representation.
 */
public final class JsonUtil {

    private JsonUtil() { }

    /* ---------------- building ---------------- */

    public static String escape(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n");  break;
                case '\r': sb.append("\\r");  break;
                case '\t': sb.append("\\t");  break;
                default:
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String value(Object v) {
        if (v == null) return "null";
        if (v instanceof Number || v instanceof Boolean) return v.toString();
        if (v instanceof Json) return v.toString();          // already-built fragment
        return "\"" + escape(v.toString()) + "\"";
    }

    /** Fluent object builder: JsonUtil.obj().put("a",1).put("b","x").toString() */
    public static Json obj() { return new Json(); }

    /** Build a JSON array from a list of pre-built Json objects or scalar values. */
    public static String arr(List<?> items) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(value(items.get(i)));
        }
        return sb.append("]").toString();
    }

    /** Wrap rows under {"data":[...]} which the frontend's get() unwraps. */
    public static String data(List<?> rows) {
        return "{\"data\":" + arr(rows) + "}";
    }

    public static String message(String msg) {
        return obj().put("message", msg).toString();
    }

    /** Fluent JSON object. */
    public static final class Json {
        private final Map<String, Object> map = new LinkedHashMap<>();
        public Json put(String k, Object v) { map.put(k, v); return this; }
        @Override public String toString() {
            StringBuilder sb = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<String, Object> e : map.entrySet()) {
                if (!first) sb.append(",");
                first = false;
                sb.append("\"").append(escape(e.getKey())).append("\":").append(value(e.getValue()));
            }
            return sb.append("}").toString();
        }
    }

    /* ---------------- parsing (flat objects) ---------------- */

    /**
     * Parses a flat JSON object body into String values (numbers/booleans kept
     * as their literal text; callers convert as needed). Nested objects/arrays
     * are not required by any endpoint and are ignored.
     */
    public static Map<String, String> parse(String json) {
        Map<String, String> out = new LinkedHashMap<>();
        if (json == null) return out;
        json = json.trim();
        if (json.isEmpty()) return out;
        int i = 0, n = json.length();
        // skip opening brace
        while (i < n && json.charAt(i) != '{') i++;
        i++;
        while (i < n) {
            i = skipWs(json, i);
            if (i >= n || json.charAt(i) == '}') break;
            // key
            if (json.charAt(i) != '"') { i++; continue; }
            StringBuilder key = new StringBuilder();
            i = readString(json, i + 1, key);
            i = skipWs(json, i);
            if (i < n && json.charAt(i) == ':') i++;
            i = skipWs(json, i);
            // value
            String val;
            if (i < n && json.charAt(i) == '"') {
                StringBuilder sb = new StringBuilder();
                i = readString(json, i + 1, sb);
                val = sb.toString();
            } else {
                int start = i;
                while (i < n && json.charAt(i) != ',' && json.charAt(i) != '}') i++;
                val = json.substring(start, i).trim();
                if ("null".equals(val)) val = null;
            }
            out.put(key.toString(), val);
            i = skipWs(json, i);
            if (i < n && json.charAt(i) == ',') i++;
        }
        return out;
    }

    private static int skipWs(String s, int i) {
        while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++;
        return i;
    }

    private static int readString(String s, int i, StringBuilder out) {
        while (i < s.length()) {
            char c = s.charAt(i++);
            if (c == '\\' && i < s.length()) {
                char e = s.charAt(i++);
                switch (e) {
                    case 'n': out.append('\n'); break;
                    case 'r': out.append('\r'); break;
                    case 't': out.append('\t'); break;
                    case '"': out.append('"');  break;
                    case '\\': out.append('\\'); break;
                    case '/': out.append('/');  break;
                    default: out.append(e);
                }
            } else if (c == '"') {
                break;
            } else {
                out.append(c);
            }
        }
        return i;
    }

    public static int asInt(Map<String, String> m, String key, int def) {
        try { return Integer.parseInt(m.get(key)); } catch (Exception e) { return def; }
    }
    public static double asDouble(Map<String, String> m, String key, double def) {
        try { return Double.parseDouble(m.get(key)); } catch (Exception e) { return def; }
    }
    public static List<String> keys() { return new ArrayList<>(); }
}
