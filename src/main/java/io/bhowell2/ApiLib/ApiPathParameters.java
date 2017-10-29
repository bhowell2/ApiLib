package io.bhowell2.ApiLib;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Holds all parameters for a given path for a given version.
 * @author Blake Howell
 */
public class ApiPathParameters {

    final boolean continueOnFailedOptionalChecks;
    final ApiVersion apiVersion;
    final ApiParameter<?>[] requiredParameters;
    final ApiParameter<?>[] optionalParameters;
    final ApiConditionalParameters[] requiredConditionalParameters;
    final ApiConditionalParameters[] optionalConditionalParameters;
    final ApiCustomParameters[] requiredCustomParameters;
    final ApiCustomParameters[] optionalCustomParameters;

    private int startingProvidedParamNamesArraySize;

    ApiPathParameters(ApiVersion apiVersion, boolean continueOnFailedOptionalChecks,
                      ApiParameter<?>[] requiredParameters, ApiParameter<?>[] optionalParameters,
                      ApiConditionalParameters[] requiredConditionalParameters,
                      ApiConditionalParameters[] optionalConditionalParameters,
                      ApiCustomParameters[] requiredCustomParameters,
                      ApiCustomParameters[] optionalCustomParameters) {
        this.apiVersion = apiVersion;
        this.continueOnFailedOptionalChecks = continueOnFailedOptionalChecks;
        this.requiredParameters = requiredParameters;
        this.optionalParameters = optionalParameters;
        this.requiredConditionalParameters = requiredConditionalParameters;
        this.optionalConditionalParameters = optionalConditionalParameters;
        this.requiredCustomParameters = requiredCustomParameters;
        this.optionalCustomParameters = optionalCustomParameters;
        // Note, these sizes were arbitrarily chosen and maybe later some heuristic code will be added to optimize the sizing of the array
        this.startingProvidedParamNamesArraySize = requiredParameters.length + optionalParameters.length/3 + requiredConditionalParameters.length * 2 +
            optionalConditionalParameters.length;
    }

    /**
     * Mainly used to compare {@link ApiPathParameters} to each other for ordering and determining which ApiPathParameters to use to check a requests parameters.
     * @return the ApiVersion for which this set of parameters is valid
     */
    public ApiVersion getApiVersion() {
        return apiVersion;
    }

    /**
     * Checks each parameter provided with the specified supplied required, optional, requiredConditional, and optionalConditional parameters. This
     * will short-circuit if a parameter does not check out (i.e., it returns as soon as a failed check occurs).
     *
     * @param requestParameters map containing all the parameters that need to be checked
     * @return returns whether or not the PathParameters were successfully checked
     */
    public PathParamsCheckTuple check(Map<String, Object> requestParameters) {
        List<String> providedParameterNames = new ArrayList<>(startingProvidedParamNamesArraySize);

        for (ApiParameter<?> param : requiredParameters) {
            ApiParameterCheckTuple checkTuple = param.check(requestParameters);
            // if not successful, short-circuit
            if (checkTuple.failed()) {
                return PathParamsCheckTuple.failed(checkTuple.errorTuple);
            }
            // else, add to list of parameters that were provided
            providedParameterNames.add(checkTuple.getParameterName());
        }

        for (ApiConditionalParameters conditionalParms : requiredConditionalParameters) {
            ConditionalCheckTuple checkTuple = conditionalParms.check(requestParameters);
            if (checkTuple.failed()) {
                return PathParamsCheckTuple.failed(checkTuple.errorTuple);
            }
            providedParameterNames.addAll(checkTuple.parameterNames);
        }

        for (ApiCustomParameters customParameters : requiredCustomParameters) {
            CustomParametersCheckTuple checkTuple = customParameters.check(requestParameters);
            if (checkTuple.failed()) {
                return PathParamsCheckTuple.failed(checkTuple.errorTuple);
            }
            providedParameterNames.addAll(checkTuple.parameterNames);
        }

        // Even though optional parameters aren't required, if they are provided and fail, it will be up to the 'continueOnFailedOptionalChecks' whether or not to fail them
        // -- this is to ensure that an optional parameter that does not meet the applications requirements is not accidentally used later in the app
        for (ApiParameter<?> param : optionalParameters) {
            ApiParameterCheckTuple checkTuple = param.check(requestParameters);
            if (checkTuple.failed()) {
                if (checkTuple.errorTuple.errorType == ErrorType.MISSING_PARAMETER || continueOnFailedOptionalChecks) {
                    continue;
                } else {
                    return PathParamsCheckTuple.failed(checkTuple.errorTuple);
                }
            }
            providedParameterNames.add(checkTuple.getParameterName());
        }

        for (ApiConditionalParameters conditionalParams : this.optionalConditionalParameters) {
            ConditionalCheckTuple checkTuple = conditionalParams.check(requestParameters);
            if (checkTuple.failed()) {
                if (checkTuple.errorTuple.errorType == ErrorType.MISSING_PARAMETER || continueOnFailedOptionalChecks) {
                    continue;
                } else {
                    return PathParamsCheckTuple.failed(checkTuple.errorTuple);
                }
            }
            providedParameterNames.addAll(checkTuple.parameterNames);
        }

        for (ApiCustomParameters customParameters : optionalCustomParameters) {
            CustomParametersCheckTuple checkTuple = customParameters.check(requestParameters);
            if (checkTuple.failed()) {
                if (checkTuple.errorTuple.errorType == ErrorType.MISSING_PARAMETER || continueOnFailedOptionalChecks) {
                    continue;
                } else {
                    return PathParamsCheckTuple.failed(checkTuple.errorTuple);
                }
            }
            providedParameterNames.addAll(checkTuple.parameterNames);
        }

        return PathParamsCheckTuple.successful(providedParameterNames);
    }

}
