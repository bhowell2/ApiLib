package io.bhowell2.ApiLib;

import java.util.List;
import java.util.Map;

/**
 * If there is an error in a nested parameter then it will bubble up to the topmost NestedParameter and return there. Therefore, the user will
 * never have to go to innerNestedParameters to get an error (and there will be no innerNestedParameters).
 *
 * @author Blake Howell
 */
public final class ApiObjectParamTuple {

    public final String parameterName;                              // only has parameter name if it is an object nested within another object
    public final List<String> providedParamNames;                   // name of parameters successfully checked/provided
    public final List<ApiCustomParamsTuple> providedCustomParams;   //
    public final Map<String, ApiObjectParamTuple> providedObjParams;       // if objects are nested within this object
    public final ErrorTuple errorTuple;                             //

    public ApiObjectParamTuple(List<String> providedParamNames,
                               Map<String, ApiObjectParamTuple> providedObjParams,
                               List<ApiCustomParamsTuple> providedCustomParams) {
        this(null, providedParamNames, providedObjParams, providedCustomParams);
    }

    public ApiObjectParamTuple(String parameterName,
                               List<String> providedParamNames,
                               Map<String, ApiObjectParamTuple> providedObjParams,
                               List<ApiCustomParamsTuple> providedCustomParams) {
        this.parameterName = parameterName;
        if (providedParamNames == null || providedParamNames.size() == 0) {
            this.providedParamNames = null;
        } else {
            this.providedParamNames = providedParamNames;
        }
        if (providedObjParams == null || providedObjParams.size() == 0) {
            this.providedObjParams = null;
        } else {
            this.providedObjParams = providedObjParams;
        }
        if (providedCustomParams == null || providedCustomParams.size() == 0) {
            this.providedCustomParams = null;
        } else {
            this.providedCustomParams = providedCustomParams;
        }
        this.errorTuple = null;
    }

    public ApiObjectParamTuple(ErrorTuple errorTuple) {
        this.errorTuple = errorTuple;
        this.parameterName = null;
        this.providedParamNames = null;
        this.providedObjParams = null;
//        this.providedArrayParams = null;
        this.providedCustomParams = null;
    }

    public boolean failed() {
        return errorTuple != null;
    }

    public boolean successful() {
        return errorTuple == null;
    }

    public boolean hasParamNames() {
        return providedParamNames != null;
    }

    public boolean hasInnerObjectParams() {
        return providedObjParams != null;
    }

    /**
     *
     * @param parameterName
     * @return
     */
    public boolean containsParameter(String parameterName) {
        for (String s : providedParamNames) {
            if (s.equals(parameterName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This linear search is much slower than using a Map, but right now I feel that the performance (in the single digit microseconds from rough micro-benchmark) will be fine as compared to
     *
     * @param parameterName
     * @return the tuple or NULL if it does not exist
     */
    public ApiObjectParamTuple getInnerObjectParamTuple(String parameterName) {
        return this.providedObjParams.get(parameterName);
    }

    /* Static creation methods */

    public static ApiObjectParamTuple success(String parameterName,
                                              List<String> providedParamNames,
                                              Map<String, ApiObjectParamTuple> providedObjParams,
                                              List<ApiCustomParamsTuple> providedCustomParams) {
        return new ApiObjectParamTuple(parameterName, providedParamNames, providedObjParams, providedCustomParams);
    }

    public static ApiObjectParamTuple failed(ErrorTuple errorTuple) {
        return new ApiObjectParamTuple(errorTuple);
    }

}
