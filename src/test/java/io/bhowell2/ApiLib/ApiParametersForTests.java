package io.bhowell2.ApiLib;

import io.bhowell2.ApiLib.extensions.map.MapRequestParameterRetrievalFunctions;
import io.bhowell2.ApiLib.utils.ParameterIntegerChecks;
import io.bhowell2.ApiLib.utils.ParameterStringChecks;

import java.util.Map;

import static io.bhowell2.ApiLib.extensions.map.MapRequestParameterRetrievalFunctions.*;

/**
 * Various parameters to use in tests.
 * @author Blake Howell
 */
public final class ApiParametersForTests {

    public static final ApiParameter<String, Map<String, Object>> STRING1 = ApiParameterBuilder.builder("String1", STRING_FROM_MAP)
                                                                                               .addCheckFunction(ParameterStringChecks.lengthGreaterThanOrEqual(1))
                                                                                               .addCheckFunction(ParameterStringChecks.lengthLessThan(100))
                                                                                               .build();

    public static final ApiParameter<String, Map<String, Object>> STRING2 = ApiParameterBuilder.builder("String2", STRING_FROM_MAP)
                                                                                               .addCheckFunction(ParameterStringChecks.lessThanNRepeatedChars(3))
                                                                                               .addCheckFunction(ParameterStringChecks
                                                                                                                     .lengthGreaterThanOrEqual(1))
                                                                                               .build();
    public static final ApiParameter<String, Map<String, Object>>  STRING3 = ApiParameterBuilder.builder("String3", STRING_FROM_MAP)
                                                                                                .addCheckFunction(ParameterStringChecks.lengthGreaterThanOrEqual(1))
                                                                                                .addCheckFunction(ParameterStringChecks.lengthLessThan(100))
                                                                                                .build();

    public static final ApiParameter<Integer, Map<String, Object>> INTEGER1 = ApiParameterBuilder.builder("Integer1", INTEGER_FROM_MAP)
                                                                                                 .addCheckFunction(ParameterIntegerChecks.valueLessThan(100))
                                                                                                 .build();

    public static final ApiParameter<Integer, Map<String, Object>> INTEGER2 = ApiParameterBuilder.builder("Integer2", INTEGER_FROM_MAP)
                                                                                                 .addCheckFunction(ParameterIntegerChecks.valueGreaterThan(100))
                                                                                                 .build();

    public static final ApiNestedParameter<Map<String, Object>, Map<String, Object>> NESTED_PARAM = ApiNestedParameterBuilder.builder("NestedParameter",
                                                                                                                                      MapRequestParameterRetrievalFunctions.INNER_MAP_FROM_MAP)
                                                                                                                             .addRequiredParameter(INTEGER1)
                                                                                                                             .addOptionalParameter(INTEGER2)
                                                                                                                             .build();

}
