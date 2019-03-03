package io.bhowell2.apilib.exceptions;

/**
 * @author Blake Howell
 */
public class ApiVersionFormatException extends IllegalArgumentException {

	public ApiVersionFormatException(String errorMessage) {
		super(errorMessage);
	}

}
