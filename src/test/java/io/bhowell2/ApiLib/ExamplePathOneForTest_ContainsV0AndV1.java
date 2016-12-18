package io.bhowell2.ApiLib;

import io.bhowell2.ApiLib.utils.ParameterStringChecks;

/**
 * Each path could make their own PATH object by extending
 *
 * @author Blake Howell
 */
public class ExamplePathOneForTest_ContainsV0AndV1 extends ApiPath {

  // Parameter must be larger than value 5
  static ApiParameter<Integer> PARAM_ONE_V0 = ApiParameterBuilder.builder("ParamOne", Integer.class)
                                                                .addCheckFunction(i -> {
                                                                  if (i > 5) {
                                                                    return FunctionCheckReturnTuple.success();
                                                                  } else {
                                                                    return FunctionCheckReturnTuple.failure();
                                                                  }
                                                                }).build();

  // Parameter must be greater than length 5, but less than or equal to length 10
  static ApiParameter<String> PARAM_TWO_V0 = ApiParameterBuilder.builder("ParamTwo", String.class)
                                                                .addCheckFunction(ParameterStringChecks.lengthLessThanOrEqual(10))
                                                                .addCheckFunction(ParameterStringChecks.lengthGreaterThan(5))
                                                                .build();

  // Param three is optional
  static ApiParameter<String> PARAM_THREE_V0 = ApiParameterBuilder.builder("ParamThree", String.class)
                                                                  .addCheckFunction(ParameterStringChecks.lengthGreaterThan(1))
                                                                  .build();

  static ApiPathParameters V0_PARAMS = ApiPathParametersBuilder.builder(AppApiVersion.V0)
                                                               .addRequiredParameter(PARAM_ONE_V0)
                                                               .addRequiredParameter(PARAM_TWO_V0)
                                                               .addOptionalParameter(PARAM_THREE_V0)
                                                               .build();

  // if they don't change, recommended to just reinitialize them like so - this should help reduce mistakes
  static ApiParameter<Integer> PARAM_ONE_V1 = PARAM_ONE_V0;
  static ApiParameter<String> PARAM_TWO_V1 = PARAM_TWO_V0;
  static ApiParameter<String> PARAM_THREE_V1 = ApiParameterBuilder.builder("ParamThree", String.class)
                                                                  .addCheckFunction(ParameterStringChecks.lengthGreaterThan(3))
                                                                  .build();

  static ApiPathParameters V1_PARAMS = ApiPathParametersBuilder.builder(AppApiVersion.V1)
                                                               .addRequiredParameter(PARAM_ONE_V1)
                                                               .addRequiredParameter(PARAM_TWO_V1)
                                                               .addRequiredParameter(PARAM_THREE_V1)
                                                               .build();

  public ExamplePathOneForTest_ContainsV0AndV1() {
    super("PathOne", V0_PARAMS, V1_PARAMS);
  }

}
