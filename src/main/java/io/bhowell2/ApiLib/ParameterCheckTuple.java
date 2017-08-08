package io.bhowell2.ApiLib;

/**
 * The return from a parameter check. If the parameter checks out, the parameter name is returned; otherwise, an error is returned. If the returned
 * error is not of type {@link io.bhowell2.ApiLib.exceptions.SafeParameterCheckException} then its error message should probably not be returned to
 * the end-user.
 *
 * @author Blake Howell
 */
public final class ParameterCheckTuple {

  final String parameterName;
  final ErrorTuple errorTuple;

  public ParameterCheckTuple(String parameterName) {
    this(parameterName, null);
  }

  public ParameterCheckTuple(String parameterName, ErrorTuple errorTuple) {
    this.parameterName = parameterName;
    this.errorTuple = errorTuple;
  }

  public boolean failed() {
    return errorTuple != null;
  }

  public boolean isSuccessful() {
    return errorTuple == null;
  }

  public ErrorTuple getErrorTuple() {
    return errorTuple;
  }

  public String getParameterName() {
    return parameterName;
  }

  /* Static creation methods */

  public static ParameterCheckTuple success(String parameterName) {
    return new ParameterCheckTuple(parameterName);
  }

  public static ParameterCheckTuple missingParameterFailure(String missingParamName) {
    return new ParameterCheckTuple(missingParamName, new ErrorTuple(missingParamName, ErrorType.MISSING_PARAMETER, "Missing parameter name: " +
        missingParamName + "."));
  }

  public static ParameterCheckTuple invalidParameterFailure(String parameterName, String failureReason) {
    return new ParameterCheckTuple(parameterName, new ErrorTuple(parameterName, ErrorType.INVALID_PARAMETER, failureReason));
  }

  public static ParameterCheckTuple parameterCastException(String parameterName, Class<?> clazz) {
    String castMessage = "Failed to cast " + parameterName + " to " + clazz.getTypeName() + ".";
    return new ParameterCheckTuple(parameterName, new ErrorTuple(parameterName, ErrorType.PARAMETER_CAST, castMessage));
  }

}
