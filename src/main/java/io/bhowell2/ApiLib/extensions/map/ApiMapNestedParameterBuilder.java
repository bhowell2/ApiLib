package io.bhowell2.ApiLib.extensions.map;

import io.bhowell2.ApiLib.ApiNestedParameterBuilder;

import java.util.Map;

/**
 * @author Blake Howell
 */
public class ApiMapNestedParameterBuilder {

    public static ApiNestedParameterBuilder<Map<String,Object>, Map<String,Object>> builder(String nestedMapName) {
        return new ApiNestedParameterBuilder<>(nestedMapName, MapRequestParameterRetrievalFunctions.INNER_MAP_FROM_MAP);
    }

}
