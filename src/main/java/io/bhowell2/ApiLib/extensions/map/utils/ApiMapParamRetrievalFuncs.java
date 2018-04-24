package io.bhowell2.ApiLib.extensions.map.utils;

import io.bhowell2.ApiLib.ParamRetrievalFunc;

import java.util.Map;
import java.util.Objects;

/**
 * Most of these are based on Vertx's JsonObject.
 * @author Blake Howell
 */
public class ApiMapParamRetrievalFuncs {

    public static <T> ParamRetrievalFunc<T, Map<String, Object>> typeFromMap(Class<T> type) {
        return typeFromMap(type, false, false);
    }

    @SuppressWarnings("unchecked")
    public static <T> ParamRetrievalFunc<T, Map<String, Object>> typeFromMap(Class<T> type, boolean parseIfString) {
        return typeFromMap(type, parseIfString, false);
    }

    @SuppressWarnings("unchecked")
    public static <T> ParamRetrievalFunc<T, Map<String, Object>> typeFromMap(Class<T> type, boolean parseIfString, boolean replaceCastInMap) {
        if (type.equals(Double.class)) {
            return (ParamRetrievalFunc<T, Map<String, Object>>)getDoubleFromMap(parseIfString, replaceCastInMap);
        } else if (type.equals(Integer.class)) {
            return (ParamRetrievalFunc<T, Map<String, Object>>)getIntegerFromMap(parseIfString, replaceCastInMap);
        } else if (type.equals(String.class)) {
            return (ParamRetrievalFunc<T, Map<String, Object>>)STRING_FROM_MAP;
        } else {
            return (key, map) -> type.cast(map.get(key));
        }
    }

    public static ParamRetrievalFunc<Integer, Map<String, Object>> getIntegerFromMap() {
        return getIntegerFromMap(false, false);
    }

     public static ParamRetrievalFunc<Integer, Map<String, Object>> getIntegerFromMap(boolean parseIfString) {
         return getIntegerFromMap(parseIfString, false);
     }

    public static ParamRetrievalFunc<Integer, Map<String, Object>> getIntegerFromMap(boolean parseIfString, boolean
        replaceCastInMap) {
        return (key, map) -> {
            Objects.requireNonNull(key);
            Object o = map.get(key);
            if (o == null) {
                return null;
            } else if (o instanceof String && parseIfString) {
                Integer parsed = Integer.parseInt((String) o);
                if (replaceCastInMap) {
                    map.put(key, parsed);
                }
                return parsed;
            } else if (o instanceof Integer) {
                return (Integer)o;
            } else {
                Integer i = ((Number)o).intValue();
                if (replaceCastInMap) {
                    map.put(key, i);
                }
                return i;
            }
        };
    }

    public static ParamRetrievalFunc<Double, Map<String, Object>> getDoubleFromMap() {
        return getDoubleFromMap(false, false);
    }

    public static ParamRetrievalFunc<Double, Map<String, Object>> getDoubleFromMap(boolean parseIfString) {
        return getDoubleFromMap(parseIfString, false);
    }

    public static ParamRetrievalFunc<Double, Map<String, Object>> getDoubleFromMap(boolean parseIfString, boolean
        replaceCastInMap) {
        return (key, map) -> {
            Objects.requireNonNull(key);
            Object o = map.get(key);
            if (o == null) {
                return null;
            } else if (o instanceof String && parseIfString) {
                Double parsed = Double.parseDouble((String) o);
                if (replaceCastInMap) {
                    map.put(key, parsed);
                }
                return parsed;
            } else if (o instanceof Double) {
                return (Double)o;
            } else {
                Double d = ((Number) o).doubleValue();
                if (replaceCastInMap) {
                    map.put(key, d);
                }
                return d;
            }
        };
    }

    public static ParamRetrievalFunc<String, Map<String, Object>> STRING_FROM_MAP = (key, map) -> (String) map.get(key);

    @SuppressWarnings("unchecked")
    public static ParamRetrievalFunc<Map<String, Object>, Map<String, Object>> INNER_MAP_FROM_MAP = (key, map) -> (Map<String, Object>) map
        .get(key);

    public static ParamRetrievalFunc<Map<String, Object>, Map<String, Object>> RETURN_SELF_MAP = (key, map) -> map;

}
