package io.bhowell2.ApiLib.extensions.map;

import io.bhowell2.ApiLib.ApiPath;
import io.bhowell2.ApiLib.ApiPathRequestParameters;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Blake Howell
 */
public class ApiMapPath extends ApiPath<Map<String, Object>> {

    public ApiMapPath(ApiPathRequestParameters<Map<String, Object>>[] apiPathRequestParameters) {
        super(apiPathRequestParameters);
    }

    public ApiMapPath(String pathName, ApiPathRequestParameters<Map<String, Object>>[] apiPathRequestParameters) {
        super(pathName, apiPathRequestParameters);
    }

    public ApiMapPath(String pathName, Pattern matchPattern, ApiPathRequestParameters<Map<String, Object>>[] apiPathRequestParameters) {
        super(pathName, matchPattern, apiPathRequestParameters);
    }

}
