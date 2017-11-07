package io.bhowell2.ApiLib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Use {@code parameterRetrievalFunction} to retrieve nested parameters and pass them into the check.
 *
 * Theoretically, there could be an arbitrary number of nested parameters (anything deeper than 2 or 3 though i'm thinking there's a high likelihood of
 * a design flaw and you should rethink your API a bit!)
 *
 * NestedParamType - the parameter type that will be retrieved from ParentParamType to be checked (note all "sub-parameters" will take in request
 * parameters of NestedParamType)
 * ParentParamType - the object from which to retrieve the nested parameter
 *
 * TODO: currently the simple ErrorTuple is returned if there is an error. It may be more appropriate to wrap this and return which nested
 * parameter the error occurred in.
 *
 * @author Blake Howell
 */
public class ApiObjectParameter<ObjectParamType, ParentParamType> {

    public final String parameterName;
    final ParameterRetrievalFunction<ObjectParamType, ParentParamType> retrievalFunction;
    final boolean continueOnOptionalFailure;
    final ApiParameter<?, ObjectParamType>[] requiredParams;
    final ApiParameter<?, ObjectParamType>[] optionalParams;
    final ApiObjectParameter<?, ObjectParamType>[] requiredObjParams;
    final ApiObjectParameter<?, ObjectParamType>[] optionalObjParams;
    final ApiCustomParameters<ObjectParamType>[] requiredCustomParams;
    final ApiCustomParameters<ObjectParamType>[] optionalCustomParams;

    /**
     * Used for top level objects (i.e., objects without a parameter name to retrieve them -- this could either be the root object or objects in an
     * array).
     * @param continueOnOptionalFailure
     * @param retrievalFunction
     * @param requiredParams
     * @param optionalParams
     * @param requiredObjParams
     * @param optionalObjParams
     * @param requiredCustomParams
     * @param optionalCustomParams
     */
    public ApiObjectParameter(boolean continueOnOptionalFailure,
                              ParameterRetrievalFunction<ObjectParamType, ParentParamType> retrievalFunction,
                              ApiParameter<?, ObjectParamType>[] requiredParams,
                              ApiParameter<?, ObjectParamType>[] optionalParams,
                              ApiObjectParameter<?, ObjectParamType>[] requiredObjParams,
                              ApiObjectParameter<?, ObjectParamType>[] optionalObjParams,
                              ApiCustomParameters<ObjectParamType>[] requiredCustomParams,
                              ApiCustomParameters<ObjectParamType>[] optionalCustomParams) {
        this(null,
             continueOnOptionalFailure,
             retrievalFunction,
             requiredParams,
             optionalParams,
             requiredObjParams,
             optionalObjParams,
             requiredCustomParams,
             optionalCustomParams);
    }

    @SuppressWarnings("unchecked")
    public ApiObjectParameter(String parameterName,
                              boolean continueOnOptionalFailure,
                              ParameterRetrievalFunction<ObjectParamType, ParentParamType> retrievalFunction,
                              ApiParameter<?, ObjectParamType>[] requiredParams,
                              ApiParameter<?, ObjectParamType>[] optionalParams,
                              ApiObjectParameter<?, ObjectParamType>[] requiredObjParams,
                              ApiObjectParameter<?, ObjectParamType>[] optionalObjParams,
                              ApiCustomParameters<ObjectParamType>[] requiredCustomParams,
                              ApiCustomParameters<ObjectParamType>[] optionalCustomParams) {
        this.parameterName = parameterName;
        this.continueOnOptionalFailure = continueOnOptionalFailure;
        this.retrievalFunction = retrievalFunction;

        // set all to zero size array -- cause of for loops used below
        // params
        if (requiredParams == null)
            this.requiredParams = new ApiParameter[0];
        else
            this.requiredParams = requiredParams;

        if (optionalParams == null)
            this.optionalParams = new ApiParameter[0];
        else
            this.optionalParams = optionalParams;

        // object
        if (requiredObjParams == null)
            this.requiredObjParams = new ApiObjectParameter[0];
        else
            this.requiredObjParams = requiredObjParams;

        if (optionalObjParams == null)
            this.optionalObjParams = new ApiObjectParameter[0];
        else
            this.optionalObjParams = optionalObjParams;

        // custom
        if (requiredCustomParams == null)
            this.requiredCustomParams = new ApiCustomParameters[0];
        else
            this.requiredCustomParams = requiredCustomParams;

        if (optionalCustomParams == null)
            this.optionalCustomParams = new ApiCustomParameters[0];
        else
            this.optionalCustomParams = optionalCustomParams;
    }

