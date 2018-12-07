package io.bhowell2.apilib;

/**
 * The return from a parameter check. If the parameter checks out, the parameter name is returned; otherwise, an error is returned. If the returned
 * error is not of type {@link io.bhowell2.apilib.exceptions.SafeParameterCheckException} then its error message should probably not be returned to
 * the end-user.
 *
 * @author Blake Howell
 */
public final class ApiParamCheck {

    public final String parameterName;
    public final ParamError paramError;

    public ApiParamCheck(String parameterName) {
        this.parameterName = parameterName;
        this.paramError = null;
    }

    public ApiParamCheck(ParamError paramError) {
        this.paramError = paramError;
        this.parameterName = null;
    }

    public boolean failed() {
        return paramError != null;
    }

    public boolean successful() {
        return paramError == null;
    }

    /**
     * Will have an error tuple if the parameter check fails.
     * @return ErrorTuple containing the parameter name, error message, and error type.
     */
    public ParamError getParamError() {
        return paramError;
    }

    /**
     * Will have parameter name if the parameter is successfully checked.
     * @return name of successfully checked parameter
     */
    public String getParameterName(){
        return parameterName;
    }

    /* Static creation methods */

    public static ApiParamCheck success(String parameterName) {
        return new ApiParamCheck(parameterName);
    }

    public static ApiParamCheck missingParameterFailure(String missingParamName) {
        return new ApiParamCheck(new ParamError(ErrorType.MISSING_PARAMETER, "Missing parameter: " +
            missingParamName + ".", missingParamName));
    }

    public static ApiParamCheck invalidParameterFailure(String parameterName, String failureReason) {
        return new ApiParamCheck(new ParamError(ErrorType.INVALID_PARAMETER, failureReason, parameterName));
    }

    public static ApiParamCheck parameterCastException(String parameterName, String message) {
        return new ApiParamCheck(new ParamError(ErrorType.PARAMETER_CAST, message, parameterName));
    }

}
