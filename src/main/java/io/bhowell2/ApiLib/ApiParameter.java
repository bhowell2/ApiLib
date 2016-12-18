package io.bhowell2.ApiLib;

import io.bhowell2.ApiLib.exceptions.ParameterCastException;
import io.bhowell2.ApiLib.exceptions.SafeInvalidApiParameterException;
import io.bhowell2.ApiLib.exceptions.SafeParameterCheckException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Each parameter should extend this class. All functionality is provided and only {@link #check(Map)} or {@link #check(Object)} need be called. Generally
 * the class type and function checks will be the same for every version, but in the rare case that either of these changes, a new class should be
 * created for the parameter.
 *
 * @author Blake Howell
 */
public class ApiParameter<T> {

  private final String parameterName;
  private final List<Function<T, FunctionCheckReturnTuple>> paramCheckFunctions;
  private final Class<T> clazz;
  private final boolean safeToReturnFunctionCheckFailureReason;

  public ApiParameter(String parameterName, Class<T> parameterClassType, boolean safeToReturnFunctionCheckFailure, List<Function<T,
      FunctionCheckReturnTuple>> paramCheckFunctions) {
    this.parameterName = parameterName;
    this.clazz = parameterClassType;
    this.safeToReturnFunctionCheckFailureReason = safeToReturnFunctionCheckFailure;
    this.paramCheckFunctions = paramCheckFunctions;
  }

  public String getParameterName() {
    return this.parameterName;
  }

  /**
   * Some web frameworks/platforms automatically wrap the parameters for the user in a Map (or JsonObject - which is sometimes just a wrapping
   * over {@link Map}. e.g., Vert.x). This method will pull the parameter from the map, with the {@link #parameterName} provided in the constructor.
   * @param requestParameters contains the parameter to be checked, which will be pulled from the map
   * @return a tuple with the name of the successfully checked parameter, or an exception (that may be pulled from the return tuple)
   */
  @SuppressWarnings("unchecked")
  public ParameterCheckReturnTuple check(Map<String, Object> requestParameters) {
    T param = (T) requestParameters.get(this.parameterName); // really this is just cast to object due to type erasure
    if (param == null)
      return ParameterCheckReturnTuple.missingParameterFailure(this.parameterName);
    if (!this.clazz.isInstance(param))
      return new ParameterCheckReturnTuple(new ParameterCastException(this.parameterName, this.clazz));
    for (Function<T, FunctionCheckReturnTuple> functionCheck : paramCheckFunctions) {
      FunctionCheckReturnTuple checkTuple = functionCheck.apply(param);
      if (checkTuple.failed()) {
        if (safeToReturnFunctionCheckFailureReason) {
          return checkTuple.hasFailureMessage() ? ParameterCheckReturnTuple.safeInvalidParameterFailure(this.parameterName, checkTuple.failureMessage)
              : ParameterCheckReturnTuple.invalidParameterFailure(this.parameterName);
        }
        return checkTuple.hasFailureMessage() ? ParameterCheckReturnTuple.invalidParameterFailure(this.parameterName, checkTuple.getFailureMessage()) :
            ParameterCheckReturnTuple.invalidParameterFailure(this.parameterName);
      }
    }
    return ParameterCheckReturnTuple.success(this.parameterName);
  }

//  /**
//   * If the user has the parameters individually rather than in a {@link Map}, they may simply call this method, which will ensure that the
//   * parameter meets all criteria.
//   * @param param the parameter to be checked
//   * @return a tuple with the name of the successfully checked parameter, or an exception (that may be pulled from the return tuple)
//   */
//  public ParameterCheckReturnTuple check(T param) {
//    if (param == null)
//      return ParameterCheckReturnTuple.invalidParameterFailure(this.parameterName, "argument was null");
//    if (!this.clazz.isInstance(param))
//      return ParameterCheckReturnTuple.parameterCastException(this.parameterName, this.clazz);
//    for (Function<T, FunctionCheckReturnTuple> functionCheck : paramCheckFunctions) {
//      FunctionCheckReturnTuple checkTuple = functionCheck.apply(param);
//      if (checkTuple.failed()) {
//        // returns the reason for failure if one is available
//        if (safeToReturnFunctionCheckFailureReason) {
//          return checkTuple.hasFailureMessage() ? ParameterCheckReturnTuple.safeInvalidParameterFailure(this.parameterName, checkTuple.failureMessage)
//              : ParameterCheckReturnTuple.invalidParameterFailure(this.parameterName);
//        }
//        return checkTuple.hasFailureMessage() ? ParameterCheckReturnTuple.invalidParameterFailure(this.parameterName, checkTuple.getFailureMessage()) :
//            ParameterCheckReturnTuple.invalidParameterFailure(this.parameterName);
//      }
//    }
//    return ParameterCheckReturnTuple.success(this.parameterName);
//  }

}
