package io.github.bhowell2.apilib.checks;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * @author Blake Howell
 */
public class ListChecks {

	public static <T> Check<List<T>> checkSize(Check<Integer> sizeCheck) {
		return list -> sizeCheck.check(list.size());
	}

	/**
	 * Creates a check that ensures some value is unique within the list.
	 * @param failureMessage message to return when the index is not unique
	 * @param <T> the list's index type
	 * @return the created check to ensure the index is unique
	 */
	public static <T> Check<List<T>> checkUnique(String failureMessage) {
		return checkUnique(v -> v, failureMessage);
	}

	/**
	 * Creates a check that ensures some value is unique within the list.
	 * @param uniqueValRetriever function that retrieves the value that should be unique in the list
	 *                           from each index
	 * @param failureMessage message to return when the index is not unique
	 * @param <T> the list's index type type
	 * @param <R> the retrieved type (from each index of type T)
	 * @return the created check to ensure the retrieved value at each index is unique
	 */
	public static <T, R> Check<List<T>> checkUnique(Function<T, R> uniqueValRetriever, String failureMessage) {
		return list -> {
			Set<R> uniqueSet = new HashSet<>();
			for (T idx : list) {
				if (!uniqueSet.add(uniqueValRetriever.apply(idx))) {
					return Check.Result.failure(failureMessage);
				}
			}
			return Check.Result.success();
		};
	}

}
