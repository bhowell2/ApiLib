package io.bhowell2.ApiLib;

import java.util.List;

/**
 * @author Blake Howell
 */
public final class ApiPathRequestParamsCheckTuple {

    public final List<String> providedParameterNames;      // name of parameters successfully checked/provided
    public final List<ApiNestedParamCheckTuple> innerNestedParameters;
    public final ErrorTuple errorTuple;

    public ApiPathRequestParamsCheckTuple(List<String> providedParameterNames) {
        this(providedParameterNames, null);
    }

    public ApiPathRequestParamsCheckTuple(List<String> providedParameterNames, List<ApiNestedParamCheckTuple> innerNestedParameters) {
        this.providedParameterNames = providedParameterNames;
        if (innerNestedParameters == null || innerNestedParameters.size() == 0) {
            this.innerNestedParameters = null;
        } else {
            this.innerNestedParameters = innerNestedParameters;
        }
        this.errorTuple = null;
    }

    public ApiPathRequestParamsCheckTuple(ErrorTuple errorTuple) {
        this.errorTuple = errorTuple;
        this.providedParameterNames = null;
        this.innerNestedParameters = null;
    }

    public boolean failed() {
        return errorTuple != null;
    }

    public boolean successful() {
        return errorTuple == null;
    }

    public boolean hasInnerNestedParameters() {
        return innerNestedParameters != null;
    }

    /* Static creation methods */

    public static ApiPathRequestParamsCheckTuple success(List<String> providedParameterNames) {
        return new ApiPathRequestParamsCheckTuple(providedParameterNames);
    }

    public static ApiPathRequestParamsCheckTuple success(List<String> providedParameterNames, List<ApiNestedParamCheckTuple>
        innerNestedParameters) {
        return new ApiPathRequestParamsCheckTuple(providedParameterNames, innerNestedParameters);
    }

    public static ApiPathRequestParamsCheckTuple failed(ErrorTuple errorTuple) {
        return new ApiPathRequestParamsCheckTuple(errorTuple);
    }

}
