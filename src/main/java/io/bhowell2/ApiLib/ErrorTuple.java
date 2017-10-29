package io.bhowell2.ApiLib;


/**
 *
 * @author Blake Howell
 */
public final class ErrorTuple {

    public final String parameterName;
    public final ErrorType errorType;
    public final String errorMessage;

    public ErrorTuple(ErrorType type, String errorMessage) {
        this(type, errorMessage, null);
    }

    public ErrorTuple(ErrorType type, String errorMessage, String parameterName) {
        this.errorType = type;
        this.parameterName = parameterName;
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
