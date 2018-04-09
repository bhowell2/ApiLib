package io.bhowell2.ApiLib.extensions.map;

import io.bhowell2.ApiLib.FormatInsertFunc;

import java.util.Map;

/**
 * @author Blake Howell
 */
public class ApiMapFormatInsertFunc {

    public static final FormatInsertFunc<?, Map<String, Object>> MAP_FORMAT_INJECTION_FUNCTION = (name, map, o) -> map.put(name, o);

    /**
     * Used to keep type-safety of injection function.
     * @param classType
     * @param <T> type of parameter to inject back into map
     * @return a type-safe FormatInjectionFunction
     */
    public static final <T> FormatInsertFunc<T, Map<String, Object>> getFormatInjectionFunctionFor(Class<T> classType) {
        return (name, map, o) -> map.put(name, o);
    }

}
