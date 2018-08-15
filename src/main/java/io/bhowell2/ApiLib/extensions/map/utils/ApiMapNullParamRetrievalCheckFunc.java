package io.bhowell2.ApiLib.extensions.map.utils;

import io.bhowell2.ApiLib.NullParamRetrievalCheckFunc;

import java.util.Map;

/**
 * @author Blake Howell
 */
public class ApiMapNullParamRetrievalCheckFunc {

    public static NullParamRetrievalCheckFunc<Map<String, Object>> NULL_CHECK = (String parameterName, Map<String, Object> stringObjectMap) -> {
        Object param = stringObjectMap.get(parameterName);
        if (param == null) {
            return stringObjectMap.containsKey(parameterName);
        } else {
            throw new RuntimeException("Should not be checking for null parameter when the parameter is not null in the first place");
        }
    };

}
