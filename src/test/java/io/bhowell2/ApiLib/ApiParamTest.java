package io.bhowell2.ApiLib;

import io.bhowell2.ApiLib.utils.IntegerParamChecks;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.bhowell2.ApiLib.extensions.map.ApiMapParamRetrievalFunc.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Blake Howell
 */
public class ApiParamTest {

    @Test
    public void shouldFailParameterCheck() {
        ApiParam<Integer, Map<String, Object>> failingIntegerApiParam =
            ApiParamBuilder.builder("TestInteger", false, getIntegerFromMap())
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
    public void shouldFailWithMissingParameter() {
        ApiParam<Integer, Map<String, Object>> failingIntegerApiParam =
            ApiParamBuilder.builder("TestInteger", false, getIntegerFromMap())
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
            ApiParamBuilder.builder("TestInteger", false, getIntegerFromMap())
                           .addCheckFunction(IntegerParamChecks.valueLessThan(100))
                           .addCheckFunction(IntegerParamChecks.valueGreaterThan(5))
                           .build();
        Map<String, Object> stringObjectMap = new HashMap<>(1);
        stringObjectMap.put("TestInteger", 55);
        ApiParamCheck checkTuple = passingIntegerApiParam.check(stringObjectMap);
        assertTrue(checkTuple.successful());
        assertEquals("TestInteger", checkTuple.parameterName);
    }

    @Test
    public void shouldSuccessfullyCastParameterAndCheck() {
        ApiParam<Integer, Map<String, Object>> passingIntegerApiParam =
            ApiParamBuilder.builder("TestInteger", false, getIntegerFromMap(true, true))
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

}
