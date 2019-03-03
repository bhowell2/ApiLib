package io.bhowell2.apilib;

/**
 * @author Blake Howell
 */
public enum ErrorType {

	MISSING_PARAMETER,
	INVALID_PARAMETER,  // when the parameter does not meet the requirements (checks)
	PARAMETER_CAST,     // failed to cast parameter to type
	FORMAT_ERROR,       // any error that occurs when formatting the param
	OTHER_ERROR

}
