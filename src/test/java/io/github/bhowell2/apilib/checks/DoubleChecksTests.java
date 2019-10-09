package io.github.bhowell2.apilib.checks;

import org.junit.jupiter.api.Test;

/**
 * @author Blake Howell
 */
public class DoubleChecksTests extends ChecksTestBase {

	@Test
	public void testGreaterThan() throws Exception {
		Check<Double> checkZero = DoubleChecks.valueGreaterThan(0.0);
		// successful
		assertCheckSuccessful(checkZero.check(Double.MAX_VALUE));
		assertCheckSuccessful(checkZero.check(0.1));
		// unsuccessful
		assertCheckFailed(checkZero.check(+0.0));
		assertCheckFailed(checkZero.check(0.0));

		assertCheckFailed(checkZero.check(-0.0));

		// note min_value is the smallest possible POSITIVE minimum value
		assertCheckFailed(checkZero.check(-Double.MIN_VALUE));
		assertCheckFailed(checkZero.check(-Double.MAX_VALUE));

		Check<Double> checkNegative = DoubleChecks.valueGreaterThan(-0.1);
		// successful
		assertCheckSuccessful(checkNegative.check(Double.MAX_VALUE));
		assertCheckSuccessful(checkNegative.check(0.0));
		assertCheckSuccessful(checkNegative.check(-0.01));
		// note min_value is the smallest possible POSITIVE minimum value
		assertCheckSuccessful(checkNegative.check(Double.MIN_VALUE));
		// unsuccessful
		assertCheckFailed(checkNegative.check(-0.1));
		assertCheckFailed(checkNegative.check(-Double.MAX_VALUE));


		Check<Double> checkPositive = DoubleChecks.valueGreaterThan(1.0);
		// successful
		assertCheckSuccessful(checkPositive.check(1.1));
		// unsuccessful
		assertCheckFailed(checkPositive.check(1.0));
		assertCheckFailed(checkPositive.check(0.0));
		assertCheckFailed(checkPositive.check(-0.1));
	}

	@Test
	public void testGreaterThanOrEqualTo() throws Exception {
		Check<Double> checkZero = DoubleChecks.valueGreaterThanOrEqualTo(0.0);
		// successful
		assertCheckSuccessful(checkZero.check(Double.MAX_VALUE));
		assertCheckSuccessful(checkZero.check(0.1));
		assertCheckSuccessful(checkZero.check(+0.0));
		assertCheckSuccessful(checkZero.check(0.0));
		assertCheckSuccessful(checkZero.check(-0.0));

		// unsuccessful
		assertCheckFailed(checkZero.check(-0.9999));
		assertCheckFailed(checkZero.check(-Double.MIN_VALUE));
		assertCheckFailed(checkZero.check(Double.NEGATIVE_INFINITY));

		Check<Double> checkNegative = DoubleChecks.valueGreaterThanOrEqualTo(-0.1);
		// successful
		assertCheckSuccessful(checkNegative.check(Double.MAX_VALUE));
		assertCheckSuccessful(checkNegative.check(0.0));
		assertCheckSuccessful(checkNegative.check(-0.1));
		// unsuccessful
		assertCheckFailed(checkNegative.check(-Double.MAX_VALUE));
		assertCheckFailed(checkNegative.check(-0.11));


		Check<Double> checkPositive = DoubleChecks.valueGreaterThanOrEqualTo(1.0);
		// successful
		assertCheckSuccessful(checkPositive.check(1.1));
		assertCheckSuccessful(checkPositive.check(1.0));
		// unsuccessful
		assertCheckFailed(checkPositive.check(0.0));
		assertCheckFailed(checkPositive.check(-0.1));
	}

	@Test
	public void testValueEqualTo() throws Exception {
		Check<Double> check15 = DoubleChecks.valueEqualTo(15.0);
		assertCheckSuccessful(check15.check(15.0));
		assertCheckFailed(check15.check(15.0 + 0.1));

		Check<Double> check0 = DoubleChecks.valueEqualTo(0.0);
		assertCheckSuccessful(check0.check(+0.0));
		assertCheckSuccessful(check0.check(0.0));

		// -0.0 is NOT equal to 0.0 when using Double.compareTo, but is when using primitive comparison ==
		assertCheckSuccessful(check0.check(-0.0));
	}

	@Test
	public void testValueEqualToDoubleInList() throws Exception {
		Check<Double> check = DoubleChecks.valueEqualTo(new double[]{0.3, 0.5, 10.0, 999999.999});
		// successful
		assertCheckSuccessful(check.check(0.3));
		assertCheckSuccessful(check.check(0.5));
		assertCheckSuccessful(check.check(10.0));
		assertCheckSuccessful(check.check(999999.999));
		// unsuccessful
		assertCheckFailed(check.check(1.1));
		assertCheckFailed(check.check(1.0));
	}

	@Test
	public void testValueEqualToPosAndNegZero() throws Exception {
		Check<Double> check15 = DoubleChecks.valueEqualToUniquePosAndNegZero(15.0);
		assertCheckSuccessful(check15.check(15.0));
		assertCheckFailed(check15.check(15.0 + 0.1));

		Check<Double> check0 = DoubleChecks.valueEqualToUniquePosAndNegZero(0.0);
		assertCheckSuccessful(check0.check(+0.0));
		assertCheckSuccessful(check0.check(0.0));

		// -0.0 is NOT equal to 0.0 when using Double.compareTo, but is when using primitive comparison ==
		assertCheckFailed(check0.check(-0.0));
	}

	@Test
	public void testValueLessThan() throws Exception {
		Check<Double> check0 = DoubleChecks.valueLessThan(0.0);
		assertCheckSuccessful(check0.check(-0.00001));
		assertCheckFailed(check0.check(0.0));
		assertCheckFailed(check0.check(0.00001));

		Check<Double> checkNegative = DoubleChecks.valueLessThan(-0.01);
		assertCheckSuccessful(checkNegative.check(-0.1));
		assertCheckFailed(checkNegative.check(-0.01));
		assertCheckFailed(checkNegative.check(-0.001));
	}
	
	@Test
	public void testValueLessThanOrEqualTo() throws Exception {
		Check<Double> check0 = DoubleChecks.valueLessThanOrEqualTo(0.0);
		assertCheckSuccessful(check0.check(-0.00001));
		assertCheckSuccessful(check0.check(0.0));
		assertCheckFailed(check0.check(0.00001));

		Check<Double> checkNegative = DoubleChecks.valueLessThanOrEqualTo(-0.01);
		assertCheckSuccessful(checkNegative.check(-0.1));
		assertCheckSuccessful(checkNegative.check(-0.01));
		assertCheckFailed(checkNegative.check(-0.001));
	}

}
