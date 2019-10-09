package io.github.bhowell2.apilib.checks;

import io.github.bhowell2.apilib.ApiParamError;
import io.github.bhowell2.apilib.checks.utils.CodePointUtils;
import io.github.bhowell2.apilib.checks.utils.CollectionUtils;
import io.github.bhowell2.apilib.checks.utils.IntegerUtils;
import io.github.bhowell2.apilib.checks.utils.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Great care must be taken when using string checks in Java (and probably any language..). There are a few articles
 * (see below) the user may want to read to brush up on unicode and how "characters" are represented in the various
 * unicode encodings (e.g., UTF-8, UTF-16). Generally speaking a String in Java should be considered to be in UTF-16
 * (which uses 2 bytes, 16 bits, to represent a character), but as of Java 9 strings may be in an optimized format
 * (called compact. encoding: ISO-8859-1/Latin-1) if they ONLY contain characters that can be represented in 1 byte -
 * this is not too relevant to the string checks here, but is worth mentioning.
 *
 * This class will provide checks that help the user handle unicode encodings when checking a string for certain conditions.
 *
 * Unicode Articles:
 * https://www.joelonsoftware.com/2003/10/08/the-absolute-minimum-every-software-developer-absolutely-positively-must-know-about-unicode-and-character-sets-no-excuses/
 * http://kunststube.net/encoding/
 * https://kishuagarwal.github.io/unicode.html
 * http://utf8everywhere.org/ - java doesn't use this, but still provides information for a solid understanding
 * http://www.unicode.org/versions/latest/ - of course
 *
 *
 * Overview of terms (taken from: https://stackoverflow.com/questions/27331819/whats-the-difference-between-a-character-a-code-point-a-glyph-and-a-grapheme)
 *
 * Character    - is an overloaded term than can mean many things.
 *
 * Code point   - is the atomic unit of information. Text is a sequence of code points. Each code point is a number
 *                which is given meaning by the Unicode standard.
 *
 * Code unit    - is the unit of storage of a part of an encoded code point. In UTF-8 this means 8-bits, in UTF-16
 *                this means 16-bits. A single code unit may represent a full code point, or part of a code point.
 *                For example, the snowman glyph (☃) is a single code point but 3 UTF-8 code units, and 1 UTF-16 code unit.
 *
 * Grapheme     - is a sequence of one or more code points that are displayed as a single, graphical unit that a reader
 *                recognizes as a single element of the writing system. For example, both a and ä are graphemes, but
 *                they may consist of multiple code points (e.g. ä may be two code points, one for the base character a
 *                followed by one for the diaresis; but there's also an alternative, legacy, single code point
 *                representing this grapheme). Some code points are never part of any grapheme (e.g. the zero-width
 *                non-joiner, or directional overrides).
 *
 * Glyph        - is an image, usually stored in a font (which is a collection of glyphs), used to represent graphemes
 *                or parts thereof. Fonts may compose multiple glyphs into a single representation, for example, if the
 *                above ä is a single code point, a font may chose to render that as two separate, spatially overlaid
 *                glyphs. For OTF, the font's GSUB and GPOS tables contain substitution and positioning information to
 *                make this work. A font may contain multiple alternative glyphs for the same grapheme, too.
 *
 * As mentioned in the terms above, a Grapheme may appear to be one character, but could consists of multiple code points,
 * so even the string checks provided below may not work exactly as expected. Generally the user should prefer to use
 * the "codePoint*" functions over non-codePoint functions to handle unicode input better.
 *
 * E.g., {@code "🤓".length() = 2}, because it consists of 2 characters.
 * The method {@link String#charAt(int)} will return the individual char at the specified position, but this would not
 * be the actual item that was displayed at what is visibly position 0 (array index notation) of the text sequence in the
 * example, because it actually consists of 2 chars. While 2 chars (32 bits) is plenty to represent all
 * of the codepoints in the unicode standard, some "characters" at a given position in a text sequence are
 * actually a combination of codepoints (grapheme) and are thus more than even 2 chars (data type, 16 bits)..
 *
 * @author Blake Howell
 */
public final class StringChecks {

	private StringChecks() {} // no instantiation

	/**
	 * Check to ensure that the string is empty (i.e., length = 0).
	 */
	public static final Check<String> IS_EMPTY = s -> s.isEmpty()
		?
		Check.Result.success()
		:
		Check.Result.failure("Must be empty.");

	public static final Check<String> IS_NOT_EMPTY = s -> !s.isEmpty()
		?
		Check.Result.success()
		:
		Check.Result.failure("Cannot be empty");

	/**
	 * Check to ensure the string is empty or only contains whitespace.
	 * This supports supplementary unicode characters.
	 */
	public static final Check<String> IS_EMPTY_OR_ONLY_WHITESPACE = s -> {
		if (s.length() == 0) {
			return Check.Result.success();
		}
		boolean isOnlyWhitespace = true;
		for (int i = 0; i < s.length(); i++) {
			/*
			 * This should not be run for the second part of the surrogate pair, but even if the position is a low-end
			 * surrogate pair it will not be whitespace due to the low-end being U+DC00 to U+DFFF, which is not whitespace.
			 * */
			int codePoint = s.codePointAt(i);
			if (!Character.isWhitespace(codePoint)) {
				isOnlyWhitespace = false;
				break;
			}
		}
		return isOnlyWhitespace
			?
			Check.Result.success()
			:
			Check.Result.failure("Must be empty or contain only whitespace.");
	};

	/**
	 * Check to ensure the string is NOT empty or contains only whitespace.
	 */
	public static final Check<String> IS_NOT_EMPTY_OR_ONLY_WHITESPACE = s ->
		IS_EMPTY_OR_ONLY_WHITESPACE.check(s).successful()
			?
			Check.Result.failure("Cannot be empty or only contain whitespace.")
			:
			Check.Result.success();


	/**
	 * Only permits unreserved URL characters in the string (i.e., a-z, 0-9, '-', '.', '_', '~').
	 * Successful if the string only contains unreserved URL characters, fails otherwise.
	 */
	public static final Check<String> ONLY_ALLOW_UNRESERVED_URL_CHARS = s -> {
		for (int i = 0; i < s.length(); i++) {
			/*
			 * Can just use a character here, because even if the string provided contains 2-character codepoints
			 * they will be outside of the range of unreserved url characters (U+D8000 to U+DFFFF in each position).
			 * */
			char c = s.charAt(i);
			if (
				(c < 'A' || c > 'Z') &&
					(c < 'a' || c > 'z') &&
					(c < '0' || c > '9') &&
					(c != '-' && c != '.' && c != '_' && c != '~')
			) {
				return Check.Result.failure("Contains reserved URL characters. May only contain A-Z, " +
					                            "a-z, 0-9, '-', '.', '_', and '~'");
			}
		}
		return Check.Result.success();
	};

	private static final Pattern BASIC_EMAIL_PATTERN = Pattern.compile(".+@.+\\..{2,}$");

	/**
	 * A very basic email check. This only limits the email string to the form X@Y.Z,
	 * where X is at least 1 character, Y is at least 1 character, and Z is 2 or more
	 * characters (this is because public top level domains are a minimum of 2 chars).
	 *
	 * Warning: this does not limit the unicode at any position, so emojis could
	 * be submitted. The developer should always validate the email by actually
	 * sending a verification email.
	 */
	public static final Check<String> MATCHES_BASIC_EMAIL_PATTERN = s ->
		BASIC_EMAIL_PATTERN.matcher(s).matches()
			?
			Check.Result.success()
			:
			Check.Result.failure("Is not a valid email address.");

	/**
	 * Creates check which ensures the length of the string is greater than min.
	 * This uses {@link String#length()} which is based on how many java characters
	 * (each char is 16 bits) are in the string, which may not actually be desired
	 * since a visual position in the text sequence may take 2 java characters to
	 * represent it. Generally, using {@link #codePointCountGreaterThan(int)} is
	 * preferable and more aligned with expectations.
	 *
	 * E.g.,
	 * {@code "🤓".length() = 2} because the emoji takes 2 java characters to represent it,
	 * but {@code "🤓".codePointCount(..) = 1} because the emoji is one code point.
	 */
	public static Check<String> lengthGreaterThan(int min) {
		IntegerUtils.requireIntGreaterThanOrEqualTo(0, min);
		return s -> {
			if (s.length() > min) {
				return Check.Result.success();
			} else {
				return Check.Result.failure("Length must be greater than " + min);
			}
		};
	}

	/**
	 * Creates check which ensures the code point count of the string is greater than min.
	 * This uses {@link String#codePointCount(int, int)} rather than {@link String#length()}.
	 *
	 * E.g.,
	 * {@code "🤓".length() = 2} because the emoji takes 2 java characters to represent it,
	 * but {@code "🤓".codePointCount(..) = 1} because the emoji is one code point.
	 */
	public static Check<String> codePointCountGreaterThan(int min) {
		IntegerUtils.requireIntGreaterThanOrEqualTo(0, min);
		return s -> {
			if (s.codePointCount(0, s.length()) > min) {
				return Check.Result.success();
			} else {
				return Check.Result.failure("Length must be greater than " + min);
			}
		};
	}

	/**
	 * Creates check which ensures the length of the string is greater than or equal to min.
	 * This uses {@link String#length()} which is based on how many java characters
	 * (each char is 16 bits) are in the string, which may not actually be desired
	 * since a visual position in the text sequence may take 2 java characters to
	 * represent it. Generally, using {@link #codePointCountGreaterThanOrEqualTo(int)} is
	 * preferable and more aligned with expectations.
	 *
	 * E.g.,
	 * {@code "🤓".length() = 2} because the emoji takes 2 java characters to represent it,
	 * but {@code "🤓".codePointCount(..) = 1} because the emoji is one code point.
	 */
	public static Check<String> lengthGreaterThanOrEqualTo(int min) {
		IntegerUtils.requireIntGreaterThanOrEqualTo(0, min);
		return s -> {
			if (s.length() >= min) {
				return Check.Result.success();
			} else {
				return Check.Result.failure("Length must be greater than or equal to " + min);
			}
		};
	}

	/**
	 * Creates check which ensures the code point count of the string is greater than
	 * or equal to min. This uses {@link String#codePointCount(int, int)} rather than
	 * {@link String#length()}.
	 *
	 * E.g.,
	 * {@code "🤓".length() = 2} because the emoji takes 2 java characters to represent it,
	 * but {@code "🤓".codePointCount(..) = 1} because the emoji is one code point.
	 */
	public static Check<String> codePointCountGreaterThanOrEqualTo(int min) {
		IntegerUtils.requireIntGreaterThanOrEqualTo(0, min);
		return s -> {
			if (s.codePointCount(0, s.length()) >= min) {
				return Check.Result.success();
			} else {
				return Check.Result.failure("Length must be greater than or equal to " + min);
			}
		};
	}

	/**
	 * Creates check which ensures the length of the string is less than max.
	 * This uses {@link String#length()} which is based on how many java characters
	 * (each char is 16 bits) are in the string, which may not actually be desired
	 * since a visual position in the text sequence may take 2 java characters to
	 * represent it. Generally, using {@link #codePointCountLessThan(int)} is
	 * preferable and more aligned with expectations.
	 *
	 * E.g.,
	 * {@code "🤓".length() = 2} because the emoji takes 2 java characters to represent it,
	 * but {@code "🤓".codePointCount(..) = 1} because the emoji is one code point.
	 */
	public static Check<String> lengthLessThan(int max) {
		IntegerUtils.requireIntGreaterThanOrEqualTo(1, max, "String length cannot be less than 0.");
		return s -> {
			if (s.length() < max) {
				return Check.Result.success();
			} else {
				return Check.Result.failure("Length must be less than " + max);
			}
		};
	}

	/**
	 * Creates check which ensures the code point count of the string is less than max.
	 * This uses {@link String#codePointCount(int, int)} rather than {@link String#length()}.
	 *
	 * E.g.,
	 * {@code "🤓".length() = 2} because the emoji takes 2 java characters to represent it,
	 * but {@code "🤓".codePointCount(..) = 1} because the emoji is one code point.
	 */
	public static Check<String> codePointCountLessThan(int max) {
		IntegerUtils.requireIntGreaterThanOrEqualTo(1, max, "String length cannot be less than 0.");
		return s -> {
			if (s.codePointCount(0, s.length()) < max) {
				return Check.Result.success();
			} else {
				return Check.Result.failure("Length must be less than " + max);
			}
		};
	}

	/**
	 * Creates check which ensures the length of the string is less than or equal to max.
	 * This uses {@link String#length()} which is based on how many java characters
	 * (each char is 16 bits) are in the string, which may not actually be desired
	 * since a visual position in the text sequence may take 2 java characters to
	 * represent it. Generally, using {@link #codePointCountLessThan(int)} is
	 * preferable and more aligned with expectations.
	 *
	 * E.g.,
	 * {@code "🤓".length() = 2} because the emoji takes 2 java characters to represent it,
	 * but {@code "🤓".codePointCount(..) = 1} because the emoji is one code point.
	 */
	public static Check<String> lengthLessThanOrEqualTo(int max) {
		IntegerUtils.requireIntGreaterThanOrEqualTo(0, max);
		return s -> {
			if (s.length() <= max) {
				return Check.Result.success();
			} else {
				return Check.Result.failure("Length must be less than of equal to " + max);
			}
		};
	}

	/**
	 * Creates check which ensures the code point count of the string is less than or
	 * equal to max. This uses {@link String#codePointCount(int, int)} rather than
	 * {@link String#length()}.
	 *
	 * E.g.,
	 * {@code "🤓".length() = 2} because the emoji takes 2 java characters to represent it,
	 * but {@code "🤓".codePointCount(..) = 1} because the emoji is one code point.
	 */
	public static Check<String> codePointCountLessThanOrEqualTo(int max) {
		IntegerUtils.requireIntGreaterThanOrEqualTo(0, max);
		return s -> {
			if (s.codePointCount(0, s.length()) <= max) {
				return Check.Result.success();
			} else {
				return Check.Result.failure("Length must be less than of equal to " + max);
			}
		};
	}

	/**
	 * Creates check which ensures the length of the string is equal to length.
	 * This uses {@link String#length()} which is based on how many java characters
	 * (each char is 16 bits) are in the string, which may not actually be desired
	 * since a visual position in the text sequence may take 2 java characters to
	 * represent it. Generally, using {@link #codePointCountEqualTo(int)} is
	 * preferable and more aligned with expectations.
	 *
	 * E.g.,
	 * {@code "🤓".length() = 2} because the emoji takes 2 java characters to represent it,
	 * but {@code "🤓".codePointCount(..) = 1} because the emoji is one code point.
	 */
	public static Check<String> lengthEqualTo(int length) {
		IntegerUtils.requireIntGreaterThanOrEqualTo(0, length);
		return s -> {
			if (s.length() == length) {
				return Check.Result.success();
			} else {
				return Check.Result.failure("Length must be equal to " + length);
			}
		};
	}

	/**
	 * Creates check which ensures the code point count of the string is equal to length.
	 * This uses {@link String#codePointCount(int, int)} rather than {@link String#length()}.
	 *
	 * E.g.,
	 * {@code "🤓".length() = 2} because the emoji takes 2 java characters to represent it,
	 * but {@code "🤓".codePointCount(..) = 1} because the emoji is one code point.
	 */
	public static Check<String> codePointCountEqualTo(int length) {
		IntegerUtils.requireIntGreaterThanOrEqualTo(0, length);
		return s -> {
			if (s.codePointCount(0, s.length()) == length) {
				return Check.Result.success();
			} else {
				return Check.Result.failure("Length must be equal to " + length);
			}
		};
	}

	/**
	 * Creates check which ensures the param string matches the pattern or returns a failure.
	 * The failure message is generic as it would be unwise to return the pattern that was
	 * not matched as that could aid user exploitation.
	 */
	public static Check<String> matchesRegex(Pattern pattern) {
		if (pattern.pattern().equals("")) {
			throw new IllegalArgumentException("Cannot create check with empty regex. If an empty string is desired use " +
				                                   "StringChecks.lengthEqualTo(0) or StringChecks.IS_EMPTY");
		}
		return s -> pattern.matcher(s).matches()
			?
			Check.Result.success()
			:
			/*
			 * Regexes are fickle so not returning the regex pattern here to avoid the developer
			 * accidentally sending it back to the user, leading to easier exploitation.
			 * */
			Check.Result.failure("Is not of correct form.");
	}

	/**
	 * Creates check that ensures the first "character" in the string begins with one
	 * of the codepoints in the supplied string.
	 *
	 * Fails on empty string.
	 *
	 * @param codePoints string where each code point is treated individually
	 */
	public static Check<String> beginsWithCodePoints(String codePoints) {
		Objects.requireNonNull(codePoints);
		StringUtils.requireNonEmptyString(codePoints);
		StringUtils.requireUniqueCodePoints(codePoints);
		StringUtils.requireCodePointCountGreaterThanOrEqualTo(1, codePoints);
		return s -> {
			if (s.isEmpty()) {
				return Check.Result.failure("Must begin with one of the following characters: '" + codePoints + "'.");
			}
			for (int i = 0; i < codePoints.length(); i++) {
				int shouldBeginWithCodepoint = codePoints.codePointAt(i);
				if (shouldBeginWithCodepoint == s.codePointAt(0)) {
					return Check.Result.success();
				} else if (Character.isSupplementaryCodePoint(shouldBeginWithCodepoint)) {
					// skip next position, because it is 2nd char making up the single code point
					i++;
				}
			}
			return Check.Result.failure("Must begin with one of the following characters: '" + codePoints + "'.");
		};
	}

	/**
	 * Creates check that ensures the string begins with a code point in the range (inclusive).
	 *
	 * Fails on empty string.
	 *
	 * @param minCodePoint single code point string that contains beginning code point of the range (inclusive)
	 * @param maxCodePoint single code point string that contains ending code point of the range (inclusive)
	 */
	public static Check<String> beginsWithCodePointsInRange(String minCodePoint, String maxCodePoint) {
		StringUtils.requireCodePointCountEqualTo(1, minCodePoint);
		StringUtils.requireCodePointCountEqualTo(1, maxCodePoint);
		return beginsWithCodePointsInRange(minCodePoint.codePointAt(0), maxCodePoint.codePointAt(0));
	}

	/**
	 * Creates check that ensures the string begins with a code point in the range (inclusive).
	 *
	 * Fails on empty string.
	 *
	 * @param minCodePoint inclusive minimum code point
	 * @param maxCodePoint inclusive maximum code point
	 */
	public static Check<String> beginsWithCodePointsInRange(int minCodePoint, int maxCodePoint) {
		IntegerUtils.requireIntGreaterThanOrEqualTo(0, minCodePoint);
		IntegerUtils.requireIntGreaterThanOrEqualTo(minCodePoint, maxCodePoint,
		                                            "max code point must be greater than or equal to min code point.");
		return s -> {
			if (s.isEmpty()) {
				return Check.Result.failure("First character must be within range '" +
					                            String.valueOf(Character.toChars(minCodePoint)) +
					                            "' to '" +
					                            String.valueOf(Character.toChars(maxCodePoint)) +
					                            "'.");
			}
			int codePoint = s.codePointAt(0);
			if (codePoint < minCodePoint || codePoint > maxCodePoint) {
				return Check.Result.failure("First character must be within range '" +
					                            String.valueOf(Character.toChars(minCodePoint)) +
					                            "' to '" +
					                            String.valueOf(Character.toChars(maxCodePoint)) +
					                            "'.");
			}
			return Check.Result.success();
		};
	}

	/**
	 * Creates check that ensures the string does not begin with any code points in the supplied string.
	 *
	 * Does not fail on empty string.
	 *
	 * @param codePoints each code point in the string is treated individually
	 */
	public static Check<String> doesNotBeginWithCodePoints(String codePoints) {
		StringUtils.requireNonEmptyString(codePoints);
		StringUtils.requireCodePointCountGreaterThanOrEqualTo(1, codePoints);
		StringUtils.requireUniqueCodePoints(codePoints);
		return s -> {
			if (s.length() > 0) {
				int beginningCodePoint = s.codePointAt(0);
				for (int i = 0; i < codePoints.length(); i++) {
					int shouldNotBeginWithCodepoint = codePoints.codePointAt(i);
					if (shouldNotBeginWithCodepoint == beginningCodePoint) {
						return Check.Result.failure("Must begin with one of the following characters: '" + codePoints + "'.");
					} else if (Character.isSupplementaryCodePoint(shouldNotBeginWithCodepoint)) {
						// skip next position, because it is 2nd char making up the single code point
						i++;
					}
				}
			}
			return Check.Result.success();
		};
	}

	/**
	 * Creates check that ensures the string does not begin with any code point
	 * (inclusive) in the range supplied.
	 *
	 * Does not fail on empty string.
	 *
	 * @param minCodePoint single code point string that contains beginning code point of the range (inclusive)
	 * @param maxCodePoint single code point string that contains ending code point of the range (inclusive)
	 */
	public static Check<String> doesNotBeginWithCodePointsInRange(String minCodePoint, String maxCodePoint) {
		StringUtils.requireCodePointCountEqualTo(1, minCodePoint);
		StringUtils.requireCodePointCountEqualTo(1, maxCodePoint);
		return doesNotBeginWithCodePointsInRange(minCodePoint.codePointAt(0), maxCodePoint.codePointAt(0));
	}

	/**
	 * Creates check that ensures the string does not begin with any code point
	 * (inclusive) in the range supplied.
	 *
	 * Does not fail on empty string.
	 *
	 * @param minCodePoint inclusive minimum code point
	 * @param maxCodePoint inclusive maximum code point
	 */
	public static Check<String> doesNotBeginWithCodePointsInRange(int minCodePoint, int maxCodePoint) {
		IntegerUtils.requireIntGreaterThanOrEqualTo(0, minCodePoint);
		IntegerUtils.requireIntGreaterThanOrEqualTo(minCodePoint, maxCodePoint,
		                                            "max code point must be greater than or equal to min code point.");
		return s -> {
			if (s.length() > 0) {
				int beginCodePoint = s.codePointAt(0);
				if (beginCodePoint >= minCodePoint && beginCodePoint <= maxCodePoint) {
					return Check.Result.failure("First character cannot be within range '" +
						                            String.valueOf(Character.toChars(minCodePoint)) +
						                            "' to '" +
						                            String.valueOf(Character.toChars(maxCodePoint)) +
						                            "'.");
				}
			}
			return Check.Result.success();
		};
	}

	/**
	 * Creates check that ensures the string begins with one of the strings supplied.
	 *
	 * Fails on empty string.
	 *
	 * @param strings list of strings that the supplied string must begin with
	 */
	public static Check<String> beginsWithStrings(String... strings) {
		Objects.requireNonNull(strings);
		CollectionUtils.requireNonNullEntries(strings);
		return s -> {
			for (int i = 0; i < strings.length; i++) {
				String beginWithString = strings[i];
				if (s.length() < beginWithString.length()) {
					continue; // cannot begin with this string if it is longer than param string
				}
				for (int j = 0; j < beginWithString.length(); j++) {
					if (beginWithString.charAt(j) != s.charAt(j)) {
						break;    // does not equal this current beginWithString, break to go to next.
					}
					if (j == beginWithString.length() - 1) {
						// all characters checked and matched
						return Check.Result.success();
					}
				}
			}
			String joinedStringsErrMsg = Arrays.stream(strings)
			                                   .map(str -> "\"" + str + "\"")
			                                   .collect(Collectors.joining(","));
			return Check.Result.failure("Must begin with one of the following strings: " + joinedStringsErrMsg + ".");
		};
	}

	/**
	 * Creates check that ensures the string does not begin with one of the strings supplied.
	 *
	 * Does not fail on empty string.
	 *
	 * @param strings list of strings that the param string must NOT begin with
	 */
	public static Check<String> doesNotBeginWithStrings(String... strings) {
		Objects.requireNonNull(strings);
		CollectionUtils.requireNonNullEntries(strings);
		return s -> {
			// checking this here, because loop below iterates over strings to check against and not the param string supplied
			if (s.isEmpty()) {
				return Check.Result.success();
			}
			for (int i = 0; i < strings.length; i++) {
				String beginWithString = strings[i];
				if (s.length() < beginWithString.length()) {
					continue;
				}
				for (int j = 0; j < beginWithString.length(); j++) {
					if (beginWithString.charAt(j) != s.charAt(j)) {
						break;
					}
					if (j == beginWithString.length() - 1) {
						// all characters checked and matched. should not have. fail.
						String joinedStringsErrMsg = Arrays.stream(strings)
						                                   .map(str -> "\"" + str + "\"")
						                                   .collect(Collectors.joining(","));
						return Check.Result.failure("Must begin with one of the following strings: " + joinedStringsErrMsg + ".");
					}
				}
			}
			// nothing matched
			return Check.Result.success();
		};
	}

	/**
	 * Creates check that ensures ALL code points in the param string are within the
	 * range (inclusive) of code points specified. This will fail if ANY code point
	 * in the param string is outside of the range.
	 *
	 * Fails on empty string.
	 *
	 * @param minCodePoint single code point string that contains beginning code point of the range (inclusive)
	 * @param maxCodePoint single code point string that contains ending code point of the range (inclusive)
	 */
	public static Check<String> limitCodePointsToRange(String minCodePoint, String maxCodePoint, boolean allowWhitespace) {
		StringUtils.requireCodePointCountEqualTo(1, minCodePoint);
		StringUtils.requireCodePointCountEqualTo(1, maxCodePoint);
		return limitCodePointsToRange(minCodePoint.codePointAt(0), maxCodePoint.codePointAt(0), allowWhitespace);
	}

	/**
	 * Creates check that ensures all code points in the string are within the specified
	 * range (inclusive) of code points.
	 *
	 * Fails on empty string.
	 *
	 * @param minCodePoint minimum code point (inclusive)
	 * @param maxCodePoint maximum code point (inclusive)
	 * @param allowWhitespace whether or not whitespace should be checked in range
	 */
	public static Check<String> limitCodePointsToRange(int minCodePoint, int maxCodePoint, boolean allowWhitespace) {
		IntegerUtils.requireIntGreaterThanOrEqualTo(0, minCodePoint);
		IntegerUtils.requireIntGreaterThanOrEqualTo(minCodePoint, maxCodePoint,
		                                            "max code point must be greater than or equal to min code point.");
		return s -> {
			if (s.isEmpty()) {
				return Check.Result.failure("Characters must be within range '" +
					                            String.valueOf(Character.toChars(minCodePoint)) +
					                            "' to '" +
					                            String.valueOf(Character.toChars(maxCodePoint)) +
					                            "'.");
			}
			for (int i = 0; i < s.length(); i++) {
				int codePoint = s.codePointAt(i);
				if (!(allowWhitespace && Character.isWhitespace(codePoint)) &&
					(codePoint < minCodePoint || codePoint > maxCodePoint)) {
					return Check.Result.failure("Characters must be within range '" +
						                            String.valueOf(Character.toChars(minCodePoint)) +
						                            "' to '" +
						                            String.valueOf(Character.toChars(maxCodePoint)) +
						                            "'.");
				}
				if (Character.isSupplementaryCodePoint(codePoint)) {
					// skip next position, because it is 2nd char making up the single code point
					i++;
				}
			}
			return Check.Result.success();
		};
	}

	/**
	 * Creates check that ensures the string contains the (single) code point a
	 * minimum number of times.
	 *
	 * Fails on empty string.
	 *
	 * @param min minimum number of times code point must be supplied in string
	 * @param codePoint string with a single code point (i.e., {@code String.codePointCount(..) == 1})
	 */
	public static Check<String> containsCodePoint(int min, String codePoint) {
		Objects.requireNonNull(codePoint);
		IntegerUtils.requireIntGreaterThan(0, min);
		StringUtils.requireCodePointCountEqualTo(1, codePoint);
		return containsCodePoint(min, codePoint.codePointAt(0));
	}

	/**
	 * Creates check that ensures the string contains the (single) code point a
	 * minimum number of times.
	 *
	 * Fails on empty string.
	 *
	 * @param min minimum number of times code point must be supplied in string
	 * @param codePoint int of required code point
	 */
	public static Check<String> containsCodePoint(int min, int codePoint) {
		return containsCodePointsInRange(min, codePoint, codePoint, false);
	}

	/**
	 * Creates check that ensures the string contains code points within the specified
	 * range a minimum number of times. This does NOT fail if code points are outside of
	 * the range specified, only fails if (at least) the minimum number is not present
	 * in the string.
	 *
	 * Fails on empty string.
	 *
	 * @param min minimum number of times the code points in the range must appear in the string
	 * @param minCodePoint inclusive minimum code point in range
	 * @param maxCodePoint inclusive maximum code point in range. must be greater than or equal to minCodePoint
	 * @param unique true if a code point should only be counted once, false otherwise
	 * @param min
	 * @param minCodePoint inclusive
	 * @param maxCodePoint
	 * @param unique
	 * @return
	 */
	public static Check<String> containsCodePointsInRange(int min,
	                                                      String minCodePoint,
	                                                      String maxCodePoint,
	                                                      boolean unique) {
		StringUtils.requireCodePointCountEqualTo(1, minCodePoint);
		StringUtils.requireCodePointCountEqualTo(1, maxCodePoint);
		return containsCodePointsInRange(min, minCodePoint.codePointAt(0), maxCodePoint.codePointAt(0), unique);
	}

	/**
	 * Creates check that ensures the string contains code points within the specified
	 * range a minimum number of times. This does NOT fail if code points are outside of
	 * the range specified, only fails if (at least) the minimum number is not present
	 * in the string. {@code unique = true} will only count a code point once.
	 *
	 * Fails on empty string.
	 *
	 * @param min minimum number of times the code points in the range must appear in the string
	 * @param minCodePoint inclusive minimum code point in range
	 * @param maxCodePoint inclusive maximum code point in range. must be greater than or equal to minCodePoint
	 * @param unique true if a code point should only be counted once, false otherwise
	 */
	public static Check<String> containsCodePointsInRange(int min,
	                                                      int minCodePoint,
	                                                      int maxCodePoint,
	                                                      boolean unique) {
		IntegerUtils.requireIntGreaterThanOrEqualTo(1, min);
		IntegerUtils.requireIntGreaterThanOrEqualTo(0, minCodePoint);
		IntegerUtils.requireIntGreaterThanOrEqualTo(minCodePoint, maxCodePoint,
		                                            "Maximum code point must be greater than or equal to the minimum.");
		if (unique && min > maxCodePoint - minCodePoint + 1) {    // add 1 because range is inclusive
			throw new IllegalArgumentException("N (" + min + ") cannot be greater than difference between min and max " +
				                                   "code points if the count must be unique.");
		}
		return s -> {
			if (s.length() < min) {
				return minCodePoint == maxCodePoint
					?
					Check.Result.failure("Must contain '" + String.valueOf(Character.toChars(minCodePoint)) + "' " +
						                     "at least " + min + " times in the provided string.")
					:
					Check.Result.failure("Must contain at least " + min + " characters within the range '" +
						                     CodePointUtils.codePointToString(minCodePoint) +
						                     "' to '" +
						                     CodePointUtils.codePointToString(maxCodePoint) + "'.");
			}
			Set<Integer> uniqueSet = null;
			if (unique) {
				uniqueSet = new HashSet<>();
			}
			int counter = 0;
			for (int i = 0; i < s.length(); i++) {
				int curCodePoint = s.codePointAt(i);
				if (curCodePoint >= minCodePoint && curCodePoint <= maxCodePoint) {
					if (unique && uniqueSet.add(curCodePoint)) {
						// increment if unique and was successfully added (returns true if it was not already in set)
						counter++;
					} else if (!unique) {
						// always increment if not unique
						counter++;
					}
					if (counter >= min) {
						return Check.Result.success();
					} else if (Character.isSupplementaryCodePoint(curCodePoint)) {
						// skip next position, because it is 2nd char making up the single code point
						i++;
					}
				}
			}
			return minCodePoint == maxCodePoint
				?
				Check.Result.failure("Must contain '" + String.valueOf(Character.toChars(minCodePoint)) + "' " +
					                     "at least " + min + " times in the provided string.")
				:
				Check.Result.failure("Must contain at least " + min + " characters within the range '" +
					                     CodePointUtils.codePointToString(minCodePoint) +
					                     "' to '" +
					                     CodePointUtils.codePointToString(maxCodePoint) + "'.");
		};
	}

	/**
	 * Creates check that ensures the string contains code points in the 'mustContainCodePoints'
	 * argument (at least) a minimum number of times. This does NOT fail if code points in the
	 * string are not in the 'mustContainCodePoints' argument, only if the string does not contain
	 * the minimum number of code points. {@code unique = true} will only count a code point once.
	 *
	 * Fails on empty string.
	 *
	 * @param min minimum number of times the code points in the 'mustContainCodePoints' argument must appear in the string
	 * @param mustContainCodePoints string with chars/code points to be matched. each code point in the string is
	 *                                treated separately
	 * @param unique true if a code point should only be counted once, false otherwise
	 */
	public static Check<String> containsCodePoints(int min, String mustContainCodePoints, boolean unique) {
		IntegerUtils.requireIntGreaterThanOrEqualTo(1, min);
		StringUtils.requireNonEmptyString(mustContainCodePoints);
		StringUtils.requireUniqueCodePoints(mustContainCodePoints);
		if (unique) {
			int codePointCount = mustContainCodePoints.codePointCount(0, mustContainCodePoints.length());
			if (min > codePointCount) {
				throw new IllegalArgumentException("Cannot have N greater than the maximum number of code points when the " +
					                                   "code points must be unique as the check would always fail.");
			}
		}
		return s -> {
			int counter = 0;
			for (int i = 0; i < mustContainCodePoints.length(); i++) {
				int codePoint = mustContainCodePoints.codePointAt(i);
				if (Character.isSupplementaryCodePoint(codePoint)) {
					// skip next position, because it is 2nd char making up the single code point
					i++;
				}
				for (int k = 0; k < s.length(); k++) {
					int inputCodePoint = s.codePointAt(k);
					if (Character.isSupplementaryCodePoint(inputCodePoint)) {
						// skip next position, because it is 2nd char making up the single code point
						k++;
					}
					if (codePoint == inputCodePoint) {
						counter++;
						if (counter >= min) {
							return Check.Result.success();
						} else if (unique) {
							// since code point has been matched and count has not been met yet move on to next code point
							break;
						}
					}
				}
			}
			return Check.Result.failure("Must contain at least " + min + " of the following: '" +
				                            mustContainCodePoints + "'.");
		};
	}

	/**
	 * Creates check that ensures the string contains the provided string.
	 */
	public static Check<String> containsString(String s) {
		return containsStrings(1,  s);
	}

	/**
	 * Creates check that ensures the string contains (using {@link String#contains(CharSequence)})
	 * the strings in the set (at least) a minimum number of times.
	 *
	 * E.g.,
	 * min = 1, mustContainStrings = ["abc", "bb", "🤓"]
	 * "ab" will fail
	 * "a b c" will fail
	 * "abc" will pass
	 * "def🤓" will pass
	 * "zyx\uD83E\uDD13" will pass, because it contains the unicode for the nerd face
	 */
	public static Check<String> containsStrings(int min, String... mustContainStrings) {
		// use set, makes sure there are no duplicates
		return containsStrings(min, new HashSet<>(Arrays.asList(mustContainStrings)));
	}

	/**
	 * Creates check that ensures the string contains (using {@link String#contains(CharSequence)})
	 * the strings in the set (at least) a minimum number of times.
	 *
	 * E.g.,
	 * min = 1, mustContainStrings = ["abc", "bb", "🤓"]
	 * "ab" will fail
	 * "a b c" will fail
	 * "abc" will pass
	 * "def🤓" will pass
	 * "zyx\uD83E\uDD13" will pass, because it contains the unicode for the nerd face
	 */
	public static Check<String> containsStrings(int min, Set<String> mustContainStrings) {
		IntegerUtils.requireIntGreaterThanOrEqualTo(1, min);
		Objects.requireNonNull(mustContainStrings);
		CollectionUtils.requireNonNullEntries(mustContainStrings);
		CollectionUtils.requireSizeGreaterThan(0, mustContainStrings);
		CollectionUtils.requireNonEmptyStrings(mustContainStrings);
		String[] mustContainStringsArray = mustContainStrings.toArray(new String[0]);
		return s -> {
			int counter = 0;
			for (int i = 0; i < mustContainStringsArray.length; i++) {
				if (s.contains(mustContainStringsArray[i])) {
					counter++;
				}
				if (counter >= min) {
					return Check.Result.success();
				}
			}
			String errorMsgChars = String.join(", ", mustContainStringsArray);
			return Check.Result.failure("Must contain at least " + min + " of the following strings: " +
				                            errorMsgChars + ".");
		};
	}

	/**
	 * Creates check which ensures a code point cannot be repeated more than N times consecutively.
	 *
	 * Does not fail on empty string.
	 *
	 * E.g.,
	 * Assuming max = 2
	 * "aa" will pass
	 * "a a a " will pass (contains the space character between each 'a')
	 * "aaa" will fail
	 *
	 * @param max maximum number of times a code point can be repeated consecutively
	 */
	public static Check<String> limitConsecutiveCodePoints(int max) {
		IntegerUtils.requireIntGreaterThanOrEqualTo(1, max);
		return s -> {
			// always start consecutive counter at 1. whatever is at the current position has occurred 1 time..!
			int consecutiveCounter = 1;
			int strLength = s.length() - 1;
			for (int i = 0; i < strLength; i++) {
				int codePoint = s.codePointAt(i);
				if (Character.isSupplementaryCodePoint(codePoint)) {
					// skip next pos, because is part of current code point
					i++;
					/*
					 * Because a code point can take up to 2 char positions in the string, need to check here to make sure
					 * that the length (minus 1) of the string has not been exceeded.
					 *
					 * E.g., say the string is "🤓" - the length is 2 and the codepoint is supplementary, thus the index would be
					 * incremented here and then if a break was not made s would be accessed at (effectively) i + 2 (due to the
					 * increment above and the +1 below when getting the codepoint)
					 * */
					if (i >= strLength) {
						break;
					}
				}
				int codePointNext = s.codePointAt(i + 1);
				if (codePoint == codePointNext) {
					consecutiveCounter++;
					if (consecutiveCounter > max) {
						return Check.Result.failure("Cannot contain a character that repeats (consecutively) more than " +
							                            max + " times.");
					}
				} else {
					consecutiveCounter = 1;
				}
			}
			return Check.Result.success();
		};
	}

	/**
	 * Creates check that ensures the string is empty or meets the provided checks.
	 * This creates an OR condition between empty and all of the checks, either the
	 * string is empty or ALL checks provided must be successful.
	 * @param checks all checks should pass for check to return successful (or string should be empty)
	 */
	@SafeVarargs
	@SuppressWarnings("unchecked")
	public static Check<String> emptyOrMeetsChecks(Check<String>... checks) {
		return s -> {
			if (s.length() == 0) {
				return Check.Result.success();
			} else {
				for (Check<String> Check : checks) {
					Check.Result checkResult = Check.check(s);
					if (checkResult.failed()) {
						return checkResult;   // return error
					}
				}
			}
			// all checks passed
			return Check.Result.success();
		};
	}

	/**
	 * Similar to empty, but length can be 0 or all white space characters.
	 *
	 * @param checks
	 * @return
	 */
	@SafeVarargs
	@SuppressWarnings("unchecked")
	public static Check<String> emptyOrWhitespaceOrMeetsChecks(Check<String>... checks) {
		return s -> {
			if (IS_EMPTY_OR_ONLY_WHITESPACE.check(s).successful()) {
				return Check.Result.success();
			} else {
				for (Check<String> Check : checks) {
					Check.Result checkResult = Check.check(s);
					if (checkResult.failed()) {
						return checkResult;   // return unsuccessful check
					}
				}
			}
			// all checks passed
			return Check.Result.success();
		};
	}

	/**
	 * Checks that the param string equals a string in the provided list.
	 */
	public static Check<String> equalsString(String... strings) {
		return equalsString(new HashSet<>(Arrays.asList(strings)));
	}

	/**
	 * Checks that the param string equals a string in the provided set.
	 *
	 * @param strings
	 * @return
	 */
	public static Check<String> equalsString(Set<String> strings) {
		Objects.requireNonNull(strings, "Cannot create check for empty set.");
		CollectionUtils.requireSizeGreaterThan(0, strings);
		CollectionUtils.requireNonNullEntries(strings);
		String acceptableListForFailureMsg = strings.stream()
		                                            .map(setStr -> "'" + setStr + "'")
		                                            .collect(Collectors.joining(", "));
		return s -> strings.contains(s)
			?
			Check.Result.success()
			:
			Check.Result.failure("Must equal on of the following strings: " + acceptableListForFailureMsg);
	}

	public static Check<String> equalsStringIgnoreCase(String... strings) {
		return equalsStringIgnoreCase(new HashSet<>(Arrays.asList(strings)));
	}

	public static Check<String> equalsStringIgnoreCase(Set<String> strings) {
		Objects.requireNonNull(strings);
		CollectionUtils.requireSizeGreaterThan(0, strings);
		CollectionUtils.requireNonNullEntries(strings);
		Set<String> lowerCasedStrings = strings.stream().map(String::toLowerCase).collect(Collectors.toSet());
		String acceptableListForFailureMsg = lowerCasedStrings.stream()
		                                                      .map(setStr -> "'" + setStr + "'")
		                                                      .collect(Collectors.joining(", "));
		return s -> lowerCasedStrings.contains(s.toLowerCase())
			?
			Check.Result.success()
			:
			Check.Result.failure("Must equal one of the following strings: " + acceptableListForFailureMsg);
	}

	/**
	 * Checks that the param string does not equal any strings provided in the list.
	 */
	public static Check<String> doesNotEqualStrings(String... notEqualsList) {
		return doesNotEqualStrings(new HashSet<>(Arrays.asList(notEqualsList)));
	}

	/**
	 * Checks that the param string does not equal any strings in the provided set.
	 */
	public static Check<String> doesNotEqualStrings(Set<String> notEqualsSet) {
		Objects.requireNonNull(notEqualsSet);
		CollectionUtils.requireSizeGreaterThan(0, notEqualsSet);
		CollectionUtils.requireNonNullEntries(notEqualsSet);
		String unacceptableListForFailureMsg = notEqualsSet.stream().map(setStr -> "'" + setStr + "'")
		                                                   .collect(Collectors.joining(", "));
		return s ->
			notEqualsSet.contains(s)
				?
				Check.Result.failure("Cannot be one of the following strings: " +
					                     unacceptableListForFailureMsg)
				:
				Check.Result.success();
	}

	public static Check<String> doesNotEqualStringsIgnoreCase(String... notEquals) {
		return doesNotEqualStringsIgnoreCase(new HashSet<>(Arrays.asList(notEquals)));
	}

	public static Check<String> doesNotEqualStringsIgnoreCase(Set<String> notEqualsSet) {
		Objects.requireNonNull(notEqualsSet);
		CollectionUtils.requireSizeGreaterThan(0, notEqualsSet);
		CollectionUtils.requireNonNullEntries(notEqualsSet);
		Set<String> lowerCasedNotEqualsSet = notEqualsSet.stream().map(String::toLowerCase).collect(Collectors.toSet());
		String unacceptableListForFailureMsg = lowerCasedNotEqualsSet.stream()
		                                                             .map(setStr -> "'" + setStr + "'")
		                                                             .collect(Collectors.joining(", "));
		return s ->
			lowerCasedNotEqualsSet.contains(s.toLowerCase())
				?
				Check.Result.failure("Cannot be one of the following strings: " + unacceptableListForFailureMsg)
				:
				Check.Result.success();
	}

}
