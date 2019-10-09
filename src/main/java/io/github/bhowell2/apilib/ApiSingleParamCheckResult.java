package io.github.bhowell2.apilib;

import java.util.Map;

/**
 * The result returned from {@link ApiSingleParam#check(Map)}. This is only used for a
 * single parameter's keyName. If multiple parameters need to be checked at once the
 * user should use {@link ApiCustomParam} which allows for returning multiple parameter
 * keyNames in {@link ApiCustomParamCheckResult}.
 * @author Blake Howell
 */
public class ApiSingleParamCheckResult extends ApiNamedCheckResultBase {
	
	public ApiSingleParamCheckResult(String keyName) {
		super(keyName);
	}

	public ApiSingleParamCheckResult(ApiParamError apiParamError) {
		super(apiParamError);
	}

	/* Static creation methods */

	public static ApiSingleParamCheckResult success(String keyName) {
		return new ApiSingleParamCheckResult(keyName);
	}

	public static ApiSingleParamCheckResult failure(ApiParamError apiParamError) {
		return new ApiSingleParamCheckResult(apiParamError);
	}
	
}
