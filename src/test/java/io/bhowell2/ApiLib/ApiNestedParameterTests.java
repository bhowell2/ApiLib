package io.bhowell2.ApiLib;

import io.bhowell2.ApiLib.utils.MapRequestParametersRetrievalFunctions;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static io.bhowell2.ApiLib.ApiParametersForTests.*;

/**
 * @author Blake Howell
 */
public class ApiNestedParameterTests {

    @Test
    public void shouldFailFromMissingParameters() {
        ApiNestedParameter<Map<String, Object>, Map<String, Object>> nestedParameter = ApiNestedParameterBuilder.builder("",
                                                                                                                         MapRequestParametersRetrievalFunctions.RETURN_SELF_MAP)
                                                                                                                .addRequiredParameter(INTEGER1)
                                                                                                                .addRequiredParameter(INTEGER2)
                                                                                                                .build();

        Map<String, Object> requestParameters = new HashMap<>();
        requestParameters.put(INTEGER1.parameterName, 5);
        ApiNestedParamCheckTuple checkTuple = nestedParameter.check(requestParameters);
        assertTrue(checkTuple.failed());
        assertEquals(checkTuple.errorTuple.errorType, ErrorType.MISSING_PARAMETER);
        assertEquals(checkTuple.errorTuple.parameterName, INTEGER2.parameterName);

    }

    @Test
    public void shouldFailFromMissingNestedParameter() {
        ApiNestedParameter<Map<String, Object>, Map<String, Object>> nestedParameter = ApiNestedParameterBuilder.builder("",
                                                                                                                         MapRequestParametersRetrievalFunctions.RETURN_SELF_MAP)
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
        ApiNestedParamCheckTuple checkTuple = nestedParameter.check(requestParameters);
        assertTrue(checkTuple.failed());
        assertEquals(checkTuple.errorTuple.errorType, ErrorType.MISSING_PARAMETER);
        assertEquals(checkTuple.errorTuple.parameterName, INTEGER1.parameterName);
    }

}
