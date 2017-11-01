package io.bhowell2.ApiLib;

import java.util.List;

/**
 * For top level parameters, List<String> is simply returned with the provided parameters names. However, if the request has nested parameters, a
 * list of InnerNestedParamsCheckTuple is returned.
 * @author Blake Howell
 */
public final class ApiNestedParamCheckTuple {

    public final List<String> providedParameterNames;      // name of parameters successfully checked/provided
    public final ApiParamErrorTuple errorTuple;

    public ApiNestedParamCheckTuple(List<String> providedParameterNames) {
        this.providedParameterNames = providedParameterNames;
        this.errorTuple = null;
    }

    public ApiNestedParamCheckTuple(ApiParamErrorTuple errorTuple) {
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

    public static ApiNestedParamCheckTuple failed(ApiParamErrorTuple errorTuple) {
        return new ApiNestedParamCheckTuple(errorTuple);
    }

    /**
     * Used for any
     */
    public class InnerNestedParamsCheckTuple {

    }

}
