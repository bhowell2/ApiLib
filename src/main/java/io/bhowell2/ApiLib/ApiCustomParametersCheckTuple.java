package io.bhowell2.ApiLib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Return for {@link ApiCustomParameters#check(Object)}
 * @author Blake Howell
 */
public class ApiCustomParametersCheckTuple {

    final List<String> providedParameterNames;
    final ErrorTuple errorTuple;

    public ApiCustomParametersCheckTuple(String... providedParameterNames) {
        this(new ArrayList<String>(Arrays.asList(providedParameterNames)));
    }

    public ApiCustomParametersCheckTuple(List<String> providedParameterNames) {
        this.providedParameterNames = providedParameterNames;
        this.errorTuple = null;
    }

    public ApiCustomParametersCheckTuple(ErrorTuple errorTuple) {
        this.errorTuple = errorTuple;
        this.providedParameterNames = null;
    }

    public boolean failed() {
        return errorTuple != null;
    }

    public boolean successful() {
        return errorTuple == null;
    }

  /* Static creation methods */

    public static ApiCustomParametersCheckTuple success(String... parameterNames) {
        return new ApiCustomParametersCheckTuple(parameterNames);
    }

    public static ApiCustomParametersCheckTuple failure(ErrorTuple errorTuple) {
        return new ApiCustomParametersCheckTuple(errorTuple);
    }

    public static ApiCustomParametersCheckTuple failure(ErrorType errorType, String parameterName) {
        return failure(errorType, parameterName, null);
    }

    public static ApiCustomParametersCheckTuple failure(ErrorType errorType, String parameterName, String failureMessage) {
        return new ApiCustomParametersCheckTuple(new ErrorTuple(errorType, failureMessage, parameterName));
    }

}
