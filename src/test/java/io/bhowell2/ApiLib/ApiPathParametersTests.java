package io.bhowell2.ApiLib;

import io.bhowell2.ApiLib.utils.ParameterIntegerChecks;
import io.bhowell2.ApiLib.utils.ParameterStringChecks;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Blake Howell
 */
public class ApiPathParametersTests {

    public static final ApiPathParameters V0_PATH_PARAMS = ApiPathParametersBuilder.builder(ApiVersionForTests.V0, false)
                                                                                   .addRequiredParameter(ApiParametersForTests.INTEGER1)
                                                                                   .addOptionalParameter(ApiParametersForTests.STRING1)
                                                                                   .build();

    @Test
    public void shouldFailFromMissingParameter() {
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put(ApiParametersForTests.STRING1.getParameterName(), "this passes");
        PathParamsCheckTuple checkTuple = V0_PATH_PARAMS.check(requestParams);
        assertTrue(checkTuple.failed());
        assertEquals(checkTuple.errorTuple.errorType, ErrorType.MISSING_PARAMETER);
    }

    @Test
    public void shouldFailFromInvalidOptionalParameter() {
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put(ApiParametersForTests.INTEGER1.getParameterName(), 99);
        requestParams.put(ApiParametersForTests.STRING1.getParameterName(), "");
        PathParamsCheckTuple checkTuple = V0_PATH_PARAMS.check(requestParams);
        assertTrue(checkTuple.failed());
        assertEquals(checkTuple.errorTuple.errorType, ErrorType.INVALID_PARAMETER);
        assertEquals(checkTuple.errorTuple.parameterName, ApiParametersForTests.STRING1.getParameterName());
    }

    @Test
    public void shouldPassFromAllValidParameters() {

    }

}
