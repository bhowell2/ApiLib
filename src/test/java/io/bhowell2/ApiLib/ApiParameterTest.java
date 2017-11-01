package io.bhowell2.ApiLib;

import io.bhowell2.ApiLib.utils.ParameterIntegerChecks;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.bhowell2.ApiLib.utils.MapRequestParameterRetrievalFunctions.*;
import static org.junit.Assert.*;

/**
 * @author Blake Howell
 */
public class ApiParameterTest {



    @Test
    public void shouldFailParameterCheck() {
        ApiParameter<Integer, Map<String, Object>> failingIntegerApiParameter = ApiParameterBuilder.builder("TestInteger", INTEGER_FROM_MAP)
                                                                              .addCheckFunction(ParameterIntegerChecks.valueGreaterThanOrEqualTo(0))
                                                                              .addCheckFunction(i -> FunctionCheckTuple.failure("Failed!"))
                                                                              .build();
        Map<String, Object> stringObjectMap = new HashMap<>(1);
        stringObjectMap.put("TestInteger", 55);
        ApiParameterCheckTuple checkTuple = failingIntegerApiParameter.check(stringObjectMap);
        assertTrue(checkTuple.failed());
        assertEquals(checkTuple.errorTuple.errorType, ErrorType.INVALID_PARAMETER);
    }

    @Test
    public void shouldFailWithMissingParameter() {
        ApiParameter<Integer, Map<String, Object>> failingIntegerApiParameter = ApiParameterBuilder.builder("TestInteger", INTEGER_FROM_MAP)
                                                                              .addCheckFunction(ParameterIntegerChecks.valueGreaterThanOrEqualTo(0))
                                                                              .addCheckFunction(i -> FunctionCheckTuple.failure("Failed!"))
                                                                              .build();
        Map<String, Object> stringObjectMap = new HashMap<>(0);
        ApiParameterCheckTuple checkTuple = failingIntegerApiParameter.check(stringObjectMap);
        assertTrue(checkTuple.failed());
        assertEquals(checkTuple.errorTuple.errorType, ErrorType.MISSING_PARAMETER);
    }

    @Test
    public void shouldSuccessfullyCheckParameter() {
        ApiParameter<Integer, Map<String, Object>> passingIntegerApiParameter = ApiParameterBuilder.builder("TestInteger", INTEGER_FROM_MAP)
                                                                              .addCheckFunction(ParameterIntegerChecks.valueLessThan(100))
                                                                              .addCheckFunction(ParameterIntegerChecks.valueGreaterThan(5))
                                                                              .build();
        Map<String, Object> stringObjectMap = new HashMap<>(1);
        stringObjectMap.put("TestInteger", 55);
        ApiParameterCheckTuple checkTuple = passingIntegerApiParameter.check(stringObjectMap);
        assertTrue(checkTuple.successful());
        assertEquals("TestInteger", checkTuple.parameterName);
    }

    @Test
    public void shouldSuccessfullyCastParameterAndCheck() {
        ApiParameter<Integer, Map<String, Object>> passingIntegerApiParameter = ApiParameterBuilder.builder("TestInteger", CAST_AND_REPLACE_STRING_TO_INT_FROM_MAP)
                                                                              .addCheckFunction(ParameterIntegerChecks.valueLessThan(100))
                                                                              .addCheckFunction(ParameterIntegerChecks.valueGreaterThan(5))
                                                                              .build();
        Map<String, Object> stringObjectMap = new HashMap<>(1);
        stringObjectMap.put("TestInteger", "55");
        assertTrue(stringObjectMap.get("TestInteger") instanceof String);
        ApiParameterCheckTuple checkTuple = passingIntegerApiParameter.check(stringObjectMap);
        assertTrue(checkTuple.successful());
        assertEquals("TestInteger", checkTuple.parameterName);
        assertTrue(stringObjectMap.get("TestInteger") instanceof Integer);
    }

}
