package io.github.bhowell2.apilib.checks;

import io.github.bhowell2.apilib.ApiCustomParam;
import io.github.bhowell2.apilib.ApiLibSettings;

import java.util.List;

/**
 * Array checks can come in many forms, but many will be simple where each index
 * is to be checked in the same manner - this can be done by wrapping checks for
 * a type (e.g., String, Integer) in an another check that takes an array and
 * iterates through it checking each index. In the more complex case, the array
 * may be an array of arrays, or an array of maps (e.g., JsonObjects)
 * The more default checking  {@link ApiCustomParam}.
 * @author Blake Howell
 */
public final class ArrayChecks {

	/**
	 * Creates check for parameter of Array type that ensures each index in the
	 * array satisfies the checks provided. If a check fails the check result failure
	 * message will be returned and the failure position in the array will be appended.
	 *
	 * @param checks
	 * @param <T>
	 * @return
	 */
	@SafeVarargs
	public static <T> Check<T[]> checkArray(Check<T>... checks) {
		return array -> {
			for (int i = 0; i < array.length; i++) {
				for (Check<T> c : checks) {
					Check.Result result = c.check(array[i]);
					if (result.failed()) {
						return Check.Result.failure(result.failureMessage + " " +
							                            ApiLibSettings.DEFAULT_ARRAY_PARAMETER_APPENDED_POSITION_FAILURE.apply(i));
					}
				}
			}
			return Check.Result.success();
		};
	}

	/**
	 * Creates check for parameter of Array type that ensures each index in the
	 * array satisfies the checks provided. If a check fails the check result failure
	 * message will be returned and the failure position in the array will be appended.
	 *
	 * @param checks
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Check<T[]> checkArray(List<Check<T>> checks) {
		return checkArray(checks.toArray(new Check[0]));
	}

	/**
	 * Creates check for parameter of Array type that ensures each index in the
	 * array satisfies the checks provided. If a check fails the check result failure
	 * message will be returned and the failure position in the array will be appended.
	 *
	 * @param evenIndexChecks checks run on the even indexes
	 * @param oddIndexChecks checks run on the odd indexes
	 * @param <T>
	 * @return
	 */
	public static <T> Check<T[]> checkArray(List<Check<T>> evenIndexChecks, List<Check<T>> oddIndexChecks) {
		return array -> {
			for (int i = 0; i < array.length; i++) {
				if (i % 2 == 0) {
					for (Check<T> c : evenIndexChecks) {
						Check.Result result = c.check(array[i]);
						if (result.failed()) {
							return Check.Result.failure(result.failureMessage + " " +
								                            ApiLibSettings.DEFAULT_ARRAY_PARAMETER_APPENDED_POSITION_FAILURE.apply(i));
						}
					}
				} else {
					for (Check<T> c : oddIndexChecks) {
						Check.Result result = c.check(array[i]);
						if (result.failed()) {
							return Check.Result.failure(result.failureMessage + " " +
								                            ApiLibSettings.DEFAULT_ARRAY_PARAMETER_APPENDED_POSITION_FAILURE.apply(i));
						}
					}
				}
			}
			return Check.Result.success();
		};
	}

	/**
	 * Creates a check that applies different checks to each index of the array.
	 * If all of the checks are the same for each index this should not be used,
	 * but rather {@link #checkArray(List)}.
	 *
	 * @param indexChecks each position in the list should correspond to the checks that must pass for that position in
	 *                    the supplied array parameter
	 * @param <T> the type of array value
	 * @return a check for an array of type T
	 */
	public static <T> Check<T[]> checkArrayAtIndex(List<List<Check<T>>> indexChecks) {
		return array -> {
			if (array.length != indexChecks.size()) {
				return Check.Result.failure("Array is not of proper size: " + indexChecks.size());
			}
			for (int i = 0; i < array.length; i++) {
				List<Check<T>> checksForIndex = indexChecks.get(i);
				for (int j = 0; j < checksForIndex.size(); j++) {
					Check.Result result = checksForIndex.get(j).check(array[i]);
					if (result.failed()) {
						return Check.Result.failure(result.failureMessage + " " +
							                            ApiLibSettings.DEFAULT_ARRAY_PARAMETER_APPENDED_POSITION_FAILURE.apply(i));
					}
				}
			}
			return Check.Result.success();
		};
	}


	@SafeVarargs
	public static <T> Check<T[][]> checkArrayOfArrays(Check<T>... checks) {
		return arrayOfArrays -> {
			for (int i = 0; i < arrayOfArrays.length; i++) {
				T[] innerArray = arrayOfArrays[i];
				// does not do anything besides make sure is not null, skips if it is..
				if (innerArray != null) {
					for (int j = 0; j < innerArray.length; j++) {
						for (Check<T> c : checks) {
							Check.Result result = c.check(innerArray[j]);
							if (result.failed()) {
								return Check.Result.failure(result.failureMessage + " " +
									                            ApiLibSettings.DEFAULT_ARRAY_OF_ARRAY_PARAMETER_APPENDED_POSITION_FAILURE
										                            .apply(i, j));
							}
						}
					}
				}
			}
			return Check.Result.success();
		};
	}

	@SuppressWarnings("unchecked")
	public static <T> Check<T[][]> checkArrayOfArrays(List<Check<T>> checks) {
		return checkArrayOfArrays(checks.toArray(new Check[0]));
	}

}
