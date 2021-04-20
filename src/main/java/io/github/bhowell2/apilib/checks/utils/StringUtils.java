package io.github.bhowell2.apilib.checks.utils;

import io.github.bhowell2.apilib.checks.StringChecks;

import java.util.HashSet;
import java.util.Set;

/**
 * Provides utility methods for strings. These are more used to ensure that the parameters
 * provided to create checks in {@link StringChecks} are valid.
 * @author Blake Howell
 */
public class StringUtils {

	public static void requireNonEmptyString(String s) {
		requireNonEmptyString(s, "String cannot be empty.");
	}

	public static void requireNonEmptyString(String s, String errMsg) {
		if (s.isEmpty()) {
			throw new IllegalArgumentException(errMsg);
		}
	}

	public static void requireUniqueCodePoints(String s) {
		Set<Integer> codePoints = new HashSet<>();
		for (int i = 0; i < s.length(); i++) {
			int codePoint = s.codePointAt(i);
			if (Character.isSupplementaryCodePoint(codePoint)) {
				i++;
			}
			if (!codePoints.add(codePoint)) {
				char[] repeated = Character.toChars(codePoint);
				String visualChar = String.valueOf(repeated);
				throw new IllegalArgumentException("Characters in string must be unique (i.e., they cannot repeat). "
					                                   + visualChar + " was added more than once.");
			}
		}
	}

	public static void requireNonSupplementaryCodePoints(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (Character.isSupplementaryCodePoint(s.codePointAt(i))) {
				char[] chars = Character.toChars(s.codePointAt(i));
				String visualChar = String.valueOf(chars);
				throw new IllegalArgumentException("Cannot contain supplementary code points in the string. " + visualChar +
					                                   " cannot be used in the string.");
			}
		}
	}

	public static void requireCodePointCountLessThanOrEqualTo(int n, String s) {
		IntegerUtils.requireIntGreaterThan(0, n);
		if (s.codePointCount(0, s.length()) > n) {
			throw new IllegalArgumentException("String (" + s + ") must contain no more than " + n + " code points.");
		}
	}

	public static void requireCodePointCountGreaterThanOrEqualTo(int n, String s) {
		IntegerUtils.requireIntGreaterThan(0, n);
		if (s.codePointCount(0, s.length()) < n) {
			throw new IllegalArgumentException("String (" + s + ") must contain at least " + n + " code points.");
		}
	}

	public static void requireCodePointCountEqualTo(int n, String s) {
		IntegerUtils.requireIntGreaterThan(0, n);
		if (s.codePointCount(0, s.length()) != n) {
			throw new IllegalArgumentException("String (" + s + ") must contain exactly " + n + " code points.");
		}
	}

}
