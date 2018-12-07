package io.bhowell2.apilib;

import io.bhowell2.apilib.utils.IntegerParamChecks;
import io.bhowell2.apilib.utils.ParamChecks;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.bhowell2.apilib.extensions.map.utils.ApiMapParamRetrievalFuncs.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Blake Howell
 */
public class ApiParamTest {

    @Test
    public void shouldFailWithInvalidParameterErrorType() {
        ApiParam<Integer, Map<String, Object>> failingIntegerApiParam =
            ApiParamBuilder.builder("TestInteger", getIntegerFromMap())
                           .addCheckFunction(IntegerParamChecks.valueGreaterThanOrEqualTo(0))
                           .addCheckFunction(i -> CheckFuncResult.failure("Failed!"))
                           .build();
        Map<String, Object> stringObjectMap = new HashMap<>(1);
        stringObjectMap.put("TestInteger", 55);
        ApiParamCheck checkTuple = failingIntegerApiParam.check(stringObjectMap);
        assertTrue(checkTuple.failed());
        assertEquals(checkTuple.paramError.errorType, ErrorType.INVALID_PARAMETER);
    }

    @Test
    public void shouldFailWithParameterCastErrorType() {
        ApiParam<Integer, Map<String, Object>> failingIntegerApiParam =
            ApiParamBuilder.builder("TestInteger", getIntegerFromMap())
                           .addCheckFunction(IntegerParamChecks.valueGreaterThanOrEqualTo(0))
                           .addCheckFunction(i -> CheckFuncResult.failure("Failed!"))
                           .build();
        Map<String, Object> stringObjectMap = new HashMap<>(1);
        stringObjectMap.put("TestInteger", "hey");
        ApiParamCheck checkTuple = failingIntegerApiParam.check(stringObjectMap);
        assertTrue(checkTuple.failed());
        assertEquals(checkTuple.paramError.errorType, ErrorType.PARAMETER_CAST);
    }

    @Test
    public void shouldFailWithMissingParameter() {
        ApiParam<Integer, Map<String, Object>> failingIntegerApiParam =
            ApiParamBuilder.builder("TestInteger", getIntegerFromMap())
                           .addCheckFunction(IntegerParamChecks.valueGreaterThanOrEqualTo(0))
                           .addCheckFunction(i -> CheckFuncResult.failure("Failed!"))
                           .build();
        Map<String, Object> stringObjectMap = new HashMap<>(0);
        ApiParamCheck checkTuple = failingIntegerApiParam.check(stringObjectMap);
        assertTrue(checkTuple.failed());
        assertEquals(checkTuple.paramError.errorType, ErrorType.MISSING_PARAMETER);
    }

    @Test
    public void shouldSuccessfullyCheckParameter() {
        ApiParam<Integer, Map<String, Object>> passingIntegerApiParam =
            ApiParamBuilder.builder("TestInteger", getIntegerFromMap())
                           .addCheckFunction(IntegerParamChecks.valueLessThan(100))
                           .addCheckFunction(IntegerParamChecks.valueGreaterThan(5))
                           .build();
        Map<String, Object> stringObjectMap = new HashMap<>(1);
        stringObjectMap.put("TestInteger", 55);
        ApiParamCheck checkTuple = passingIntegerApiParam.check(stringObjectMap);
        assertTrue(checkTuple.successful());
        assertEquals("TestInteger", checkTuple.parameterName);
    }

    // TODO: Remove this and put in map param.
    @Test
    public void shouldSuccessfullyCastParameterAndCheck() {
        ApiParam<Integer, Map<String, Object>> passingIntegerApiParam =
            ApiParamBuilder.builder("TestInteger", getIntegerFromMap(true, true))
                           .addCheckFunction(IntegerParamChecks.valueLessThan(100))
                           .addCheckFunction(IntegerParamChecks.valueGreaterThan(5))
                           .build();
        Map<String, Object> stringObjectMap = new HashMap<>(1);
        stringObjectMap.put("TestInteger", "55");
        assertTrue(stringObjectMap.get("TestInteger") instanceof String);
        ApiParamCheck checkTuple = passingIntegerApiParam.check(stringObjectMap);
        assertTrue(checkTuple.successful());
        assertEquals("TestInteger", checkTuple.parameterName);
        assertTrue(stringObjectMap.get("TestInteger") instanceof Integer);
    }

    @Test
    public void shouldSuccessfullyFormatParamAndCheck() {
        String paramName = "p1";
        ApiParam<String, Map<String, Object>> param =
            ApiParamBuilder.builder(paramName, (String name, Map<String, Object> m) -> (String) m.get(name))
                           .addFormatFunction(s -> s.substring(0, 3))
                           .addFormatInsertionFunction((name, map, formattedStr) -> {
                               map.put(name, formattedStr);
                           })
                           .addCheckFunction(ParamChecks.alwaysPass())
                           .build();
        Map<String, Object> requestParams = new HashMap<>(1);
        requestParams.put(paramName, "will be length 3");
        ApiParamCheck check = param.check(requestParams);
        assertTrue(check.successful());
        assertEquals(3, ((String)requestParams.get(paramName)).length(), "String should have been formatted to length 3.");
    }

