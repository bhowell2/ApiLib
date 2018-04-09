package io.bhowell2.ApiLib.utils;

import io.bhowell2.ApiLib.FormatFunc;

/**
 * @author Blake Howell
 */
public class StringFormatFuncs {

    public static final FormatFunc<String> TRIM = String::trim;

    // TODO would be faster with loop rather than regex
    public static final FormatFunc<String> REMOVE_TRAILING_WHITESPACE = s -> s.replaceAll("\\s*$", "");

    public static final FormatFunc<String> replaceStringPartWith(String toReplace, String replacement) {
        return s -> s.replaceAll(toReplace, replacement);
    }

}
