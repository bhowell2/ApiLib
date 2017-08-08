package io.bhowell2.ApiLib;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Facilitates the creation of a parameter for the library user.
 * @author Blake Howell
 */
public class ApiParameterBuilder<T> {

  public static <T> ApiParameterBuilder<T> builder(String parameterName, Class<T> parameterClassType) {
    return new ApiParameterBuilder<>(parameterName, parameterClassType);
  }

  private String parameterName;
  private Class<T> parameterClassType;
  private boolean safeToReturnFunctionCheckFailure = false;              // defaults to false
  private List<Function<T, FunctionCheckTuple>> functionChecks;

  public ApiParameterBuilder(String parameterName, Class<T> parameterClassType) {
    this.parameterName = parameterName;
    this.parameterClassType = parameterClassType;
    this.functionChecks = new ArrayList<>(1);
  }

  public ApiParameter<T> build() {
    // do not want the user to accidentally create a parameter that always passes. therefore, a runtime exception will be thrown
    if (this.functionChecks.size() == 0) {
      throw new NoFunctionChecksProvidedException(this.parameterName);
    }
    return new ApiParameter<>(parameterName, parameterClassType, this.functionChecks);
  }

  public ApiParameterBuilder<T> addCheckFunction(Function<T, FunctionCheckTuple> function) {
    this.functionChecks.add(function);
    return this;
  }

  /**
   * The value defaults to false.
   * @param b whether or not to return the reason that a function check failed
   * @return the builder for which this was set
   */
  public ApiParameterBuilder<T> setSafeToReturnFunctionCheckFailure(boolean b) {
    this.safeToReturnFunctionCheckFailure = b;
    return this;
  }

  private static class NoFunctionChecksProvidedException extends RuntimeException {
    public NoFunctionChecksProvidedException(String parameterName) {
      super("No function checks were provided for the parameter " + "'" + parameterName + "'. If this is intentional, just provide a function" +
                " " +
                "check" +
                " that returns success (e.g., FunctionCheckReturnTuple.success()) if you want it to always pass, or failure if it should always " +
                "fail or is a placeholder that has not been implemented.");
    }
  }


}
