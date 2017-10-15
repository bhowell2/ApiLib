package io.bhowell2.ApiLib;

import io.bhowell2.ApiLib.utils.ParameterIntegerChecks;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Blake Howell
 */
public class ApiParameterTest {

  @Test
  public void shouldReturnFailure() {
    ApiParameter<Integer> failingIntegerApiParameter = ApiParameterBuilder.builder("TestInteger", Integer.class)
                                                                          .addCheckFunction(i -> FunctionCheckTuple.failure("Failed!"))
                                                                          .build();
    Map<String, Object> stringObjectMap = new HashMap<>(1);
    stringObjectMap.put("TestInteger", 55);
    ApiParamCheckTuple checkTuple = failingIntegerApiParameter.check(stringObjectMap);
    assertTrue(checkTuple.failed());
    assertEquals(checkTuple.errorTuple.errorType, ErrorType.INVALID_PARAMETER);
  }

  @Test
  public void shouldFailFromParameterNotProvided() {
    ApiParameter<Integer> failingIntegerApiParameter = ApiParameterBuilder.builder("TestInteger", Integer.class)
                                                                          .addCheckFunction(i -> FunctionCheckTuple.failure("Failed!"))
                                                                          .build();
    Map<String, Object> stringObjectMap = new HashMap<>(0);
    ApiParamCheckTuple checkTuple = failingIntegerApiParameter.check(stringObjectMap);
    assertTrue(checkTuple.failed());
    assertEquals(checkTuple.errorTuple.errorType, ErrorType.MISSING_PARAMETER);
  }

  @Test
  public void shouldPassFromParameterMeetingChecks() {
    ApiParameter<Integer> passingIntegerApiParameter = ApiParameterBuilder.builder("TestInteger", Integer.class)
                                                                          .addCheckFunction(ParameterIntegerChecks.valueGreaterThan(5))
                                                                          .build();
    Map<String, Object> stringObjectMap = new HashMap<>(1);
    stringObjectMap.put("TestInteger", 55);
    ApiParamCheckTuple checkTuple = passingIntegerApiParameter.check(stringObjectMap);
    assertTrue(checkTuple.isSuccessful());
    assertEquals("TestInteger", checkTuple.parameterName);
  }

  @Test
  public void shouldSuccessfulyCastParameterAndCheck() {
    ApiParameter<Integer> passingIntegerApiParameter = ApiParameterBuilder.builder("TestInteger", Integer.class)
                                                                          .setParameterCastFunction(o -> Integer.parseInt((String) o))
                                                                          .addCheckFunction(ParameterIntegerChecks.valueGreaterThan(5))
                                                                          .build();
    Map<String, Object> stringObjectMap = new HashMap<>(1);
    stringObjectMap.put("TestInteger", "55");
    ApiParamCheckTuple checkTuple = passingIntegerApiParameter.check(stringObjectMap);
    assertTrue(checkTuple.isSuccessful());
    assertEquals("TestInteger", checkTuple.parameterName);
    assertTrue(stringObjectMap.get("TestInteger") instanceof Integer);
  }

}
