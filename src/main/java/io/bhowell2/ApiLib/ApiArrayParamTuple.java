package io.bhowell2.ApiLib;

import java.util.List;

/**
 * @author Blake Howell
 */
public class ApiArrayParamTuple {

    public final String parameterName;
    public final List<ApiCustomParamsTuple> providedCustomParams;
    public final ErrorTuple errorTuple;

    public ApiArrayParamTuple(List<ApiCustomParamsTuple> providedCustomParams) {
        this(null, providedCustomParams);
    }

    public ApiArrayParamTuple(String parameterName, List<ApiCustomParamsTuple> providedCustomParams) {
        this.parameterName = parameterName;
        this.providedCustomParams = providedCustomParams;
        this.errorTuple = null;
    }

    public ApiArrayParamTuple(ErrorTuple errorTuple) {
        this.errorTuple = errorTuple;
        this.providedCustomParams = null;
        this.parameterName = null;
    }

    public boolean failed() {
        return errorTuple != null;
    }

    public boolean success() {
        return errorTuple == null;
    }

    /* */

    public static ApiArrayParamTuple failed(ErrorTuple errorTuple) {
        return new ApiArrayParamTuple(errorTuple);
    }

    public static ApiArrayParamTuple success(List<ApiCustomParamsTuple> providedCustomParams) {
        return new ApiArrayParamTuple(providedCustomParams);
    }

    public static ApiArrayParamTuple success(String parameterName, List<ApiCustomParamsTuple> providedCustomParams) {
        return new ApiArrayParamTuple(parameterName, providedCustomParams);
    }

}
