package io.bhowell2.ApiLib;

import java.util.List;

/**
 * @author Blake Howell
 */
public final class ErrorTuple {

  final String parameterName;
  final ErrorType errorType;
  final String errorMessage;

  public ErrorTuple(String parameterName, ErrorType type) {
    this(parameterName, type, null);
  }

  public ErrorTuple(ErrorType type, String errorMessage) {
    this(null, type, errorMessage);
  }

  public ErrorTuple(String parameterName, ErrorType type, String errorMessage) {
    this.parameterName = parameterName;
    this.errorType = type;
    this.errorMessage = errorMessage;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public ErrorType getErrorType() {
    return errorType;
  }

  public String getParameterName() {
    return parameterName;
  }

}
