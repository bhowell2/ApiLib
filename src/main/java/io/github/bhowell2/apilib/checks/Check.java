package io.github.bhowell2.apilib.checks;

import io.github.bhowell2.apilib.ApiLibSettings;
import io.github.bhowell2.apilib.ApiSingleParam;

import java.util.Map;

/**
 * Intended to be used to check the value of a single parameter - a parameter that does not contain
 * named sub-parameters (e.g., Map/JsonObject). This is because the Check does not return the names
 * of any parameters, only whether or not the check was successful and (optionally) a message
 * indicating the reason for failure.
 *
 * Failure messages should.
 * @author Blake Howell
 */
@FunctionalInterface
public interface Check<T> {

	/**
	 * Allows for returning whether or not the parameter's value was successfully
	 * checked or that it failed and an failure message can optionally be returned.
	 */
	final class Result {

		public String failureMessage;

		/**
		 * Requiring use of static creation methods to disambiguate a successful result
		 * (no param in constructor) and a failure (requiring value for failure message).
		 */
		private Result() {}

		// for failure
		private Result(String failureMessage) {
			/*
			* this cannot be null due to successful and failed methods below
			* would need to add additional boolean for success status otherwise
			* */
			this.failureMessage = failureMessage;
		}

		/**
		 * Whether or not the check was successful.
		 */
		public boolean successful() {
			return this.failureMessage == null;
		}

		/**
		 * Whether or not the check failed.
		 */
		public boolean failed() {
			return this.failureMessage != null;
		}

		/* Static Creation Methods */

		/**
		 * Avoid creating a new object every time a check is successful.
		 */
		private static Result SUCCESSFUL = new Result();

		/* Implementation  */
		public static Result success() {
			return SUCCESSFUL;
		}

		/**
		 * Creates a failed check result with the default failure message:
		 * {@link ApiLibSettings#DEFAULT_INVALID_PARAMETER_MESSAGE}.
		 */
		public static Result failure() {
			return new Result(ApiLibSettings.DEFAULT_INVALID_PARAMETER_MESSAGE);
		}

		/**
		 * Creates a failed check result with the provided failure message.
		 * If the failure message is null it will be set to
		 * {@link ApiLibSettings#DEFAULT_INVALID_PARAMETER_MESSAGE}.
		 */
		public static Result failure(String failureMessage) {
			if (failureMessage == null) {
				/*
				* Cannot be null or would need to add a boolean to differentiate a failure
				* from success, due to how successful and failure methods are implemented.
				* */
				return new Result(ApiLibSettings.DEFAULT_INVALID_PARAMETER_MESSAGE);
			}
			return new Result(failureMessage);
		}

		/**
		 * To avoid creating a new always failing check every time with {@link Check#alwaysFail()},
		 * this is used instead. It is created here in a package-private manner so that it is not
		 * accessible to the public, but can be used by the outer interface.
		 */
		static final Check ALWAYS_FAIL_UNTYPED_CHECK = t -> Result.failure();
	}

	/**
	 * Should be used when it is only necessary to check if the parameter is provided
	 * and is of the correct type. The actual value does not matter. This can be used
	 * with {@link ApiSingleParam#canBeNull} as true or false. If {@code canBeNull=true}
	 * then this will pass either way. If {@code canBeNull=false} then this will cause
	 * {@link ApiSingleParam#check(Map)}} to only pass if the parameter is NOT null and
	 * is of the correct type.
	 *
	 * @param clazz class (or parent class) the parameter must be an instance of (will fail if it is not)
	 * @param <T> type of the parameter
	 */
	static <T> Check<T> alwaysPass(Class<T> clazz) {
		return alwaysPass(clazz, ApiLibSettings.DEFAULT_CASTING_ERROR_MESSAGE);
	}

	/**
	 * Should be used when it is only necessary to check if the parameter is provided
	 * and is of the correct type. The actual value does not matter. This can be used
	 * with {@link ApiSingleParam#canBeNull} as true or false. If {@code canBeNull=true}
	 * then this will pass either way. If {@code canBeNull=false} then this will cause
	 * {@link ApiSingleParam#check(Map)}} to only pass if the parameter is NOT null and
	 * is of the correct type.
	 *
	 * @param clazz class (or parent class) the parameter must be an instance of (will fail if it is not)
	 * @param invalidClassErrorMessage failure message returned if param is not of correct type
	 * @param <T> type of the parameter
	 * @return
	 */
	static <T> Check<T> alwaysPass(Class<T> clazz, String invalidClassErrorMessage) {
		return param -> clazz.isInstance(param)
			?
			Check.Result.success()
			:
			Check.Result.failure(invalidClassErrorMessage);

	}

	/**
	 * Should be used when the parameter check should always fail regardless of the value and
	 * parameter class type - this does not type check like {@link #alwaysPass(Class)}, since
	 * it always fails.
	 *
	 * @param <T> type of the parameter
	 * @return a check that always fails
	 */
	@SuppressWarnings("unchecked")
	static <T> Check<T> alwaysFail() {
		return Result.ALWAYS_FAIL_UNTYPED_CHECK;
	}

	/**
	 * Should be used when the parameter check should always fail regardless of the value and
	 * parameter class type - this does not type check like {@link #alwaysPass(Class)}, since
	 * it always fails.
	 *
	 * @param failureMessage the message to return with the check failure
	 * @param <T> type of the parameter (is not actually checked)
	 * @return a check that always fails
	 */
	static <T> Check<T> alwaysFail(String failureMessage) {
		return (T t) -> Result.failure(failureMessage);
	}

	/**
	 * Check that the value of the parameter satisfies this check.
	 * @param param the parameter value to check
	 * @return
	 */
	Result check(T param);

}
