package io.github.bhowell2.apilib.checks.utils;

import java.util.Collection;

/**
 * @author Blake Howell
 */
public class CollectionUtils {

	@SafeVarargs
	public static <T> void requireNonNullEntries(T... ary) {
		requireNonNullEntries("Cannot have null value in varargs.", ary);
	}

	@SafeVarargs
	public static <T> void requireNonNullEntries(String errMsg, T... ary) {
		for (Object o : ary) {
			if (o == null) {
				throw new IllegalArgumentException(errMsg);
			}
		}
	}

	public static void requireNonNullEntries(Collection<?> c) {
		requireNonNullEntries(c, "Cannot have null entry in collection.");
	}

	public static void requireNonNullEntries(Collection<?> c, String errMsg) {
		for (Object o : c) {
			if (o == null) {
				throw new IllegalArgumentException(errMsg);
			}
		}
	}

	public static void requireSizeGreaterThan(int size, Collection<?> c) {
		requireSizeGreaterThan(size, c, "Collection size must be greater than " + size + ".");
	}


	public static void requireSizeGreaterThan(int size, Collection<?> c, String errMsg) {
		IntegerUtils.requireIntGreaterThan(size, c.size(), errMsg);
	}

	public static void requireNonEmptyStrings(Collection<String> c) {
		for (String s : c) {
			StringUtils.requireNonEmptyString(s, "String in collection cannot be empty.");
		}
	}

}
