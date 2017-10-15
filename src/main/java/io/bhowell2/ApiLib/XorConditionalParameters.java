//package io.bhowell2.ApiLib;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
///**
// * Only one of the possible parameters should be provided and pass checks. If multiple parameters are provided, the test automatically fails.
// * @author Blake Howell
// */
//public class XorConditionalParameters extends ConditionalParameters {
//
//  public XorConditionalParameters(ApiParameter<?>... apiParameters) {
//    this(apiParameters, null);
//  }
//
//  public XorConditionalParameters(ApiParameter<?>[] apiParameters, ConditionalParameters[] conditionalParameters) {
//    super(apiParameters, conditionalParameters);
//  }
//
//  @Override
//  public ConditionalCheckTuple check(Map<String, Object> parameters) {
//    // only one check can pass, but not multiple
//    List<String> passedParameters = new ArrayList<>();
//    List<ErrorTuple> failedParameterChecks = new ArrayList<>();
//
//  }
//
//}
