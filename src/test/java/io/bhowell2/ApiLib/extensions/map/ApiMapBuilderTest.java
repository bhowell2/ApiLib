package io.bhowell2.ApiLib.extensions.map;


import io.bhowell2.ApiLib.utils.ParamChecks;
import org.junit.jupiter.api.Test;

/**
 * @author Blake Howell
 */
public class ApiMapBuilderTest {

    @Test
    public void testBuildMapParam() {
        ApiMapParamBuilder.builder("test", true, String.class)
                          .addCheckFunction(ParamChecks.alwaysPass())
                          .build();
    }

}
