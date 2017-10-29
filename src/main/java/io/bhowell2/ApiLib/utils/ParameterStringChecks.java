package io.bhowell2.ApiLib.utils;

import io.bhowell2.ApiLib.FunctionCheckTuple;

import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * @author Blake Howell
 */
public final class ParameterStringChecks {

    public static Function<String, FunctionCheckTuple> lengthGreaterThan(int minLength) {
        return s -> {
            if (s.length() > minLength) {
                return FunctionCheckTuple.success();
            } else {
                return FunctionCheckTuple.failure("parameter length must be greater than " + minLength);
            }
        };
    }

    public static Function<String, FunctionCheckTuple> lengthGreaterThanOrEqual(int minLength) {
        return s -> {
            if (s.length() >= minLength) {
                return FunctionCheckTuple.success();
            } else {
                return FunctionCheckTuple.failure("parameter length must be greater than or equal to " + minLength);
            }
        };
    }

    public static Function<String, FunctionCheckTuple> lengthLessThan(int maxLength) {
        return s -> {
            if (s.length() < maxLength) {
                return FunctionCheckTuple.success();
            } else {
                return FunctionCheckTuple.failure("parameter length must be less than " + maxLength);
            }
        };
    }

    public static Function<String, FunctionCheckTuple> lengthLessThanOrEqual(int maxLength) {
        return s -> {
            if (s.length() <= maxLength) {
                return FunctionCheckTuple.success();
            } else {
                return FunctionCheckTuple.failure("parameter length must be less than of equal to " + maxLength);
            }
        };
    }

    public static Function <String, FunctionCheckTuple> lengthEqualTo(int length) {
        return s -> {
            if (s.length() == length) {
                return FunctionCheckTuple.success();
            } else {
                return FunctionCheckTuple.failure("parameter length must be equal to " + length);
            }
        };
    }

    public static Function<String, FunctionCheckTuple> matchesRegex(Pattern pattern) {
        return s -> {
            if (pattern.matcher(s).matches()) {
                return FunctionCheckTuple.success();
            } else {
                return FunctionCheckTuple.failure("string parameter is not of correct form.");
            }
        };
    }

    public static Function<String, FunctionCheckTuple> mustContainAtLeastNChars(int n, String mustContainChars) {
        return s -> {
            int counter = 0;
          for (int i = 0; i < mustContainChars.length(); i++) {
              char c = mustContainChars.charAt(i);
              for (int k = 0; k < s.length(); k++) {
                  if (s.charAt(k) == c) {
                      counter++;
                  }
                  if (counter >= n) {
                      break;
                  }
              }
          }
          return counter >= n ? FunctionCheckTuple.success() : FunctionCheckTuple.failure("String must contain at least " + n + " of the following chars; '" + mustContainChars + "'.");
        };
    }

    public static Function<String, FunctionCheckTuple> lessThanNRepeatedChars(int n) {
        return s -> {
            if (s.length() > 0) {
                Character consecutiveChar = s.charAt(0);
                int consecutiveCounter = 0;
                for (int i = 1; i < s.length(); i++) {
                    if (s.charAt(i) == consecutiveChar) {
                        consecutiveCounter++;
                        if (consecutiveCounter >= n) {
                            return FunctionCheckTuple.failure("string cannot contain a character that repeats more than " + n + " times.");
                        }
                    } else {
                        consecutiveChar = s.charAt(i);
                        consecutiveCounter = 0;
                    }
                }
            }
            return FunctionCheckTuple.success();
        };
    }

}
