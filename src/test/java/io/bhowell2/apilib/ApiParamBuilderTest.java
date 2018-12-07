package io.bhowell2.apilib;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Blake Howell
 */
public class ApiParamBuilderTest {

    @Test
    public void testApiParamBuilder() {
        ApiParamBuilder<String, Map<String, Object>> builder = ApiParamBuilder.builder("test",
                                                                                       (String s, Map<String, Object> m) -> (String) m.get(s))
                                                                              .addCheckFunction(s -> s.length() > 5 ? CheckFuncResult.success() : CheckFuncResult.failure());
        ApiParam<String, Map<String, Object>> param = builder.build();
        assertEquals(param.parameterName, "test");
        assertEquals(param.checkFuncs.length, 1);

        ApiParam<String, Map<String, Object>> copiedParam = ApiParamBuilder.copyFrom("test2", param).build();
        assertEquals(copiedParam.parameterName, "test2", "Should be different name than copied param.");
        assertEquals(copiedParam.checkFuncs.length, 1);

        ApiParam<String, Map<String, Object>> copiedParam2 = ApiParamBuilder.copyFrom(param)
                                                                            .addCheckFunction(s -> s.length() < 100 ? CheckFuncResult.success() :
                                                                                CheckFuncResult.failure()).build();
        // same name.
        assertEquals(copiedParam2.parameterName, "test", "Should be same name as copied param as it wasnt changed.");
        assertEquals(copiedParam2.checkFuncs.length, 2, "Should have added a check function.");
    }

    @Test
    public void shouldFailToBuild() {
        assertThrows(RuntimeException.class, () -> {
            ApiParam<String, Object> param = ApiParamBuilder.builder("test", (n, o) ->n)
                                                            .build();
        }, "Should fail to build because no ParamChecks were provided.");
        assertThrows(RuntimeException.class, () -> {
            ApiParam<String, Object> param = ApiParamBuilder.builder("test", (n, o) -> n)
                                                            .addFormatFunction(s -> s.replace("a", "b"))
                                                            .build();
        }, "Should fail to build because a format function was provided, but no injection function was provided.");
    }

}
