package io.bhowell2.ApiLib.utils;

import io.bhowell2.ApiLib.CheckFunction;
import io.bhowell2.ApiLib.CheckFunctionTuple;

import java.util.regex.Pattern;

/**
 * @author Blake Howell
 */
public final class ParameterStringChecks {

    public static final CheckFunction<String> lengthGreaterThan(int minLength) {
        return s -> {
            if (s.length() > minLength) {
                return CheckFunctionTuple.success();
            } else {
                return CheckFunctionTuple.failure("parameter length must be greater than " + minLength);
            }
        };
    }

    public static final CheckFunction<String> lengthGreaterThanOrEqual(int minLength) {
        return s -> {
            if (s.length() >= minLength) {
                return CheckFunctionTuple.success();
            } else {
                return CheckFunctionTuple.failure("parameter length must be greater than or equal to " + minLength);
            }
        };
    }

    public static final CheckFunction<String> lengthLessThan(int maxLength) {
        return s -> {
            if (s.length() < maxLength) {
                return CheckFunctionTuple.success();
            } else {
                return CheckFunctionTuple.failure("parameter length must be less than " + maxLength);
            }
        };
    }

    public static final CheckFunction<String> lengthLessThanOrEqual(int maxLength) {
        return s -> {
            if (s.length() <= maxLength) {
                return CheckFunctionTuple.success();
            } else {
                return CheckFunctionTuple.failure("parameter length must be less than of equal to " + maxLength);
            }
        };
    }

    public static final CheckFunction<String> lengthEqualTo(int length) {
        return s -> {
            if (s.length() == length) {
                return CheckFunctionTuple.success();
            } else {
                return CheckFunctionTuple.failure("parameter length must be equal to " + length);
            }
        };
    }

    public static final CheckFunction<String> matchesRegex(Pattern pattern) {
        return s -> {
            if (pattern.matcher(s).matches()) {
                return CheckFunctionTuple.success();
            } else {
                return CheckFunctionTuple.failure("string parameter is not of correct form.");
            }
        };
    }

    public static final CheckFunction<String> mustContainAtLeastNChars(int n, String mustContainChars) {
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
            return counter >= n ? CheckFunctionTuple.success() : CheckFunctionTuple.failure("String must contain at least " + n + " of the following chars; '" + mustContainChars + "'.");
        };
    }

    public static final CheckFunction<String> lessThanNRepeatedChars(int n) {
        return s -> {
            if (s.length() > 0) {
                Character consecutiveChar = s.charAt(0);
                int consecutiveCounter = 0;
                for (int i = 1; i < s.length(); i++) {
                    if (s.charAt(i) == consecutiveChar) {
                        consecutiveCounter++;
                        if (consecutiveCounter >= n) {
                            return CheckFunctionTuple.failure("string cannot contain a character that repeats more than " + n + " times.");
                        }
                    } else {
                        consecutiveChar = s.charAt(i);
                        consecutiveCounter = 0;
                    }
                }
            }
            return CheckFunctionTuple.success();
        };
    }

    public static final CheckFunction<String> emptyOrMeetsChecks(CheckFunction<String>... checkFunctions) {
        return s -> {
            if (s.length() == 0) {
                return CheckFunctionTuple.success();
            } else {
                for (CheckFunction<String> checkFunction : checkFunctions) {
                    CheckFunctionTuple tuple = checkFunction.check(s);
                    if (!tuple.successful) {
                        return tuple;
                    }
                }
            }
            // all checks passed
            return CheckFunctionTuple.success();
        };
    }

}
