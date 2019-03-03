package io.bhowell2.apilib.exceptions;

import java.util.List;

/**
 * Use when conditional parameters are required and therefore must inform the recipient that multiple parameters are missing.
 *
 * @author Blake Howell
 */
public class SafeMissingMultipleApiParametersException extends SafeParameterCheckException {
	public SafeMissingMultipleApiParametersException(String message, List<String> paramNames) {
		super(message, String.join(", ", paramNames));
	}
}
