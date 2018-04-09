package io.bhowell2.ApiLib;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Blake Howell
 */
public class ApiObjParam<ObjectParamType, ParentParamType> {

    public final String parameterName;
    final ParamRetrievalFunc<ObjectParamType, ParentParamType> retrievalFunction;
    final boolean isRequired;
    final boolean continueOnOptionalFailure;
    final ApiParam<?, ObjectParamType>[] parameters;
    final ApiObjParam<?, ObjectParamType>[] objectParameters;
    final ApiCustomParam<ObjectParamType>[] customParameters;

    /**
     * Used for top level objects (i.e., objects without a parameter name to retrieve them -- this could either be the root object or objects in an
     * array).
     * @param continueOnOptionalFailure
     * @param retrievalFunction
     * @param parameters
     */
    public ApiObjParam(boolean continueOnOptionalFailure,
                       ParamRetrievalFunc<ObjectParamType, ParentParamType> retrievalFunction,
                       ApiParam<?, ObjectParamType>[] parameters,
                       ApiObjParam<?, ObjectParamType>[] objectParameters,
                       ApiCustomParam<ObjectParamType>[] customParameters) {
        this(null,
             continueOnOptionalFailure,
             true, // doesn't matter for top level object parameter
             retrievalFunction,
             parameters,
             objectParameters,
             customParameters);
    }

    @SuppressWarnings("unchecked")
    public ApiObjParam(String parameterName,
                       boolean continueOnOptionalFailure,
                       boolean isRequired,
                       ParamRetrievalFunc<ObjectParamType, ParentParamType> retrievalFunction,
                       ApiParam<?, ObjectParamType>[] parameters,
                       ApiObjParam<?, ObjectParamType>[] objectParameters,
                       ApiCustomParam<ObjectParamType>[] customParameters) {
        this.parameterName = parameterName;
        this.continueOnOptionalFailure = continueOnOptionalFailure;
        this.isRequired = isRequired;
        this.retrievalFunction = retrievalFunction;

        // set all to zero size array -- cause of for loops used below
        // params
        this.parameters = parameters == null ? new ApiParam[0] : parameters;
        // sort so that failure will short circuit when checked
        Arrays.sort(this.parameters, (a, b) -> a.isRequired == b.isRequired ? 0 : a.isRequired ? -1 : 1);
        this.objectParameters = objectParameters == null ? new ApiObjParam[0] : objectParameters;
        Arrays.sort(this.objectParameters, (a, b) -> a.isRequired == b.isRequired ? 0 : a.isRequired ? -1 : 1);
        // because custom parameters are left completely up to the user, they can choose to pass or fail the check
        // based on whether or not it is required or optional
        this.customParameters = customParameters == null ? new ApiCustomParam[0] : customParameters;
    }

    @SuppressWarnings({"unchecked", "null"})
    public ApiObjParamCheck check(ParentParamType requestParameters) {
        ObjectParamType objParamsToCheck = this.retrievalFunction.retrieveParameter(this.parameterName, requestParameters);

        // TODO: Write code to dynamically choose best array sizes -- may have thread safety issues
        // do not need to create objects for nothing here
        Set<String> providedParamNames = new HashSet<>(this.parameters.length);
        Map<String, ApiCustomParamCheck> providedCustomParams = null;
        Map<String, ApiObjParamCheck> providedObjParams = null;
        if (customParameters.length > 0) {
            providedCustomParams = new HashMap<>(this.customParameters.length);
            providedObjParams = new HashMap<>(0);
        }
        // providedObjParams may also receive values from custom params, so it will have already been created above if custom parameters are supplied
        if (providedObjParams == null && objectParameters.length > 0)
            providedObjParams = new HashMap<>(this.objectParameters.length);

        for (ApiParam<?, ObjectParamType> apiParam : parameters) {
            if (apiParam.formatFuncs.length > 0) {

            }
            ApiParamCheck checkTuple = apiParam.check(objParamsToCheck);
            // if fails AND it is required OR it isn't required, but should continue NOT continue on optional failure
            if (checkTuple.failed() && apiParam.isRequired) {
                return ApiObjParamCheck.failed(wrapErrorTupleForObject(checkTuple.paramError));
            } else if (checkTuple.failed() && !apiParam.isRequired) {
                if (this.continueOnOptionalFailure) {
                    continue;   // do not add parameter to providedParamNames, because it was not correctly checked
                } else {
                    return ApiObjParamCheck.failed(wrapErrorTupleForObject(checkTuple.paramError));
                }
            }
            providedParamNames.add(checkTuple.parameterName);
        }

        for (ApiCustomParam<ObjectParamType> apiCustomParam : customParameters) {
            try {
                ApiCustomParamCheck checkTuple = apiCustomParam.check(objParamsToCheck);
                if (checkTuple.failed()) {
                    return ApiObjParamCheck.failed(wrapErrorTupleForObject(checkTuple.paramError));
                }
                providedCustomParams.put(checkTuple.customParameterName, checkTuple);
                providedParamNames.addAll(checkTuple.providedParamNames);
                providedObjParams.putAll(checkTuple.providedObjParams);
            } catch (ClassCastException e) {
                return ApiObjParamCheck
                    .failed(new ParamError(ErrorType.PARAMETER_CAST,
                                           "Failed to case parameter in ApiCustomParameters check. " + e.getMessage(),
                                           "Custom Parameter Error."));
            }
        }

        for (ApiObjParam<?, ObjectParamType> objectParameter : objectParameters) {
            try {
                ApiObjParamCheck checkTuple = objectParameter.check(objParamsToCheck);
                if (checkTuple.failed() && objectParameter.isRequired) {
                    return ApiObjParamCheck.failed(wrapErrorTupleForObject(checkTuple.paramError));
                } else if (checkTuple.failed() && !objectParameter.isRequired) {
                    if (this.continueOnOptionalFailure) {
                        continue;   // do not add parameter to providedParamNames, because it was not correctly checked
                    } else {
                        return ApiObjParamCheck.failed(wrapErrorTupleForObject(checkTuple.paramError));
                    }
                }
                // all objects checked here will have an object name (the only time that an object will not have an object name is when it is an array value (and, technically the root object))
                providedObjParams.put(checkTuple.parameterName, checkTuple);
                providedParamNames.add(checkTuple.parameterName);
            } catch (ClassCastException e) {
                return ApiObjParamCheck
                    .failed(new ParamError(ErrorType.PARAMETER_CAST,
                                           "Failed to cast parameter to correct type. " + e.getMessage(),
                                           objectParameter.parameterName));
            }
        }
        return ApiObjParamCheck.success(this.parameterName, providedParamNames, providedObjParams, providedCustomParams);
    }

    /**
     * Returns an error tuple that specifies the topmost object parameter in which the error occurred.
     * @param paramError
     * @return
     */
    private ParamError wrapErrorTupleForObject(ParamError paramError) {
        if (this.parameterName == null) {
            return paramError;
        }
        return new ParamError(paramError.errorType, paramError.errorMessage, paramError.parameterName + " of " + this.parameterName);

    }

}
