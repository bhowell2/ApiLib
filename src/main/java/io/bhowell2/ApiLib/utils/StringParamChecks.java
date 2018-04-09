package io.bhowell2.ApiLib.utils;

import io.bhowell2.ApiLib.CheckFunc;
import io.bhowell2.ApiLib.CheckFuncResult;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Blake Howell
 */
public class StringParamChecks {

    public static CheckFunc<String> lengthGreaterThan(int minLength) {
        return s -> {
            if (s.length() > minLength) {
                return CheckFuncResult.success();
            } else {
                return CheckFuncResult.failure("length must be greater than " + minLength);
            }
        };
    }

    public static CheckFunc<String> lengthGreaterThanOrEqual(int minLength) {
        return s -> {
            if (s.length() >= minLength) {
                return CheckFuncResult.success();
            } else {
                return CheckFuncResult.failure("length must be greater than or equal to " + minLength);
            }
        };
    }

    public static CheckFunc<String> lengthLessThan(int maxLength) {
        return s -> {
            if (s.length() < maxLength) {
                return CheckFuncResult.success();
            } else {
                return CheckFuncResult.failure("length must be less than " + maxLength);
            }
        };
    }

    public static CheckFunc<String> lengthLessThanOrEqual(int maxLength) {
        return s -> {
            if (s.length() <= maxLength) {
                return CheckFuncResult.success();
            } else {
                return CheckFuncResult.failure("length must be less than of equal to " + maxLength);
            }
        };
    }

    public static CheckFunc<String> lengthEqualTo(int length) {
        return s -> {
            if (s.length() == length) {
                return CheckFuncResult.success();
            } else {
                return CheckFuncResult.failure("length must be equal to " + length);
            }
        };
    }

    public static CheckFunc<String> matchesRegex(Pattern pattern) {
        return s -> {
            if (pattern.matcher(s).matches()) {
                return CheckFuncResult.success();
            } else {
                return CheckFuncResult.failure("is not of correct form.");
            }
        };
    }

    public static CheckFunc<String> mustContainAtLeastNChars(int n, String mustContainChars) {
        return s -> {
            int counter = 0;
            for (int i = 0; i < mustContainChars.length(); i++) {
                char c = mustContainChars.charAt(i);
                for (int k = 0; k < s.length(); k++) {
                    if (s.charAt(k) == c) {
                        counter++;
                    }
                    if (counter >= n) {
                        return CheckFuncResult.success();
                    }
                }
            }
            return CheckFuncResult.failure("must contain at least " + n + " of the following chars: '" + mustContainChars + "'.");
        };
    }

    public static CheckFunc<String> mustContainAtLeastNChars(int n, Set<Character> mustContainChars) {
        return s -> {
            int counter = 0;
            for (int i = 0; i < s.length(); i++) {
                if (mustContainChars.contains(s.charAt(i))) {
                    counter++;
                }
                if (counter >= n) {
                    return CheckFuncResult.success();
                }
            }
            String errorMsgChars = mustContainChars.stream().map(c -> c.toString()).collect(Collectors.joining());
            return CheckFuncResult.failure("must contain at least " + n + " of the following chars: '" + errorMsgChars + "'.");
        };
    }

    public static CheckFunc<String> lessThanNRepeatedChars(int n) {
        return s -> {
            if (s.length() > 0) {
                Character consecutiveChar = s.charAt(0);
                int consecutiveCounter = 0;
                for (int i = 1; i < s.length(); i++) {
                    if (s.charAt(i) == consecutiveChar) {
                        consecutiveCounter++;
                        if (consecutiveCounter >= n) {
                            return CheckFuncResult.failure("cannot contain a character that repeats more than " + n + " times.");
                        }
                    } else {
                        consecutiveChar = s.charAt(i);
                        consecutiveCounter = 0;
                    }
                }
            }
            return CheckFuncResult.success();
        };
    }

    public static CheckFunc<String> emptyOrMeetsChecks(CheckFunc<String>... checkFuncs) {
        return s -> {
            if (s.length() == 0) {
                return CheckFuncResult.success();
            } else {
                for (CheckFunc<String> checkFunc : checkFuncs) {
                    CheckFuncResult tuple = checkFunc.check(s);
                    if (!tuple.successful) {
                        return tuple;   // return tuple with error
                    }
                }
            }
            // all checks passed
            return CheckFuncResult.success();
        };
    }

    /**
     * Similar to empty, but length can be 0 or all white space characters.
     * @param checkFuncs
     * @return
     */
    public static CheckFunc<String> emptyWhitespaceOrMeetsChecks(CheckFunc<String>... checkFuncs) {
        return s -> {
            if (s.length() == 0 || s.matches("\\s+")) {
                return CheckFuncResult.success();
            } else {
                for (CheckFunc<String> checkFunc : checkFuncs) {
                    CheckFuncResult tuple = checkFunc.check(s);
                    if (!tuple.successful) {
                        return tuple;   // return tuple with error
                    }
                }
            }
            // all checks passed
            return CheckFuncResult.success();
        };
    }

}
