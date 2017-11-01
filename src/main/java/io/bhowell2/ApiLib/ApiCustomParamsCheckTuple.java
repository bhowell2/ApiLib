package io.bhowell2.ApiLib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Return for {@link ApiCustomParameters#check(Object)}
 * @author Blake Howell
 */
public class ApiCustomParamsCheckTuple {

    final List<String> providedParameterNames;
    final ApiParamErrorTuple errorTuple;

    public ApiCustomParamsCheckTuple(String... providedParameterNames) {
        this(new ArrayList<String>(Arrays.asList(providedParameterNames)));
    }

    public ApiCustomParamsCheckTuple(List<String> providedParameterNames) {
        this.providedParameterNames = providedParameterNames;
        this.errorTuple = null;
    }

    public ApiCustomParamsCheckTuple(ApiParamErrorTuple errorTuple) {
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

    public static ApiCustomParamsCheckTuple success(String... parameterNames) {
        return new ApiCustomParamsCheckTuple(parameterNames);
    }

    public static ApiCustomParamsCheckTuple failure(ApiParamErrorTuple errorTuple) {
        return new ApiCustomParamsCheckTuple(errorTuple);
    }

    public static ApiCustomParamsCheckTuple failure(ErrorType errorType, String parameterName) {
        return failure(errorType, parameterName, null);
    }

    public static ApiCustomParamsCheckTuple failure(ErrorType errorType, String parameterName, String failureMessage) {
        return new ApiCustomParamsCheckTuple(new ApiParamErrorTuple(errorType, failureMessage, parameterName));
    }

}
