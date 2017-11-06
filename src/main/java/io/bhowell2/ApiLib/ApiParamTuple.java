package io.bhowell2.ApiLib;

/**
 * The return from a parameter check. If the parameter checks out, the parameter name is returned; otherwise, an error is returned. If the returned
 * error is not of type {@link io.bhowell2.ApiLib.exceptions.SafeParameterCheckException} then its error message should probably not be returned to
 * the end-user.
 *
 * @author Blake Howell
 */
public final class ApiParamTuple {

  public final String parameterName;
  public final ErrorTuple errorTuple;

  public ApiParamTuple(String parameterName) {
      this.parameterName = parameterName;
      this.errorTuple = null;
  }

  public ApiParamTuple(ErrorTuple errorTuple) {
    this.errorTuple = errorTuple;
    this.parameterName = null;
  }

  public boolean failed() {
    return errorTuple != null;
  }

  public boolean successful() {
    return errorTuple == null;
  }

  public ErrorTuple getErrorTuple() {
    return errorTuple;
  }

  public String getParameterName(){
    return parameterName;
  }

  /* Static creation methods */

  public static ApiParamTuple success(String parameterName) {
    return new ApiParamTuple(parameterName);
  }

  public static ApiParamTuple missingParameterFailure(String missingParamName) {
    return new ApiParamTuple(new ErrorTuple(ErrorType.MISSING_PARAMETER, "Missing parameter name: " +
        missingParamName + ".", missingParamName));
  }

  public static ApiParamTuple invalidParameterFailure(String parameterName, String failureReason) {
    return new ApiParamTuple(new ErrorTuple(ErrorType.INVALID_PARAMETER, failureReason, parameterName));
  }

  public static ApiParamTuple parameterCastException(String parameterName, String message) {
    return new ApiParamTuple(new ErrorTuple(ErrorType.PARAMETER_CAST, message, parameterName));
  }

}
