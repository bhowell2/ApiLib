package io.bhowell2.ApiLib;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Facilitates the creation of a parameter for the library user.
 * @author Blake Howell
 */
public final class ApiParameterBuilder<T> {

  public static <T> ApiParameterBuilder<T> builder(String parameterName, Class<T> parameterClassType) {
    return new ApiParameterBuilder<>(parameterName, parameterClassType);
  }

  private String parameterName;
  private Class<T> parameterClassType;
  private List<Function<T, FunctionCheckTuple>> functionChecks;
  private Function<? super Object, T> parameterCastFunction;

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
    return new ApiParameter<>(parameterName, parameterClassType, this.functionChecks, this.parameterCastFunction);
  }

  public ApiParameterBuilder<T> setParameterCastFunction(Function<? super Object, T> castFunction) {
    if (this.parameterCastFunction != null) {
      throw new RuntimeException("Cast function has already been set for " + this.parameterName);
    }
    this.parameterCastFunction = castFunction;
    return this;
  }

  public ApiParameterBuilder<T> addCheckFunction(Function<T, FunctionCheckTuple> function) {
    this.functionChecks.add(function);
    return this;
  }

  private static class NoFunctionChecksProvidedException extends RuntimeException {
    public NoFunctionChecksProvidedException(String parameterName) {
      super("No function checks were provided for the parameter " + "'" + parameterName + "'. If this is intentional, just provide a function" +
                "check that returns success (e.g., FunctionCheckReturnTuple.success()) if you want it to always pass, or failure if it should " +
                "always fail or is a placeholder that has not yet been implemented.");
    }
  }


}
