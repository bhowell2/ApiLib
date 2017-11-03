package io.bhowell2.ApiLib;

import java.util.ArrayList;
import java.util.List;

/**
 * For this base case, {@link ApiNestedParameter#parameterRetrievalFunction} will just return the object to be checked (not pulling out a nested
 * parameter) -- use {@link ApiPathRequestParametersBuilder}) to do this.
 *
 * @author Blake Howell
 */
public class ApiPathRequestParameters<RequestParameters> {

    public final ApiVersion apiVersion;
    final boolean continueOnOptionalFailure;
    final ApiParameter<?, RequestParameters>[] requiredParameters;
    final ApiParameter<?, RequestParameters>[] optionalParameters;
    final ApiNestedParameter<?, RequestParameters>[] requiredNestedParameters;
    final ApiNestedParameter<?, RequestParameters>[] optionalNestedParameters;
    final ApiCustomParameters<RequestParameters>[] requiredCustomParameters;
    final ApiCustomParameters<RequestParameters>[] optionalCustomParameters;

    int startingProvidedParametersArraySize;

    public ApiPathRequestParameters(ApiVersion apiVersion,
                                    boolean continueOnOptionalFailure,
                                    ApiParameter<?, RequestParameters>[] requiredParameters,
                                    ApiParameter<?, RequestParameters>[] optionalParameters,
                                    ApiNestedParameter<?, RequestParameters>[] requiredNestedParameters,
                                    ApiNestedParameter<?, RequestParameters>[] optionalNestedParameters,
                                    ApiCustomParameters<RequestParameters>[] requiredCustomParameters,
                                    ApiCustomParameters<RequestParameters>[] optionalCustomParameters) {
        this.apiVersion = apiVersion;
        this.continueOnOptionalFailure = continueOnOptionalFailure;
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

    public ApiPathRequestParamsCheckTuple check(RequestParameters requestParameters) {
        List<String> providedParameterNames = new ArrayList<>(this.startingProvidedParametersArraySize);
        List<ApiNestedParamCheckTuple> providedNestedParameters = new ArrayList<>(this.requiredNestedParameters.length);
        // check required first so failure will short-circuit

        for (ApiParameter<?, RequestParameters> apiParameter : requiredParameters) {
            ApiParameterCheckTuple checkTuple = apiParameter.check(requestParameters);
            if (checkTuple.failed()) {
                return ApiPathRequestParamsCheckTuple.failed(checkTuple.errorTuple);
            }
            providedParameterNames.add(checkTuple.parameterName);
        }

        for (ApiCustomParameters<RequestParameters> apiCustomParameters : requiredCustomParameters) {
            try {
                ApiCustomParamsCheckTuple checkTuple = apiCustomParameters.check(requestParameters);
                if (checkTuple.failed()) {
                    return ApiPathRequestParamsCheckTuple.failed(checkTuple.errorTuple);
                }
                providedParameterNames.addAll(checkTuple.providedParameterNames);
            } catch (ClassCastException e) {
                return ApiPathRequestParamsCheckTuple
                    .failed(new ErrorTuple(ErrorType.PARAMETER_CAST, "Failed to case parameter in ApiCustomParameters check. " + e.getMessage()));
            }
        }

        // It's easily possible that a ClassCastException happens in the retrieval
        for (ApiNestedParameter<?, RequestParameters> nestedParameter : requiredNestedParameters) {
            try {
                ApiNestedParamCheckTuple checkTuple = nestedParameter.check(requestParameters);
                if (checkTuple.failed()) {
                    return ApiPathRequestParamsCheckTuple.failed(checkTuple.errorTuple);
                }
                providedNestedParameters.add(checkTuple);
                providedParameterNames.add(checkTuple.nestedParameterName);
            } catch (ClassCastException e) {
                return ApiPathRequestParamsCheckTuple
                    .failed(new ErrorTuple(ErrorType.PARAMETER_CAST, "Failed to cast parameter to correct type. " + e.getMessage()));
            }
        }

        for (ApiParameter<?, RequestParameters> apiParameter : optionalParameters) {
            ApiParameterCheckTuple checkTuple = apiParameter.check(requestParameters);
            if (checkTuple.failed()) {
                if (checkTuple.errorTuple.errorType == ErrorType.MISSING_PARAMETER || continueOnOptionalFailure) {
                    continue;
                } else {
                    return ApiPathRequestParamsCheckTuple.failed(checkTuple.errorTuple);
                }
            }
            providedParameterNames.add(checkTuple.parameterName);
        }

        for (ApiCustomParameters<RequestParameters> apiCustomParameters : optionalCustomParameters) {
            ApiCustomParamsCheckTuple checkTuple = apiCustomParameters.check(requestParameters);
            if (checkTuple.failed()) {
                if (checkTuple.errorTuple.errorType == ErrorType.MISSING_PARAMETER || continueOnOptionalFailure) {
                    continue;
                } else {
                    return ApiPathRequestParamsCheckTuple.failed(checkTuple.errorTuple);
                }
            }
            providedParameterNames.addAll(checkTuple.providedParameterNames);
        }

        for (ApiNestedParameter<?, RequestParameters> nestedParameter : optionalNestedParameters) {
            ApiNestedParamCheckTuple checkTuple = nestedParameter.check(requestParameters);
            if (checkTuple.failed()) {
                if (checkTuple.errorTuple.errorType == ErrorType.MISSING_PARAMETER || continueOnOptionalFailure) {
                    continue;
                } else {
                    return ApiPathRequestParamsCheckTuple.failed(checkTuple.errorTuple);
                }
            }
            providedNestedParameters.add(checkTuple);
            providedParameterNames.add(checkTuple.nestedParameterName);
        }

        return ApiPathRequestParamsCheckTuple.success(providedParameterNames, providedNestedParameters);
    }

}
