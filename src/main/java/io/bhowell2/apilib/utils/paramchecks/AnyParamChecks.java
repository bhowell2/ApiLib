package io.bhowell2.apilib.utils.paramchecks;

import io.bhowell2.apilib.CheckFunc;
import io.bhowell2.apilib.CheckFuncResult;

/**
 * Checks that do not care about the parameter type.
 *
 * @author Blake Howell
 */
public class AnyParamChecks {

	/**
	 * When the parameter just needs to be provided, but does not have any real requirements.
	 *
	 * @param <T>
	 * @return a successful CheckFunctionTuple
	 */
	public static <T> CheckFunc<T> alwaysPass() {
		return (T t) -> CheckFuncResult.success();
	}

	public static final CheckFunc<?> ALWAYS_PASS = t -> CheckFuncResult.success();

	/**
	 * When a failure should occur when the parameter is provided.
	 *
	 * @param <T>
	 * @return a failed CheckFunctionTuple
	 */
	public static <T> CheckFunc<T> alwaysFail() {
		return (T t) -> CheckFuncResult.failure();
	}

	public static final CheckFunc<?> ALWAYS_FAIL = t -> CheckFuncResult.failure();

}
