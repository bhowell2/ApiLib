package io.github.bhowell2.apilib.formatters;

import java.text.Normalizer;

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

	public static final Formatter<String, String> NORMALIZE_NFC = s ->
		Formatter.Result.success(Normalizer.normalize(s, Normalizer.Form.NFC));

	public static final Formatter<String, String> NORMALIZE_NFD = s ->
		Formatter.Result.success(Normalizer.normalize(s, Normalizer.Form.NFD));

	public static final Formatter<String, String> NORMALIZE_NFKC = s ->
		Formatter.Result.success(Normalizer.normalize(s, Normalizer.Form.NFKC));

	public static final Formatter<String, String> NORMALIZE_NFKD = s ->
		Formatter.Result.success(Normalizer.normalize(s, Normalizer.Form.NFKD));


	public static final Formatter<String, Integer> STRING_TO_INTEGER_FORMATTER =
		(String s) -> {
			try {
				return Formatter.Result.success(Integer.parseInt(s));
			} catch (Exception e) {
				return Formatter.Result.failure("Failed to parse integer from string '" + s + "'.");
			}
		};

}
