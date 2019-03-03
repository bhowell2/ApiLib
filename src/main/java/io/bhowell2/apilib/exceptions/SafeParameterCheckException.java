package io.bhowell2.apilib.exceptions;

/**
 * @author Blake Howell
 */
public class SafeParameterCheckException extends ParameterCheckException {
	public SafeParameterCheckException(String message, String paramName) {
		super(message, paramName, true);
	}
}
