package io.bhowell2.ApiLib;

import io.bhowell2.ApiLib.utils.IntegerParamChecks;
import io.bhowell2.ApiLib.utils.ParamChecks;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.bhowell2.ApiLib.extensions.map.ApiMapParamRetrievalFuncs.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
                           .addFormatInjectionFunction((name, map, formattedStr) -> {
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
    public void shouldThrowExceptionOnConstruction() {
        // No param retrieval function is provided
        assertThrows(RuntimeException.class, () -> {
            new ApiParam<String, Object>("whatever",
                                 null,  // param retrieval func
                                 null,
                                 null,
                                 new CheckFunc[]{ParamChecks.alwaysPass()});
        }, "Should throw runtime exception when parameter retrieval function is not provided.");

        // No check function is provided (null)
        assertThrows(RuntimeException.class, () -> {
            new ApiParam<String, Object>("whatever",
                                         (name, obj) -> name,  // param retrieval func (not really a correct use, but works here)
                                         null,
                                         null,
                                         null);
        }, "Should throw runtime exception when check functions are not provided.");

        // Check function is not provided (length 0)
        assertThrows(RuntimeException.class, () -> {
            new ApiParam<String, Object>("whatever",
                                         (name, obj) -> name,  // param retrieval func (not really a correct use, but works here)
                                         null,
                                         null,
                                         new CheckFunc[0]);
        }, "Should throw runtime exception when check functions are not provided (length 0).");

        // Format function is provided, but no function to inject the formatted value
        assertThrows(RuntimeException.class, () -> {
            new ApiParam<String, Object>("whatever",
                                         (name, obj) -> name,  // param retrieval func (not really a correct use, but works here)
                                         new FormatFunc[]{(s) -> "hey"},
                                         null,
                                         new CheckFunc[]{ParamChecks.alwaysPass()});
        }, "Should throw runtime exception when format functions are provided, but no FormatInsertFunc is provided.");
    }

}
