package io.bhowell2.ApiLib;

import java.util.ArrayList;
import java.util.List;

/**
 * Use {@code parameterRetrievalFunction} to retrieve nested parameters and pass them into the check.
 *
 * NestedParamType - the parameter type that will be retrieved from ParentParamType to be checked (note all "sub-parameters" will take in request
 * parameters of NestedParamType)
 * ParentParamType - the object from which to retrieve
 *
 * TODO: currently the simple ErrorTuple is returned if there is an error. It may be more appropriate to wrap this and return which nested
 * parameter the error occurred in
 *
 * @author Blake Howell
 */
public class ApiNestedParameter<NestedParamType, ParentParamType> {

    public final String parameterName;
    final ParameterRetrievalFunction<NestedParamType, ParentParamType> parameterRetrievalFunction;
    final boolean continueOnOptionalFailure;
    final ApiParameter<?, NestedParamType>[] requiredParameters;
    final ApiParameter<?, NestedParamType>[] optionalParameters;
    final ApiNestedParameter<?, NestedParamType>[] requiredNestedParameters;
    final ApiNestedParameter<?, NestedParamType>[] optionalNestedParameters;
    final ApiCustomParameters<NestedParamType>[] requiredCustomParameters;
    final ApiCustomParameters<NestedParamType>[] optionalCustomParameters;

    int startingProvidedParametersArraySize;

    public ApiNestedParameter(String parameterName,
                              boolean continueOnOptionalFailure,
                              ParameterRetrievalFunction<NestedParamType, ParentParamType> parameterRetrievalFunction,
                              ApiParameter<?, NestedParamType>[] requiredParameters,
                              ApiParameter<?, NestedParamType>[] optionalParameters,
                              ApiNestedParameter<?, NestedParamType>[] requiredNestedParameters,
                              ApiNestedParameter<?, NestedParamType>[] optionalNestedParameters,
                              ApiCustomParameters<NestedParamType>[] requiredCustomParameters,
                              ApiCustomParameters<NestedParamType>[] optionalCustomParameters) {
        this.parameterName = parameterName;
        this.continueOnOptionalFailure = continueOnOptionalFailure;
        this.parameterRetrievalFunction = parameterRetrievalFunction;
        this.requiredParameters = requiredParameters;
        this.optionalParameters = optionalParameters;
        this.requiredNestedParameters = requiredNestedParameters;
        this.optionalNestedParameters = optionalNestedParameters;
        this.requiredCustomParameters = requiredCustomParameters;
        this.optionalCustomParameters = optionalCustomParameters;

        // this was arbitrarily chosen, sizing is probably off
        // TODO: Write code to dynamically chose best array size
        this.startingProvidedParametersArraySize = requiredParameters.length + requiredCustomParameters.length + optionalParameters.length / 3 + optionalCustomParameters.length / 3;
    }

    @SuppressWarnings({"unchecked", "null"})
    public ApiNestedParamCheckTuple check(ParentParamType requestParameters) {
        NestedParamType paramsToCheck = this.parameterRetrievalFunction.retrieveParameter(this.parameterName, requestParameters);
        List<String> providedParameterNames = new ArrayList<>(this.startingProvidedParametersArraySize);

        // check required first so failure will short-circuit

        for (ApiParameter<?, NestedParamType> apiParameter : requiredParameters) {
            ApiParameterCheckTuple checkTuple = apiParameter.check(paramsToCheck);
            if (checkTuple.failed()) {
                return ApiNestedParamCheckTuple.failed(checkTuple.errorTuple);
            }
            providedParameterNames.add(checkTuple.parameterName);
        }

        for (ApiCustomParameters<NestedParamType> apiCustomParameters : requiredCustomParameters) {
            try {
                ApiCustomParamsCheckTuple checkTuple = apiCustomParameters.check(paramsToCheck);
                if (checkTuple.failed()) {
                    return ApiNestedParamCheckTuple.failed(checkTuple.errorTuple);
                }
                providedParameterNames.addAll(checkTuple.providedParameterNames);
            } catch (ClassCastException e) {
                return ApiNestedParamCheckTuple
                    .failed(new ApiParamErrorTuple(ErrorType.PARAMETER_CAST, "Failed to case parameter in ApiCustomParameters check. " + e.getMessage()));
            }
        }

        // It's easily possible that a ClassCastException happens in the retrieval
        for (ApiNestedParameter<?, NestedParamType> nestedParameter : requiredNestedParameters) {
            try {
                ApiNestedParamCheckTuple checkTuple = nestedParameter.check(paramsToCheck);
                if (checkTuple.failed()) {
                    return ApiNestedParamCheckTuple.failed(checkTuple.errorTuple);
                }
                providedParameterNames.addAll(checkTuple.providedParameterNames);
            } catch (ClassCastException e) {
                return ApiNestedParamCheckTuple
                    .failed(new ApiParamErrorTuple(ErrorType.PARAMETER_CAST, "Failed to cast parameter to correct type. " + e.getMessage()));
            }
        }

        for (ApiParameter<?, NestedParamType> apiParameter : optionalParameters) {
            ApiParameterCheckTuple checkTuple = apiParameter.check(paramsToCheck);
            if (checkTuple.failed()) {
                if (checkTuple.errorTuple.errorType == ErrorType.MISSING_PARAMETER || continueOnOptionalFailure) {
                    continue;
                } else {
                    return ApiNestedParamCheckTuple.failed(checkTuple.errorTuple);
                }
            }
            providedParameterNames.add(checkTuple.parameterName);
        }

        for (ApiCustomParameters<NestedParamType> apiCustomParameters : optionalCustomParameters) {
            ApiCustomParamsCheckTuple checkTuple = apiCustomParameters.check(paramsToCheck);
            if (checkTuple.failed()) {
                if (checkTuple.errorTuple.errorType == ErrorType.MISSING_PARAMETER || continueOnOptionalFailure) {
                    continue;
                } else {
                    return ApiNestedParamCheckTuple.failed(checkTuple.errorTuple);
                }
            }
            providedParameterNames.addAll(checkTuple.providedParameterNames);
        }

        for (ApiNestedParameter<?, NestedParamType> nestedParameter : optionalNestedParameters) {
            ApiNestedParamCheckTuple checkTuple = nestedParameter.check(paramsToCheck);
            if (checkTuple.failed()) {
                if (checkTuple.errorTuple.errorType == ErrorType.MISSING_PARAMETER || continueOnOptionalFailure) {
                    continue;
                } else {
                    return ApiNestedParamCheckTuple.failed(checkTuple.errorTuple);
                }
            }
            providedParameterNames.addAll(checkTuple.providedParameterNames);
        }

        return ApiNestedParamCheckTuple.success(providedParameterNames);
    }

}