    @SuppressWarnings({"unchecked", "null"})
    public ApiObjectParamTuple check(ParentParamType requestParameters) {
        ObjectParamType objParamsToCheck = this.retrievalFunction.retrieveParameter(this.parameterName, requestParameters);

        // TODO: Write code to dynamically chose best array sizes
        // do not need to create objects for nothing here
        List<String> providedParamNames = new ArrayList<>(this.requiredParams.length);
        List<ApiCustomParamsTuple> providedCustomParams = null;
        Map<String, ApiObjectParamTuple> providedObjParams = null;
        if (requiredCustomParams.length > 0 || optionalCustomParams.length > 0)
            providedCustomParams = new ArrayList<>(this.requiredCustomParams.length);
        if (requiredObjParams.length > 0 || optionalObjParams.length > 0)
            providedObjParams = new HashMap<>(this.requiredObjParams.length);

        // check REQUIRED params first so failure will short-circuit

        for (ApiParameter<?, ObjectParamType> apiParameter : requiredParams) {
            ApiParamTuple checkTuple = apiParameter.check(objParamsToCheck);
            if (checkTuple.failed()) {
                return ApiObjectParamTuple.failed(wrapErrorTupleForObject(checkTuple.errorTuple));
            }
            providedParamNames.add(checkTuple.parameterName);
        }

        for (ApiCustomParameters<ObjectParamType> apiCustomParameters : requiredCustomParams) {
            try {
                ApiCustomParamsTuple checkTuple = apiCustomParameters.check(objParamsToCheck);
                if (checkTuple.failed()) {
                    return ApiObjectParamTuple.failed(wrapErrorTupleForObject(checkTuple.errorTuple));
                }
                providedCustomParams.add(checkTuple);
                providedParamNames.addAll(checkTuple.providedParamNames);
            } catch (ClassCastException e) {
                return ApiObjectParamTuple
                    .failed(new ErrorTuple(ErrorType.PARAMETER_CAST, "Failed to case parameter in ApiCustomParameters check. " + e.getMessage()));
            }
        }

        for (ApiObjectParameter<?, ObjectParamType> objectParameter : requiredObjParams) {
            try {
                ApiObjectParamTuple checkTuple = objectParameter.check(objParamsToCheck);
                if (checkTuple.failed()) {
                    // wrap the errortuple, describing which nested parameter the error occured in (this will work for arbitrary depth)
                    return ApiObjectParamTuple.failed(wrapErrorTupleForObject(checkTuple.errorTuple));
                }
                // all objects checked here will have an object name (the only time that an object will not have an object name is when it is an array value (and, technically the root object))
                providedObjParams.put(checkTuple.parameterName, checkTuple);
                providedParamNames.add(checkTuple.parameterName);
            } catch (ClassCastException e) {
                return ApiObjectParamTuple
                    .failed(new ErrorTuple(ErrorType.PARAMETER_CAST, "Failed to cast parameter to correct type. " + e.getMessage()));
            }
        }

        for (ApiParameter<?, ObjectParamType> apiParameter : optionalParams) {
            ApiParamTuple checkTuple = apiParameter.check(objParamsToCheck);
            if (checkTuple.failed()) {
                if (checkTuple.errorTuple.errorType == ErrorType.MISSING_PARAMETER || continueOnOptionalFailure) {
                    continue;
                } else {
                    return ApiObjectParamTuple.failed(wrapErrorTupleForObject(checkTuple.errorTuple));
                }
            }
            providedParamNames.add(checkTuple.parameterName);
        }

        for (ApiCustomParameters<ObjectParamType> apiCustomParameters : optionalCustomParams) {
            ApiCustomParamsTuple checkTuple = apiCustomParameters.check(objParamsToCheck);
            if (checkTuple.failed()) {
                if (checkTuple.errorTuple.errorType == ErrorType.MISSING_PARAMETER || continueOnOptionalFailure) {
                    continue;
                } else {
                    return ApiObjectParamTuple.failed(wrapErrorTupleForObject(checkTuple.errorTuple));
                }
            }
            providedCustomParams.add(checkTuple);
            providedParamNames.addAll(checkTuple.providedParamNames);
        }

        for (ApiObjectParameter<?, ObjectParamType> nestedParameter : optionalObjParams) {
            ApiObjectParamTuple checkTuple = nestedParameter.check(objParamsToCheck);
            if (checkTuple.failed()) {
                if (checkTuple.errorTuple.errorType == ErrorType.MISSING_PARAMETER || continueOnOptionalFailure) {
                    continue;
                } else {
                    return ApiObjectParamTuple.failed(wrapErrorTupleForObject(checkTuple.errorTuple));
                }
            }
            providedObjParams.put(checkTuple.parameterName, checkTuple);
            providedParamNames.add(checkTuple.parameterName);
        }

        return ApiObjectParamTuple.success(this.parameterName, providedParamNames, providedObjParams, providedCustomParams);
    }

    /**
     * Returns an error tuple that specifies the topmost object parameter in which the error occurred.
     * @param errorTuple
     * @return
     */
    private ErrorTuple wrapErrorTupleForObject(ErrorTuple errorTuple) {
        if (this.parameterName == null) {
            return errorTuple;
        }
        return new ErrorTuple(errorTuple.errorType, errorTuple.errorMessage, errorTuple.parameterName + " of " + this.parameterName);

    }

}
