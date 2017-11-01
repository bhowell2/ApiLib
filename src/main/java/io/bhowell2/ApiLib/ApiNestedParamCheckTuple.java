package io.bhowell2.ApiLib;

import java.util.List;

/**
 * @author Blake Howell
 */
public final class ApiNestedParamCheckTuple {

    final List<String> providedParameterNames;      // name of parameters successfully checked/provided
    final ErrorTuple errorTuple;

    public ApiNestedParamCheckTuple(List<String> providedParameterNames) {
        this.providedParameterNames = providedParameterNames;
        this.errorTuple = null;
    }

    public ApiNestedParamCheckTuple(ErrorTuple errorTuple) {
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

    public static ApiNestedParamCheckTuple success(List<String> providedParameterNames) {
        return new ApiNestedParamCheckTuple(providedParameterNames);
    }

    public static ApiNestedParamCheckTuple failed(ErrorTuple errorTuple) {
        return new ApiNestedParamCheckTuple(errorTuple);
    }

}
