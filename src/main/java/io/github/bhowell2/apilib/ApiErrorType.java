package io.github.bhowell2.apilib;

import java.util.Map;

/**
 * Descriptive types for errors that may occur when checking a parameter.
 * This allows for the developer to easily check what type of error occurred
 * and whether or not they want to return the error message (potentially)
 * provided to the end user.
 *
 * The are default error message for each type (except EXCEPTIONAL) that can
 * be overridden at {@link ApiLibSettings}.
 *
 * @author Blake Howell
 */
public enum ApiErrorType {

	/**
	 * The parameter was not provided. This will only be returned to the
	 * user if {@link ApiSingleParam#check(Map)} is called directly (uncommon) or
	 * if the missing parameter was set as required by {@link ApiMapParam}.
	 * Default message: {@link ApiLibSettings#DEFAULT_MISSING_PARAMETER_MESSAGE}.
	 */
	MISSING_PARAMETER,

	/**
	 * The parameter was provided, but one of the checks failed. A failed
	 * check may return a failure message that will be returned in {@link ApiParamError}.
	 * Default message: {@link ApiLibSettings#DEFAULT_INVALID_PARAMETER_MESSAGE}.
	 */
	INVALID_PARAMETER,

	/**
	 * The parameter was provided, but a formatter failed in some way. A failed
	 * formatter may return a failure message that will be returned in {@link ApiParamError}.
	 * Default message: {@link ApiLibSettings#DEFAULT_FORMATTING_ERROR_MESSAGE}.
	 */
	FORMAT_ERROR,

	/**
	 * Some condition ({@link io.github.bhowell2.apilib.checks.ConditionalCheck})
	 * is not met in {@link ApiMapParam}. A failed condition may return a failure
	 * message that will be returned in {@link ApiParamError}.
	 * Default message: {@link ApiLibSettings#DEFAULT_CONDITIONAL_FAILURE_MESSAGE}.
	 */
	CONDITIONAL_ERROR,

	/**
	 * When a ClassCastException is thrown when checking any parameter. While this could
	 * happen anywhere, it will be caught by the parameter being checked if it is not
	 * handled before and returned in {@link ApiParamError} for the parameter in
	 * which the exception was caught.
	 * Default message: {@link ApiLibSettings}
	 */
	CASTING_ERROR,

	/**
	 * An exception was thrown when checking the parameter. The developer
	 * likely does not want to return the error message to the user but
	 * may want to log it.
	 */
	EXCEPTIONAL

}
