package io.bhowell2.ApiLib;

import io.bhowell2.ApiLib.utils.ParameterIntegerChecks;
import io.bhowell2.ApiLib.utils.ParameterStringChecks;

/**
 * Various parameters to use in tests.
 * @author Blake Howell
 */
public final class ApiParametersForTests {

    public static final ApiParameter<String> STRING1 = ApiParameterBuilder.builder("String1", String.class)
                                                                          .addCheckFunction(ParameterStringChecks.lengthGreaterThanOrEqual(1))
                                                                          .addCheckFunction(ParameterStringChecks.lengthLessThan(100))
                                                                          .build();

    public static final ApiParameter<String> STRING2 = ApiParameterBuilder.builder("String2", String.class)
                                                                          .addCheckFunction(ParameterStringChecks.lessThanNRepeatedChars(3))
                                                                          .addCheckFunction(ParameterStringChecks
                                                                                                .lengthGreaterThanOrEqual(1))
                                                                          .build();
    public static final ApiParameter<String> STRING3 = ApiParameterBuilder.builder("String3", String.class)
                                                                          .addCheckFunction(ParameterStringChecks.lengthGreaterThanOrEqual(1))
                                                                          .addCheckFunction(ParameterStringChecks.lengthLessThan(100))
                                                                          .build();

    public static final ApiParameter<Integer> INTEGER1 = ApiParameterBuilder.builder("Integer1", Integer.class)
                                                                            .addCheckFunction(ParameterIntegerChecks.valueLessThan(100))
                                                                            .build();

    public static final ApiParameter<Integer> INTEGER2 = ApiParameterBuilder.builder("Integer2", Integer.class)
                                                                            .addCheckFunction(ParameterIntegerChecks.valueGreaterThan(100))
                                                                            .build();


}
