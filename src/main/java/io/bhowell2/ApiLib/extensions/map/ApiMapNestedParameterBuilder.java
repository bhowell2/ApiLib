package io.bhowell2.ApiLib.extensions.map;

import io.bhowell2.ApiLib.ApiObjectParameterBuilder;

import java.util.Map;

/**
 * @author Blake Howell
 */
public class ApiMapNestedParameterBuilder {

    public static ApiObjectParameterBuilder<Map<String,Object>, Map<String,Object>> builder(String nestedMapName) {
        return new ApiObjectParameterBuilder<>(nestedMapName, MapRequestParameterRetrievalFunctions.INNER_MAP_FROM_MAP);
    }

}
