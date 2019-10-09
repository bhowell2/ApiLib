package io.github.bhowell2.apilib.checks;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Provides common checks for doubles. Beware when using doubles due to floating point arithmetic.
 * E.g., 0.3 != 0.1 + 0.2, but 0.3 == 0.5 - 0.2
 * Use the BigDecimal checks/formatters if you need to be 100% accurate and careful. 
 *
 * @author Blake Howell
 */
public final class DoubleChecks {

	private DoubleChecks() {} // no instantiation

	public static Check<Double> valueGreaterThan(double d) {
		return input -> {
			if (input > d) {
				return Check.Result.success();
			} else {
				return Check.Result.failure("Must be greater than " + d);
			}
		};
	}

	public static Check<Double> valueGreaterThanOrEqualTo(double d) {
		return input -> {
			if (input >= d) {
				return Check.Result.success();
			} else {
				return Check.Result.failure("Must be greater than or equal to " + d);
			}
		};
	}

	public static Check<Double> valueLessThan(double d) {
		return input -> {
			if (input < d) {
				return Check.Result.success();
			} else {
				return Check.Result.failure("Must be less than " + d);
			}
		};
	}

	public static Check<Double> valueLessThanOrEqualTo(double d) {
		return input -> {
			if (input <= d) {
				return Check.Result.success();
			} else {
				return Check.Result.failure("Must be less than or equal to " + d);
			}
		};
	}

	/**
	 * Takes {@link Double#doubleValue()} and uses == to compare. This results in
	 * -0.0 and 0.0 being equal. If this is not desired, use {@link #valueEqualToUniquePosAndNegZero(double)}.
	 * @param d
	 * @return
	 */
	public static Check<Double> valueEqualTo(double d) {
		return input -> {
			// this will result in -0.0 == 0.0, whereas Double.valueOf(0.0).equals(Double.valueOf(-0.0)) = false
			if (input == d) {
				return Check.Result.success();
			} else {
				return Check.Result.failure("Must be equal to " + d);
			}
		};
	}

	/**
	 * Similar to {@link #valueEqualTo(double)} but does not return that -0.0 == 0.0.
	 * @param d
	 * @return
	 */
	public static Check<Double> valueEqualToUniquePosAndNegZero(double d) {
		return input -> {
			if (input.equals(d)) {
				return Check.Result.success();
			} else {
				return Check.Result.failure("Must be equal to " + d);
			}
		};
	}

	public static Check<Double> valueEqualTo(double[] acceptableDoubles) {
		String acceptableListForFailureMsg = Arrays.stream(acceptableDoubles).mapToObj(Double::toString).collect(Collectors.joining(", "));
		return input -> {
			for (int i = 0; i < acceptableDoubles.length; i++) {
				if (acceptableDoubles[i] == input) {
					return Check.Result.success();
				}
			}
			return Check.Result.failure("Must equal one of the following: " + acceptableListForFailureMsg);
		};
	}

}
