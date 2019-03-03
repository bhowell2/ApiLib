package io.bhowell2.apilib.utils.formatfuncs;

import io.bhowell2.apilib.FormatFunc;

import java.util.Locale;

/**
 * @author Blake Howell
 */
public class StringFormatFuncs {

	public static final FormatFunc<String, String> TRIM = String::trim;

	// TODO would be faster with loop rather than regex
	public static final FormatFunc<String, String> REMOVE_TRAILING_WHITESPACE = s -> s.replaceAll("\\s*$", "");

	public static final FormatFunc<String, String> replaceStringPartWith(String toReplace, String replacement) {
		return s -> s.replaceAll(toReplace, replacement);
	}

	public static final FormatFunc<String, String> TO_UPPER_CASE = String::toUpperCase;

	public static final FormatFunc<String, String> toUpperCaseLocale(Locale locale) {
		return s -> s.toUpperCase(locale);
	}

}
