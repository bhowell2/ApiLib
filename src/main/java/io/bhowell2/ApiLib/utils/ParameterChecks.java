package io.bhowell2.ApiLib.utils;

import io.bhowell2.ApiLib.CheckFunction;
import io.bhowell2.ApiLib.CheckFunctionTuple;

/**
 * Generic parameter checks.
 * @author Blake Howell
 */
public class ParameterChecks {

    /**
     * When the parameter just needs to be provided, but does not have any real requirements.
     * @param <T>
     * @return a successful CheckFunctionTuple
     */
    public static <T> CheckFunction<T> alwaysPass() {
        return (T t) -> CheckFunctionTuple.success();
    }

    /**
     * When a failure should occur when the parameter is provided.
     * @param <T>
     * @return a failed CheckFunctionTuple
     */
    public static <T> CheckFunction<T> alwaysFail() {
        return (T t) -> CheckFunctionTuple.failure();
    }

}
