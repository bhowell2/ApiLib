package io.bhowell2.apilib.extensions.map;


import io.bhowell2.apilib.utils.paramchecks.AnyParamChecks;
import org.junit.jupiter.api.Test;

/**
 * @author Blake Howell
 */
public class ApiMapBuilderTest {

    @Test
    public void testBuildMapParam() {
        ApiMapParamBuilder.builder("test", String.class)
                          .addCheckFunction(AnyParamChecks.alwaysPass())
                          .build();
    }

}