    @Test
    public void shouldSuccessfullyCheckNullParameter() {
        String paramName = "p1";
        ApiParam<String, Map<String, Object>> param =
            ApiParamBuilder.builder(paramName, (String name, Map<String, Object> m) -> (String) m.get(name))
                           .addFormatFunction(s -> s.substring(0, 3))
                           .addFormatInsertionFunction((name, map, formattedStr) -> {
                               map.put(name, formattedStr);
                           })
                           .addCheckFunction(ParamChecks.alwaysPass())
                           .setCanBeNull(true)
                           .setNullRetrievalCheckFunc((name, map) -> map.containsKey(name))
                           .build();
        Map<String, Object> requestParams = new HashMap<>(1);
        requestParams.put(paramName, null);
        ApiParamCheck check = param.check(requestParams);
        assertTrue(check.successful(), "Should have successfully checked the parameter when it was null.");
        assertFalse(check.failed(), "Just a redundant sanity check.");
    }

    @Test
    public void shouldFailToCheckNullParameter() {
        String paramName = "p1";
        ApiParam<String, Map<String, Object>> param =
            ApiParamBuilder.builder(paramName, (String name, Map<String, Object> m) -> (String) m.get(name))
                           .addFormatFunction(s -> s.substring(0, 3))
                           .addFormatInsertionFunction((name, map, formattedStr) -> {
                               map.put(name, formattedStr);
                           })
                           .addCheckFunction(ParamChecks.alwaysPass())
                           .setCanBeNull(true)
                           .setNullRetrievalCheckFunc((name, map) -> map.containsKey(name))
                           .build();
        Map<String, Object> requestParams = new HashMap<>(1);
        ApiParamCheck check = param.check(requestParams);
        assertTrue(check.failed(), "Should have successfully checked the parameter when it was null.");
        ApiParam<String, Map<String, Object>> param2 =
            ApiParamBuilder.builder(paramName, (String name, Map<String, Object> m) -> (String) m.get(name))
                           .addFormatFunction(s -> s.substring(0, 3))
                           .addFormatInsertionFunction((name, map, formattedStr) -> {
                               map.put(name, formattedStr);
                           })
                           .addCheckFunction(ParamChecks.alwaysPass())
                           .build();
        ApiParamCheck check2 = param2.check(requestParams);
        assertTrue(check.failed());
    }


    @Test
    public void shouldThrowExceptionOnConstruction() {
        // No param retrieval function is provided
        assertThrows(RuntimeException.class, () -> {
            new ApiParam<String, Object>("whatever",
                                 null,  // param retrieval func
                                 null,
                                 null,
                                 new CheckFunc[]{ParamChecks.alwaysPass()},
                                         false,
                                         null);
        }, "Should throw runtime exception when parameter retrieval function is not provided.");

        // No check function is provided (null)
        assertThrows(RuntimeException.class, () -> {
            new ApiParam<String, Object>("whatever",
                                         (name, obj) -> name,  // param retrieval func (not really a correct use, but works here)
                                         null,
                                         null,
                                         null,
                                         false,
                                         null);
        }, "Should throw runtime exception when check functions are not provided.");

        // Check function is not provided (length 0)
        assertThrows(RuntimeException.class, () -> {
            new ApiParam<String, Object>("whatever",
                                         (name, obj) -> name,  // param retrieval func (not really a correct use, but works here)
                                         null,
                                         null,
                                         new CheckFunc[0],
                                         false,
                                         null);
        }, "Should throw runtime exception when check functions are not provided (length 0).");

        // Format function is provided, but no function to inject the formatted value
        assertThrows(RuntimeException.class, () -> {
            new ApiParam<String, Object>("whatever",
                                         (name, obj) -> name,  // param retrieval func (not really a correct use, but works here)
                                         new FormatFunc[]{(s) -> "hey"},
                                         null,
                                         new CheckFunc[]{ParamChecks.alwaysPass()},
                                         false,
                                         null);
        }, "Should throw runtime exception when format functions are provided, but no FormatInsertFunc is provided.");

        assertThrows(RuntimeException.class, () -> {
           new ApiParam<String, Object>("whatever",
                                        (name, obj) -> name,
                                        new FormatFunc[]{(s) -> "hey"},
                                        (name, params, formattedParam) -> {},
                                        new CheckFunc[]{ParamChecks.alwaysFail()},
                                        true,
                                        null);
        }, "Should throw runtime when can be null is set to true, but no NullParamRetrievalCheckFunc is provided.");
    }

}
