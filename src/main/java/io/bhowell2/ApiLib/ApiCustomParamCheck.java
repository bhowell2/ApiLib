package io.bhowell2.ApiLib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Custom parameters
 * @author Blake Howell
 */
public final class ApiCustomParamCheck {

    public final String customParameterName;                        // name of custom parameter to put in the ApiObjectParamCheck#providedCustomParams map
    public final Set<String> providedParamNames;                    // name of parameters successfully checked/provided
    public final Map<String, ApiObjParamCheck> providedObjParams;
    public final List<ApiObjParamCheck> arrayObjParams;       // if objects are nested within this custom parameter as arrays
    public final ParamError paramError;

    public ApiCustomParamCheck(String customParameterName, Set<String> providedParamNames,
                               Map<String, ApiObjParamCheck> providedObjParams, List<ApiObjParamCheck> arrayObjParams) {
        this.customParameterName = customParameterName;
        if (providedParamNames == null) {
            this.providedParamNames = new HashSet<>(0);
        } else {
            this.providedParamNames = providedParamNames;
        }
        if (providedObjParams == null) {
            this.providedObjParams = new HashMap<>(0);
        } else {
            this.providedObjParams = providedObjParams;
        }
        if (arrayObjParams == null) {
            this.arrayObjParams = new ArrayList<>(0);
        } else {
            this.arrayObjParams = arrayObjParams;
        }
        this.paramError = null;
    }

    public ApiCustomParamCheck(ParamError paramError) {
        this.paramError = paramError;
        this.customParameterName = null;
        this.arrayObjParams = null;
        this.providedParamNames = null;
        this.providedObjParams = null;
    }

    public boolean failed() {
        return paramError != null;
    }

    public boolean successful() {
        return paramError == null;
    }


    /* Static creation methods */

    /**
     * In the case that it needs to be stated the check passed, but there is really no information to return other than a successful check.
     *
     * E.g., the user wants to know whether all values in an array pass a check:
     * 1. The user could simply implement a custom parameter and return the name of the array.
     * 2. The user could make an ApiArrayParameter, add an ApiCustomParameter check which would just pass.
     * @return
     */
    public static ApiCustomParamCheck success(String customParameterName) {
        return new ApiCustomParamCheck(customParameterName, null, null, null);
    }

    public static ApiCustomParamCheck success(String customParameterName,
                                              Set<String> providedParamNames) {
        return success(customParameterName, providedParamNames, null, null);
    }

    public static ApiCustomParamCheck success(String customParameterName,
                                              Map<String, ApiObjParamCheck> providedObjParams) {
        return success(customParameterName, null, providedObjParams, null);
    }

    public static ApiCustomParamCheck success(String customParameterName,
                                              List<ApiObjParamCheck> arrayObjParams) {
        return success(customParameterName, null, null, arrayObjParams);
    }

    /**
     *
     * @param customParameterName
     * @param providedParamNames
     * @param arrayObjParams
     * @return
     */
    public static ApiCustomParamCheck success(String customParameterName,
                                              Set<String> providedParamNames,
                                              Map<String, ApiObjParamCheck> providedObjParams,
                                              List<ApiObjParamCheck> arrayObjParams) {
        return new ApiCustomParamCheck(customParameterName, providedParamNames, providedObjParams, arrayObjParams);
    }

    public static ApiCustomParamCheck failure(ParamError paramError) {
        return new ApiCustomParamCheck(paramError);
    }

    public static ApiCustomParamCheck failure(ErrorType errorType, String parameterName) {
        return failure(errorType, parameterName, "Custom parameter failed to check.");
    }

    public static ApiCustomParamCheck failure(ErrorType errorType, String parameterName, String failureMessage) {
        return new ApiCustomParamCheck(new ParamError(errorType, failureMessage, parameterName));
    }

}
