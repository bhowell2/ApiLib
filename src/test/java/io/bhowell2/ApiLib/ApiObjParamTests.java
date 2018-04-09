package io.bhowell2.ApiLib;

import io.bhowell2.ApiLib.extensions.map.ApiMapParamRetrievalFunc;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.bhowell2.ApiLib.ApiParametersForTests.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Blake Howell
 */
public class ApiObjParamTests {

    @Test
    public void shouldFailFromMissingParameter() {
        ApiObjParam<Map<String, Object>, Map<String, Object>> objectParam =
            ApiObjParamBuilder.builder(false, ApiMapParamRetrievalFunc.RETURN_SELF_MAP)
                              .addParameter(INTEGER1)
                              .addParameter(INTEGER2)
                              .build();

        Map<String, Object> requestParameters = new HashMap<>();
        requestParameters.put(INTEGER1.parameterName, 5);
        ApiObjParamCheck checkTuple = objectParam.check(requestParameters);
        assertTrue(checkTuple.failed());
        assertEquals(checkTuple.paramError.errorType, ErrorType.MISSING_PARAMETER);
        assertTrue(checkTuple.paramError.parameterName.matches("Integer2"));
    }

    @Test
    public void shouldFailFromMissingInnerObjectParameter() {
        ApiObjParam<Map<String, Object>, Map<String, Object>> objectParam
            = ApiObjParamBuilder.builder(false, ApiMapParamRetrievalFunc.RETURN_SELF_MAP)
                                .addParameter(INTEGER1)
                                .addParameter(INTEGER2)
                                .addObjectParameter(INNER_OBJ_PARAM)
                                .build();

        Map<String, Object> requestParameters = new HashMap<>();
        requestParameters.put(INTEGER1.parameterName, 5);
        requestParameters.put(INTEGER2.parameterName, 101);

        // test again by putting in nested parameters
        Map<String, Object> innerNestedParameters = new HashMap<>();
        innerNestedParameters.put(INTEGER2.parameterName, 101);
        requestParameters.put(INNER_OBJ_PARAM.parameterName, innerNestedParameters);
        ApiObjParamCheck checkTuple = objectParam.check(requestParameters);
        assertTrue(checkTuple.failed());
        assertEquals(checkTuple.paramError.errorType, ErrorType.MISSING_PARAMETER);
        assertTrue(checkTuple.paramError.parameterName.matches("Integer1 of .*"));
    }

    @Test
    public void shouldFailFromFailingCustomParameterCheck() {
        ApiObjParam<Map<String, Object>, Map<String, Object>> requestParams =
            ApiObjParamBuilder.builder(false, ApiMapParamRetrievalFunc.RETURN_SELF_MAP)
                              .addCustomParameter(CUSTOM_ARRAY1)
                              .build();

        List<Integer> failingList = new ArrayList<>();
        failingList.add(5);
        failingList.add(6);

        Map<String, Object> failingArrayObject = new HashMap<>();
        failingArrayObject.put("Array1", failingList);
        ApiObjParamCheck failedCheckTuple = requestParams.check(failingArrayObject);
        assertTrue(failedCheckTuple.failed());
        assertEquals(failedCheckTuple.paramError.errorType, ErrorType.INVALID_PARAMETER);
        assertEquals(failedCheckTuple.paramError.parameterName, "Array1");
    }

    @Test
    public void shouldPassFromPassingCustomParameterCheck() {
        ApiObjParam<Map<String, Object>, Map<String, Object>> requestParams =
            ApiObjParamBuilder.builder(false, ApiMapParamRetrievalFunc.RETURN_SELF_MAP)
                              .addCustomParameter(CUSTOM_ARRAY1)
                              .build();

        List<Integer> passingList = new ArrayList<>();
        passingList.add(11);
        passingList.add(10);

        Map<String, Object> passingArrayObject = new HashMap<>();
        passingArrayObject.put("Array1", passingList);
        ApiObjParamCheck passingCheckTuple = requestParams.check(passingArrayObject);
        assertTrue(passingCheckTuple.successful());
        assertNull(passingCheckTuple.parameterName);
        assertEquals(passingCheckTuple.providedParamNames.size(), 1);
        assertTrue(passingCheckTuple.containsParameter("Array1"));
        assertEquals(passingCheckTuple.providedCustomParams.size(), 1);
        assertEquals(passingCheckTuple.providedCustomParams.get("Array1").providedParamNames.size(), 1);
        assertEquals(passingCheckTuple.providedCustomParams.get("Array1").customParameterName, "Array1");
    }

}
