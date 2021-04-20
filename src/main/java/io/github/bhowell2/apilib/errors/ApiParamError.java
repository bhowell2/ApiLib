package io.github.bhowell2.apilib.errors;


import io.github.bhowell2.apilib.ApiCollectionParam;
import io.github.bhowell2.apilib.ApiCustomParam;
import io.github.bhowell2.apilib.ApiLibSettings;
import io.github.bhowell2.apilib.ApiMapParam;
import io.github.bhowell2.apilib.ApiMapParamConditionalCheck;
import io.github.bhowell2.apilib.ApiParamBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Returned when some type of error occurred checking a parameter. This provides
 * the key name and display name (if set) of the parameter where the error occurred,
 * as well as the error type ({@link ApiErrorType}), optionally an error message
 * (may be default message for the given error type), an exception if the error was
 * caused by a thrown exception, and if the error occurred in a nested {@link ApiMapParam}
 * an ApiParamError will be returned with the nested ApiParamErrors which can be traversed
 * to obtain the innermost ApiParamError where the error/failure occurred.
 *
 * @author Blake Howell
 */
public class ApiParamError {

	/**
	 * In most cases the key name will be set, but this is not the case when
	 * the failure is of type {@link ApiErrorType#CONDITIONAL_ERROR} and this
	 * may also not be the case in an {@link ApiCustomParam} check.
	 */
	public final String keyName;

	/**
	 * Parameter's display name if it was set.
	 */
	public final String displayName;

	/**
	 * In the case of {@link ApiCollectionParam} this is used to track the index
	 * where the error occurred. This can be combined with {@link #childParamError}
	 * for multidimensional collections. If an {@link ApiParamError} has {@link #index}
	 * set and a {@link #childParamError} then it is either a multidimensional array
	 * or an array of maps.
	 */
	public final Integer index;

	/**
	 * The error type will be the same for all nested errors. I.e., if the error
	 * occurs in a nested map the top level map will have the same errorType as
	 * the failing, nested, parameter.
	 */
	public final ApiErrorType errorType;

	/**
	 * Error message will only exist in the root ApiParamError (i.e., the innermost
	 * childApiParamError). Error messages should be descriptive and usually won't
	 * return a name in them. The user can use the keyName or displayName and concat
	 * it with the error message (e.g., The error message "Must be at least 5 characters
	 * in length." is returned and can be sent back to the user as "Parameter error
	 * in 'displayName'|'keyName'. Must be at least 5 characters in length.").
	 */
	public final String errorMessage;

	/**
	 * Provided so that the library user can access this if need be - can be helpful
	 * for debugging purposes. Will be null if no exception was thrown when checking.
	 */
	public final Exception exception;

	/**
	 * Allows for tracking exactly where the error occurred in nested parameters.
	 */
	public final ApiParamError childParamError;

	public ApiParamError(String keyName,
	                     String displayName,
	                     Exception exception) {
		this(keyName, displayName, ApiErrorType.EXCEPTIONAL, null, exception, null, null);
	}

	public ApiParamError(String keyName,
	                     String displayName,
	                     ApiErrorType type,
	                     String errorMessage) {
		this(keyName, displayName, type, errorMessage, null, null, null);
	}

	public ApiParamError(String keyName,
	                     String displayName,
	                     ApiErrorType type,
	                     String errorMessage,
	                     Exception exception) {
		this(keyName, displayName, type, errorMessage, exception, null, null);
	}

	public ApiParamError(String keyName,
	                     String displayName,
	                     ApiErrorType type,
	                     String errorMessage,
	                     Exception exception,
	                     ApiParamError childParamError) {
		this(keyName, displayName, type, errorMessage, exception, null, childParamError);
	}

	public ApiParamError(String keyName,
	                     String displayName,
	                     ApiErrorType type,
	                     String errorMessage,
	                     Exception exception,
	                     Integer index,
	                     ApiParamError childParamError) {
		this.keyName = keyName;
		this.displayName = displayName;
		this.errorType = type;
		this.errorMessage = errorMessage;
		this.exception = exception;
		this.index = index;
		this.childParamError = childParamError;
	}

	/**
	 * In certain cases there will not be a key name.
	 * E.g., an {@link ApiMapParamConditionalCheck} may not return
	 * a key name.
	 * @return true if the key name is not null. false otherwise.
	 */
	public boolean hasKeyName() {
		return this.keyName != null;
	}

