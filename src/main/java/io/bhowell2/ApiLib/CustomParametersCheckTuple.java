package io.bhowell2.ApiLib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Return of {@link ApiCustomParameters#check(Map)}
 * @author Blake Howell
 */
public class CustomParametersCheckTuple {

    final List<String> parameterNames;
    final ErrorTuple errorTuple;

    public CustomParametersCheckTuple(String... parameterNames) {
        this(null, new ArrayList<String>(Arrays.asList(parameterNames)));
    }

    public CustomParametersCheckTuple(List<String> parameterNames) {
        this(null, parameterNames);
    }

    public CustomParametersCheckTuple(ErrorTuple errorTuple, List<String> parameterNames) {
        this.errorTuple = errorTuple;
        this.parameterNames = parameterNames;
    }

    public boolean failed() {
        return errorTuple != null;
    }

    public boolean successful() {
        return errorTuple == null;
    }

    public ErrorTuple getErrorTuple() {
        return errorTuple;
    }

    public List<String> getParameterNames() {
        return parameterNames;
    }

  /* Static creation methods */

    public static CustomParametersCheckTuple success(String... parameterNames) {
        return new CustomParametersCheckTuple(parameterNames);
    }

    public static CustomParametersCheckTuple failure(ErrorTuple errorTuple) {
        return new CustomParametersCheckTuple(errorTuple, null);
    }

    public static CustomParametersCheckTuple failure(ErrorType errorType, String parameterName) {
        return failure(errorType, parameterName, null);
    }

    public static CustomParametersCheckTuple failure(ErrorType errorType, String parameterName, String failureMessage) {
        return new CustomParametersCheckTuple(new ErrorTuple(errorType, failureMessage, parameterName), null);
    }

}
