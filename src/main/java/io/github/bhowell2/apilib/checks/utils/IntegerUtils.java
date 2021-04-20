package io.github.bhowell2.apilib.checks.utils;

/**
 * Provides utility methods for requiring integers.
 * @author Blake Howell
 */
public class IntegerUtils {

	public static void requireIntGreaterThan(int min, int value) {
		requireIntGreaterThan(min,
		                      value,
		                      "Integer must be greater than " + min + " but was " + value + ".");
	}

	public static void requireIntGreaterThan(int min, int value, String message) {
		requireIntGreaterThanOrEqualTo(min + 1,
		                               value,
		                               message);
	}

	public static void requireIntGreaterThanOrEqualTo(int min, int value) {
		requireIntGreaterThanOrEqualTo(min,
		                               value,
		                               "Integer must be greater than or equal to " + min + " but was " + value + ".");
	}

	public static void requireIntGreaterThanOrEqualTo(int min, int value, String message) {
		if (value < min) {
			throw new IllegalArgumentException(message);
		}
	}


	public static void requireIntLessThan(int max, int value) {
		if (value < max) {
			throw new IllegalArgumentException("Integer must be less than " + max + " but was " + value + ".");
		}
	}

	public static void requireIntLessThanOrEqualTo(int max, int value) {
		if (value <= max) {
			throw new IllegalArgumentException("Integer must be less than or equal to " + max + " but was " + value + ".");
		}
	}

}
