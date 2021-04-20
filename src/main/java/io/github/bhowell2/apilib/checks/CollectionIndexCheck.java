package io.github.bhowell2.apilib.checks;

import java.util.List;

/**
 * Check type for {@link io.github.bhowell2.apilib.ApiCollectionParam}. These are
 * similar to {@link Check}, but the index that is being checked is also provided.
 *
 * @author Blake Howell
 */
public interface CollectionIndexCheck<Param> {

	/**
	 * Takes a regular check and wraps it. Use this when the index itself is
	 * irrelevant (i.e., the check applies to all indices).
	 * @param check wraps the check to ensure that
	 * @param <Param>
	 * @return
	 */
	static <Param> CollectionIndexCheck<Param> wrapCheck(Check<Param> check) {
		return (i, value) -> check.check(value);
	}

	/**
	 * Checks the even indexes with the supplied checks and the odd indexes
	 * with the supplied checks.
	 * @param evenIndexChecks checks to run on even indexes (e.g., 0, 2, 4)
	 * @param oddIndexChecks checks to run on odd indexes (e.g., 1, 3, 5)
	 * @param <Param> the index parameter
	 * @return whether or not the indexes were checked successfully
	 */
	@SuppressWarnings("unchecked")
	static <Param> CollectionIndexCheck<Param> checkEvenAndOdd(List<Check<Param>> evenIndexChecks,
	                                                           List<Check<Param>> oddIndexChecks) {
		return checkEvenAndOdd(evenIndexChecks.toArray(new Check[0]), oddIndexChecks.toArray(new Check[0]));
	}

	/**
	 * Checks the even indexes with the supplied checks and the odd indexes
	 * with the supplied checks.
	 * @param evenIndexChecks checks to run on even indexes (e.g., 0, 2, 4)
	 * @param oddIndexChecks checks to run on odd indexes (e.g., 1, 3, 5)
	 * @param <Param> the index parameter
	 * @return whether or not the indexes were checked successfully
	 */
	static <Param> CollectionIndexCheck<Param> checkEvenAndOdd(Check<Param>[] evenIndexChecks,
	                                                           Check<Param>[] oddIndexChecks) {
		return (i, value) -> {
			if (i % 2 == 0) {
				for (int j = 0; j < evenIndexChecks.length; j++) {
					Check.Result result = evenIndexChecks[j].check(value);
					if (result.failed()) {
						return result;
					}
				}
			} else {
				for (int j = 0; j < oddIndexChecks.length; j++) {
					Check.Result result = oddIndexChecks[j].check(value);
					if (result.failed()) {
						return result;
					}
				}
			}
			return Check.Result.success();
		};
	}

	/**
	 * Allows for passing in the index that is being checked in case it is needed
	 * for the check. Usually this will not be necessary and a simple {@link Check}
	 * would suffice. However, since this is required by
	 * {@link io.github.bhowell2.apilib.ApiCollectionParam}, a wrapper has been
	 * supplied for standard checks: {@link #wrapCheck(Check)}.
	 *
	 * @param index the index that is being checked
	 * @param value the value at the given idnex
	 * @return whether zor not the check was successful
	 */
	Check.Result check(int index, Param value);

}
