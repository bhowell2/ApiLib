package io.bhowell2.ApiLib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The return object from checking an ApiObjParam. An object may contain individual parameters (e.g., string, int), nested object parameters, and
 * arrays. The array parameters should be checked using ApiCustomParam. (ApiCustomParam can also be used to satisfy conditional requirements. E.g.,
 * param1 OR param2 is required, but perhaps not both.) The ApiCustomParam's parameterNames and
 * @author Blake Howell
 */
public final class ApiObjParamCheck {

    // only has parameter name if it is an object nested within another object
    public final String parameterName;

    // Name of successfully checked parameters. This includes inner object params as well as custom params that return a list of params
    public final Set<String> providedParamNames;                            // name of parameters successfully checked/provided
    public final Map<String, ApiCustomParamCheck> providedCustomParams;     // custom parameters must have a name, so they can be retrieved
    public final Map<String, ApiObjParamCheck> providedObjParams;           // if objects are nested within this object they should be added here so that their ApiObjectParamTuple may be obtained to see what parameters were provided
    public final ParamError paramError;                                     // if an error occurred will be non-null (all others, besides, potentially, parameterName will be null)

    public ApiObjParamCheck(Set<String> providedParamNames,
                            Map<String, ApiObjParamCheck> providedObjParams,
                            Map<String, ApiCustomParamCheck> providedCustomParams) {
        this(null, providedParamNames, providedObjParams, providedCustomParams);
    }

    public ApiObjParamCheck(String parameterName,
                            Set<String> providedParamNames,
                            Map<String, ApiObjParamCheck> providedObjParams,
                            Map<String, ApiCustomParamCheck> providedCustomParams) {
        this.parameterName = parameterName;
        this.providedParamNames = providedParamNames != null ? providedParamNames : new HashSet<>(0);
        this.providedObjParams = providedObjParams != null ? providedObjParams : new HashMap<>(0);
        this.providedCustomParams = providedCustomParams != null ? providedCustomParams : new HashMap<>(0);
        this.paramError = null;
    }

    public ApiObjParamCheck(ParamError paramError) {
        this.paramError = paramError;
        this.parameterName = null;
        this.providedParamNames = null;
        this.providedObjParams = null;
        this.providedCustomParams = null;
    }

    public boolean failed() {
        return paramError != null;
    }

    public boolean successful() {
        return paramError == null;
    }

    public boolean containsParameter(String parameterName) {
        return providedParamNames.contains(parameterName);
    }

    /**
     * @param objectParamName name of the object parameter that was checked
     * @return the ApiObjectParamTuple or null
     */
    public ApiObjParamCheck getObjectParamTuple(String objectParamName) {
        return providedObjParams.get(objectParamName);
    }

    /**
     * @param customParamName name that was given to the ApiCustomParamsTuple on checking
     * @return the ApiCustomParamsTuple or null
     */
    public ApiCustomParamCheck getCustomParamsTuple(String customParamName) {
        return providedCustomParams.get(customParamName);
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
    public ApiObjParamCheck getInnerObjectParamTuple(String parameterName) {
        return this.providedObjParams.get(parameterName);
    }

    /* Static creation methods */

    public static ApiObjParamCheck success(String parameterName,
                                           Set<String> providedParamNames,
                                           Map<String, ApiObjParamCheck> providedObjParams,
                                           Map<String, ApiCustomParamCheck> providedCustomParams) {
        return new ApiObjParamCheck(parameterName, providedParamNames, providedObjParams, providedCustomParams);
    }

    public static ApiObjParamCheck failed(ParamError paramError) {
        return new ApiObjParamCheck(paramError);
    }

}
