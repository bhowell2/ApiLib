package io.bhowell2.ApiLib.exceptions;

/**
 * @author Blake Howell
 */
public class SafeMissingApiParametersException extends SafeParameterCheckException {
  public SafeMissingApiParametersException(String message) {
    super(message);
  }
}
