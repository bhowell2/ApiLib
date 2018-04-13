package io.bhowell2.ApiLib;

import io.bhowell2.ApiLib.extensions.map.ApiMapParamRetrievalFuncs;
import io.bhowell2.ApiLib.utils.ParamChecks;
import io.bhowell2.ApiLib.utils.StringParamChecks;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static io.bhowell2.ApiLib.ApiParametersForTests.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Blake Howell
 */
public class ApiObjParamTests {

    @Test
    public void shouldPassOnEmptyRequestWithNoRequiredParams() {
        ApiObjParam<Map<String, Object>, Map<String, Object>> objectParam =
            ApiObjParamBuilder.rootObjBuilder(ApiMapParamRetrievalFuncs.RETURN_SELF_MAP)
                              .build();
        Map<String, Object> requestParams = new HashMap<>(0);
        ApiObjParamCheck check = objectParam.check(requestParams);
        assertTrue(check.successful());
        assertEquals(0, check.providedObjParams.size());
        assertEquals(0, check.providedParamNames.size());
        assertEquals(0, check.providedCustomParams.size());
    }

    @Test
    public void shouldFailFromMissingParameter() {
        ApiObjParam<Map<String, Object>, Map<String, Object>> objectParam =
            ApiObjParamBuilder.rootObjBuilder(ApiMapParamRetrievalFuncs.RETURN_SELF_MAP)
                              .addRequiredParam(INTEGER1)
                              .addRequiredParam(INTEGER2)
                              .build();
        Map<String, Object> requestParameters = new HashMap<>();
        requestParameters.put(INTEGER1.parameterName, 5);
        ApiObjParamCheck check = objectParam.check(requestParameters);
        assertTrue(check.failed());
        assertFalse(check.successful());       // only checking this once to make sure it never becomes NOT the case
        assertEquals(check.paramError.errorType, ErrorType.MISSING_PARAMETER);
        assertTrue(check.paramError.paramName.matches("Integer2"));
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
        ApiObjParamCheck check = objectParam.check(requestParameters);
        assertTrue(check.failed());
        assertEquals(check.paramError.errorType, ErrorType.MISSING_PARAMETER);
        assertTrue(check.paramError.paramName.matches("Integer1 of .*"));
    }

    @Test
    public void shouldFailFromFailingCustomParameterCheck() {
        ApiObjParam<Map<String, Object>, Map<String, Object>> requestParams =
            ApiObjParamBuilder.rootObjBuilder(ApiMapParamRetrievalFuncs.RETURN_SELF_MAP)
                              .addRequiredCustomParam(CUSTOM_ARRAY1)
                              .build();

        List<Integer> failingList = new ArrayList<>();
        failingList.add(5);
        failingList.add(6);

        Map<String, Object> failingArrayObject = new HashMap<>();
        failingArrayObject.put("Array1", failingList);
        ApiObjParamCheck failedCheck = requestParams.check(failingArrayObject);
        assertTrue(failedCheck.failed());
        assertEquals(failedCheck.paramError.errorType, ErrorType.INVALID_PARAMETER);
        assertEquals(failedCheck.paramError.paramName, "Array1");
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
        assertTrue(check.paramError.paramName.matches("String1 of .*"));
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
        assertTrue(check.paramError.paramName.matches("Integer1 of .*"));
    }

    @Test
    public void shouldPassFromPassingCustomParameterCheck() {
        ApiObjParam<Map<String, Object>, Map<String, Object>> objParam =
            ApiObjParamBuilder.rootObjBuilder(ApiMapParamRetrievalFuncs.RETURN_SELF_MAP)
                              .addRequiredCustomParam(CUSTOM_ARRAY1)
                              .build();

        List<Integer> passingList = new ArrayList<>();
        passingList.add(11);
        passingList.add(10);

        Map<String, Object> passingArrayObject = new HashMap<>();
        passingArrayObject.put("Array1", passingList);
        ApiObjParamCheck passingCheckTuple = objParam.check(passingArrayObject);
        assertTrue(passingCheckTuple.successful());
        assertNull(passingCheckTuple.paramName);
        assertEquals(passingCheckTuple.providedParamNames.size(), 1);
        assertTrue(passingCheckTuple.containsParameter("Array1"));
        assertEquals(passingCheckTuple.providedCustomParams.size(), 1);
        assertEquals(passingCheckTuple.providedCustomParams.get("Array1").providedParamNames.size(), 1);
        assertEquals(passingCheckTuple.providedCustomParams.get("Array1").customParameterName, "Array1");
    }


    @Test
    public void shouldFailFromMissingCustomParam() {
        ApiObjParam<Map<String, Object>, Map<String, Object>> objParam =
            ApiObjParamBuilder.rootObjBuilder(ApiMapParamRetrievalFuncs.RETURN_SELF_MAP)
                              .addRequiredCustomParam((m) -> ApiCustomParamCheck.failure(ErrorType.MISSING_PARAMETER, "CustomParam"))
                              .build();
        Map<String, Object> requestParams = new HashMap<>(1);
        requestParams.put("whatever", "not checked");
        ApiObjParamCheck check = objParam.check(requestParams);
        assertTrue(check.failed());
        assertEquals(ErrorType.MISSING_PARAMETER, check.paramError.errorType);
        assertEquals("CustomParam", check.paramError.paramName);
    }

