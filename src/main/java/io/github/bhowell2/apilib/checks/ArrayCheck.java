package io.github.bhowell2.apilib.checks;

import java.util.List;

/**
 * @author Blake Howell
 */
public interface ArrayCheck<Param> {

	/**
	 * Takes a regular check and wraps it. Use then when the index is irrelevant
	 * to the check itself (i.e., the check applies to all indices).
	 * @param check
	 * @param <Param>
	 * @return
	 */
	static <Param> ArrayCheck<Param> wrapCheck(Check<Param> check) {
		return (i, value) -> check.check(value);
	}

	/**
	 * Applies the provided checks to the
	 * @param evenIndexChecks
	 * @param oddIndexChecks
	 * @param <Param>
	 * @return
	 */
	static <Param> ArrayCheck<Param> checkEvenAndOdd(Check<Param>[] evenIndexChecks, Check<Param>[] oddIndexChecks) {
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

	@SuppressWarnings("unchecked")
	static <Param> ArrayCheck<Param> checkEvenAndOdd(List<Check<Param>> evenIndexChecks, List<Check<Param>> oddIndexChecks) {
		return checkEvenAndOdd(evenIndexChecks.toArray(new Check[0]), oddIndexChecks.toArray(new Check[0]));
	}

	/**
	 * Allows for passing in the index that is being checked in case it
	 * is needed for the check. Usually this will not be necessary and
	 * a simple {@link Check} would suffice. However, since this is
	 * required by {@link io.github.bhowell2.apilib.ApiArrayOrListParamBase},
	 * a wrapper has been supplied for standard checks:
	 * @param index
	 * @param value
	 * @return
	 */
	Check.Result check(int index, Param value);

}
