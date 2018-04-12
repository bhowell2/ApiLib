package io.bhowell2.ApiLib;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The ApiObjParam is designed to represent some type of container object (e.g., a JsonObject). As far as APIs are concerned, there may be a root
 * JsonObject which does not have a name to be retrieved by. In this case, the ParamRetrievalFunc should just return the object itself.
 * @param <ObjectParamType> the object type of this object parameter (this is the type that will be returned from the retrieval function)
 * @param <ParentParamType> the object type that will be passed to the check method (the object type that will be passed into the retrieval function)
 * @author Blake Howell
 */
public class ApiObjParam<ObjectParamType, ParentParamType> {

    public final String paramName;
    final ParamRetrievalFunc<ObjectParamType, ParentParamType> retrievalFunction;
    final boolean continueOnOptionalFailure;
    final ApiParam<?, ObjectParamType>[] requiredParams;
    final ApiParam<?, ObjectParamType>[] optionalParams;
    final ApiObjParam<?, ObjectParamType>[] requiredObjParams;
    final ApiObjParam<?, ObjectParamType>[] optionalObjParams;
    final ApiCustomParam<ObjectParamType>[] customParams;

    /**
     * Used for top level objects (i.e., objects without a parameter name to retrieve them -- this could either be the root object or objects in an
     * array).
     * @param continueOnOptionalFailure
     * @param retrievalFunction
     * @param requiredParams
     * @param optionalParams
     * @param requiredObjParams
     * @param optionalObjParams
     * @param customParams
     */
    public ApiObjParam(boolean continueOnOptionalFailure,
                       ParamRetrievalFunc<ObjectParamType, ParentParamType> retrievalFunction,
                       ApiParam<?, ObjectParamType>[] requiredParams,
                       ApiParam<?, ObjectParamType>[] optionalParams,
                       ApiObjParam<?, ObjectParamType>[] requiredObjParams,
                       ApiObjParam<?, ObjectParamType>[] optionalObjParams,
                       ApiCustomParam<ObjectParamType>[] customParams) {
        this(null,
             continueOnOptionalFailure,
             retrievalFunction,
             requiredParams,
             optionalParams,
             requiredObjParams,
             optionalObjParams,
             customParams);
    }

    @SuppressWarnings("unchecked")
    public ApiObjParam(String paramName,
                       boolean continueOnOptionalFailure,
                       ParamRetrievalFunc<ObjectParamType, ParentParamType> retrievalFunction,
                       ApiParam<?, ObjectParamType>[] requiredParams,
                       ApiParam<?, ObjectParamType>[] optionalParams,
                       ApiObjParam<?, ObjectParamType>[] requiredObjParams,
                       ApiObjParam<?, ObjectParamType>[] optionalObjParams,
                       ApiCustomParam<ObjectParamType>[] customParams) {
        this.paramName = paramName;
        this.continueOnOptionalFailure = continueOnOptionalFailure;
        this.retrievalFunction = retrievalFunction;
        this.requiredParams = requiredParams;
        this.optionalParams = optionalParams;
        this.requiredObjParams = requiredObjParams;
        this.optionalObjParams = optionalObjParams;
        this.customParams = customParams;
    }

    /**
     * Returns boolean specifying whether or not to continue checking (and potentially passing) after an optional fails to check.
     * @return
     */
    public boolean getContinueOnOptionalFailure() {
        return continueOnOptionalFailure;
    }

//    public ParamRetrievalFunc<ObjectParamType, ParentParamType> getRetrievalFunction() {
//        return retrievalFunction;
//    }
//
//    public ApiParam<?, ObjectParamType>[] getRequiredParams() {
//        return requiredParams.clone();
//    }
//
//    public ApiParam<?, ObjectParamType>[] getOptionalParams() {
//        return optionalParams.clone();
//    }
//
//    public ApiCustomParam<ObjectParamType>[] getCustomParams() {
//        return customParams.clone();
//    }
//
//    public ApiObjParam<?, ObjectParamType>[] getRequiredObjParams() {
//        return requiredObjParams.clone();
//    }
//
//    public ApiObjParam<?, ObjectParamType>[] getOptionalObjParams() {
//        return optionalObjParams.clone();
//    }

