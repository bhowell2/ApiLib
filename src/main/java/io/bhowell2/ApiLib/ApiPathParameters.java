package io.bhowell2.ApiLib;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Blake Howell
 */
public class ApiPathParameters {

    final ApiVersion apiVersion;
    final ApiParameter<?>[] requiredParameters;
    final ApiParameter<?>[] optionalParameters;
    final ConditionalParameters[] requiredConditionalParameters;
    final ConditionalParameters[] optionalConditionalParameters;

    private final int startingProvidedParamNamesArraySize;
    // TODO: Conditional params need to be added
    ApiPathParameters(ApiVersion apiVersion, ApiParameter<?>[] requiredParameters, ApiParameter<?>[] optionalParameters,
                      ConditionalParameters[] requiredConditionalParameters, ConditionalParameters[] optionalConditionalParameters) {
        this.apiVersion = apiVersion;
        this.requiredParameters = requiredParameters;
        this.optionalParameters = optionalParameters;
        this.requiredConditionalParameters = requiredConditionalParameters;
        this.optionalConditionalParameters = optionalConditionalParameters;
        // Note, these sizes were arbitrarily chosen and maybe later some heuristic code will be added to optimize the sizing of the array
        this.startingProvidedParamNamesArraySize = requiredParameters.length + optionalParameters.length/3 + requiredConditionalParameters.length * 2 +
            optionalConditionalParameters.length;
    }

    /**
     * Mainly used to compare {@link ApiPathParameters} to each other for ordering.
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
            ApiParamCheckTuple apiParamCheckTuple = param.check(requestParameters);
            // if not successful, short-circuit
            if (apiParamCheckTuple.failed()) {
                return PathParamsCheckTuple.failed(apiParamCheckTuple.errorTuple);
            }
            // else, add to list of parameters that were provided
            providedParameterNames.add(apiParamCheckTuple.getParameterName());
        }

        for (ConditionalParameters conditionalParms : this.requiredConditionalParameters) {
            ConditionalCheckTuple conditionalCheckTuple = conditionalParms.check(requestParameters);
            if (conditionalCheckTuple.failed()) {
                return PathParamsCheckTuple.failed(conditionalCheckTuple.errorTuple);
            }
            providedParameterNames.addAll(conditionalCheckTuple.parameterNames);
        }

        // Even though optional parameters aren't required, if they are provided and fail, a failure is returned -- this is to ensure that an optional
        // parameter is not provided
        for (ApiParameter<?> param : optionalParameters) {
            ApiParamCheckTuple apiParamCheckTuple = param.check(requestParameters);
            // different than the required cases - if it is successful, add the parameter name
            // otherwise, ignore
            if (apiParamCheckTuple.failed()) {
                return PathParamsCheckTuple.failed(apiParamCheckTuple.errorTuple);
            }
            providedParameterNames.add(apiParamCheckTuple.getParameterName());
        }

        for (ConditionalParameters conditionalParams : this.optionalConditionalParameters) {
            ConditionalCheckTuple conditionalCheckTuple = conditionalParams.check(requestParameters);
            if (conditionalCheckTuple.failed()) {
                return PathParamsCheckTuple.failed(conditionalCheckTuple.errorTuple);
            }
            providedParameterNames.addAll(conditionalCheckTuple.parameterNames);
        }

        return PathParamsCheckTuple.successful(providedParameterNames);
    }

}
