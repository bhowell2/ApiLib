package io.github.bhowell2.apilib.checks;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * Common checks for an array.
 * @author Blake Howell
 */
public final class ArrayChecks {

	public static <T> Check<T[]> checkSize(Check<Integer> sizeCheck) {
		return ary -> sizeCheck.check(ary.length);
	}

	/**
	 * Creates a check that ensures each value is unique within the array.
	 * @param failureMessage message to return when the index is not unique
	 * @param <T> the index type
	 * @return the created check to ensure the index is unique
	 */
	public static <T> Check<T[]> checkUnique(String failureMessage) {
		return checkUnique(v -> v, failureMessage);
	}

	/**
	 * Creates a check that ensures some value is unique within the array.
	 * @param uniqueValRetriever function that retrieves the value that should be unique in the array
	 *                           from each index
	 * @param failureMessage message to return when the index is not unique
	 * @param <T> the list's index type type
	 * @param <R> the retrieved type (from each index of type T)
	 * @return the created check to ensure the retrieved value at each index is unique
	 */
	public static <T, R> Check<T[]> checkUnique(Function<T, R> uniqueValRetriever, String failureMessage) {
		return array -> {
			Set<R> uniqueSet = new HashSet<>();
			for (T idx : array) {
				if (!uniqueSet.add(uniqueValRetriever.apply(idx))) {
					return Check.Result.failure(failureMessage);
				}
			}
			return Check.Result.success();
		};
	}

}