	/**
	 * @return true if the display name is not null. false otherwise.
	 */
	public boolean hasDisplayName() {
		return this.displayName != null;
	}

	public boolean hasIndex() {
		return this.index != null;
	}

	/**
	 * @return true if error message is not null. false otherwise.
	 */
	public boolean hasErrorMessage() {
		return this.errorMessage != null;
	}

	/**
	 * @return true if child param error is not null. false otherwise.
	 */
	public boolean hasChildError() {
		return this.childParamError != null;
	}

	/**
	 * Creates a list for this error and all of its child errors. These are
	 * in order where index 0 is THIS (calling class) error and the ascending
	 * indices are the previous index's child error.
	 *
	 * Note: if this error is the child of another error the parent is not
	 * included (as the child does not have a reference to the parent so it
	 * cannot retrieve it).
	 * @return list of this ApiParamError and any child errors that caused this ApiParamError.
	 */
	public List<ApiParamError> getErrorsAsList() {
		if (this.hasChildError()) {
			ApiParamError error = this;
			List<ApiParamError> errorList = new ArrayList<>(2);
			while (error.hasChildError()) {
				errorList.add(error);
				error = error.childParamError;
			}
			errorList.add(error);
			return errorList;
		}
		return Collections.singletonList(this);
	}

	/**
	 * Creates a map from the fields. This map will be as complex as the
	 * API. If there are many nested maps/arrays they will have to be 
	 * traversed in the map.
	 * @return
	 */
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		if (this.keyName != null) {
			map.put(ApiLibSettings.ErrorMessageToMapParamNames.KEY_NAME, this.keyName);
		}
		if (this.displayName != null) {
			map.put(ApiLibSettings.ErrorMessageToMapParamNames.DISPLAY_NAME, this.displayName);
		}
		if (this.index != null) {
			map.put(ApiLibSettings.ErrorMessageToMapParamNames.INDEX, this.index);
		}
		if (this.errorMessage != null) {
			map.put(ApiLibSettings.ErrorMessageToMapParamNames.ERROR_MESSAGE, this.errorMessage);
		}
		if (this.errorType != null) {
			map.put(ApiLibSettings.ErrorMessageToMapParamNames.ERROR_TYPE, this.errorType.name());
		}
		if (this.hasChildError()) {
			map.put(ApiLibSettings.ErrorMessageToMapParamNames.CHILD_ERROR, this.childParamError.toMap());
		}
		return map;
	}

	@Override
	public String toString() {
		return "Key name: " + this.keyName +
			". Display name: " + this.displayName +
			". Error message: " + this.errorMessage +
			". Error type: " + this.errorType +
			(this.exception != null ? ("Exception message: " + this.exception.getMessage()) : ".");
	}


	/* Static Creation Methods */

	public static ApiParamError invalid(String keyName) {
		return invalid(keyName, null);
	}

	/**
	 * Creates ApiParamError with {@link ApiErrorType#INVALID_PARAMETER}
	 * and {@link ApiLibSettings#DEFAULT_INVALID_PARAMETER_MESSAGE}.
	 */
	public static ApiParamError invalid(ApiParamBase apiParam) {
		return invalid(apiParam.keyName, apiParam.displayName);
	}

	/**
	 * Creates ApiParamError with {@link ApiErrorType#INVALID_PARAMETER}
	 * and {@link ApiLibSettings#DEFAULT_INVALID_PARAMETER_MESSAGE}.
	 */
	public static ApiParamError invalid(ApiParamBase apiParam, String errorMessage) {
		return invalid(apiParam.keyName, apiParam.displayName, errorMessage);
	}

	/**
	 * Creates ApiParamError with {@link ApiErrorType#INVALID_PARAMETER}
	 * and {@link ApiLibSettings#DEFAULT_INVALID_PARAMETER_MESSAGE}.
	 */
	public static ApiParamError invalid(String keyName, String displayName) {
		return invalid(keyName, displayName, ApiLibSettings.DEFAULT_INVALID_PARAMETER_MESSAGE);
	}

	/**
	 * Creates ApiParamError with {@link ApiErrorType#INVALID_PARAMETER}
	 * and supplied error message.
	 */
	public static ApiParamError invalid(String keyName, String displayName, String failureMessage) {
		return new ApiParamError(keyName, displayName, ApiErrorType.INVALID_PARAMETER, failureMessage);
	}

	/**
	 * Creates ApiParamError with {@link ApiErrorType#MISSING_PARAMETER}
	 * and {@link ApiLibSettings#DEFAULT_MISSING_PARAMETER_MESSAGE}.
	 */
	public static ApiParamError missing(ApiParamBase apiParam) {
		return missing(apiParam.keyName, apiParam.displayName);
	}

	/**
	 * Creates ApiParamError with {@link ApiErrorType#MISSING_PARAMETER}
	 * and {@link ApiLibSettings#DEFAULT_MISSING_PARAMETER_MESSAGE}.
	 */
	public static ApiParamError missing(String keyName, String displayName) {
		return missing(keyName, displayName, ApiLibSettings.DEFAULT_MISSING_PARAMETER_MESSAGE);
	}

	/**
	 * Creates ApiParamError with {@link ApiErrorType#MISSING_PARAMETER} and supplied error message.
	 */
	public static ApiParamError missing(String keyName, String displayName, String errorMessage) {
		return new ApiParamError(keyName, displayName, ApiErrorType.MISSING_PARAMETER, errorMessage);
	}

	/**
	 * Creates ApiParamError with {@link ApiErrorType#FORMAT_ERROR}
	 * and {@link ApiLibSettings#DEFAULT_FORMATTING_ERROR_MESSAGE}.
	 */
	public static ApiParamError format(ApiParamBase apiParam) {
		return format(apiParam.keyName, apiParam.displayName, ApiLibSettings.DEFAULT_FORMATTING_ERROR_MESSAGE);
	}

	/**
	 * Creates ApiParamError with {@link ApiErrorType#FORMAT_ERROR}
	 * and {@link ApiLibSettings#DEFAULT_FORMATTING_ERROR_MESSAGE}.
	 */
	public static ApiParamError format(ApiParamBase apiParam, String errorMessage) {
		return format(apiParam.keyName, apiParam.displayName, errorMessage);
	}

	/**
	 * Creates ApiParamError with {@link ApiErrorType#FORMAT_ERROR} and supplied error message.
	 */
	public static ApiParamError format(String keyName, String displayName, String failureMessage) {
		return new ApiParamError(keyName, displayName, ApiErrorType.FORMAT_ERROR, failureMessage);
	}

	/**
	 * Creates an ApiParamError with {@link ApiErrorType#CONDITIONAL_ERROR}
	 * and returns the error message supplied.
	 */
	public static ApiParamError conditional(String failureMessage) {
		return conditional(null, null, failureMessage);
	}

	public static ApiParamError conditional(String keyName, String displayName, String failureMessage) {
		return new ApiParamError(keyName, displayName, ApiErrorType.CONDITIONAL_ERROR, failureMessage);
	}

	/**
	 * Creates ApiParamError with {@link ApiErrorType#CASTING_ERROR}
	 * and {@link ApiLibSettings#DEFAULT_CASTING_ERROR_MESSAGE}
	 * as the error message.
	 */
	public static ApiParamError cast(ApiParamBase apiParam, Exception exception) {
		return cast(apiParam.keyName, apiParam.displayName, exception);
	}

	/**
	 * Creates ApiParamError with {@link ApiErrorType#CASTING_ERROR} and supplied error message.
	 */
	public static ApiParamError cast(String keyName, String displayName, Exception exception) {
		return new ApiParamError(keyName,
		                         displayName,
		                         ApiErrorType.CASTING_ERROR,
		                         ApiLibSettings.DEFAULT_CASTING_ERROR_MESSAGE,
		                         exception);
	}

	/**
	 * Creates ApiParmError with {@link ApiErrorType#EXCEPTIONAL}
	 * @param apiParam
	 * @param exception
	 * @return
	 */
	public static ApiParamError exceptional(ApiParamBase apiParam, Exception exception) {
		return exceptional(apiParam.keyName, apiParam.displayName, exception);
	}

	public static ApiParamError exceptional(String keyName, String displayName, Exception exception) {
		return new ApiParamError(keyName,
		                         displayName,
		                         ApiErrorType.EXCEPTIONAL,
		                         null,
		                         exception,
		                         null,
		                         null);
	}
}
