package io.bhowell2.ApiLib;


/**
 *
 * @author Blake Howell
 */
public final class ParamError {

    public final String parameterName;
    public final ErrorType errorType;
    public final String errorMessage;

    public ParamError(ErrorType type, String errorMessage, String parameterName) {
        this.errorType = type;
        this.parameterName = parameterName;
        this.errorMessage = errorMessage;
    }

}
