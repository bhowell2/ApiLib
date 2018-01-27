package io.bhowell2.ApiLib;

import io.bhowell2.ApiLib.utils.ParameterIntegerChecks;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.bhowell2.ApiLib.extensions.map.MapParameterRetrievalFunctions.*;
import static org.junit.Assert.*;

/**
 * @author Blake Howell
 */
public class ApiParameterTest {



    @Test
    public void shouldFailParameterCheck() {
        ApiParameter<Integer, Map<String, Object>> failingIntegerApiParameter = ApiParameterBuilder.builder("TestInteger", getIntegerFromMap())
                                                                              .addCheckFunction(ParameterIntegerChecks.valueGreaterThanOrEqualTo(0))
                                                                              .addCheckFunction(i -> CheckFunctionTuple.failure("Failed!"))
                                                                              .build();
        Map<String, Object> stringObjectMap = new HashMap<>(1);
        stringObjectMap.put("TestInteger", 55);
        ApiParamTuple checkTuple = failingIntegerApiParameter.check(stringObjectMap);
        assertTrue(checkTuple.failed());
        assertEquals(checkTuple.errorTuple.errorType, ErrorType.INVALID_PARAMETER);
    }

    @Test
    public void shouldFailWithMissingParameter() {
        ApiParameter<Integer, Map<String, Object>> failingIntegerApiParameter = ApiParameterBuilder.builder("TestInteger", getIntegerFromMap())
                                                                              .addCheckFunction(ParameterIntegerChecks.valueGreaterThanOrEqualTo(0))
                                                                              .addCheckFunction(i -> CheckFunctionTuple.failure("Failed!"))
                                                                              .build();
        Map<String, Object> stringObjectMap = new HashMap<>(0);
        ApiParamTuple checkTuple = failingIntegerApiParameter.check(stringObjectMap);
        assertTrue(checkTuple.failed());
        assertEquals(checkTuple.errorTuple.errorType, ErrorType.MISSING_PARAMETER);
    }

    @Test
    public void shouldSuccessfullyCheckParameter() {
        ApiParameter<Integer, Map<String, Object>> passingIntegerApiParameter = ApiParameterBuilder.builder("TestInteger", getIntegerFromMap())
                                                                              .addCheckFunction(ParameterIntegerChecks.valueLessThan(100))
                                                                              .addCheckFunction(ParameterIntegerChecks.valueGreaterThan(5))
                                                                              .build();
        Map<String, Object> stringObjectMap = new HashMap<>(1);
        stringObjectMap.put("TestInteger", 55);
        ApiParamTuple checkTuple = passingIntegerApiParameter.check(stringObjectMap);
        assertTrue(checkTuple.successful());
        assertEquals("TestInteger", checkTuple.parameterName);
    }

    @Test
    public void shouldSuccessfullyCastParameterAndCheck() {
        ApiParameter<Integer, Map<String, Object>> passingIntegerApiParameter = ApiParameterBuilder.builder("TestInteger", getIntegerFromMap(true,
                                                                                                                                             true))
                                                                                                   .addCheckFunction(ParameterIntegerChecks
                                                                                                                         .valueLessThan(100))
                                                                                                   .addCheckFunction(ParameterIntegerChecks
                                                                                                                         .valueGreaterThan(5))
                                                                                                   .build();
        Map<String, Object> stringObjectMap = new HashMap<>(1);
        stringObjectMap.put("TestInteger", "55");
        assertTrue(stringObjectMap.get("TestInteger") instanceof String);
        ApiParamTuple checkTuple = passingIntegerApiParameter.check(stringObjectMap);
        assertTrue(checkTuple.successful());
        assertEquals("TestInteger", checkTuple.parameterName);
        assertTrue(stringObjectMap.get("TestInteger") instanceof Integer);
    }

}
