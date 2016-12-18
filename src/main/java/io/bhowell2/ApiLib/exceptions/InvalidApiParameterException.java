package io.bhowell2.ApiLib.exceptions;

/**
 * @author Blake Howell
 */
public class InvalidApiParameterException extends ParameterCheckException {

  public InvalidApiParameterException(String invalidParamName) {
    super("The parameter provided was invalid for: " + invalidParamName + ".");
  }

  public InvalidApiParameterException(String invalidParamName, String reasonForInvalidity) {
    super("The parameter provided was invalid for: " + invalidParamName + ". Because, " + reasonForInvalidity);
  }

}
