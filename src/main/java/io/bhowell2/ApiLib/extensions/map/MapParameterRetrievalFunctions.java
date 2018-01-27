package io.bhowell2.ApiLib.extensions.map;

import io.bhowell2.ApiLib.ParameterRetrievalFunction;

import java.util.Map;
import java.util.Objects;

/**
 * Most of these are copied and modifed from Vert.x's JsonObject.
 * @author Blake Howell
 */
public class MapParameterRetrievalFunctions {

    public static <T> ParameterRetrievalFunction<T, Map<String, Object>> typeFromMap(Class<T> type) {
        return typeFromMap(type, false, false);
    }

    @SuppressWarnings("unchecked")
    public static <T> ParameterRetrievalFunction<T, Map<String, Object>> typeFromMap(Class<T> type, boolean attemptParseFromString) {
        return typeFromMap(type, attemptParseFromString, false);
    }

    @SuppressWarnings("unchecked")
    public static <T> ParameterRetrievalFunction<T, Map<String, Object>> typeFromMap(Class<T> type, boolean attemptParseFromString, boolean
        replaceInMap) {
        if (type.equals(Double.class)) {
            return (ParameterRetrievalFunction<T, Map<String, Object>>)getDoubleFromMap(attemptParseFromString, replaceInMap);
        } else if (type.equals(Integer.class)) {
            return (ParameterRetrievalFunction<T, Map<String, Object>>)getIntegerFromMap(attemptParseFromString, replaceInMap);
        } else if (type.equals(String.class)) {
            return (ParameterRetrievalFunction<T, Map<String, Object>>)STRING_FROM_MAP;
        } else {
            return (key, map) -> type.cast(map.get(key));
        }
    }

    public static ParameterRetrievalFunction<Integer, Map<String, Object>> getIntegerFromMap() {
        return getIntegerFromMap(false, false);
    }

     public static ParameterRetrievalFunction<Integer, Map<String, Object>> getIntegerFromMap(boolean attemptParseFromString) {
         return getIntegerFromMap(attemptParseFromString, false);
     }

    public static ParameterRetrievalFunction<Integer, Map<String, Object>> getIntegerFromMap(boolean attemptParseFromString, boolean
        replaceParsedInMap) {
        return (key, map) -> {
            Objects.requireNonNull(key);
            Object o = map.get(key);
            if (o == null) {
                return null;
            } else if (o instanceof String && attemptParseFromString) {
                Integer parsed = Integer.parseInt((String) o);
                if (replaceParsedInMap) {
                    map.put(key, parsed);
                }
                return parsed;
            } else if (o instanceof Integer) {
                return (Integer)o;
            } else {
                Number num = (Number)o;
                return num.intValue();
            }
        };
    }

    public static ParameterRetrievalFunction<Double, Map<String, Object>> getDoubleFromMap() {
        return getDoubleFromMap(false, false);
    }

    public static ParameterRetrievalFunction<Double, Map<String, Object>> getDoubleFromMap(boolean attemptParseFromString) {
        return getDoubleFromMap(attemptParseFromString, false);
    }

    public static ParameterRetrievalFunction<Double, Map<String, Object>> getDoubleFromMap(boolean attemptParseFromString, boolean
        replaceParsedInMap) {
        return (key, map) -> {
            Objects.requireNonNull(key);
            Object o = map.get(key);
            if (o == null) {
                return null;
            } else if (o instanceof String && attemptParseFromString) {
                Double parsed = Double.parseDouble((String) o);
                if (replaceParsedInMap) {
                    map.put(key, parsed);
                }
                return parsed;
            } else if (o instanceof Double) {
                return (Double)o;
            } else {
                Number num = (Number)o;
                return num.doubleValue();
            }
        };
    }

    public static ParameterRetrievalFunction<String, Map<String, Object>> STRING_FROM_MAP = (key, map) -> (String) map.get(key);

    public static ParameterRetrievalFunction<Integer, Map<String, Object>> CAST_STRING_TO_INT_FROM_MAP = (key, map) -> Integer.parseInt((String)map
        .get(key));

    public static ParameterRetrievalFunction<Integer, Map<String, Object>> CAST_AND_REPLACE_STRING_TO_INT_FROM_MAP = (key, map) -> {
        Integer i = Integer.parseInt((String) map.get(key));
        map.put(key, i);
        return i;
    };

//    public static ParameterRetrievalFunction<Double, Map<String, Object>> DOUBLE_FROM_MAP = (key, map) -> (Double) map.get(s);

    @SuppressWarnings("unchecked")
    public static ParameterRetrievalFunction<Map<String, Object>, Map<String, Object>> INNER_MAP_FROM_MAP = (key, map) -> (Map<String, Object>) map
        .get(key);

    public static ParameterRetrievalFunction<Map<String, Object>, Map<String, Object>> RETURN_SELF_MAP = (key, map) -> map;

}
