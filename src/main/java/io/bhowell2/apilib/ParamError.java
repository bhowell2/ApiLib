package io.bhowell2.apilib;


/**
 * If an error occurs, the error type should be specified, an error message (can just be empty string), and the name of the parameter where the
 * error occurred.
 *
 * @author Blake Howell
 */
public final class ParamError {

	public final String paramName;
	public final ErrorType errorType;
	public final String errorMessage;

	public ParamError(ErrorType type, String errorMessage, String paramName) {
		this.errorType = type;
		this.paramName = paramName;
		this.errorMessage = errorMessage;
	}

}
