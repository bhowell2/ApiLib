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
//public class OrConditionalParameters extends ApiConditionalParameters {
//
//    public OrConditionalParameters(ApiParameter<?>... apiParameters) {
//        super(apiParameters);
//    }
//
//    public OrConditionalParameters(ApiParameter<?>[] apiParameters, ApiConditionalParameters[] conditionalParameters) {
//        super(apiParameters, conditionalParameters);
//    }
//
//    @Override
//    public ConditionalCheckTuple check(Map<String, Object> parameters) {
//        List<String> passedParameters = new ArrayList<>();
//        for (ApiParameter<?> parameter : apiParameters) {
//            ApiParameterCheckTuple checkTuple = parameter.check(parameters);
//            if (checkTuple.successful()) {
//                passedParameters.add(checkTuple.parameterName);
//            }
//        }
//        for (ApiConditionalParameters conditionalParams : conditionalParameters) {
//            ConditionalCheckTuple conditionalCheckTuple = conditionalParams.check(parameters);
//            if (conditionalCheckTuple.successful()) {
//                passedParameters.addAll(conditionalCheckTuple.parameterNames);
//            }
//        }
//        if (passedParameters.size() > 0) {
//            return ConditionalCheckTuple.success(passedParameters);
//        } else {
//            return ConditionalCheckTuple.failure(new ErrorTuple(ErrorType.MISSING_PARAMETER, "None of the required parameters were provided."));
//        }
//    }
//
//}
