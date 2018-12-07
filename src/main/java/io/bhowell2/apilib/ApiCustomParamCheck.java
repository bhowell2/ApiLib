package io.bhowell2.apilib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Custom parameters need to at least return the name of the custom parameter. This does not actually have to be the name of a parameter in the
 * object, but it is required so that the user may retrieve the check (or know that the custom parameter was provided and passed). HOWEVER, it is
 * recommended that the the name of the provided parameters is returned if they were provided (as providedParamNames).
 *
 * arrayObjParams should contain the successfully checked ApiObjParamCheck if the custom parameter is checking the elements of an array and they
 * are objects. The array might look like this: [{a: "a param", b: "2nd param"}, {a: "aaaa"}], and then it is necessary to know the parameters of
 * each object (hence, the ApiObjcParamCheck). Otherwise, if an array is to be checked, it could just be set in providedParamNames if the object
 * checks out correctly (e.g., array parameters are numbers that are all greater than X)
 *
 * providedObjParams should contain the name and ApiObjParamCheck of any object parameters that the custom parameter checks.
 *
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
        this.providedParamNames = providedParamNames != null ? providedParamNames : new HashSet<>(0);
        this.providedObjParams = providedObjParams != null ? providedObjParams : new HashMap<>(0);
        this.arrayObjParams = arrayObjParams != null ? arrayObjParams : new ArrayList<>(0);
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

    /**
     *
     * @param objName
     * @return the ApiObjParamCheck or null
     */
    public ApiObjParamCheck getObjParamCheck(String objName) {
        return this.providedObjParams.get(objName);
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