    @Test
    public void shouldPassFromMissingOptionalCustomParam() {
        ApiObjParam<Map<String, Object>, Map<String, Object>> objParam =
            ApiObjParamBuilder.rootObjBuilder(ApiMapParamRetrievalFuncs.RETURN_SELF_MAP)
                              .addOptionalCustomParam((m) -> ApiCustomParamCheck.failure(ErrorType.MISSING_PARAMETER, "CustomParam"))
                              .build();
        Map<String, Object> requestParams = new HashMap<>(1);
        requestParams.put("whatever", "not checked");
        ApiObjParamCheck check = objParam.check(requestParams);
        assertTrue(check.successful());
        assertEquals(0, check.providedCustomParams.size());
        assertEquals(0, check.providedParamNames.size());
        assertEquals(0, check.providedObjParams.size());
    }

    @Test
    public void shouldPassFromInvalidOptionalCustomParam() {
        ApiObjParam<Map<String, Object>, Map<String, Object>> objParam =
            ApiObjParamBuilder.rootObjBuilder(ApiMapParamRetrievalFuncs.RETURN_SELF_MAP)
                              .addOptionalCustomParam((m) -> {
                                  String s = (String) m.get("CustomParam");
                                  if (s == null) {
                                      return ApiCustomParamCheck.failure(ErrorType.MISSING_PARAMETER, "CustomParam");
                                  } else {
                                      return s.length() > 5 ? ApiCustomParamCheck.success("CustomParam") :
                                          ApiCustomParamCheck.failure(ErrorType.INVALID_PARAMETER, "CustomParam");
                                  }
                                  })
                              .setContinueOnOptionalFailure(true)
                              .build();
        Map<String, Object> requestParams = new HashMap<>(1);
        requestParams.put("CustomParam", "l");  // check above will fail if length less than 5
        ApiObjParamCheck check = objParam.check(requestParams);
        assertTrue(check.successful());
        assertEquals(0, check.providedCustomParams.size(), "The check should have failed and thus shouldnt have added parameters.");
        assertEquals(0, check.providedParamNames.size(), "The check");
    }

    // encompasses everything
    @Test
    public void shouldPassFromAllRequiredAndOptionalParameterProvided() {

        ParamRetrievalFunc<String, Map<String, Object>> stringRetrievalFunc = (String name, Map<String, Object> map) -> (String)map.get(name);

        ApiParam<String, Map<String, Object>> param1 = ApiParamBuilder.builder("param1", stringRetrievalFunc)
                                                                      .addCheckFunction(StringParamChecks.lengthGreaterThanOrEqual(2))
                                                                      .build();
        ApiParam<String, Map<String, Object>> param2 = ApiParamBuilder.builder("param2", stringRetrievalFunc)
                                                                      .addCheckFunction(StringParamChecks.lengthGreaterThanOrEqual(5))
                                                                      .build();
        ApiParam<String, Map<String, Object>> param3 = ApiParamBuilder.builder("param3", stringRetrievalFunc)
                                                                      .addCheckFunction(ParamChecks.alwaysPass()) // doesnt matter what it is,
                                                                      .build();

        ApiParam<String, Map<String, Object>> innerParam1 = ApiParamBuilder.builder("innerParam1", stringRetrievalFunc)
                                                                      .addCheckFunction(ParamChecks.alwaysPass()) // doesnt matter what it is,
                                                                      .build();

        ApiObjParam<Map<String, Object>, Map<String, Object>> innerObjParam =
            ApiObjParamBuilder.builder("innerobj", (String name, Map<String, Object> map) -> (Map<String, Object>) map.get(name))
                              .addRequiredParam(innerParam1)
                              .build();

        ApiCustomParam<Map<String, Object>> customParam = (map) -> {
            // don't really need to worry about cast exception (if used in ApiObjParam as it will catch it)
            List<String> stringListParam = (List<String>) map.get("customparam");
            if (stringListParam == null) {
                return ApiCustomParamCheck.failure(ErrorType.MISSING_PARAMETER, "customparam");
            } else {
                // exists, does it meet conditions?
                // let's say that every string in the list must have a length greater than 5
                for (String s : stringListParam) {
                    if (s.length() <= 5) {
                        return ApiCustomParamCheck.failure(ErrorType.INVALID_PARAMETER, "customparam");
                    }
                }
                return ApiCustomParamCheck.success("SomeSpecialCustomParamName", new HashSet<>(Arrays.asList("customparam")));
            }
        };

        ApiObjParam<Map<String, Object>, Map<String, Object>> rootObjParam =
            ApiObjParamBuilder.rootObjBuilder((String name, Map<String, Object> map) -> map)    // just return the map since it is the root
                              .addRequiredParams(param1, param2)
                              .addOptionalParam(param3)
                              .addOptionalObjParam(innerObjParam)
                              .addRequiredCustomParam(customParam)
                              .build();

        Map<String, Object> innerObjOfRequest = new HashMap<>(1);
        innerObjOfRequest.put("innerParam1", "whatever");
        innerObjOfRequest.put("anything", "won't be returned in ApiObjParamCheck, because it is not a parameter that is checked by innerObjParam");

        Map<String, Object> requestParams = new HashMap<>(4);
        requestParams.put("param1", "long enough");
        requestParams.put("param2", "long enough too");
        requestParams.put("param3", "doesn't matter");
        requestParams.put("customparam", Arrays.asList("long enough", "still long enough"));
        requestParams.put("innerobj", innerObjOfRequest);

        ApiObjParamCheck check = rootObjParam.check(requestParams);
        assertTrue(check.successful());
        assertEquals(5, check.providedParamNames.size());
        assertEquals(1, check.providedCustomParams.size());
        assertEquals(1, check.providedObjParams.get("innerobj").providedParamNames.size());
        assertTrue(check.providedObjParams.get("innerobj").providedParamNames.contains("innerParam1"));
    }

}
