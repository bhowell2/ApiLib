package io.bhowell2.ApiLib;

/**
 * The return from a parameter check. If the parameter checks out, the parameter name is returned; otherwise, an error is returned. If the returned
 * error is not of type {@link io.bhowell2.ApiLib.exceptions.SafeParameterCheckException} then its error message should probably not be returned to
 * the end-user.
 *
 * @author Blake Howell
 */
public final class ApiParameterCheckTuple {

  public final String parameterName;
  public final ApiParamErrorTuple errorTuple;

  public ApiParameterCheckTuple(String parameterName) {
      this.parameterName = parameterName;
      this.errorTuple = null;
  }

  public ApiParameterCheckTuple(ApiParamErrorTuple errorTuple) {
    this.errorTuple = errorTuple;
    this.parameterName = null;
  }

  public boolean failed() {
    return errorTuple != null;
  }

  public boolean successful() {
    return errorTuple == null;
  }

  public ApiParamErrorTuple getErrorTuple() {
    return errorTuple;
  }

  public String getParameterName(){
    return parameterName;
  }

  /* Static creation methods */

  public static ApiParameterCheckTuple success(String parameterName) {
    return new ApiParameterCheckTuple(parameterName);
  }

  public static ApiParameterCheckTuple missingParameterFailure(String missingParamName) {
    return new ApiParameterCheckTuple(new ApiParamErrorTuple(ErrorType.MISSING_PARAMETER, "Missing parameter name: " +
        missingParamName + ".", missingParamName));
  }

  public static ApiParameterCheckTuple invalidParameterFailure(String parameterName, String failureReason) {
    return new ApiParameterCheckTuple(new ApiParamErrorTuple(ErrorType.INVALID_PARAMETER, failureReason, parameterName));
  }

  public static ApiParameterCheckTuple parameterCastException(String parameterName, String message) {
    return new ApiParameterCheckTuple(new ApiParamErrorTuple(ErrorType.PARAMETER_CAST, message, parameterName));
  }

}
