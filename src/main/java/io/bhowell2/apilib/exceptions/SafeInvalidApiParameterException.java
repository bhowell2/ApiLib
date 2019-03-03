package io.bhowell2.apilib.exceptions;

/**
 * Informs the receiver that the reason for failure is safe to return to the user if desired.
 *
 * @author Blake Howell
 */
public class SafeInvalidApiParameterException extends SafeParameterCheckException {

	public SafeInvalidApiParameterException(String invalidParamName) {
		super("The parameter provided was invalid for: " + invalidParamName + ".", invalidParamName);
	}

	public SafeInvalidApiParameterException(String invalidParamName, String reasonForInvalidity) {
		super("The parameter provided was invalid for: " + invalidParamName + ". Because, " + reasonForInvalidity, invalidParamName);
	}

}
