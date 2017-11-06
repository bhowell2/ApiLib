package io.bhowell2.ApiLib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Return for {@link ApiCustomParameters#check(Object)}. Usually will have providedParameterNames, but it's possible (especially for case of an
 * array) that there are no actually returned parameter names.
 * @author Blake Howell
 */
public class ApiCustomParamsTuple {

    public final List<String> providedParamNames;                   // name of parameters successfully checked/provided
    public final List<ApiObjectParamTuple> providedObjParams;       // if objects are nested within this object
    public final List<ApiArrayParamTuple> providedArrayParams;      //
    final ErrorTuple errorTuple;

    public ApiCustomParamsTuple() {
        this(null, null, null);
    }

    public ApiCustomParamsTuple(String... providedParameterNames) {
        this(new ArrayList<>(Arrays.asList(providedParameterNames)), null, null);
    }

    public ApiCustomParamsTuple(List<String> providedParamNames, List<ApiObjectParamTuple> providedObjParams,
                                List<ApiArrayParamTuple> providedArrayParams) {
        this.errorTuple = null;
        this.providedParamNames = providedParamNames;
        this.providedObjParams = providedObjParams;
        this.providedArrayParams = providedArrayParams;
    }

    public ApiCustomParamsTuple(ErrorTuple errorTuple) {
        this.errorTuple = errorTuple;
        this.providedArrayParams = null;
        this.providedObjParams = null;
        this.providedParamNames = null;
    }

    public boolean failed() {
        return errorTuple != null;
    }

    public boolean successful() {
        return errorTuple == null;
    }

    /* Static creation methods */


    /**
     * In the case that it needs to be stated the check passed, but there is really no information to return other than a successful check.
     *
     * E.g., the user wants to know whether all values in an array pass a check:
     * 1. The user could simply implement a custom parameter and return the name of the array.
     * 2. The user could make an ApiArrayParameter, add an ApiCustomParameter check which would just pass.
     * @return
     */
    public static ApiCustomParamsTuple success() {
        return new ApiCustomParamsTuple();
    }

    public static ApiCustomParamsTuple success(List<String> providedParamNames,
                                               List<ApiObjectParamTuple> providedObjParams,
                                               List<ApiArrayParamTuple> providedArrayParams) {
        return new ApiCustomParamsTuple(providedParamNames, providedObjParams, providedArrayParams);
    }

    public static ApiCustomParamsTuple success(String... parameterNames) {
        return new ApiCustomParamsTuple(parameterNames);
    }

    public static ApiCustomParamsTuple failure(ErrorTuple errorTuple) {
        return new ApiCustomParamsTuple(errorTuple);
    }

    public static ApiCustomParamsTuple failure(ErrorType errorType, String parameterName) {
        return failure(errorType, parameterName, null);
    }

    public static ApiCustomParamsTuple failure(ErrorType errorType, String parameterName, String failureMessage) {
        return new ApiCustomParamsTuple(new ErrorTuple(errorType, failureMessage, parameterName));
    }

}
