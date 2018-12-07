package io.bhowell2.apilib.exceptions;

/**
 * @author Blake Howell
 */
public class InvalidApiParameterException extends ParameterCheckException {

  public InvalidApiParameterException(String paramName) {
    super("The parameter provided was invalid for: " + paramName + ".", paramName, false);
  }

  public InvalidApiParameterException(String paramName, String reasonForInvalidity) {
    super("The parameter provided was invalid for: " + paramName + ". Because, " + reasonForInvalidity, paramName, false);
  }

}
