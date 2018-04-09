package io.bhowell2.ApiLib;

import io.bhowell2.ApiLib.extensions.map.ApiMapParamRetrievalFunc;
import io.bhowell2.ApiLib.utils.IntegerParamChecks;
import io.bhowell2.ApiLib.utils.StringParamChecks;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static io.bhowell2.ApiLib.extensions.map.ApiMapParamRetrievalFunc.*;

/**
 * Various parameters to use in tests.
 * @author Blake Howell
 */
public final class ApiParametersForTests {

    public static final ApiParam<String, Map<String, Object>> STRING1 =
        ApiParamBuilder.builder("String1", false, STRING_FROM_MAP)
                       .addCheckFunction(StringParamChecks.lengthGreaterThanOrEqual(1))
                       .addCheckFunction(StringParamChecks.lengthLessThan(100))
                       .build();

    public static final ApiParam<String, Map<String, Object>> STRING2 =
        ApiParamBuilder.builder("String2", false, STRING_FROM_MAP)
                       .addCheckFunction(StringParamChecks.lessThanNRepeatedChars(3))
                       .addCheckFunction(StringParamChecks
                                                 .lengthGreaterThanOrEqual(1))
                       .build();
    public static final ApiParam<String, Map<String, Object>> STRING3 =
        ApiParamBuilder.builder("String3", false, STRING_FROM_MAP)
                       .addCheckFunction(StringParamChecks.lengthGreaterThanOrEqual(1))
                       .addCheckFunction(StringParamChecks.lengthLessThan(100))
                       .build();


    public static final ApiParam<Integer, Map<String, Object>> INTEGER1 =
        ApiParamBuilder.builder("Integer1", false, getIntegerFromMap(false))
                       .addCheckFunction(IntegerParamChecks.valueLessThan(100))
                       .build();

    public static final ApiParam<Integer, Map<String, Object>> INTEGER2 =
        ApiParamBuilder.builder("Integer2", false, getIntegerFromMap(false))
                       .addCheckFunction(IntegerParamChecks.valueGreaterThan(100))
                       .build();

    public static final ApiObjParam<Map<String, Object>, Map<String, Object>> INNER_OBJ_PARAM =
        ApiObjParamBuilder.builder("NestedParameter",
                                   false,
                                   ApiMapParamRetrievalFunc.INNER_MAP_FROM_MAP)
                          .addParameter(INTEGER1)
                          .addParameter(INTEGER2)
                          .build();

    @SuppressWarnings("unchecked")
    public static final ApiCustomParam<Map<String, Object>> CUSTOM_ARRAY1 = map -> {
        for (Integer i : (List<Integer>)map.get("Array1")) {
            if (i < 10)
                return ApiCustomParamCheck
                    .failure(new ParamError(ErrorType.INVALID_PARAMETER, "Integers in list must be greater than or equal to 10", "Array1"));
        }
        return ApiCustomParamCheck.success("Array1", new HashSet<>(Arrays.asList("Array1")), null, null);
    };

}
