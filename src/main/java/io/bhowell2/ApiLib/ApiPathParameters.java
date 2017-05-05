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

  private final int startingProvidedParamNamesArraySize;

  ApiPathParameters(ApiVersion apiVersion, ApiParameter<?>[] requiredParameters, ApiParameter<?>[] optionalParameters) {
    this.apiVersion = apiVersion;
    this.requiredParameters = requiredParameters;
    this.optionalParameters = optionalParameters;
    // Note, optional/3 is arbitrarily chosen and maybe later some code will be added to optimize the sizing of the array, but this may not even
    // really be worth it as the code to dynamically optimize may take longer
    this.startingProvidedParamNamesArraySize = requiredParameters.length + optionalParameters.length/3;
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
  public PathParamsTuple check(Map<String, Object> requestParameters) {
    List<String> providedParameterNames = new ArrayList<>(startingProvidedParamNamesArraySize);
    for (ApiParameter<?> param : requiredParameters) {
      ParameterCheckReturnTuple parameterCheckReturnTuple = param.check(requestParameters);
      // if not successful, short-circuit
      if (!parameterCheckReturnTuple.isSuccessful()) {
        return PathParamsTuple.failed(parameterCheckReturnTuple.checkFailure);
      }
      // else, add to list of parameters that were provided
      providedParameterNames.add(parameterCheckReturnTuple.getParameterName());
    }
    // Do required conditional params for short circuit capabilities
    // Since optional parameters aren't required, they do not ever result in an error
    // TODO: It may be desired to return an error message noting why the optional parameter failed to checkout if it was provided
    // On the optional parameters, check for the different error types (e.g., if instanceof MissingParam.. ignore it, otherwise maybe add reason
    // for failure)
    for (ApiParameter<?> param : optionalParameters) {
      ParameterCheckReturnTuple parameterCheckReturnTuple = param.check(requestParameters);
      // different than the required cases - if it is successful, add the parameter name
      // otherwise, ignore
      if (parameterCheckReturnTuple.isSuccessful()) {
        providedParameterNames.add(parameterCheckReturnTuple.getParameterName());
      }
    }
    return PathParamsTuple.successful(providedParameterNames);
  }

}
