package io.bhowell2.ApiLib;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Each parameter should extend this class. All functionality is provided and only {@link #check(Map)} needs to be called. Generally
 * the class type and function checks will be the same for every version, but in the rare case that either of these changes, a new class should be created for the parameter.
 *
 * @author Blake Howell
 */
public class ApiParameter<T> {

  private final String parameterName;
  private final List<Function<T, FunctionCheckTuple>> paramCheckFunctions;
  private final Class<T> parameterClassType;

  public ApiParameter(String parameterName, Class<T> parameterClassType, List<Function<T,
      FunctionCheckTuple>> paramCheckFunctions) {
    this.parameterName = parameterName;
    this.parameterClassType = parameterClassType;
    this.paramCheckFunctions = paramCheckFunctions;
  }

  public String getParameterName() {
    return this.parameterName;
  }

  /**
   * Some web frameworks/platforms automatically wrap the parameters for the user in a Map (or JsonObject - which is sometimes just a wrapping
   * over {@link Map}. e.g., Vert.x). This method will pull the parameter from the map, with the {@link #parameterName} provided in the constructor.
   * @param requestParameters contains the parameter to be checked, which will be pulled from the map
   * @return a tuple with the name of the successfully checked parameter, or error information
   */
  @SuppressWarnings("unchecked")
  public ParameterCheckTuple check(Map<String, Object> requestParameters) {
    T param = (T) requestParameters.get(this.parameterName); // really this is just cast to object due to type erasure
    if (param == null)
      return ParameterCheckTuple.missingParameterFailure(this.parameterName);

    if (!this.parameterClassType.isInstance(param))
      return ParameterCheckTuple.parameterCastException(this.parameterName, this.parameterClassType);

    for (Function<T, FunctionCheckTuple> functionCheck : paramCheckFunctions) {
      FunctionCheckTuple checkTuple = functionCheck.apply(param);

      // short circuit checks and return (if one check fails, it all fails)
      if (checkTuple.failed()) {
        return checkTuple.hasFailureMessage() ? ParameterCheckTuple.invalidParameterFailure(this.parameterName, checkTuple.getFailureMessage()) :
            ParameterCheckTuple.invalidParameterFailure(this.parameterName, "Failed to pass parameter check.");
      }

    }
    // all checks have passed, parameter is deemed successfully checked
    return ParameterCheckTuple.success(this.parameterName);
  }

}