    @SuppressWarnings({"unchecked", "null"})
    public ApiObjParamCheck check(ParentParamType requestParameters) {
        ObjectParamType objParamsToCheck = this.retrievalFunction.retrieveParameter(this.paramName, requestParameters);

        // TODO: Write code to dynamically choose best array sizes -- may have thread safety issues, so prob need sync
        // do not need to create objects for nothing here
        Set<String> providedParamNames = new HashSet<>(this.requiredParams.length + this.optionalObjParams.length / 2);
        Map<String, ApiCustomParamCheck> providedCustomParams = new HashMap<>(this.customParams.length);
        Map<String, ApiObjParamCheck> providedObjParams = new HashMap<>(this.requiredObjParams.length + this.optionalObjParams.length / 2);

        for (ApiParam<?, ObjectParamType> apiParam : requiredParams) {
            ApiParamCheck paramCheck = apiParam.check(objParamsToCheck);
            // if fails AND it is required OR it isn't required, but should continue NOT continue on optional failure
            if (paramCheck.failed()) {
                return ApiObjParamCheck.failed(wrapObjParamCheckError(paramCheck.paramError));
            }
            providedParamNames.add(paramCheck.parameterName);
        }

        for (ApiCustomParam<ObjectParamType> customParam : customParams) {
            try {
                ApiCustomParamCheck customParamCheck = customParam.check(objParamsToCheck);
                if (customParamCheck.failed()) {
                    return ApiObjParamCheck.failed(wrapObjParamCheckError(customParamCheck.paramError));
                }
                providedCustomParams.put(customParamCheck.customParameterName, customParamCheck);
                providedParamNames.addAll(customParamCheck.providedParamNames);
                providedObjParams.putAll(customParamCheck.providedObjParams);
            } catch (ClassCastException e) {
                return ApiObjParamCheck
                    .failed(wrapObjParamCheckError(new ParamError(ErrorType.PARAMETER_CAST,
                                           "Failed to case parameter in ApiCustomParameters check. " + e.getMessage(),
                                           "Custom Parameter Error.")));
            }
        }

        for (ApiObjParam<?, ObjectParamType> objParam : requiredObjParams) {
            try {
                ApiObjParamCheck objParamCheck = objParam.check(objParamsToCheck);
                if (objParamCheck.failed()) {
                    return ApiObjParamCheck.failed(wrapObjParamCheckError(objParamCheck.paramError));
                }
                // all objects checked here will have an object name (the only time that an object will not have an object name is when it is an array value (and, technically the root object))
                providedObjParams.put(objParamCheck.parameterName, objParamCheck);
                providedParamNames.add(objParamCheck.parameterName);
            } catch (ClassCastException e) {
                return ApiObjParamCheck
                    .failed(wrapObjParamCheckError(new ParamError(ErrorType.PARAMETER_CAST,
                                           "Failed to cast parameter to correct type. " + e.getMessage(),
                                           objParam.paramName)));
            }
        }

        for (ApiParam<?, ObjectParamType> apiParam : optionalParams) {
            ApiParamCheck paramCheck = apiParam.check(objParamsToCheck);
            if (paramCheck.failed()) {
                if (this.continueOnOptionalFailure || paramCheck.paramError.errorType == ErrorType.MISSING_PARAMETER) {
                    continue;
                }
                return ApiObjParamCheck.failed(wrapObjParamCheckError(paramCheck.paramError));
            }
            providedParamNames.add(paramCheck.parameterName);
        }

        for (ApiObjParam<?, ObjectParamType> objParam : optionalObjParams) {
            try {
                ApiObjParamCheck objParamCheck = objParam.check(objParamsToCheck);
                if (objParamCheck.failed()) {
                    if (this.continueOnOptionalFailure || objParamCheck.paramError.errorType == ErrorType.MISSING_PARAMETER) {
                        continue;
                    }
                    return ApiObjParamCheck.failed(wrapObjParamCheckError(objParamCheck.paramError));
                }
                providedObjParams.put(objParamCheck.parameterName, objParamCheck);
                providedParamNames.add(objParamCheck.parameterName);
            } catch (ClassCastException e) {
                return ApiObjParamCheck
                    .failed(wrapObjParamCheckError(new ParamError(ErrorType.PARAMETER_CAST,
                                                                  "Failed to cast parameter to correct type. " + e.getMessage(),
                                                                  objParam.paramName)));
            }
        }

        return ApiObjParamCheck.success(this.paramName, providedParamNames, providedObjParams, providedCustomParams);
    }

    /**
     * Returns an error that specifies the topmost object parameter in which the error occurred.
     * @param paramError
     * @return
     */
    private ParamError wrapObjParamCheckError(ParamError paramError) {
        if (this.paramName == null) {
            return paramError;
        }
        return new ParamError(paramError.errorType, paramError.errorMessage, paramError.parameterName + " of " + this.paramName);

    }

}
