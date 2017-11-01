//package io.bhowell2.ApiLib;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
///**
// * All parameters must be provided and pass checks. (See AND truth table.)
// * @author Blake Howell
// */
//public class AndConditionalParameters extends ApiConditionalParameters {
//
//    public AndConditionalParameters(ApiParameter<?>... apiParameters) {
//        super(apiParameters);
//    }
//
//    public AndConditionalParameters(ApiParameter<?>[] apiParameters, ApiConditionalParameters[] conditionalParameters) {
//        super(apiParameters, conditionalParameters);
//    }
//
//    @Override
//    public ConditionalCheckTuple check(Map<String, Object> parameters) {
//        // these are AND parameters, so everything must pass
//        List<String> passedParameters = new ArrayList<>(apiParameters.length + conditionalParameters.length * 2);
//        for (ApiParameter<?> parameter : apiParameters) {
//            ApiParameterCheckTuple checkTuple = parameter.check(parameters);
//            if (checkTuple.failed()) {
//                return ConditionalCheckTuple.failure(checkTuple.errorTuple);
//            } else {
//                passedParameters.add(checkTuple.parameterName);
//            }
//        }
//        for (ApiConditionalParameters conditionalParams : conditionalParameters) {
//            ConditionalCheckTuple conditionalCheckTuple = conditionalParams.check(parameters);
//            if (conditionalCheckTuple.failed()) {
//                return ConditionalCheckTuple.failure(conditionalCheckTuple.errorTuple);
//            } else {
//                passedParameters.addAll(conditionalCheckTuple.parameterNames);
//            }
//        }
//        // if there are no failures, the AND condition is satisfied
//        return ConditionalCheckTuple.success(passedParameters);
//    }
//
//}
