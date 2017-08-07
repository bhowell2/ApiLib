package io.bhowell2.ApiLib.exceptions;

/**
 * @author Blake Howell
 */
public class SafeMissingApiParameterException extends SafeParameterCheckException {
  public SafeMissingApiParameterException(String parameterName) {
    super("The following parameter is missing from the request: " + parameterName + ".", parameterName);
  }
}
