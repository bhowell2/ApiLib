package io.github.bhowell2.apilib.checks;

import org.junit.jupiter.api.Test;

/**
 * @author Blake Howell
 */
public class IntegerChecksTests extends ChecksTestBase {

	@Test
	public void testGreaterThan() throws Exception {
		Check<Integer> check0 = IntegerChecks.valueGreaterThan(0);
		// successful
		assertCheckSuccessful(check0.check(1));
		assertCheckSuccessful(check0.check(9999));
		// unsuccessful
		assertCheckFailed(check0.check(0));
		assertCheckFailed(check0.check(-1));
		assertCheckFailed(check0.check(-999));

		Check<Integer> check1 = IntegerChecks.valueGreaterThan(1);
		// successful
		assertCheckSuccessful(check1.check(2));
		assertCheckSuccessful(check1.check(9999));
		// unsuccessful
		assertCheckFailed(check1.check(0));
		assertCheckFailed(check1.check(-1));
		assertCheckFailed(check1.check(-999));

		Check<Integer> checkNegative100 = IntegerChecks.valueGreaterThan(-100);
		// successful
		assertCheckSuccessful(checkNegative100.check(-99));
		assertCheckSuccessful(checkNegative100.check(2));
		assertCheckSuccessful(checkNegative100.check(9999));
		// unsuccessful
		assertCheckFailed(checkNegative100.check(-100));
		assertCheckFailed(checkNegative100.check(-101));
		assertCheckFailed(checkNegative100.check(-999));
	}

	@Test
	public void testGreaterThanOrEqualTo() throws Exception {
		Check<Integer> check0 = IntegerChecks.valueGreaterThanOrEqualTo(0);
		// successful
		assertCheckSuccessful(check0.check(0));
		assertCheckSuccessful(check0.check(1));
		assertCheckSuccessful(check0.check(9999));
		// unsuccessful
		assertCheckFailed(check0.check(-1));
		assertCheckFailed(check0.check(-999));

		Check<Integer> check1 = IntegerChecks.valueGreaterThanOrEqualTo(1);
		// successful
		assertCheckSuccessful(check1.check(1));
		assertCheckSuccessful(check1.check(2));
		assertCheckSuccessful(check1.check(9999));
		// unsuccessful
		assertCheckFailed(check1.check(0));
		assertCheckFailed(check1.check(-1));
		assertCheckFailed(check1.check(-999));

		Check<Integer> checkNegative100 = IntegerChecks.valueGreaterThanOrEqualTo(-100);
		// successful
		assertCheckSuccessful(checkNegative100.check(-99));
		assertCheckSuccessful(checkNegative100.check(2));
		assertCheckSuccessful(checkNegative100.check(9999));
		assertCheckSuccessful(checkNegative100.check(-100));
		// unsuccessful
		assertCheckFailed(checkNegative100.check(-101));
		assertCheckFailed(checkNegative100.check(-999));
	}

	@Test
	public void testValueLessThan() throws Exception {
		Check<Integer> check0 = IntegerChecks.valueLessThan(0);
		// successful
		assertCheckSuccessful(check0.check(-1));
		assertCheckSuccessful(check0.check(-100));
		// unsuccessful
		assertCheckFailed(check0.check(0));
		assertCheckFailed(check0.check(100));

		Check<Integer> check100 = IntegerChecks.valueLessThan(100);
		// successful
		assertCheckSuccessful(check100.check(-1));
		assertCheckSuccessful(check100.check(-100));
		assertCheckSuccessful(check100.check(99));
		// unsuccessful
		assertCheckFailed(check100.check(101));
		assertCheckFailed(check100.check(1000));

		Check<Integer> checkNegative100 = IntegerChecks.valueLessThan(-100);
		// successful
		assertCheckSuccessful(checkNegative100.check(-101));
		assertCheckSuccessful(checkNegative100.check(-999));
		assertCheckSuccessful(checkNegative100.check(-Integer.MAX_VALUE));
		// unsuccessful
		assertCheckFailed(checkNegative100.check(-100));
		assertCheckFailed(checkNegative100.check(-99));
		assertCheckFailed(checkNegative100.check(101));
		assertCheckFailed(checkNegative100.check(1000));
	}

	@Test
	public void testValueLessThanOrEqualTo() throws Exception {
		Check<Integer> check0 = IntegerChecks.valueLessThanOrEqualTo(0);
		// successful
		assertCheckSuccessful(check0.check(0));
		assertCheckSuccessful(check0.check(-0));
		assertCheckSuccessful(check0.check(+0));
		assertCheckSuccessful(check0.check(-1));
		assertCheckSuccessful(check0.check(-100));
		// unsuccessful
		assertCheckFailed(check0.check(1));
		assertCheckFailed(check0.check(100));

		Check<Integer> check100 = IntegerChecks.valueLessThanOrEqualTo(100);
		// successful
		assertCheckSuccessful(check100.check(-1));
		assertCheckSuccessful(check100.check(-100));
		assertCheckSuccessful(check100.check(99));
		assertCheckSuccessful(check100.check(100));
		// unsuccessful
		assertCheckFailed(check100.check(101));
		assertCheckFailed(check100.check(1000));

		Check<Integer> checkNegative100 = IntegerChecks.valueLessThanOrEqualTo(-100);
		// successful
		assertCheckSuccessful(checkNegative100.check(-100));
		assertCheckSuccessful(checkNegative100.check(-101));
		assertCheckSuccessful(checkNegative100.check(-999));
		assertCheckSuccessful(checkNegative100.check(-Integer.MAX_VALUE));
		// unsuccessful
		assertCheckFailed(checkNegative100.check(-99));
		assertCheckFailed(checkNegative100.check(101));
		assertCheckFailed(checkNegative100.check(1000));
	}

	@Test
	public void valueEqualTo() throws Exception {
		Check<Integer> check0 = IntegerChecks.valueEqualTo(0);
		// successful
		assertCheckSuccessful(check0.check(0));
		assertCheckSuccessful(check0.check(-0));
		assertCheckSuccessful(check0.check(+0));
		// unsuccessful
		assertCheckFailed(check0.check(1));
		assertCheckFailed(check0.check(-9999));

		Check<Integer> checkPos = IntegerChecks.valueEqualTo(999);
		// successful
		assertCheckSuccessful(checkPos.check(999));
		// unsuccessful
		assertCheckFailed(checkPos.check(-999));
		assertCheckFailed(checkPos.check(-99));

		Check<Integer> checkNeg = IntegerChecks.valueEqualTo(-9);
		// successful
		assertCheckSuccessful(checkNeg.check(-9));
		// unsuccessful
		assertCheckFailed(checkNeg.check(9));
		assertCheckFailed(checkNeg.check(100));
	}

	@Test
	public void testEqualToAcceptableInts() throws Exception {
		Check<Integer> checkInList = IntegerChecks.valueEqualTo(new int[]{0, 1, 99, -5});
		// successful
		assertCheckSuccessful(checkInList.check(0));
		assertCheckSuccessful(checkInList.check(1));
		assertCheckSuccessful(checkInList.check(99));
		assertCheckSuccessful(checkInList.check(-5));
		// unsuccessful
		assertCheckFailed(checkInList.check(999));
		assertCheckFailed(checkInList.check(11));
		assertCheckFailed(checkInList.check(Integer.MAX_VALUE));
	}

}
