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

    public ApiCustomParamsTuple(String... providedParameterNames) {
        this(new ArrayList<String>(Arrays.asList(providedParameterNames)), null, null);
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
