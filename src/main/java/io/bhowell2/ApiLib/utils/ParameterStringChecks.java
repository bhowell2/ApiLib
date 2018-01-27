package io.bhowell2.ApiLib.utils;

import io.bhowell2.ApiLib.CheckFunction;
import io.bhowell2.ApiLib.CheckFunctionTuple;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Blake Howell
 */
public class ParameterStringChecks {

    public static CheckFunction<String> lengthGreaterThan(int minLength) {
        return s -> {
            if (s.length() > minLength) {
                return CheckFunctionTuple.success();
            } else {
                return CheckFunctionTuple.failure("length must be greater than " + minLength);
            }
        };
    }

    public static CheckFunction<String> lengthGreaterThanOrEqual(int minLength) {
        return s -> {
            if (s.length() >= minLength) {
                return CheckFunctionTuple.success();
            } else {
                return CheckFunctionTuple.failure("length must be greater than or equal to " + minLength);
            }
        };
    }

    public static CheckFunction<String> lengthLessThan(int maxLength) {
        return s -> {
            if (s.length() < maxLength) {
                return CheckFunctionTuple.success();
            } else {
                return CheckFunctionTuple.failure("length must be less than " + maxLength);
            }
        };
    }

    public static CheckFunction<String> lengthLessThanOrEqual(int maxLength) {
        return s -> {
            if (s.length() <= maxLength) {
                return CheckFunctionTuple.success();
            } else {
                return CheckFunctionTuple.failure("length must be less than of equal to " + maxLength);
            }
        };
    }

    public static CheckFunction<String> lengthEqualTo(int length) {
        return s -> {
            if (s.length() == length) {
                return CheckFunctionTuple.success();
            } else {
                return CheckFunctionTuple.failure("length must be equal to " + length);
            }
        };
    }

    public static CheckFunction<String> matchesRegex(Pattern pattern) {
        return s -> {
            if (pattern.matcher(s).matches()) {
                return CheckFunctionTuple.success();
            } else {
                return CheckFunctionTuple.failure("is not of correct form.");
            }
        };
    }

    public static CheckFunction<String> mustContainAtLeastNChars(int n, String mustContainChars) {
        return s -> {
            int counter = 0;
            for (int i = 0; i < mustContainChars.length(); i++) {
                char c = mustContainChars.charAt(i);
                for (int k = 0; k < s.length(); k++) {
                    if (s.charAt(k) == c) {
                        counter++;
                    }
                    if (counter >= n) {
                        return CheckFunctionTuple.success();
                    }
                }
            }
            return CheckFunctionTuple.failure("must contain at least " + n + " of the following chars: '" + mustContainChars + "'.");
        };
    }

    public static CheckFunction<String> mustContainAtLeastNChars(int n, Set<Character> mustContainChars) {
        return s -> {
            int counter = 0;
            for (int i = 0; i < s.length(); i++) {
                if (mustContainChars.contains(s.charAt(i))) {
                    counter++;
                }
                if (counter >= n) {
                    return CheckFunctionTuple.success();
                }
            }
            String errorMsgChars = mustContainChars.stream().map(c -> c.toString()).collect(Collectors.joining());
            return CheckFunctionTuple.failure("must contain at least " + n + " of the following chars: '" + errorMsgChars + "'.");
        };
    }

    public static CheckFunction<String> lessThanNRepeatedChars(int n) {
        return s -> {
            if (s.length() > 0) {
                Character consecutiveChar = s.charAt(0);
                int consecutiveCounter = 0;
                for (int i = 1; i < s.length(); i++) {
                    if (s.charAt(i) == consecutiveChar) {
                        consecutiveCounter++;
                        if (consecutiveCounter >= n) {
                            return CheckFunctionTuple.failure("cannot contain a character that repeats more than " + n + " times.");
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

    public static CheckFunction<String> emptyOrMeetsChecks(CheckFunction<String>... checkFunctions) {
        return s -> {
            if (s.length() == 0) {
                return CheckFunctionTuple.success();
            } else {
                for (CheckFunction<String> checkFunction : checkFunctions) {
                    CheckFunctionTuple tuple = checkFunction.check(s);
                    if (!tuple.successful) {
                        return tuple;   // return tuple with error
                    }
                }
            }
            // all checks passed
            return CheckFunctionTuple.success();
        };
    }

    /**
     * Similar to empty, but length can be 0 or all white space characters.
     * @param checkFunctions
     * @return
     */
    public static CheckFunction<String> emptyWhitespaceOrMeetsChecks(CheckFunction<String>... checkFunctions) {
        return s -> {
            if (s.length() == 0 || s.matches("\\s+")) {
                return CheckFunctionTuple.success();
            } else {
                for (CheckFunction<String> checkFunction : checkFunctions) {
                    CheckFunctionTuple tuple = checkFunction.check(s);
                    if (!tuple.successful) {
                        return tuple;   // return tuple with error
                    }
                }
            }
            // all checks passed
            return CheckFunctionTuple.success();
        };
    }

}
