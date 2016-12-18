package io.bhowell2.ApiLib.utils;

import io.bhowell2.ApiLib.FunctionCheckReturnTuple;

import java.util.function.Function;

/**
 * @author Blake Howell
 */
public final class ParameterStringChecks {

  public static Function<String, FunctionCheckReturnTuple> lengthGreaterThan(int minLength) {
    return s -> {
      if (s.length() > minLength)
        return FunctionCheckReturnTuple.success();
      return FunctionCheckReturnTuple.failure("parameter length must be greater than " + minLength);
    };
  }

  public static Function<String, FunctionCheckReturnTuple> lengthGreaterThanOrEqual(int minLength) {
    return s -> {
      if (s.length() >= minLength)
        return FunctionCheckReturnTuple.success();
      return FunctionCheckReturnTuple.failure("parameter length must be greater than or equal to " + minLength);
    };
  }

  public static Function<String, FunctionCheckReturnTuple> lengthLessThan(int maxLength) {
    return s -> {
      if (s.length() < maxLength)
        return FunctionCheckReturnTuple.success();
      return FunctionCheckReturnTuple.failure("parameter length must be less than " + maxLength);
    };
  }

  public static Function<String, FunctionCheckReturnTuple> lengthLessThanOrEqual(int maxLength) {
    return s -> {
      if (s.length() <= maxLength)
        return FunctionCheckReturnTuple.success();
      return FunctionCheckReturnTuple.failure("parameter length must be less than of equal to " + maxLength);
    };
  }

}
