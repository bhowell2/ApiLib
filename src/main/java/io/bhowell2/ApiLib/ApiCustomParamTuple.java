package io.bhowell2.ApiLib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Blake Howell
 */
public class ApiCustomParamTuple {

    public final String customParameterName;                        // name of custom parameter to put in the ApiObjectParamTuple#providedCustomParams map
    public final Set<String> providedParamNames;                    // name of parameters successfully checked/provided
    public final Map<String, ApiObjectParamTuple> providedObjParams;
    public final List<ApiObjectParamTuple> arrayObjParams;       // if objects are nested within this object
    public final ErrorTuple errorTuple;

//    public ApiCustomParamTuple(String customParameterName, List<String> providedParamNames,
//                               Map<String, ApiObjectParamTuple> providedParamObjects, List<ApiObjectParamTuple> arrayParamObjects) {
//        this.customParameterName = customParameterName;
//        if (providedParamNames == null) {
//            this.providedParamNames = new HashSet<>(0);
//        } else {
//            this.providedParamNames = new HashSet<>(providedParamNames);
//        }
//        if (providedParamObjects == null) {
//            this.providedParamObjects = new HashMap<>(0);
//        } else {
//            this.providedParamObjects = providedParamObjects;
//        }
//        if (arrayParamObjects == null) {
//            this.arrayParamObjects = new ArrayList<>(0);
//        } else {
//            this.arrayParamObjects = arrayParamObjects;
//        }
//        this.errorTuple = null;
//    }

    public ApiCustomParamTuple(String customParameterName, Set<String> providedParamNames,
                               Map<String, ApiObjectParamTuple> providedObjParams, List<ApiObjectParamTuple> arrayObjParams) {
        this.customParameterName = customParameterName;
        if (providedParamNames == null) {
            this.providedParamNames = new HashSet<>(0);
        } else {
            this.providedParamNames = providedParamNames;
        }
        if (providedObjParams == null) {
            this.providedObjParams = new HashMap<>(0);
        } else {
            this.providedObjParams = providedObjParams;
        }
        if (arrayObjParams == null) {
            this.arrayObjParams = new ArrayList<>(0);
        } else {
            this.arrayObjParams = arrayObjParams;
        }
        this.errorTuple = null;
    }


    public ApiCustomParamTuple(ErrorTuple errorTuple) {
        this.errorTuple = errorTuple;
        this.customParameterName = null;
        this.arrayObjParams = null;
        this.providedParamNames = null;
        this.providedObjParams = null;
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
        return new ApiCustomParamTuple(customParameterName, null, null, null);
    }

    /**
     *
     * @param customParameterName
     * @param providedParamNames
     * @param arrayParamObjects
     * @return
     */
    public static ApiCustomParamTuple success(String customParameterName,
                                              Set<String> providedParamNames,
                                              Map<String, ApiObjectParamTuple> providedParamObjects,
                                              List<ApiObjectParamTuple> arrayParamObjects) {
        return new ApiCustomParamTuple(customParameterName, providedParamNames, providedParamObjects, arrayParamObjects);
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
