//package io.bhowell2.ApiLib;
//
//import io.bhowell2.ApiLib.exceptions.InvalidApiParameterException;
//import io.bhowell2.ApiLib.exceptions.SafeMissingMultipleApiParametersException;
//import io.bhowell2.ApiLib.exceptions.ParameterCastException;
//import io.bhowell2.ApiLib.exceptions.ParameterCheckException;
//
///**
// * @author Blake Howell
// */
//public final class ConditionalParamTuple {
//
//  public final String[] parameterNames;
//  public final ParameterCheckException error;
//
//
//  public ConditionalParamTuple(String[] parameterNames) {
//    this.parameterNames = parameterNames;
//    this.error = null;
//  }
//
//  public ConditionalParamTuple(ParameterCheckException error) {
//    this.error = error;
//    this.parameterNames = null;
//  }
//
//  public boolean isSuccessful() {
//    return error == null;
//  }
//
//  public Exception getError() {
//    return error;
//  }
//
//  /* Static creation methods */
//
//  public static ConditionalParamTuple success(String[] parameterNames) {
//    return new ConditionalParamTuple(parameterNames);
//  }
//
//  public static ConditionalParamTuple missingParameterFailure(String[] missingParamNames) {
//    StringBuilder missingParams = new StringBuilder();
//    missingParams.append("The following parameters were missing from the request: ");
//    for (String s : missingParamNames) {
//      missingParams.append(s).append(", ");
//    }
//    missingParams.replace(missingParams.length() - 2, missingParams.length(), "."); // replace the trailing ", " with a period.
//    return new ConditionalParamTuple(new SafeMissingMultipleApiParametersException(missingParams.toString()));
//  }
//
//  public static ConditionalParamTuple invalidParameterFailure(String parameterNames) {
//    return new ConditionalParamTuple(new InvalidApiParameterException(parameterNames));
//  }
//
//  public static ConditionalParamTuple invalidParameterFailure(String parameterName, String reasonForFailure) {
//    return new ConditionalParamTuple(new InvalidApiParameterException(parameterName, reasonForFailure));
//  }
//
//  public static ParameterCheckTuple parameterCastException(String parameterName, Class<?> clazz) {
//    return new ParameterCheckTuple(new ParameterCastException(parameterName, clazz));
//  }
//
//}
