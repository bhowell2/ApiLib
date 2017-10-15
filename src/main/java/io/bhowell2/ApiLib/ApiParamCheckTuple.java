package io.bhowell2.ApiLib;

/**
 * The return from a parameter check. If the parameter checks out, the parameter name is returned; otherwise, an error is returned. If the returned
 * error is not of type {@link io.bhowell2.ApiLib.exceptions.SafeParameterCheckException} then its error message should probably not be returned to
 * the end-user.
 *
 * @author Blake Howell
 */
public final class ApiParamCheckTuple {

  public final String parameterName;
  public final ErrorTuple errorTuple;

  public ApiParamCheckTuple(String parameterName) {
    this(parameterName, null);
  }

  public ApiParamCheckTuple(String parameterName, ErrorTuple errorTuple) {
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

  public static ApiParamCheckTuple success(String parameterName) {
    return new ApiParamCheckTuple(parameterName);
  }

  public static ApiParamCheckTuple missingParameterFailure(String missingParamName) {
    return new ApiParamCheckTuple(missingParamName, new ErrorTuple(ErrorType.MISSING_PARAMETER, "Missing parameter name: " +
        missingParamName + ".", missingParamName));
  }

  public static ApiParamCheckTuple invalidParameterFailure(String parameterName, String failureReason) {
    return new ApiParamCheckTuple(parameterName, new ErrorTuple(ErrorType.INVALID_PARAMETER, failureReason, parameterName));
  }

  public static ApiParamCheckTuple parameterCastException(String parameterName, Class<?> clazz) {
    String castMessage = "Failed to cast " + parameterName + " to " + clazz.getTypeName() + ".";
    return new ApiParamCheckTuple(parameterName, new ErrorTuple(ErrorType.PARAMETER_CAST, castMessage, parameterName));
  }

}
