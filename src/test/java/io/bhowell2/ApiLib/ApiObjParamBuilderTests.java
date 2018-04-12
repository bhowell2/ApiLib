package io.bhowell2.ApiLib;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Blake Howell
 */
public class ApiObjParamBuilderTests {

    @Test
    public void shouldFailToBuild() {
        assertThrows(RuntimeException.class, () -> {
            ApiObjParamBuilder.rootObjBuilder((String name, Map<String, Object> params) -> params)
                              .addRequiredParams(ApiParametersForTests.INTEGER2, ApiParametersForTests.INTEGER2)
                              .build();
        }, "Should have thrown runtime exception from parameter being added twice.");

        assertThrows(RuntimeException.class, () -> {
            ApiObjParamBuilder.rootObjBuilder((String name, Map<String, Object> params) -> params)
                              .addRequiredParams(ApiParametersForTests.INTEGER2)
                              .addRequiredParam(ApiParametersForTests.INTEGER1)
                              .addRequiredParam(ApiParametersForTests.INTEGER2)
                              .build();
        }, "Should have thrown runtime exception from parameter being added twice.");

        assertThrows(RuntimeException.class, () -> {
            ApiObjParamBuilder.rootObjBuilder((String name, Map<String, Object> params) -> params)
                              .addRequiredParams(ApiParametersForTests.INTEGER2)
                              .addRequiredParam(ApiParametersForTests.INTEGER1)
                              .addOptionalParam(ApiParametersForTests.INTEGER2)
                              .build();
        }, "Should have thrown runtime exception from parameter being added twice.");

        assertThrows(RuntimeException.class, () -> {
            ApiObjParamBuilder.rootObjBuilder((String name, Map<String, Object> params) -> params)
                              .addRequiredObjParams(ApiParametersForTests.INNER_OBJ_PARAM, ApiParametersForTests.INNER_OBJ_PARAM)
                              .build();
        }, "Should have thrown runtime exception from object parameter being added twice.");

        assertThrows(RuntimeException.class, () -> {
            ApiObjParamBuilder.rootObjBuilder((String name, Map<String, Object> params) -> params)
                              .addRequiredObjParams(ApiParametersForTests.INNER_OBJ_PARAM)
                              .addRequiredObjParam(ApiParametersForTests.INNER_OBJ_PARAM)
                              .build();
        }, "Should have thrown runtime exception from object parameter being added twice.");
    }

}
