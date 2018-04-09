package io.bhowell2.ApiLib;

import io.bhowell2.ApiLib.utils.ParamChecks;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Blake Howell
 */
public class ApiParamBuilderTest {

    @Test
    public void testApiParamBuilder() {
        ApiParamBuilder<String, Map<String, Object>> builder = ApiParamBuilder.builder("test", true,
                                                                                       (String s, Map<String, Object> m) -> (String) m.get(s))
                                                                              .addCheckFunction(s -> s.length() > 5 ? CheckFuncResult.success() : CheckFuncResult.failure());
        ApiParam<String, Map<String, Object>> param = builder.build();
        assertEquals(param.parameterName, "test");
        assertTrue(param.isRequired);
        assertEquals(param.paramCheckFuncs.length, 1);

        ApiParam<String, Map<String, Object>> copiedParam = ApiParamBuilder.copyFrom("test2", false, param).build();
        assertEquals(copiedParam.parameterName, "test2", "Should be different name than copied param.");
        assertFalse(copiedParam.isRequired);
        assertEquals(copiedParam.paramCheckFuncs.length, 1);

        ApiParam<String, Map<String, Object>> copiedParam2 = ApiParamBuilder.copyFrom(true, param)
                                                                            .addCheckFunction(s -> s.length() < 100 ? CheckFuncResult.success() :
                                                                                CheckFuncResult.failure()).build();
        // same name.
        assertEquals(copiedParam2.parameterName, "test", "Should be same name as copied param as it wasnt changed.");
        assertTrue(copiedParam2.isRequired);
        assertEquals(copiedParam2.paramCheckFuncs.length, 2, "Should have added a check function.");
    }

    @Test
    public void testFailureToBuild() {
        assertThrows(RuntimeException.class, () -> {
            ApiParam<String, Object> param = ApiParamBuilder.builder("test", true, (n, o) ->n)
                                                            .build();
        });
        assertThrows(RuntimeException.class, () -> {
            ApiParam<String, Object> param = ApiParamBuilder.builder("test", true, (n, o) -> n)
                                                            .addFormatFunction(s -> s.replace("a", "b"))
                                                            .build();
        });
    }

}
