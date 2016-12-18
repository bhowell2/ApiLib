package io.bhowell2.ApiLib.exceptions;

/**
 * @author Blake Howell
 */
public class ApiVersionFormatException extends IllegalArgumentException {

  public ApiVersionFormatException(String errorMessage) {
    super(errorMessage);
  }

}
