package io.bhowell2.apilib;

/**
 * @author Blake Howell
 */
public enum ErrorType {

    MISSING_PARAMETER,
    INVALID_PARAMETER,  // when the parameter does not meet the requirements (checks)
    PARAMETER_CAST,     // failed to cast parameter to type

}
