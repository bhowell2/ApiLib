package io.github.bhowell2.apilib.checks;

import io.github.bhowell2.apilib.ApiParamError;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author Blake Howell
 */
public final class IntegerChecks {

	private IntegerChecks() {}  // no instantiation

	public static Check<Integer> valueGreaterThan(int i) {
		return input -> {
			if (input > i) {
				return Check.Result.success();
			} else {
				return Check.Result.failure("Must be greater than " + i);
			}
		};
	}

	public static Check<Integer> valueGreaterThanOrEqualTo(int i) {
		return input -> {
			if (input >= i) {
				return Check.Result.success();
			} else {
				return Check.Result.failure("Must be greater than or equal to " + i);
			}
		};
	}

	public static Check<Integer> valueLessThan(int i) {
		return input -> {
			if (input < i) {
				return Check.Result.success();
			} else {
				return Check.Result.failure("Must be less than " + i);
			}
		};
	}

	public static Check<Integer> valueLessThanOrEqualTo(int i) {
		return input -> {
			if (input <= i) {
				return Check.Result.success();
			} else {
				return Check.Result.failure("Must be less than or equal to " + i);
			}
		};
	}

	public static Check<Integer> valueEqualTo(int i) {
		return input -> {
			if (input == i) {
				return Check.Result.success();
			} else {
				return Check.Result.failure("Must be equal to " + i);
			}
		};
	}

	/**
	 * Checks whether or not the value is equal to one of the values in the list.
	 * @param acceptableInts
	 * @return
	 */
	public static Check<Integer> valueEqualTo(int[] acceptableInts) {
		String acceptableListForFailureMsg = Arrays.stream(acceptableInts)
		                                           .mapToObj(Integer::toString)
		                                           .collect(Collectors.joining(", "));
		return input -> {
			for (int i : acceptableInts) {
				if (input == i) {
					return Check.Result.success();
				}
			}
			return Check.Result.failure("Does not equal any of the acceptable integers: " + acceptableListForFailureMsg);
		};
	}

	public static Check<Integer> valueIsEven() {
		return input -> input % 2 == 0
			?
			Check.Result.success()
			:
			Check.Result.failure("Must be an even integer.");
	}

	public static Check<Integer> valueIsOdd() {
		return input -> input % 2 != 0
			?
			Check.Result.success()
			:
			Check.Result.failure("Must be an odd integer.");
	}

}
