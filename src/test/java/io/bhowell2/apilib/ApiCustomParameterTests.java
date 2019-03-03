package io.bhowell2.apilib;

import io.bhowell2.apilib.extensions.map.utils.ApiMapParamRetrievalFuncs;
import io.bhowell2.apilib.utils.paramchecks.IntegerParamChecks;
import io.bhowell2.apilib.utils.paramchecks.AnyParamChecks;
import io.bhowell2.apilib.utils.paramchecks.StringParamChecks;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Blake Howell
 */
public class ApiCustomParameterTests {

    @Test
    public void testSimpleArrayAsCustomParameterPassing() {
        String customParamName = "ACustomArrayParam";
        String arrayParamName = "stringList";
        // Pull array from requestParameters (Map of type String,Object)
        ApiCustomParam<Map<String, Object>> mapApiCustomParam = (requestParameters -> {
            @SuppressWarnings("unchecked")
            List<String> stringList = (List<String>)requestParameters.get(arrayParamName);
            for (String s : stringList) {
                if (s.length() < 5) {
                    return ApiCustomParamCheck.failure(ErrorType.INVALID_PARAMETER, arrayParamName);
                }
            }
            // the custom parameter must have a name - it may not be used, but it is required
            // the custom parameter checks an array named stringList and therefore should return the name of the successfully checked parameter
            return ApiCustomParamCheck.success(customParamName, new HashSet<>(Arrays.asList(arrayParamName)));
        });

        Map<String, Object> requestParams = new HashMap<>(1);
        List<String> stringList = new ArrayList<>();
        stringList.add("long enough");
        stringList.add("still long");
        stringList.add("wont fail");
        requestParams.put(arrayParamName, stringList);

        ApiCustomParamCheck check = mapApiCustomParam.check(requestParams);
        assertTrue(check.successful());
        assertEquals(customParamName, check.customParameterName);
        assertTrue(check.providedParamNames.contains(arrayParamName));
        assertEquals(check.arrayObjParams.size(), 0);
        assertEquals(check.providedObjParams.size(), 0);

        Map<String, Object> failingRequestParams = new HashMap<>(1);
        List<String> failingStringList = new ArrayList<>();
        stringList.add("fail");
        failingRequestParams.put(arrayParamName, stringList);
        ApiCustomParamCheck failedCheck = mapApiCustomParam.check(failingRequestParams);
        assertTrue(failedCheck.failed());
        assertEquals(failedCheck.paramError.errorType, ErrorType.INVALID_PARAMETER);
        assertEquals(failedCheck.paramError.paramName, arrayParamName);
    }

