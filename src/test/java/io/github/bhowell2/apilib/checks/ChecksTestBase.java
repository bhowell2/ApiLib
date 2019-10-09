package io.github.bhowell2.apilib.checks;

import org.junit.jupiter.api.Assertions;

/**
 * @author Blake Howell
 */
public abstract class ChecksTestBase {

	public static void assertCheckSuccessful(Check.Result result) {
		Assertions.assertTrue(result.successful(), result.failureMessage);
	}

	public static void assertCheckFailed(Check.Result result) {
		assertCheckFailed(result, "Check should have failed, but passed.");
	}

	public static void assertCheckFailed(Check.Result result, String errMsg) {
		Assertions.assertTrue(result.failed(), errMsg);
	}

}
