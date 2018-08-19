package io.bhowell2.ApiLib.utils;

import io.bhowell2.ApiLib.CheckFunc;
import io.bhowell2.ApiLib.CheckFuncResult;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
                    CheckFuncResult checkResult = checkFunc.check(s);
                    if (!checkResult.successful) {
                        return checkResult;   // return error
                    }
                }
            }
            // all checks passed
            return CheckFuncResult.success();
        };
    }

    public static CheckFunc<String> IS_NOT_EMPTY_OR_ONLY_WHITESPACE = s -> s.matches("^\\s*$") ?
        CheckFuncResult.failure("cannot be empty or only contain whitespace.") : CheckFuncResult.success();

    public static CheckFunc<String> IS_EMPTY_OR_ONLY_WHITESPACE = s -> s.matches("^\\s*$") ? CheckFuncResult.success() :
    CheckFuncResult.failure("must be empty or only contain whitespace.");

    /**
     * Similar to empty, but length can be 0 or all white space characters.
     * @param checkFuncs
     * @return
     */
    public static CheckFunc<String> emptyWhitespaceOrMeetsChecks(CheckFunc<String>... checkFuncs) {
        return s -> {
            if (s.length() == 0 || s.matches("^\\s+$")) {
                return CheckFuncResult.success();
            } else {
                for (CheckFunc<String> checkFunc : checkFuncs) {
                    CheckFuncResult checkResult = checkFunc.check(s);
                    if (!checkResult.successful) {
                        return checkResult;   // return error
                    }
                }
            }
            // all checks passed
            return CheckFuncResult.success();
        };
    }

    /**
     * Checks that the string does not begin with any of the provided characters.
     * @param chars
     * @return
     */
    public static CheckFunc<String> cannotBeginWithChars(Set<Character> chars) {
        return s -> {
            for (Character c : chars) {
                if (s.charAt(0) == c) {
                    return CheckFuncResult.failure("cannot begin with '" + c + "'.");
                }
            }
            return CheckFuncResult.success();
        };
    }

    /**
     * Checks that the string does not begin with any of the provided characters in the string.
     * @param chars
     * @return
     */
    public static CheckFunc<String> cannotBeginWithChars(String chars) {
        return s -> {
            for (int i = 0; i < chars.length(); i++) {
                if (s.charAt(0) == chars.charAt(i)) {
                    return CheckFuncResult.failure("cannot begin with '" + chars.charAt(i) + "'.");
                }
            }
            return CheckFuncResult.success();
        };
    }

    /**
     * Not a regex match, so casing does matter.
     * @param equalsList
     * @return checkFunc that ensures the string to be checked equals one of the strings in the list
     */
    public static CheckFunc<String> equalsStringInList(List<String> equalsList) {
        return s -> {
            for (String equals : equalsList) {
                if (equals.equals(s)) {
                    return CheckFuncResult.success();
                }
            }
            return CheckFuncResult.failure("does not equal one of the following strings: " + equalsList.stream().collect(Collectors.joining(",")));
        };
    }

    /**
     * Not a regex match, so casing does matter.
     * Using some type of Hashed set will be faster than using the list alternative of this.
     * @param equalsSet
     * @return
     */
    public static CheckFunc<String> equalsStringInSet(Set<String> equalsSet) {
        return s -> equalsSet.contains(s) ? CheckFuncResult.success() :
            CheckFuncResult.failure("must equal: " + equalsSet.stream().collect(Collectors.joining(",")));
    }

    /**
     * Not a regex match, so casing does matter.
     * Checks to ensure that the string does not equal any strings provided in the list.
     * @param notEqualsList
     * @return
     */
    public static CheckFunc<String> doesNotEqualStringInList(List<String> notEqualsList) {
        return s -> {
            for (String dontEqualString : notEqualsList) {
                if (s.equals(dontEqualString)) {
                    return CheckFuncResult.failure("cannot be equal to '" + dontEqualString + "'.");
                }
            }
            return CheckFuncResult.success();
        };
    }

    /**
     * Not a regex match, so casing does matter.
     * Using some type of Hashed set will be faster than using the list alternative of this.
     * @param notEqualsSet
     * @return
     */
    public static CheckFunc<String> doesNotEqualStringInSet(Set<String> notEqualsSet) {
        return s ->
            notEqualsSet.contains(s) ?
                CheckFuncResult.failure("cannot be one of the following strings: " + notEqualsSet.stream().collect(Collectors.joining(","))) :
                CheckFuncResult.success();
    }

    public static CheckFunc<String> doesNotEqualStringInListIgnoreCase(List<String> notEqualList) {
        return s -> {
            s = s.toLowerCase();
            for (String notEqualString : notEqualList) {
                if (notEqualString.toLowerCase().equals(s)) {
                    return CheckFuncResult
                        .failure("Cannot equal one of the following strings: " + notEqualList.stream().collect(Collectors.joining(",")));
                }
            }
            return CheckFuncResult.success();
        };
    }

    /**
     * Only permits unreserved URL characters in the string (i.e., a-z, 0-9, '-', '.', '_', '~')
     * @return successful if the string only contains unreserved URL characters. otherwise it fails
     */
    public static CheckFunc<String> ONLY_ALLOW_UNRESERVED_URL_CHARS = s -> {
        String lower = s.toLowerCase();
        for (int i = 0; i < lower.length(); i++) {
            if ((lower.charAt(i) < 'a' || lower.charAt(i) > 'z') &&
                (lower.charAt(i) < '0' || lower.charAt(i) > '9') &&
                (lower.charAt(i) != '-' && lower.charAt(i) != '.' && lower.charAt(i) != '_' && lower.charAt(i) != '~')) {
                return CheckFuncResult.failure("contains reserved URL characters. May only contain a-z, 0-9, '-', '.', '_', and '~'");
            }
        }
        return CheckFuncResult.success();
    };

}
