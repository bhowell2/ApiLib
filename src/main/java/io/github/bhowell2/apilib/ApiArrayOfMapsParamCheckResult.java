package io.github.bhowell2.apilib;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Blake Howell
 */
public class ApiArrayOfMapsParamCheckResult extends ApiNamedCheckResultBase {

	List<ApiMapParamCheckResult> arrayMapResults;

	public ApiArrayOfMapsParamCheckResult(String keyName, List<ApiMapParamCheckResult> arrayMapResults) {
		super(keyName);
		this.arrayMapResults = arrayMapResults != null ? arrayMapResults : new ArrayList<>();
	}

	public ApiArrayOfMapsParamCheckResult(ApiParamError apiParamError) {
		super(apiParamError);
	}

	public static ApiArrayOfMapsParamCheckResult success(String keyName, List<ApiMapParamCheckResult> arrayMapResults) {
		return new ApiArrayOfMapsParamCheckResult(keyName, arrayMapResults);
	}

	public static ApiArrayOfMapsParamCheckResult failure(ApiParamError apiParamError) {
		return new ApiArrayOfMapsParamCheckResult(apiParamError);
	}

}
