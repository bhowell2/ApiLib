package io.bhowell2.ApiLib;

import org.junit.Test;

import static org.junit.Assert.*;
import static io.bhowell2.ApiLib.ApiParametersForTests.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Blake Howell
 */
public class ApiPathRequestParametersTests {

    @Test
    public void shouldFailFromMissingParameter() {
        ApiPathRequestParameters<Map<String, Object>> pathRequestParameters = ApiPathRequestParametersBuilder.<Map<String, Object>>builder(ApiVersionForTests.V0)
            .addRequiredParameter(INTEGER1)
            .addRequiredParameter(INTEGER2)
            .build();

        Map<String, Object> requestParameters = new HashMap<>();
        requestParameters.put(INTEGER1.parameterName, 5);
        ApiNestedParamCheckTuple checkTuple = pathRequestParameters.check(requestParameters);
        assertTrue(checkTuple.failed());
        assertEquals(checkTuple.errorTuple.errorType, ErrorType.MISSING_PARAMETER);
        assertEquals(checkTuple.errorTuple.parameterName, INTEGER2.parameterName);



    }

    @Test
    public void shoudFailFromMissingNestedParameter() {
        ApiPathRequestParameters<Map<String, Object>> pathRequestParameters = ApiPathRequestParametersBuilder.<Map<String, Object>>builder(ApiVersionForTests.V0)
            .addRequiredParameter(INTEGER1)
            .addRequiredParameter(INTEGER2)
            .addRequiredNestedParameter(NESTED_PARAM)
            .build();

        Map<String, Object> requestParameters = new HashMap<>();
        requestParameters.put(INTEGER1.parameterName, 5);
        requestParameters.put(INTEGER2.parameterName, 101);

        // test again by putting in nested parameters
        Map<String, Object> innerNestedParameters = new HashMap<>();
        innerNestedParameters.put(INTEGER2.parameterName, 101);
        requestParameters.put(NESTED_PARAM.parameterName, innerNestedParameters);
        ApiNestedParamCheckTuple checkTuple = pathRequestParameters.check(requestParameters);
        assertTrue(checkTuple.failed());
        assertEquals(checkTuple.errorTuple.errorType, ErrorType.MISSING_PARAMETER);
        assertEquals(checkTuple.errorTuple.parameterName, INTEGER1.parameterName);
    }

}
