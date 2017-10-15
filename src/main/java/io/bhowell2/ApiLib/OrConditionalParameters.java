//package io.bhowell2.ApiLib;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
///**
// * At least one parameter must be provided and pass checks, but all parameters are acceptable. (See OR truth table.)
// * @author Blake Howell
// */
//public class OrConditionalParameters extends ConditionalParameters {
//
//  public OrConditionalParameters(ApiParameter<?>... apiParameters) {
//    this(apiParameters, null);
//  }
//
//  public OrConditionalParameters(ApiParameter<?>[] apiParameters, ConditionalParameters[] conditionalParameters) {
//    super(apiParameters, conditionalParameters);
//  }
//
//  @Override
//  public ConditionalCheckTuple check(Map<String, Object> parameters) {
//    List<String> passedParameters = new ArrayList<>();
//    List<ErrorTuple> failedParameterChecks = new ArrayList<>();
//    for (ApiParameter<?> parameter : apiParameters) {
//      ApiParamCheckTuple paramCheckTuple = parameter.check(parameters);
//      if (paramCheckTuple.failed()) {
//        failedParameterChecks.add(paramCheckTuple.errorTuple);
//      } else {
//        passedParameters.add(paramCheckTuple.parameterName);
//      }
//    }
//    for (ConditionalParameters conditionalParams : conditionalParameters) {
//      ConditionalCheckTuple conditionalCheckTuple = conditionalParams.check(parameters);
//      if (conditionalCheckTuple.failed()) {
//        failedParameterChecks.addAll();
//      } else {
//        passedParameters.addAll(conditionalCheckTuple.parameterNames);
//      }
//    }
//    if (passedParameters.size() > 0) {
//      return ConditionalCheckTuple.success(passedParameters);
//    } else {
//      return ConditionalCheckTuple.failure(new ErrorTuple(ErrorType.MISSING_PARAMETER, "None of the required parameters were provided."));
//    }
//  }
//
//}
