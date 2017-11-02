package io.bhowell2.ApiLib.extensions.map;

import io.bhowell2.ApiLib.ParameterRetrievalFunction;

import java.util.Map;

/**
 * @author Blake Howell
 */
public class MapRequestParameterRetrievalFunctions {

    public static <T> ParameterRetrievalFunction<T, Map<String, Object>> typeFromMap(Class<T> type) {
        return (s, m) -> type.cast(m.get(s));
    }

    public static ParameterRetrievalFunction<String, Map<String, Object>> STRING_FROM_MAP = (s, m) -> (String) m.get(s);

    public static ParameterRetrievalFunction<Integer, Map<String, Object>> INTEGER_FROM_MAP = (s, m) -> (Integer) m.get(s);

    public static ParameterRetrievalFunction<Integer, Map<String, Object>> CAST_STRING_TO_INT_FROM_MAP = (s, m) -> Integer.parseInt((String)m.get(s));

    public static ParameterRetrievalFunction<Integer, Map<String, Object>> CAST_AND_REPLACE_STRING_TO_INT_FROM_MAP = (s, m) -> {
        Integer i = Integer.parseInt((String) m.get(s));
        m.put(s, i);
        return i;
    };

    public static ParameterRetrievalFunction<Double, Map<String, Object>> DOUBLE_FROM_MAP = (s, m) -> (Double) m.get(s);

    @SuppressWarnings("unchecked")
    public static ParameterRetrievalFunction<Map<String, Object>, Map<String, Object>> INNER_MAP_FROM_MAP = (s, m) -> (Map<String, Object>) m.get(s);

    public static ParameterRetrievalFunction<Map<String, Object>, Map<String, Object>> RETURN_SELF_MAP = (s, m) -> m;

}
