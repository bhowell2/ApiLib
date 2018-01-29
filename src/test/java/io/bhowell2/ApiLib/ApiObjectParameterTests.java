package io.bhowell2.ApiLib;

import io.bhowell2.ApiLib.extensions.map.MapParameterRetrievalFunctions;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static io.bhowell2.ApiLib.ApiParametersForTests.*;

/**
 * @author Blake Howell
 */
public class ApiObjectParameterTests {

    @Test
    public void shouldFailFromMissingParameter() {
        ApiObjectParameter<Map<String, Object>, Map<String, Object>> objectParam =
            ApiObjectParameterBuilder.builder(MapParameterRetrievalFunctions.RETURN_SELF_MAP)
                                     .addRequiredParameter(INTEGER1)
                                     .addRequiredParameter(INTEGER2)
                                     .build();

        Map<String, Object> requestParameters = new HashMap<>();
        requestParameters.put(INTEGER1.parameterName, 5);
        ApiObjectParamTuple checkTuple = objectParam.check(requestParameters);
        assertTrue(checkTuple.failed());
        assertEquals(checkTuple.errorTuple.errorType, ErrorType.MISSING_PARAMETER);
        assertTrue(checkTuple.errorTuple.parameterName.matches("Integer2"));
    }

    @Test
    public void shouldFailFromMissingInnerObjectParameter() {
        ApiObjectParameter<Map<String, Object>, Map<String, Object>> objectParam
            = ApiObjectParameterBuilder.builder(MapParameterRetrievalFunctions.RETURN_SELF_MAP)
                                       .addRequiredParameter(INTEGER1)
                                       .addRequiredParameter(INTEGER2)
                                       .addRequiredObjectParameter(INNER_OBJ_PARAM)
                                       .build();

        Map<String, Object> requestParameters = new HashMap<>();
        requestParameters.put(INTEGER1.parameterName, 5);
        requestParameters.put(INTEGER2.parameterName, 101);

        // test again by putting in nested parameters
        Map<String, Object> innerNestedParameters = new HashMap<>();
        innerNestedParameters.put(INTEGER2.parameterName, 101);
        requestParameters.put(INNER_OBJ_PARAM.parameterName, innerNestedParameters);
        ApiObjectParamTuple checkTuple = objectParam.check(requestParameters);
        assertTrue(checkTuple.failed());
        assertEquals(checkTuple.errorTuple.errorType, ErrorType.MISSING_PARAMETER);
        assertTrue(checkTuple.errorTuple.parameterName.matches("Integer1 of .*"));
    }

    @Test
    public void shouldFailFromFailingCustomParameterCheck() {
        ApiObjectParameter<Map<String, Object>, Map<String, Object>> requestParams =
            ApiObjectParameterBuilder.builder(MapParameterRetrievalFunctions.RETURN_SELF_MAP)
                                     .addRequiredCustomParameters(CUSTOM_ARRAY1)
                                     .build();

        List<Integer> failingList = new ArrayList<>();
        failingList.add(5);
        failingList.add(6);

        Map<String, Object> failingArrayObject = new HashMap<>();
        failingArrayObject.put("Array1", failingList);
        ApiObjectParamTuple failedCheckTuple = requestParams.check(failingArrayObject);
        assertTrue(failedCheckTuple.failed());
        assertEquals(failedCheckTuple.errorTuple.errorType, ErrorType.INVALID_PARAMETER);
        assertEquals(failedCheckTuple.errorTuple.parameterName, "Array1");
    }

    @Test
    public void shouldPassFromPassingCustomParameterCheck() {
        ApiObjectParameter<Map<String, Object>, Map<String, Object>> requestParams =
            ApiObjectParameterBuilder.builder(MapParameterRetrievalFunctions.RETURN_SELF_MAP)
                                     .addRequiredCustomParameters(CUSTOM_ARRAY1)
                                     .build();

        List<Integer> passingList = new ArrayList<>();
        passingList.add(11);
        passingList.add(10);

        Map<String, Object> passingArrayObject = new HashMap<>();
        passingArrayObject.put("Array1", passingList);
        ApiObjectParamTuple passingCheckTuple = requestParams.check(passingArrayObject);
        assertTrue(passingCheckTuple.successful());
        assertNull(passingCheckTuple.parameterName);
        assertEquals(passingCheckTuple.providedParamNames.size(), 1);
        assertTrue(passingCheckTuple.providedParamNames.contains("Array1"));
        assertEquals(passingCheckTuple.providedCustomParams.size(), 1);
        assertEquals(passingCheckTuple.providedCustomParams.get("Array1").providedParamNames.size(), 1);
        assertEquals(passingCheckTuple.providedCustomParams.get("Array1").customParameterName, "Array1");
    }

}
