package io.github.bhowell2.apilib;


import io.github.bhowell2.apilib.errors.ApiErrorType;
import io.github.bhowell2.apilib.errors.ApiParamError;

import java.util.Map;

/**
 * Called at the end of a {@link ApiMapParam#check(Map)} to ensure that some
 * conditions were met. This allows for checking whether or not certain parameters
 * were provided together (or not provided together). Since this is called at the
 * end of the check, the {@link ApiMapParam.Result} is passed into the conditional
 * check so the user can see which parameters were successfully checked along with
 * the map of parameters so that value(s) can be retrieved if necessary/desired.
 *
 * This is simpler than using {@link ApiCustomParam} to ensure that multiple parameters
 * are provided together (or not), because it ensures that the parameters are valid
 * (have successfully met all checks) by the time they reach this conditional check.
 *
 * This does not return whether or not a particular parameter (by key name) failed, but
 * only whether or not some condition is met. An informative failure message may be
 * returned so that it can be displayed to the user. If the user needs to return the key
 * name explicitly a {@link ApiCustomParam} should be used.
 *
 * E.g.,
 * The user wants to make sure an email is provided if they set e_billing to
 * true, they only need to get the value of e_billing and ensure that email
 * has been successfully checked:
 * <pre>
 * {@code
 *  Result check(Map<String, Object> params, ApiMapParam.Result result) {
 *    // return success if e_billing was NOT provided OR it is provided AND is true AND email was provided
 *    if (!result.containsParameter("e_billing")
 *        || (
 *            result.containsParameter("e_billing")
 *            &&
 *            (Boolean)params.get("e_billing")
 *            &&
 *            result.containsParameter("email")
 *            )
 *        ) {
 *        return Result.success();
 *    }
 *    return Result.failure("Must provide email if e_billing is set to true");
 *  }
 * }
 * </pre>
 *
 * Doing this with {@link ApiCustomParam} could be done in two ways:
 * <pre>
 * {@code
 *  ApiCustomParam ebillingAndEmail = new ApiC
 * }
 * </pre>
 * @author Blake Howell
 */
public interface ApiMapParamConditionalCheck {

	/**
	 * This is run after all other parameter checks, which ensures that at
	 * this point all parameters have successfully been checked and now allows
	 * the user to implement some type of conditional checking.
	 *
	 * @return
	 */
	Result check(Map<String, Object> params, ApiMapParam.Result mapCheckResult);

	final class Result {

		/**
		 * An error representing the failure. Allows for returning a key name and/or
		 * an error message. Generally this will not contain more than the key name
		 * or error message.
		 */
		public final ApiParamError error;

		private Result() {
			this.error = null;
		}

		private Result(ApiParamError error) {
			this.error = error;
		}

		public boolean successful() {
			return error == null;
		}

		public boolean failed() {
			return error != null;
		}

		private static final Result SUCCESSFUL = new Result();

		public static Result success() {
			return SUCCESSFUL;
		}

		/**
		 * Creates a failed result with an {@link ApiParamError} that has no
		 * key name or display name and the error type is
		 * {@link ApiErrorType#CONDITIONAL_ERROR} and the error message is
		 * {@link ApiLibSettings#DEFAULT_CONDITIONAL_FAILURE_MESSAGE}
		 *
		 * @return a failed result.
		 */
		public static Result failure() {
			return failure(new ApiParamError(null,
			                                 null,
			                                 ApiErrorType.CONDITIONAL_ERROR,
			                                 ApiLibSettings.DEFAULT_CONDITIONAL_FAILURE_MESSAGE));
		}

		/**
		 * Creates a failed result with an {@link ApiParamError} that has the
		 * supplied key name, display name, and error message with error type
		 * {@link ApiErrorType#CONDITIONAL_ERROR}.
		 *
		 * @param keyName the key name for the {@link ApiParamError} (may be null)
		 * @param displayName the display name for the {@link ApiParamError} (may be null)
		 * @param errorMessage the error message for the {@link ApiParamError} (may be null, but probably should be)
		 * @return a failed result
		 */
		public static Result failure(String keyName, String displayName, String errorMessage) {
			return failure(ApiParamError.conditional(keyName, displayName, errorMessage));
		}

		/**
		 * Creates a failed result with the supplied {@link ApiParamError}.
		 * @param error the error to return in the result
		 * @return a failed result
		 */
		public static Result failure(ApiParamError error) {
			return new Result(error);
		}
	}

}