    // show/test case of checking an array with objects as parameters
    @Test
    public void testArrayWithObjectsAsIndices() {
        String customParamName = "AnArrayWithObjectsAsValues";
        String arrayParamName = "objArray";
        String param1Name = "param1";
        String param2Name = "param2";
        String param3Name = "param3";

        ApiParam<String, Map<String, Object>> param1 = ApiParamBuilder.builder(param1Name,
                                                                               (String name, Map<String, Object> map) -> (String) map.get(name))
                                                                      .addCheckFunction(AnyParamChecks.alwaysPass())
                                                                      .build();

        ApiParam<String, Map<String, Object>> param2 = ApiParamBuilder.builder(param2Name,
                                                                               (String name, Map<String, Object> map) -> (String) map.get(name))
                                                                      .addCheckFunction(StringParamChecks.lengthGreaterThanOrEqual(2))
                                                                      .build();

        ApiParam<Integer, Map<String, Object>> param3 = ApiParamBuilder.builder(param3Name, ApiMapParamRetrievalFuncs.getIntegerFromMap())
                                                                       .addCheckFunction(IntegerParamChecks.valueGreaterThan(3))
                                                                       .build();

        ParamRetrievalFunc<Map<String, Object>, Map<String, Object>> mapParamRetrievalFunc = (name, map) -> map;

        ApiObjParam<Map<String, Object>, Map<String, Object>> expectedInnerObject =
            ApiObjParamBuilder.rootObjBuilder(mapParamRetrievalFunc)
                              .addRequiredParams(param1, param2, param3)
                              .build();

        ApiCustomParam<Map<String, Object>> mapApiCustomParam = (requestParameters -> {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> arrayOfObjects = (List<Map<String, Object>>) requestParameters.get(arrayParamName);
            if (arrayOfObjects == null) {
                return ApiCustomParamCheck.failure(ErrorType.MISSING_PARAMETER, arrayParamName);
            }
            List<ApiObjParamCheck> checkedObjectsOfArray = new ArrayList<>();
            for (Map<String, Object> map : arrayOfObjects) {
                ApiObjParamCheck check = expectedInnerObject.check(map);
                if (check.failed()) {
                    return ApiCustomParamCheck.failure(check.paramError);
                }
                checkedObjectsOfArray.add(check);
            }
            return ApiCustomParamCheck.success(customParamName, checkedObjectsOfArray);
        });

        Map<String, Object> innerPassingReqObj = new HashMap<>(3);
        innerPassingReqObj.put(param1Name, "always pass");
        innerPassingReqObj.put(param2Name, "pass");
        innerPassingReqObj.put(param3Name, 4);

        Map<String, Object> innerPassingReqObj2 = new HashMap<>(3);
        innerPassingReqObj2.put(param1Name, "always pass");
        innerPassingReqObj2.put(param2Name, "pass");
        innerPassingReqObj2.put(param3Name, 4);

        List<Map<String, Object>> array = Arrays.asList(innerPassingReqObj, innerPassingReqObj2);
        Map<String, Object> passingReqParams = new HashMap<>(1);
        passingReqParams.put(arrayParamName, array);

        ApiCustomParamCheck check = mapApiCustomParam.check(passingReqParams);
        assertTrue(check.successful());
        assertEquals(2, check.arrayObjParams.size(), "Array size should be 2. One for each object in array position.");

        // Now check for failing

        // In this case the check fails because one of the inner objects is missing the required parameters for it
        Map<String, Object> innerFailingReqObj = new HashMap<>();
        List<Map<String, Object>> failingArray = Arrays.asList(innerPassingReqObj, innerPassingReqObj2, innerFailingReqObj);
        Map<String, Object> failingReqParams = new HashMap<>(1);
        failingReqParams.put(arrayParamName, failingArray);

        ApiCustomParamCheck failingCheck = mapApiCustomParam.check(failingReqParams);
        assertTrue(failingCheck.failed());
        assertEquals(ErrorType.MISSING_PARAMETER, failingCheck.paramError.errorType, "Parameter should have been invalid. (none were provided)");
        // they are checked in the order they are provided, so
        assertEquals(failingCheck.paramError.paramName, param1Name);

        Map<String, Object> missingArrayReqParams = new HashMap<>();
        ApiCustomParamCheck failingCheck2 = mapApiCustomParam.check(missingArrayReqParams);
        assertTrue(failingCheck2.failed());
        assertEquals(ErrorType.MISSING_PARAMETER, failingCheck2.paramError.errorType, "Should be missing array param");
        assertEquals(arrayParamName, failingCheck2.paramError.paramName);
    }

    @Test
    public void testConditionalCustomParam() {
        String param1Name = "param1";
        String param2Name = "param2";

        // We will require that either param1 or param2 is supplied, but not both
        ApiCustomParam<Map<String, Object>> customParam = (requestMap) -> {
            if (requestMap.get(param1Name) == null && requestMap.get(param2Name) == null) {
                return ApiCustomParamCheck.failure(ErrorType.MISSING_PARAMETER, "Neither 'param1' nor 'param2' was supplied.");
            } else if (requestMap.get(param1Name) != null && requestMap.get(param2Name) != null) {
                return ApiCustomParamCheck.failure(ErrorType.INVALID_PARAMETER, "Both 'param1' and 'param2' was supplied. Only 1 should be.");
            } else if (requestMap.get(param1Name) != null) {
                // do your check here. we will just pass for the purposes of the test
                Set<String> providedParamName = new HashSet<>(1);
                providedParamName.add(param1Name);
                return ApiCustomParamCheck.success("acustomparam", providedParamName);
            } else {
                // param2 not null
                Set<String> providedParamName = new HashSet<>(1);
                providedParamName.add(param2Name);
                return ApiCustomParamCheck.success("acustomparam", providedParamName);
            }
        };

        Map<String, Object> requestParams = new HashMap<>(1);
        requestParams.put(param1Name, "all pass so long as provided");
        ApiCustomParamCheck check1 = customParam.check(requestParams);
        assertTrue(check1.successful());
        assertTrue(check1.providedParamNames.contains(param1Name));

        Map<String, Object> requestParams2 = new HashMap<>(1);
        requestParams2.put(param2Name, "all pass so long as provided");
        ApiCustomParamCheck check2 = customParam.check(requestParams2);
        assertTrue(check2.successful());
        assertTrue(check2.providedParamNames.contains(param2Name));

        // failing tests

        Map<String, Object> failingRequestParams = new HashMap<>(2);
        failingRequestParams.put(param1Name, "");
        failingRequestParams.put(param2Name, "");
        ApiCustomParamCheck failingCheck1 = customParam.check(failingRequestParams);
        assertTrue(failingCheck1.failed());
        assertEquals(failingCheck1.paramError.errorType, ErrorType.INVALID_PARAMETER);

        Map<String, Object> failingRequestParams2 = new HashMap<>(2);
        ApiCustomParamCheck failingCheck2 = customParam.check(failingRequestParams2);
        assertTrue(failingCheck2.failed());
        assertEquals(failingCheck2.paramError.errorType, ErrorType.MISSING_PARAMETER);
    }

}
