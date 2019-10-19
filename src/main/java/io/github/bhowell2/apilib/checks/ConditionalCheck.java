package io.github.bhowell2.apilib.checks;


import io.github.bhowell2.apilib.ApiLibSettings;
import io.github.bhowell2.apilib.ApiMapCheckResult;
import io.github.bhowell2.apilib.ApiMapParam;

import java.util.Map;

/**
 * Conditional checks are run after all the other checks in {@link ApiMapParam}.
 * This allows for passing all of the successfully checked parameter names to the
 * conditional check so it can be ascertained whether or not certain parameters
 * were provided together (and passed). This is intended to check that multiple
 * parameters were provided together (or not) in some type of conditional manner.
 *
 * E.g.,
 * The developer wants to require the user to submit an email if e_billing is set
 * to true, but does not care if the email is submitted if e_billing is set to false
 * or if it is not provided at all.
 *
 * @author Blake Howell
 */
@FunctionalInterface
public interface ConditionalCheck {

	final class Result {

		/**
		 * Message that can be returned to user when failure occurs. Thi
		 */
		public String failureMessage;

		private Result(String failureMessage) {
			this.failureMessage = failureMessage;
		}

		public boolean successful() {
			return failureMessage == null;
		}

		public boolean failed() {
			return failureMessage != null;
		}

		public static Result success() {
			return new Result(null);
		}

		/**
		 * Creates failed result with default message ({@link ApiLibSettings#DEFAULT_CONDITIONAL_FAILURE_MESSAGE})
		 */
		public static Result failure() {
			return failure(ApiLibSettings.DEFAULT_CONDITIONAL_FAILURE_MESSAGE);
		}

		/**
		 * Creates failed result message supplied.
		 * @param failureMessage message to return with failure (defaults to
		 *                       {@link ApiLibSettings#DEFAULT_CONDITIONAL_FAILURE_MESSAGE} if null)
		 */
		public static Result failure(String failureMessage) {
			if (failureMessage == null) {
				// not allowing null error message, set to default.
				failureMessage = ApiLibSettings.DEFAULT_CONDITIONAL_FAILURE_MESSAGE;
			}
			return new Result(failureMessage);
		}
	}

	/**
	 * Is run after all other parameter checks. Allows the user
	 * to implement some type of conditional checking of parameters.
	 *
	 * @return
	 */
	Result check(Map<String, Object> params, ApiMapCheckResult mapCheckResult);

}
