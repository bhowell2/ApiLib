package io.bhowell2.ApiLib;


/**
 *
 * @author Blake Howell
 */
public final class ApiParamErrorTuple {

    public final String parameterName;
    public final ErrorType errorType;
    public final String errorMessage;


    public ApiParamErrorTuple(ErrorType type, String errorMessage) {
        this(type, errorMessage, null);
    }

    public ApiParamErrorTuple(ErrorType type, String errorMessage, String parameterName) {
        this.errorType = type;
        this.parameterName = parameterName;
        this.errorMessage = errorMessage;
    }

}
