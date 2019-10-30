package io.github.bhowell2.apilib.checks;

import org.junit.jupiter.api.Test;

/**
 * Ensure that conditional checks work as expected.
 * @author Blake Howell
 */
public class ConditionalChecksTests extends ChecksTestBase {
	
	@Test
	public void testOrConditonalCheck() throws Exception {
		/*
		* Requires value be greater than 100 or less than 100. Obviously it cannot be both, so these
		* would not work with the standard AND conditional. This simulates requiring the absolute
		* value of the parameter is greater than 100.
		* */
		Check<Integer> check = ConditionalChecks.orConditionalCheck(IntegerChecks.valueGreaterThan(100),
		                                                            IntegerChecks.valueLessThan(-100));
		// successful
		assertCheckSuccessful(check.check(101));
		assertCheckSuccessful(check.check(-101));
		assertCheckSuccessful(check.check(Integer.MAX_VALUE));
		assertCheckSuccessful(check.check(Integer.MIN_VALUE));
		// unsuccessful
		assertCheckFailed(check.check(0));
		assertCheckFailed(check.check(100));
		assertCheckFailed(check.check(-100));
		assertCheckFailed(check.check(-50));
		assertCheckFailed(check.check(25));
	}

	@Test
	public void testExclusiveConditionalCheck() throws Exception {
		Check<String> check = ConditionalChecks.exclusiveConditionalCheck("Should contain a '!' or a '@', but not both.",
		                                                                  StringChecks.containsCodePoint(1, "!"),
		                                                                  StringChecks.containsCodePoint(1, "@"));
		// successful
		assertCheckSuccessful(check.check("heyyy!"));
		assertCheckSuccessful(check.check("heyyy!!"));
		assertCheckSuccessful(check.check("@heyyy@"));
		assertCheckSuccessful(check.check("@"));
		// unsuccessful
		assertCheckFailed(check.check("!@"));
		assertCheckFailed(check.check("@whatever!!"));

		Check<String> xorXorCheck = ConditionalChecks
			.exclusiveConditionalCheck("Should contain '!', '@', or a '#' but not a combination of the three.",
                                 StringChecks.containsCodePoint(1, "!"),
                                 StringChecks.containsCodePoint(1, "@"),
			                           StringChecks.containsCodePoint(1, "#"));
		// successful
		assertCheckSuccessful(xorXorCheck.check("!"));
		assertCheckSuccessful(xorXorCheck.check("@"));
		assertCheckSuccessful(xorXorCheck.check("#"));
		assertCheckSuccessful(xorXorCheck.check("yy#yy"));
		// unsuccessful
		assertCheckFailed(xorXorCheck.check("!@"));
		assertCheckFailed(xorXorCheck.check("!#"));
		assertCheckFailed(xorXorCheck.check("@#"));
		assertCheckFailed(xorXorCheck.check("!@#"));
		assertCheckFailed(xorXorCheck.check("a!1@234lliuh#"));
	}
	
}
