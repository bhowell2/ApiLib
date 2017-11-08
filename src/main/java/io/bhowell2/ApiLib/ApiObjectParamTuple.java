package io.bhowell2.ApiLib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * If there is an error in a nested parameter then it will bubble up to the topmost NestedParameter and return there. Therefore, the user will
 * never have to go to innerNestedParameters to get an error (and there will be no innerNestedParameters).
 *
 * @author Blake Howell
 */
public final class ApiObjectParamTuple {

    public final String parameterName;                              // only has parameter name if it is an object nested within another object
    public final Set<String> providedParamNames;                   // name of parameters successfully checked/provided
    public final List<ApiCustomParamsTuple> providedCustomParams;   //
    public final Map<String, ApiObjectParamTuple> providedObjParams;       // if objects are nested within this object
    public final ErrorTuple errorTuple;                             //

    public ApiObjectParamTuple(Set<String> providedParamNames,
                               Map<String, ApiObjectParamTuple> providedObjParams,
                               List<ApiCustomParamsTuple> providedCustomParams) {
        this(null, providedParamNames, providedObjParams, providedCustomParams);
    }

    public ApiObjectParamTuple(String parameterName,
                               Set<String> providedParamNames,
                               Map<String, ApiObjectParamTuple> providedObjParams,
                               List<ApiCustomParamsTuple> providedCustomParams) {
        this.parameterName = parameterName;
        if (providedParamNames == null) {
            this.providedParamNames = new HashSet<>();
        } else {
            this.providedParamNames = providedParamNames;
        }
        if (providedObjParams == null) {
            this.providedObjParams = new HashMap<>();
        } else {
            this.providedObjParams = providedObjParams;
        }
        if (providedCustomParams == null) {
            this.providedCustomParams = new ArrayList<>();
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

    public boolean containsParameter(String parameterName) {
        return providedParamNames.contains(parameterName);
    }

    public List<String> getProvidedParamsAsList() {
        return new ArrayList<>(this.providedParamNames);
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
                                              Set<String> providedParamNames,
                                              Map<String, ApiObjectParamTuple> providedObjParams,
                                              List<ApiCustomParamsTuple> providedCustomParams) {
        return new ApiObjectParamTuple(parameterName, providedParamNames, providedObjParams, providedCustomParams);
    }

    public static ApiObjectParamTuple failed(ErrorTuple errorTuple) {
        return new ApiObjectParamTuple(errorTuple);
    }

}
