package io.bhowell2.ApiLib.utils;

import io.bhowell2.ApiLib.CheckFunction;
import io.bhowell2.ApiLib.CheckFunctionTuple;

/**
 * Generic parameter checks.
 * @author Blake Howell
 */
public class ParameterChecks {

    public static <T> CheckFunction<T> alwaysPass() {
        return (T t) -> CheckFunctionTuple.success();
    }

    public static <T> CheckFunction<T> alwaysFail() {
        return (T t) -> CheckFunctionTuple.failure();
    }

}
