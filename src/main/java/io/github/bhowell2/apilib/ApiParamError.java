package io.github.bhowell2.apilib;

import io.github.bhowell2.apilib.checks.Check;

import java.util.ArrayList;
import java.util.List;

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
	 * In most cases keyName and displayName will be set. This is not the
	 * case when the failure is of type {@link ApiErrorType#CONDITIONAL_ERROR} and
	 * the error occurs in the top-most/root ApiMapParam. This may also not
	 * be the case in an {@link ApiCustomParam} check.
	 */
	public final String keyName, displayName;

	/**
	 * If the error occurs in a nested map (i.e. a map within a map) then these will be
	 * set. This allows for tracing the error to the source parameter. Note index 0 of
	 * the list is the closest parent while the highest index is the most outer "parent".
	 *
	 * E.g.,
	 * {
	 *    outer1: {
	 *      outer2: {
	 *        param1: "failing!"
	 *      }
	 *    }
	 * }
	 * Will return a keyName of "param1" and the parentKeyNames list will be:
	 * ["outer2", "outer1"].
	 *
	 * This is necessary to disambiguate where an error actually occurred.
	 * E.g.,
	 * {
	 *   nested1: {
	 *     param1: "this should fail",
	 *     param2: "pass"
	 *   },
	 *   nested2: {
	 *     param1: "pass",
	 *     param2: "pass"
	 *   }
	 * }
	 * If 'param1' failed in the above JSON then it would not be clear if this occurred in
	 * 'nested1' or 'nested2'.
	 *
	 * TODO: Handle map within array
	 */
	private List<String> parentKeyNames, parentDisplayNames;

	/**
	 * The error type will be the same for all nested errors. I.e., if the error
	 * occurs in a nested map the top leve
	 */
	public final ApiErrorType errorType;

	/**
	 * Error message will only exist in the root ApiParamError (i.e., the innermost
	 * childApiParamError). Error messages should be descriptive and usually won't
	 * return a name in them. The user can use the keyName or displayName and concat
	 * it with the error message (e.g., "Failure in displayName. Must be at least 5
	 * characters in length.").
	 */
	public final String errorMessage;

	/**
	 * Provided so that the library user can access this if need be - can be helpful
	 * for debugging purposes. Will be null if no exception was thrown when checking.
	 */
	public final Exception exception;

	public ApiParamError(String keyName,
	                     String displayName,
	                     Exception exception) {
		this(keyName, displayName, ApiErrorType.EXCEPTIONAL, null, exception);
	}

	public ApiParamError(String keyName,
	                     String displayName,
	                     ApiErrorType type,
	                     String errorMessage) {
		this(keyName, displayName, type, errorMessage, null);
	}

	public ApiParamError(String keyName,
	                     String displayName,
	                     ApiErrorType type,
	                     String errorMessage,
	                     Exception exception) {
		this.keyName = keyName;
		this.displayName = displayName;
		this.errorType = type;
		this.errorMessage = errorMessage;
		this.exception = exception;
	}

	/**
	 * @return true if error message is not nul
	 */
	public boolean hasErrorMessage() {
		return this.errorMessage != null;
	}

	/**
	 * @return true if the error is nested within another parameter (e.g., a map within a map). false otherwise.
	 */
	public boolean isNestedError() {
		return this.parentKeyNames != null;
	}

	/**
	 * If the error occurs within a nested map the nested map name should be
	 * @param keyName
	 * @param displayName
	 */
	public void addParentKeyAndDisplayNames(String keyName, String displayName) {
		if (this.parentKeyNames == null) {
			this.parentKeyNames = new ArrayList<>(1);
		}
		this.parentKeyNames.add(keyName);
		if (this.parentDisplayNames == null) {
			this.parentDisplayNames = new ArrayList<>(1);
		}
		this.parentDisplayNames.add(displayName);
	}

	private static String joinKeyOrDisplayParentsIfExists(String keyOrDisplayName,
	                                                      List<String> parentKeyOrDisplayNames,
	                                                      String delimiter,
	                                                      boolean reversed) {
		if (parentKeyOrDisplayNames == null) {
			return keyOrDisplayName;
		}
		if (reversed) {
			StringBuilder builder = new StringBuilder();
			for (int i = parentKeyOrDisplayNames.size() - 1; i >= 0; i--) {
				builder.append(parentKeyOrDisplayNames.get(i))
				       .append(delimiter);
			}
			// has final delimiter from above
			return builder.append(keyOrDisplayName).toString();
		} else {
			return keyOrDisplayName + delimiter + String.join(delimiter, parentKeyOrDisplayNames);
		}
	}

	/**
	 * @return the list of the parameter's parents' key names. null if error is not nested.
	 */
	public List<String> getParentKeyNames() {
		return this.parentKeyNames;
	}

	/**
	 * Joins the parent key names with the key name of the failed parameter.
	 *
	 * The normal order is error-key-name -> parent 1 -> grandparent 1 -> great grand parent
	 * @param delimiter delimiter between key names
	 * @param reverse true for order from the error key name to topmost (named) map or
	 *                false for the order from the topmost (named) map to the error key name.
	 * @return
	 */
	public String joinParentKeyNamesWithErrorKeyName(String delimiter, boolean reverse) {
		return joinKeyOrDisplayParentsIfExists(this.keyName, this.parentKeyNames, delimiter, reverse);
	}

	/**
	 * @return the list of the parameter's parents' display names. null if error is not nested.
	 */
	public List<String> getParentDisplayNames() {
		return parentDisplayNames;
	}

	public String joinParentDisplayNamesWithErrorDisplayName(String delimiter, boolean reverse) {
		return joinKeyOrDisplayParentsIfExists(this.displayName, this.parentDisplayNames, delimiter, reverse);
	}

	/**
	 * Whether or not this error was caused by an exception. In the case that
	 * this error was not caused by an exception it was caused by a strict
	 * checking failure of the parameter (e.g., parameter is missing or parameter
	 * did not satisfy checks).
	 * @return
	 */
	public boolean wasExceptionalError() {
		return this.errorType == ApiErrorType.EXCEPTIONAL;
	}

//	/**
//	 * Creates a list of the nested display names where this error occurred.
//	 * Generally the user will only want to call this when {@link #hasChildError()}
//	 * is true to obtain a tree of where the error occurred.
//	 */
//	public List<String> getListOfDisplayNames() {
//		ApiParamError traverser = this;
//		List<String> list = new ArrayList<>();
//		while (traverser.hasChildError()) {
//			list.add(traverser.displayName);
//			traverser = traverser.childApiParamError;
//		}
//		list.add(traverser.displayName);
//		return list;
//	}
//
//	/**
//	 * Creates a list of the nested display names where this error occurred.
//	 * Generally the user will only want to call this when {@link #hasChildError()}
//	 * is true to obtain a tree of where the error occurred.
//	 */
//	public List<String> getListOfKeyNames() {
//		ApiParamError traverser = this;
//		List<String> list = new ArrayList<>();
//		while (traverser.hasChildError()) {
//			list.add(traverser.keyName);
//			traverser = traverser.childApiParamError;
//		}
//		list.add(traverser.displayName);
//		return list;
//	}
//
//	/**
//	 * Returns a Map containing the name of the parameter
//	 */
//	public Map<String, Object> getCompleteErrorMessage() {
//		Map<String, Object> message = new HashMap<>();
//		message.put(ApiLibSettings.ErrorMessageParamNames.ERROR_PARAM_KEY_NAME, getInnermostChildKeyName());
//		message.put(ApiLibSettings.ErrorMessageParamNames.ERROR_PARAM_DISPLAY_NAME, getInnermostChildKeyName());
//		message.put(ApiLibSettings.ErrorMessageParamNames.ERROR_PARAM_KEY_NAME_LIST, getListOfKeyNames());
//		message.put(ApiLibSettings.ErrorMessageParamNames.ERROR_PARAM_DISPLAY_NAME_LIST, getListOfDisplayNames());
//		message.put(ApiLibSettings.ErrorMessageParamNames.ERROR_MESSAGE, getInnermostErrorMessage());
//		return message;
//	}

//	private static String injectNameIntoTemplateVar(String templatedErrMsg, String replacementValue) {
//		/*
//		 * StringBuilder.replace is much more efficient that String.replace since the latter uses
//		 * a Pattern (and creates a new one every-time at that).
//		 * */
//		int startTemplateVarPos = templatedErrMsg.indexOf(PARAM_NAME_TEMPLATE_VAR);
//		if (startTemplateVarPos >= 0) {
//			int endTemplateVarPos = startTemplateVarPos + PARAM_NAME_TEMPLATE_VAR_LENGTH;
//			return new StringBuilder(templatedErrMsg)
//				.replace(startTemplateVarPos, endTemplateVarPos, replacementValue == null ? "" : replacementValue)
//				.toString();
//		}
//		// nothing to replace, just return message.
//		return templatedErrMsg;
//	}

	/**
	 * Creates an error message by joining the supplied list with the delimiter and the error message. Allows for
	 * reversing the name list, which puts the innermost parameter name (display or key) first.
	 * @param templatedErrMsg
	 * @param displayOrKeyNames
	 * @param joinDelimiter
	 * @param reversed
	 * @return
	 */
//	private static String generateErrorMessage(String templatedErrMsg,
//	                                           List<String> displayOrKeyNames,
//	                                           String joinDelimiter,
//	                                           boolean reversed) {
//		StringBuilder paramName = new StringBuilder();
//		if (reversed) {
//			int endPos = 0;
//			for (int i = displayOrKeyNames.size() - 1; i >= endPos; i--) {
//				if (i != endPos) {
//					paramName.append(displayOrKeyNames.get(i))
//					         .append(joinDelimiter);
//				} else {
//					paramName.append(displayOrKeyNames.get(i));
//				}
//			}
//		} else {
//			int endPos = displayOrKeyNames.size() - 1;
//			for (int i = 0; i <= endPos; i++) {
//				if (i != endPos) {
//					paramName.append(displayOrKeyNames.get(i))
//					         .append(joinDelimiter);
//				} else {
//					paramName.append(displayOrKeyNames.get(i));
//				}
//			}
//		}
////		return injectNameIntoTemplateVar(templatedErrMsg, paramName.toString());
//	}

	/**
	 *
	 * @return
	 */
//	public String getErrorMessageWithDisplayName() {
//		return getErrorMessageWithDisplayName(".", false);
//	}

	/**
	 * Injects the display name into the error message (at '$PARAM_NAME$').
	 * For all {@link Check}s used from this library this will generate a
	 * coherent error message (e.g., "'Username' must have a length greater
	 * than 3.") The user should be mindful of this when creating their own
	 * {@link Check}s and put '$PARAM_NAME$' where it makes the most sense.
	 *
	 * The {@link #childApiParamError} is used when an {@link ApiMapParam} is
	 * nested within another {@link ApiMapParam}. It allows for creating a
	 * graph of sorts to determine where the error occurred. This method will
	 * traverse the {@link #childApiParamError}s until it is null and create
	 * the graph joining them all the way up to the root. The order the parameter
	 * names (display name or key name) is reversible. By default (non-reversed),
	 * the top-most parameter is given first.
	 *
	 * E.g., (JSON)
	 * {
	 *   inner1: {
	 *     inner2: {
	 *       param1: "this will fail because it contains more than 10 characters"
	 *     }
	 *   }
	 * }
	 *
	 * With joinDelimiter = "." and reverseNameOrder = false a string will be
	 * returned that looks like:
	 * "inner1.inner2.param1 cannot be longer than 10 characters."
	 *
	 * With joinDelimiter = " of " and reverseNameOrder = true a string will be
	 * returned that looks like:
	 * "param1 of inner2 of inner1 cannot be longer than 10 characters."
	 *
	 * @param joinDelimiter will be placed between display name and error message (spaces should be added as desired)
	 * @param reverseNameOrder whether or not to reverse the order display names appear
	 * @return  the display name (and display name's of parent {@link ApiMapParam}s if applicable ) joined with
	 *          the error message
	 */
//	public String getErrorMessageWithDisplayName(String joinDelimiter, boolean reverseNameOrder) {
//		if (hasChildError()) {
//			ApiParamError traverser = this;
//			// size 2 for THIS and the child. rarely will depth be more than 1, so should not have to resize list often.
//			List<String> displayNames = new ArrayList<>(2);
//			// add first time, before changing traverser
//			displayNames.add(traverser.displayName);
//			String innerMostErrorMessage = traverser.errorMessage;
//			while (traverser.hasChildError()) {
//				traverser = traverser.childApiParamError;
//				// add after changing traverser
//				displayNames.add(traverser.displayName);
//				innerMostErrorMessage = traverser.errorMessage;
//			}
//			return generateErrorMessage(innerMostErrorMessage, displayNames, joinDelimiter, reverseNameOrder);
//		} else {
//			/*
//			 * StringBuilder.replace is much more efficient that String.replace since the latter uses
//			 * a Pattern (and creates a new one every-time at that).
//			 * */
//			int startTemplateVarPos = this.errorMessage.indexOf(PARAM_NAME_TEMPLATE_VAR);
//			if (startTemplateVarPos >= 0) {
//				int endTemplateVarPos = startTemplateVarPos + PARAM_NAME_TEMPLATE_VAR_LENGTH;
//				return new StringBuilder(this.errorMessage)
//					.replace(startTemplateVarPos, endTemplateVarPos, this.displayName == null ? "" : this.displayName)
//					.toString();
//			}
//			// nothing to replace, just return message.
//			return this.errorMessage;
//		}
//	}
//
//	public String getErrorMessageWithKeyName() {
//		return getErrorMessageWithKeyName(".", false);
//	}
//
	/**
	 * Combines the key name with the error message. For all {@link Check}s used from this library this will
	 * generate a coherent error message (e.g., 'Username' must have a length greater than 3.) The user should be
	 * mindful of this when creating their own {@link Check}s.
	 *
	 * The {@link #childApiParamError} is used when an {@link ApiMapParam} is nested within another {@link ApiMapParam}.
	 * It allows for creating a graph of sorts to determine where the error occurred. This method will traverse the
	 * {@link #childApiParamError}s until it is null and create the graph joining them all the way up to the root. The
	 * order the parameter names (display name or key name) is reversible. By default (non-reversed), the top-most
	 * parameter is given first.
	 * E.g., (JSON)
	 * {
	 *   inner1: {
	 *     inner2: {
	 *       param1: "this will fail because it contains more than 10 characters"
	 *     }
	 *   }
	 * }
	 *
	 * With joinDelimiter = "." and reverseNameOrder = false a string will be returned that looks like:
	 * "inner1.inner2.param1 cannot be longer than 10 characters."
	 *
	 * With joinDelimiter = " of " and reverseNameOrder = true a string will be returned that looks like:
	 * "param1 of inner2 of inner1 cannot be longer than 10 characters."
	 *
	 * @param joinDelimiter will be placed between display name and error message (spaces should be added as desired)
	 * @param reverseNameOrder whether or not to reverse the order display names appear
	 * @return  the display name (and display name's of parent {@link ApiMapParam}s if applicable ) joined with
	 *          the error message
	 */
//	public String getErrorMessageWithKeyName(String joinDelimiter, boolean reverseNameOrder) {
//		if (hasChildError()) {
//			ApiParamError traverser = this;
//			// size 2 for THIS and the child. rarely will depth be more than 1, so should not have to resize list often.
//			List<String> keyNames = new ArrayList<>(2);
//			// add first time, before changing traverser
//			keyNames.add(traverser.displayName);
//			String innerMostErrorMessage = traverser.errorMessage;
//			while (traverser.hasChildError()) {
//				traverser = traverser.childApiParamError;
//				// add after changing traverser
//				keyNames.add(traverser.keyName);
//				innerMostErrorMessage = traverser.errorMessage;
//			}
//			return generateErrorMessage(innerMostErrorMessage, keyNames, joinDelimiter, reverseNameOrder);
//		} else {
//			/*
//			 * StringBuilder.replace is much more efficient that String.replace since the latter uses
//			 * a Pattern (and creates a new one every-time at that).
//			 * */
//			int startTemplateVarPos = this.errorMessage.indexOf(PARAM_NAME_TEMPLATE_VAR);
//			if (startTemplateVarPos >= 0) {
//				int endTemplateVarPos = startTemplateVarPos + PARAM_NAME_TEMPLATE_VAR_LENGTH;
//				return new StringBuilder(this.errorMessage)
//					.replace(startTemplateVarPos, endTemplateVarPos, this.displayName == null ? "" : this.keyName)
//					.toString();
//			}
//			// nothing to replace, just return message.
//			return this.errorMessage;
//		}
//	}


	/* Static Creation Methods */


	public static ApiParamError invalid(String failureMessage) {
		return invalid(null, null, failureMessage);
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
	public static ApiParamError invalid(ApiParamBase apiParam, String templatedErrMsg) {
		return invalid(apiParam.keyName, apiParam.displayName, templatedErrMsg);
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
	 * and returns the conditional error message supplied.
	 * @param failureMessage
	 * @return
	 */
	public static ApiParamError conditional(String failureMessage) {
		return conditional(null, null, failureMessage);
	}

	public static ApiParamError conditional(String keyname, String displayName, String failureMessage) {
		return new ApiParamError(keyname, displayName, ApiErrorType.CONDITIONAL_ERROR, failureMessage);
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
		                         exception);
	}
}
