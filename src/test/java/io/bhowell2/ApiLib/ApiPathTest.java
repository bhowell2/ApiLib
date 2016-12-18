package io.bhowell2.ApiLib;

import io.bhowell2.ApiLib.exceptions.UnsupportedApiVersionForPath;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Blake Howell
 */
public class ApiPathTest {

  static ExamplePathOneForTest_ContainsV0AndV1 pathOneForTest;

  @BeforeClass

  public static void setupClass() {
    pathOneForTest = new ExamplePathOneForTest_ContainsV0AndV1();
  }

  @Test
  public void shouldPassWithAdmissibleParametersForV0() {
    ApiVersion requestVersion = AppApiVersion.V0;
    Map<String, Object> requestParameters = new HashMap<>();
    requestParameters.put("ParamOne", 6);               // must be greater than 5
    requestParameters.put("ParamTwo", "pass this");     // 5 < string length <= 10
    requestParameters.put("ParamThree", "11");          // string length > 1
    assertTrue(pathOneForTest.check(requestVersion, requestParameters).isSuccessful());
  }


  @Test
  public void shouldFailWithInvalidParametersForV0() {
    ApiVersion requestVersion = AppApiVersion.V0;
    Map<String, Object> requestParameters = new HashMap<>();
    requestParameters.put("ParamOne", 4);               // must be greater than 5 - should cause failure
    requestParameters.put("ParamTwo", "pass this");     // 5 < string length <= 10
    requestParameters.put("ParamThree", "11");          // string length > 1
    assertFalse(pathOneForTest.check(requestVersion, requestParameters).isSuccessful());
  }

  @Test
  public void shouldUseLatestAvailableVersion() {
    // Expecting the path to check with the latest version - which would be V1
    // for V1 to pass, the length of ParamThree must be greater than 3
    ApiVersion requestVersion = AppApiVersion.V2;
    Map<String, Object> requestParameters = new HashMap<>();
    requestParameters.put("ParamOne", 6);               // must be greater than 5 - should cause failure
    requestParameters.put("ParamTwo", "pass this");     // 5 < string length <= 10
    requestParameters.put("ParamThree", "11");          // string length > 1
    assertFalse(pathOneForTest.check(requestVersion, requestParameters).isSuccessful());
  }

  @Test
  public void shouldReturnIllegalVersionException() {
    ApiPathParameters pathParameters = ApiPathParametersBuilder.builder(AppApiVersion.V2)
                                                               .build();
    ApiPath path = new ApiPath(pathParameters);
    PathParametersCheckReturnTuple returnTuple = path.check(AppApiVersion.V1, new HashMap<>());
    assertFalse(returnTuple.isSuccessful());
    assertTrue(returnTuple.getCheckFailure() instanceof UnsupportedApiVersionForPath);
  }

}
