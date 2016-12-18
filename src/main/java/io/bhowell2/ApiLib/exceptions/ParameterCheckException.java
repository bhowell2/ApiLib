package io.bhowell2.ApiLib.exceptions;

/**
 * This should be returned if there was an exception during the checking process. If the function is not of the {@link SafeParameterCheckException}
 * type, then it is likely that the error message should not be returned to the user and a generic message should be returned instead. This should be
 * done to avoid giving the user information that could potentially aid exploits of the parameter checking system.
 *
 * @author Blake Howell
 */
public class ParameterCheckException extends IllegalArgumentException {

  public ParameterCheckException(String message) {super(message);}

}
