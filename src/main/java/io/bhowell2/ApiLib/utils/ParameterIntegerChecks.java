package io.bhowell2.ApiLib.utils;

import io.bhowell2.ApiLib.FunctionCheckTuple;

import java.util.function.Function;

/**
 * @author Blake Howell
 */
public class ParameterIntegerChecks {

  public static Function<Integer, FunctionCheckTuple> valueGreaterThan(Integer i) {
    return n -> {
      if (n > i) {
        return FunctionCheckTuple.success();
      } else {
        return FunctionCheckTuple.failure("Value not greater than " + i);
      }
    };
  }

  public static Function<Integer, FunctionCheckTuple> valueGreaterThanOrEqualTo(Integer i) {
    return n -> {
      if (n >= i) {
        return FunctionCheckTuple.success();
      } else {
        return FunctionCheckTuple.failure("Value not greater than or equal to " + i);
      }
    };
  }

  public static Function<Integer, FunctionCheckTuple> valueLessThan(Integer i) {
    return n -> {
      if (n < i) {
        return FunctionCheckTuple.success();
      } else {
        return FunctionCheckTuple.failure("Value not less than " + i);
      }
    };
  }

  public static Function<Integer, FunctionCheckTuple> valueLessThanOrEqualTo(Integer i) {
    return n -> {
      if (n <= i) {
        return FunctionCheckTuple.success();
      } else {
        return FunctionCheckTuple.failure("Value not less than or equal to " + i);
      }
    };
  }
  
}
