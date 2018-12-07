package io.bhowell2.apilib.utils;

import io.bhowell2.apilib.CheckFunc;
import io.bhowell2.apilib.CheckFuncResult;

/**
 * Generic parameter checks.
 * @author Blake Howell
 */
public class ParamChecks {

    /**
     * When the parameter just needs to be provided, but does not have any real requirements.
     * @param <T>
     * @return a successful CheckFunctionTuple
     */
    public static <T> CheckFunc<T> alwaysPass() {
        return (T t) -> CheckFuncResult.success();
    }

    /**
     * When a failure should occur when the parameter is provided.
     * @param <T>
     * @return a failed CheckFunctionTuple
     */
    public static <T> CheckFunc<T> alwaysFail() {
        return (T t) -> CheckFuncResult.failure();
    }

}
