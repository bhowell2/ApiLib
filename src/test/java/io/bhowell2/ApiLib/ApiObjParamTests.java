package io.bhowell2.ApiLib;

import io.bhowell2.ApiLib.extensions.map.ApiMapParamRetrievalFuncs;
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
            ApiObjParamBuilder.rootObjBuilder(ApiMapParamRetrievalFuncs.RETURN_SELF_MAP)
                              .addRequiredParam(INTEGER1)
                              .addRequiredParam(INTEGER2)
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
            = ApiObjParamBuilder.rootObjBuilder(ApiMapParamRetrievalFuncs.RETURN_SELF_MAP)
                                .addRequiredParams(INTEGER1, INTEGER2)
                                .addRequiredObjParam(INNER_OBJ_PARAM)
                                .build();

        Map<String, Object> requestParameters = new HashMap<>();
        requestParameters.put(INTEGER1.parameterName, 5);
        requestParameters.put(INTEGER2.parameterName, 101);

        // test again by putting in nested parameters
        Map<String, Object> innerNestedParameters = new HashMap<>();
        innerNestedParameters.put(INTEGER2.parameterName, 101);
        requestParameters.put(INNER_OBJ_PARAM.paramName, innerNestedParameters);
        ApiObjParamCheck checkTuple = objectParam.check(requestParameters);
        assertTrue(checkTuple.failed());
        assertEquals(checkTuple.paramError.errorType, ErrorType.MISSING_PARAMETER);
        assertTrue(checkTuple.paramError.parameterName.matches("Integer1 of .*"));
    }

    @Test
    public void shouldFailFromFailingCustomParameterCheck() {
        ApiObjParam<Map<String, Object>, Map<String, Object>> requestParams =
            ApiObjParamBuilder.rootObjBuilder(ApiMapParamRetrievalFuncs.RETURN_SELF_MAP)
                              .addCustomParam(CUSTOM_ARRAY1)
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
    public void shouldFailOnOptionalFailure() {
        // check for  regular param
        ApiObjParam<Map<String, Object>, Map<String, Object>> objParam =
            ApiObjParamBuilder.rootObjBuilder((String name, Map<String, Object> map) -> map)
                              .addOptionalParam(STRING1)
                              .build();

        Map<String, Object> requestParams = new HashMap<>(1);
        requestParams.put(STRING1.parameterName, "");  // should fail - length too short

        ApiObjParamCheck check = objParam.check(requestParams);
        assertTrue(check.failed());
        assertEquals(ErrorType.INVALID_PARAMETER, check.paramError.errorType, "Should have failed when optional parameter was provided, but failed" +
            "to check.");
    }

    @Test
    public void shouldFailOnInnerObjOptionalFailure() {
        Map<String, Object> requestParams = new HashMap<>(1);
        requestParams.put(STRING1.parameterName, "");  // should fail - length too short

        // same as above, but has a name of innerobj
        ApiObjParam<Map<String, Object>, Map<String, Object>> innerObjParam =
            ApiObjParamBuilder.builder("innerobj",
                                       (String name, Map<String, Object> map) -> (Map<String, Object>) map.get(name))
                              .addOptionalParam(STRING1)
                              .build();

        Map<String, Object> withInnerObjRequestParams = new HashMap<>(1);
        withInnerObjRequestParams.put("innerobj", requestParams);

        // check for obj param
        ApiObjParam<Map<String, Object>, Map<String, Object>> objParam =
            ApiObjParamBuilder.rootObjBuilder((String name, Map<String, Object> map) -> map)
                              .addOptionalObjParam(innerObjParam)
                              .build();

        ApiObjParamCheck check = objParam.check(withInnerObjRequestParams);
        assertTrue(check.failed());
        assertEquals(ErrorType.INVALID_PARAMETER, check.paramError.errorType, "Should have failed from missing optional in inner obj param.");
        assertTrue(check.paramError.parameterName.matches("String1 of .*"));
    }

    @Test
    public void shouldNotFailFromInvalidOptionalParameter() {
        // check for  regular param
        ApiObjParam<Map<String, Object>, Map<String, Object>> objParam1 =
            ApiObjParamBuilder.rootObjBuilder((String name, Map<String, Object> map) -> map)
                              .addOptionalParam(STRING1)
                              .setContinueOnOptionalFailure(true)
                              .build();

        Map<String, Object> requestParams = new HashMap<>(1);
        requestParams.put(STRING1.parameterName, "");  // should fail - length too short

        ApiObjParamCheck check1 = objParam1.check(requestParams);
        assertTrue(check1.successful());

        // check for obj param
        ApiObjParam<Map<String, Object>, Map<String, Object>> objParam2 =
            ApiObjParamBuilder.rootObjBuilder((String name, Map<String, Object> map) -> map)
                              .addOptionalObjParam(objParam1)
                              .setContinueOnOptionalFailure(true)
                              .build();

        ApiObjParamCheck check2 = objParam2.check(requestParams);
        assertTrue(check2.successful());
    }

    @Test
    public void shouldFailFromParameterCastErrorType() {
        ApiObjParam<Map<String, Object>, Map<String, Object>> innerObjParam =
            ApiObjParamBuilder.builder("innerobj",
                                       (String name, Map<String, Object> map) -> (Map<String, Object>) map.get(name))
                              .addRequiredParam(INTEGER1)
                              .addRequiredParam(STRING1)
                              .build();

        ApiObjParam<Map<String, Object>, Map<String, Object>> objParam =
            ApiObjParamBuilder.rootObjBuilder((String name, Map<String, Object> map) -> map)
                              .addRequiredObjParam(innerObjParam)
                              .build();

        Map<String, Object> innerRequestParams = new HashMap<>(2);
        innerRequestParams.put(INTEGER1.parameterName, "50");   // will cause cast failure
        innerRequestParams.put(STRING1.parameterName, "pass");

        Map<String, Object> requestParams = new HashMap<>(1);
        requestParams.put("innerobj", innerRequestParams);

        ApiObjParamCheck check = objParam.check(requestParams);

        assertTrue(check.failed());
        assertEquals(ErrorType.PARAMETER_CAST, check.paramError.errorType);
        assertTrue(check.paramError.parameterName.matches("Integer1 of .*"));
    }

    @Test
    public void shouldPassFromPassingCustomParameterCheck() {
        ApiObjParam<Map<String, Object>, Map<String, Object>> objParam =
            ApiObjParamBuilder.rootObjBuilder(ApiMapParamRetrievalFuncs.RETURN_SELF_MAP)
                              .addCustomParam(CUSTOM_ARRAY1)
                              .build();

        List<Integer> passingList = new ArrayList<>();
        passingList.add(11);
        passingList.add(10);

        Map<String, Object> passingArrayObject = new HashMap<>();
        passingArrayObject.put("Array1", passingList);
        ApiObjParamCheck passingCheckTuple = objParam.check(passingArrayObject);
        assertTrue(passingCheckTuple.successful());
        assertNull(passingCheckTuple.parameterName);
        assertEquals(passingCheckTuple.providedParamNames.size(), 1);
        assertTrue(passingCheckTuple.containsParameter("Array1"));
        assertEquals(passingCheckTuple.providedCustomParams.size(), 1);
        assertEquals(passingCheckTuple.providedCustomParams.get("Array1").providedParamNames.size(), 1);
        assertEquals(passingCheckTuple.providedCustomParams.get("Array1").customParameterName, "Array1");
    }

}
