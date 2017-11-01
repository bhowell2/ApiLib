package io.bhowell2.ApiLib;

import java.util.List;

/**
 * If there is an error in a nested parameter then it will bubble up to the topmost NestedParameter and return there. Therefore, the user will
 * never have to go to innerNestedParameters to get an error (and there will be no innerNestedParameters).
 *
 * @author Blake Howell
 */
public final class ApiNestedParamCheckTuple {

    public final String nestedParameterName;
    public final List<String> providedParameterNames;      // name of parameters successfully checked/provided
    public final List<ApiNestedParamCheckTuple> innerNestedParameters;
    public final ErrorTuple errorTuple;

    public ApiNestedParamCheckTuple(String nestedParameterName, List<String> providedParameterNames) {
        this(nestedParameterName, providedParameterNames, null);
    }

    public ApiNestedParamCheckTuple(String nestedParameterName, List<String> providedParameterNames, List<ApiNestedParamCheckTuple> innerNestedParameters) {
        this.nestedParameterName = nestedParameterName;
        this.providedParameterNames = providedParameterNames;
        if (innerNestedParameters == null || innerNestedParameters.size() == 0) {
            this.innerNestedParameters = null;
        } else {
            this.innerNestedParameters = innerNestedParameters;
        }
        this.errorTuple = null;
    }

    public ApiNestedParamCheckTuple(ErrorTuple errorTuple) {
        this.errorTuple = errorTuple;
        this.nestedParameterName = null;
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

    public static ApiNestedParamCheckTuple success(String nestedParameterName, List<String> providedParameterNames) {
        return new ApiNestedParamCheckTuple(nestedParameterName, providedParameterNames);
    }

    public static ApiNestedParamCheckTuple success(String nestedParameterName, List<String> providedParameterNames, List<ApiNestedParamCheckTuple>
        innerNestedParameters) {
        return new ApiNestedParamCheckTuple(nestedParameterName, providedParameterNames, innerNestedParameters);
    }

    public static ApiNestedParamCheckTuple failed(ErrorTuple errorTuple) {
        return new ApiNestedParamCheckTuple(errorTuple);
    }

}
