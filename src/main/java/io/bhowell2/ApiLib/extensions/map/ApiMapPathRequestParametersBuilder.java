package io.bhowell2.ApiLib.extensions.map;

import io.bhowell2.ApiLib.ApiPathRequestParametersBuilder;
import io.bhowell2.ApiLib.ApiVersion;

import java.util.Map;

/**
 * @author Blake Howell
 */
public class ApiMapPathRequestParametersBuilder {

    public static ApiPathRequestParametersBuilder<Map<String, Object>> builder(ApiVersion apiVersion) {
        return ApiPathRequestParametersBuilder.builder(apiVersion);
    }

}
