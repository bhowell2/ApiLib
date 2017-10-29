package io.bhowell2.ApiLib;

import java.util.Map;

/**
 * Allows users to create a custom check for a parameter. This may be that the parameter is a JsonObject within an JsonOject or JsonArray.
 * @author Blake Howell
 */
public interface ApiCustomParameters {

    /**
     *
     * @param requestParameters
     * @return
     */
    CustomParametersCheckTuple check(Map<String, Object> requestParameters);

}
