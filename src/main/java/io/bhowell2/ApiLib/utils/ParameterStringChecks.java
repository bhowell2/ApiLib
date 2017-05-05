package io.bhowell2.ApiLib.utils;

import io.bhowell2.ApiLib.FunctionCheckTuple;

import java.util.function.Function;

/**
 * @author Blake Howell
 */
public final class ParameterStringChecks {

  public static Function<String, FunctionCheckTuple> lengthGreaterThan(int minLength) {
    return s -> {
      if (s.length() > minLength)
        return FunctionCheckTuple.success();
      return FunctionCheckTuple.failure("parameter length must be greater than " + minLength);
    };
  }

  public static Function<String, FunctionCheckTuple> lengthGreaterThanOrEqual(int minLength) {
    return s -> {
      if (s.length() >= minLength)
        return FunctionCheckTuple.success();
      return FunctionCheckTuple.failure("parameter length must be greater than or equal to " + minLength);
    };
  }

  public static Function<String, FunctionCheckTuple> lengthLessThan(int maxLength) {
    return s -> {
      if (s.length() < maxLength)
        return FunctionCheckTuple.success();
      return FunctionCheckTuple.failure("parameter length must be less than " + maxLength);
    };
  }

  public static Function<String, FunctionCheckTuple> lengthLessThanOrEqual(int maxLength) {
    return s -> {
      if (s.length() <= maxLength)
        return FunctionCheckTuple.success();
      return FunctionCheckTuple.failure("parameter length must be less than of equal to " + maxLength);
    };
  }

}
