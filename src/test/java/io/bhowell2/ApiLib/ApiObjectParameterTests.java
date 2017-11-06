package io.bhowell2.ApiLib;

import io.bhowell2.ApiLib.extensions.map.MapRequestParameterRetrievalFunctions;
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
            ApiObjectParameterBuilder.builder(MapRequestParameterRetrievalFunctions.RETURN_SELF_MAP)
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
            = ApiObjectParameterBuilder.builder(MapRequestParameterRetrievalFunctions.RETURN_SELF_MAP)
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
    public void shouldFailFromFailingArrayCheck() {
        ApiObjectParameter<Map<String, Object>, Map<String, Object>> objectParam =
            ApiObjectParameterBuilder.builder(MapRequestParameterRetrievalFunctions.RETURN_SELF_MAP)
                                     .addRequiredArrayParameter(ARRAY1)
                                     .build();

        List<Integer> failingList = new ArrayList<>();
        failingList.add(5);
        failingList.add(6);
        List<Integer> passingList = new ArrayList<>();
        passingList.add(11);
        passingList.add(10);

        Map<String, Object> failingArrayObject = new HashMap<>();
        failingArrayObject.put("Array1", failingList);
        ApiObjectParamTuple failedCheckTuple = objectParam.check(failingArrayObject);
        assertTrue(failedCheckTuple.failed());
        assertEquals(failedCheckTuple.errorTuple.errorType, ErrorType.INVALID_PARAMETER);
        assertEquals(failedCheckTuple.errorTuple.parameterName, "Array1");

        Map<String, Object> passingArrayObject = new HashMap<>();
        passingArrayObject.put("Array1", passingList);
        ApiObjectParamTuple passingCheckTuple = objectParam.check(passingArrayObject);
        assertTrue(passingCheckTuple.successful());
        assertNull(passingCheckTuple.parameterName);
        assertEquals(passingCheckTuple.providedParamNames.size(), 1);
        assertEquals(passingCheckTuple.providedParamNames.get(0), "Array1");
        assertEquals(passingCheckTuple.providedArrayParams.size(), 1);
        assertEquals(passingCheckTuple.providedArrayParams.get(0).parameterName, "Array1");
    }

}
