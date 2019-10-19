package io.github.bhowell2.apilib.checks;

import io.github.bhowell2.apilib.ApiLibSettings;

import java.util.List;

/**
 * Provides functionality to check List type parameters. This does not
 * return any information except whether or not the parameter was
 * successfully checked or a failure message. This means if information
 * must be returned about the values of the list the user should
 * use {@link io.github.bhowell2.apilib.ApiListParam} or implement their
 * own list parameter with {@link io.github.bhowell2.apilib.ApiParam}
 * to return the desired information in
 * @author Blake Howell
 */
public class ListChecks {

	/**
	 * Creates check for parameter of List type that ensures each position in the
	 * list satisfies the checks provided. If a check fails the check result failure
	 * message will be returned and the failure position in the list will be appended.
	 *
	 * @param checks
	 * @param <T>
	 * @return
	 */
	@SafeVarargs
	public static <T> Check<List<T>> checkList(Check<T>... checks) {
		return list -> {
			for (int i = 0; i < list.size(); i++) {
				for (Check<T> c : checks) {
					Check.Result result = c.check(list.get(i));
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
	 * Creates check for parameter of List type that ensures each position in the
	 * list satisfies the checks provided. If a check fails the check result failure
	 * message will be returned and the failure position in the list will be appended.
	 *
	 * @param checks
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Check<List<T>> checkList(List<Check<T>> checks) {
		return checkList(checks.toArray(new Check[0]));
	}

	/**
	 * Creates check for parameter of List type that ensures each index in the
	 * list satisfies the checks provided. If a check fails the check result failure
	 * message will be returned and the failure position in the list will be appended.
	 *
	 * @param evenIndexChecks checks run on the even indexes
	 * @param oddIndexChecks checks run on the odd indexes
	 * @param <T>
	 * @return
	 */
	public static <T> Check<List<T>> checkList(List<Check<T>> evenIndexChecks, List<Check<T>> oddIndexChecks) {
		return list -> {
			for (int i = 0; i < list.size(); i++) {
				if (i % 2 == 0) {
					for (Check<T> c : evenIndexChecks) {
						Check.Result result = c.check(list.get(i));
						if (result.failed()) {
							return Check.Result.failure(result.failureMessage + " " +
								                            ApiLibSettings.DEFAULT_ARRAY_PARAMETER_APPENDED_POSITION_FAILURE.apply(i));
						}
					}
				} else {
					for (Check<T> c : oddIndexChecks) {
						Check.Result result = c.check(list.get(i));
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


	public static <T> Check<List<T>> checkListWithIndividualIndexChecks(List<List<Check<T>>> indexChecks) {
		return list -> {
			if (list.size() != indexChecks.size()) {
				// it is okay if there are less than the provided index checks, just cannot be more than.
				return Check.Result.failure("Array is not of proper size: " + indexChecks.size());
			}
			for (int i = 0; i < list.size(); i++) {
				List<Check<T>> checksForIndex = indexChecks.get(i);
				for (int j = 0; j < checksForIndex.size(); j++) {
					Check.Result result = checksForIndex.get(j).check(list.get(i));
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
	public static <T> Check<List<List<T>>> checkListOfLists(Check<T>... checks) {
		return listOfLists -> {
			for (int i = 0; i < listOfLists.size(); i++) {
				List<T> innerList = listOfLists.get(i);
				// does not do anything besides make sure is not null, skips if it is..
				if (innerList != null) {
					for (int j = 0; j < innerList.size(); j++) {
						for (Check<T> c : checks) {
							Check.Result result = c.check(innerList.get(j));
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
	public static <T> Check<List<List<T>>> checkListOfLists(List<Check<T>> checks) {
		return checkListOfLists(checks.toArray(new Check[0]));
	}

}
