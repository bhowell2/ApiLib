package io.bhowell2.apilib.exceptions;

/**
 * This should be returned if there was an exception during the checking process. If the function is not of the {@link SafeParameterCheckException}
 * type, then it is likely that the error message should not be returned to the user and a generic message should be returned instead. This should be
 * done to avoid giving the user information that could potentially aid exploits of the parameter checking system.
 *
 * @author Blake Howell
 */
public class ParameterCheckException extends IllegalArgumentException {

	private final String paramName;
	private final boolean isSafeToReturnMessage;

	public ParameterCheckException(String message, String paramName, boolean isSafeToReturnMessage) {
		super(message);
		this.paramName = paramName;
		this.isSafeToReturnMessage = isSafeToReturnMessage;
	}

	public String getParamName() {
		return paramName;
	}

	public boolean isSafeToReturnMessage() {
		return isSafeToReturnMessage;
	}

}
