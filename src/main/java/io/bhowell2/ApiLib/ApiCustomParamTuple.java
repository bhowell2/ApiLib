package io.bhowell2.ApiLib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Return for {@link ApiCustomParameter#check(Object)}. Usually will have providedParameterNames, but it's possible (especially for case of an
 * array) that there are no actually returned parameter names.
 * @author Blake Howell
 */
public class ApiCustomParamTuple {

    public final String customParameterName;                        // name of custom parameter (at least to put in ObjectParameterT)
    public final Set<String> providedParamNames;                    // name of parameters successfully checked/provided
    public final List<ApiObjectParamTuple> providedObjParams;       // if objects are nested within this object
    public final ErrorTuple errorTuple;

    public ApiCustomParamTuple(String customParameterName) {
        this(customParameterName, null, null, null);
    }

    public ApiCustomParamTuple(String customParameterName, String... providedParamNames) {
        this(customParameterName, new ArrayList<>(Arrays.asList(providedParamNames)), null);
    }

    public ApiCustomParamTuple(String customParameterName, List<String> providedParamNames, List<ApiObjectParamTuple> providedObjParams) {
        this.customParameterName = customParameterName;
        this.providedParamNames = new HashSet<>(providedParamNames);
        this.providedObjParams = providedObjParams;
        this.errorTuple = null;
    }

    public ApiCustomParamTuple(String customParameterName, Set<String> providedParamNames, List<ApiObjectParamTuple> providedObjParams) {
        this.customParameterName = customParameterName;
        this.providedParamNames = providedParamNames;
        this.providedObjParams = providedObjParams;
        this.errorTuple = null;
    }


    public ApiCustomParamTuple(ErrorTuple errorTuple) {
        this.errorTuple = errorTuple;
        this.customParameterName = null;
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
    public static ApiCustomParamTuple success(String customParameterName) {
        return new ApiCustomParamTuple(customParameterName);
    }

    public static ApiCustomParamTuple success(String customParameterName,
                                              List<String> providedParamNames,
                                              List<ApiObjectParamTuple> providedObjParams) {
        return new ApiCustomParamTuple(customParameterName, providedParamNames, providedObjParams);
    }

    public static ApiCustomParamTuple success(String customParameterName, String... parameterNames) {
        return new ApiCustomParamTuple(customParameterName, parameterNames);
    }

    public static ApiCustomParamTuple failure(ErrorTuple errorTuple) {
        return new ApiCustomParamTuple(errorTuple);
    }

    public static ApiCustomParamTuple failure(ErrorType errorType, String parameterName) {
        return failure(errorType, parameterName, null);
    }

    public static ApiCustomParamTuple failure(ErrorType errorType, String parameterName, String failureMessage) {
        return new ApiCustomParamTuple(new ErrorTuple(errorType, failureMessage, parameterName));
    }

}
