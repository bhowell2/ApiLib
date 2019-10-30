package io.github.bhowell2.apilib.formatters;

/**
 * @author Blake Howell
 */
public final class StringFormatters {

	public static final Formatter<String, String> TRIM_LEADING_AND_TRAILING_WHITESPACE = s -> Formatter.Result.success(s.trim());

	public static final Formatter<String, String> TRIM_LEADING_WHITESPACE = s -> {
		// like java source code for String#trim(), but only leading whitespace.
		int len = s.length();
		int start = 0;
		while ((start < len) && (s.charAt(start) <= ' ')) {
			start++;
		}
		return Formatter.Result.success(s.substring(start));
	};

	public static final Formatter<String, String> TRIM_TRAILING_WHITESPACE = s -> {
		// like java source code for String#trim(), but only trailing whitespace
		int len = s.length() - 1;
		while ((len > 0) && (s.charAt(len) <= ' ')) {
			len--;
		}
		// have to add 1, because substring end point is exclusive
		return Formatter.Result.success(s.substring(0, len + 1));
	};

	public static final Formatter<String, String> TO_UPPERCASE = s -> Formatter.Result.success(s.toUpperCase());

}
