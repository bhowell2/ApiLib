package io.bhowell2.ApiLib.utils;

import io.bhowell2.ApiLib.CheckFunction;
import io.bhowell2.ApiLib.CheckFunctionTuple;

/**
 * Generic parameter checks.
 * @author Blake Howell
 */
public final class ParameterChecks {

    public static final CheckFunction<?> ALWAYS_PASS = (any) -> CheckFunctionTuple.success();

    public static final CheckFunction<?> ALWAYS_FAIL = (any) -> CheckFunctionTuple.failure();

}
