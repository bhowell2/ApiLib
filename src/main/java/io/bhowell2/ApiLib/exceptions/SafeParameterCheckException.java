package io.bhowell2.ApiLib.exceptions;

/**
 * @author Blake Howell
 */
public class SafeParameterCheckException extends ParameterCheckException {
  public SafeParameterCheckException(String message) {
    super(message);
  }
}
