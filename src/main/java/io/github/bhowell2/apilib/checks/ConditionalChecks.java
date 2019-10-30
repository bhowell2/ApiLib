package io.github.bhowell2.apilib.checks;

import io.github.bhowell2.apilib.checks.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides conditional functionality for checks.
 * @author Blake Howell
 */
public final class ConditionalChecks {

	/**
	 * Creates check which ensures at least one of the checks provided passes. Combines failure
	 * messages from each check if they all fail, joining them with the string " OR ".
	 *
	 * @param checks one of these checks must pass
	 * @param <T> the parameter type
	 * @return a check that passes if one of the supplied checks passes. otherwise it will fail.
	 */
	@SafeVarargs
	static <T> Check<T> orConditionalCheck(Check<T>... checks) {
		return orConditionalCheck(null, checks);
	}

	/**
	 * Creates check which ensures at least one of the checks provided passes.
	 *
	 * @param failureMessage overrides failure messages returned from checks. if this is null then
	 *                       the error messages from each check will be collected and combined with
	 *                       the string " OR ".
	 * @param checks one of these checks must pass
	 * @param <T> the parameter type
	 * @return a check that passes if one of the supplied checks passes. otherwise it will fail.
	 */
	@SafeVarargs
	static <T> Check<T> orConditionalCheck(String failureMessage, Check<T>... checks) {
		CollectionUtils.requireNonNullEntries(checks);
		return param -> {
			/*
			 * Used to combine error messages if an explicit failure message is not provided.
			 * If a failure
			 * */
			List<String> combinedErrorMessages = null;
			for (int i = 0; i < checks.length; i++) {
				Check.Result result = checks[i].check(param);
				if (result.failed()) {
					if (failureMessage == null) {
						if (combinedErrorMessages == null) {
							combinedErrorMessages = new ArrayList<>();
						}
						combinedErrorMessages.add(result.failureMessage);
					}
				} else {
					// if any single check passes this is successful
					return Check.Result.success();
				}
			}
			if (failureMessage != null) {
				return Check.Result.failure(failureMessage);
			}
			return Check.Result.failure(String.join(" OR ", combinedErrorMessages));
		};
	}

	/**
	 * Behaves like an XOR gate, which ensures that only one of the checks passes, but not both.
	 *
	 * Result:
	 * Both fail: check fails
	 * One passes: check passes
	 * Both pass: check fails
	 *
	 * @param failureMessage message to return
	 * @param check1
	 * @param check2
	 * @param <T>
	 * @return
	 */
	static <T> Check<T> xorConditionalCheck(String failureMessage, Check<T> check1, Check<T> check2) {
		return param -> {
			Check.Result result1 = check1.check(param);
			Check.Result result2 = check2.check(param);
			if (result1.failed() && result2.failed() || (result1.successful() && result2.successful())) {
				return Check.Result.failure(failureMessage);
			} else {
				return Check.Result.success();
			}
		};
	}

	/**
	 * Only one of the checks may pass.
	 * @param failureMessage
	 * @param checks
	 * @param <T>
	 * @return
	 */
	@SafeVarargs
	static <T> Check<T> exclusiveConditionalCheck(String failureMessage, Check<T>... checks) {
		CollectionUtils.requireNonNullEntries(checks);
		return param -> {
			boolean successfulResult = false;
			for (Check<T> c : checks) {
				Check.Result result = c.check(param);
				if (result.successful()) {
					if (successfulResult) {
						return Check.Result.failure(failureMessage);
					}
					successfulResult = true;
				}
			}
			return successfulResult ? Check.Result.success() : Check.Result.failure(failureMessage);
		};
	}

}
