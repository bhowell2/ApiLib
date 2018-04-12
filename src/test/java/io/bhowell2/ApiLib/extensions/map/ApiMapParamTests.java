package io.bhowell2.ApiLib.extensions.map;

import io.bhowell2.ApiLib.ApiParamCheck;
import io.bhowell2.ApiLib.CheckFunc;
import io.bhowell2.ApiLib.utils.IntegerParamChecks;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Blake Howell
 */
public class ApiMapParamTests {

    @Test
    public void shouldParseButNotReplaceIfString() {
        ApiMapParam<Integer> intMapParam = new ApiMapParam<Integer>("int",
                                                                    Integer.class,
                                                                    true,
                                                                    false,
                                                                    new CheckFunc[]{IntegerParamChecks.valueGreaterThan(5)});
        Map<String, Object> requestParams = new HashMap<>(1);
        requestParams.put("int", "12");
        ApiParamCheck check = intMapParam.check(requestParams);
        assertTrue(check.successful());
        assertEquals("12", requestParams.get("int"), "Should be of string type. Not integer.");
        assertEquals("int", check.parameterName);

        // Should successfully check int
        Map<String, Object> requestParams2 = new HashMap<>(1);
        requestParams2.put("int", 12);
        ApiParamCheck check2 = intMapParam.check(requestParams2);
        assertTrue(check2.successful());
        assertEquals(12, requestParams2.get("int"), "Should be of integer type. Not String.");
        assertEquals("int", check2.parameterName);
    }

    @Test
    public void shouldParseAndReplaceIfString() {
        ApiMapParam<Integer> intMapParam = new ApiMapParam<Integer>("int",
                                                                    Integer.class,
                                                                    true,
                                                                    true,
                                                                    new CheckFunc[]{IntegerParamChecks.valueGreaterThan(5)});
        Map<String, Object> requestParams = new HashMap<>(1);
        requestParams.put("int", "12");
        ApiParamCheck check = intMapParam.check(requestParams);
        assertTrue(check.successful());
        assertEquals(12, requestParams.get("int"), "Should be of integer type. Not string -- string should have been replaced.");
        assertEquals("int", check.parameterName);
    }

}
