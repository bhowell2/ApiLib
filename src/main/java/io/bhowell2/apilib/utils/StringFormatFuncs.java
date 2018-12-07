package io.bhowell2.apilib.utils;

import io.bhowell2.apilib.FormatFunc;

import java.util.Locale;

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

    public static final FormatFunc<String> TO_UPPER_CASE = String::toUpperCase;

    public static final FormatFunc<String> toUpperCaseLocale(Locale locale) {
        return s -> s.toUpperCase(locale);
    }

}
