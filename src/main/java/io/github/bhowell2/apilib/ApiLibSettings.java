package io.github.bhowell2.apilib;

import io.github.bhowell2.apilib.checks.Check;
import io.github.bhowell2.apilib.formatters.Formatter;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Blake Howell
 */
public class ApiLibSettings {

	/**
	 * Used when a parameter is set to null, but null is not a valid value for the parameter.
	 */
	public static volatile String DEFAULT_CANNOT_BE_NULL_MESSAGE = "Cannot be null.";

	/**
	 * Used when a parameter is not supplied. For optional parameters this message
	 * will never be returned, because if they are missing they were optional anyway.
	 */
	public static volatile String DEFAULT_MISSING_PARAMETER_MESSAGE = "Required, but was not provided.";

	/**
	 * Used when no "failure message" is supplied to {@link Check.Result} (on failure, of course).
	 */
	public static volatile String DEFAULT_INVALID_PARAMETER_MESSAGE = "Did not meet requirements.";

	/**
	 * Used as default conditional failure message. This does not recognize param-name-template-var,
	 * because conditional are intended to apply to multiple parameters, so the user should return
	 * an appropriate error message describing the reason for failure.
	 */
	public static volatile String DEFAULT_CONDITIONAL_FAILURE_MESSAGE = "Some condition was not met.";

	/**
	 * Error message used when a ClassCastException is thrown when checking a parameter.
	 */
	public static volatile String DEFAULT_CASTING_ERROR_MESSAGE = "Was of incorrect data type.";

	/**
	 * Error message used when an error occurred during formatting and an error message to
	 * {@link Formatter.Result#failure(String)} was not supplied.
	 */
	public static volatile String DEFAULT_FORMATTING_ERROR_MESSAGE = "Was not formattable.";

	/**
	 * Used to
	 */
	public static volatile Function<Integer, String> DEFAULT_ARRAY_PARAMETER_APPENDED_POSITION_FAILURE = pos ->
		"Failure occurred at index " + pos + ".";

	public static volatile BiFunction<Integer, Integer, String> DEFAULT_ARRAY_OF_ARRAY_PARAMETER_APPENDED_POSITION_FAILURE =
		(outer, inner) -> "Failure occurred at position (" + outer + "," + inner + ").";


	/**
	 * These are used to set the
	 */
	public static class ErrorMessageParamNames {
		public static volatile String KEY_NAME = "keyName";
		public static volatile String DISPLAY_NAME = "displayName";
		public static volatile String INDEX = "index";
		public static volatile String ERROR_TYPE = "errorType";
		public static volatile String ERROR_MESSAGE = "errorMessage";
		public static volatile String CHILD_ERROR = "childError";

	}

}
