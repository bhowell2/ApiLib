package io.bhowell2.apilib.extensions.map;

import io.bhowell2.apilib.ApiObjParamBuilder;
import io.bhowell2.apilib.ParamRetrievalFunc;

import java.util.Map;

/**
 * @author Blake Howell
 */
public class ApiMapObjParamBuilder extends ApiObjParamBuilder<Map<String, Object>, Map<String, Object>> {

	/**
	 * With no parameter name the retrieval function will just return the object passed in (i.e., the map). This should be used for root obj
	 * parameters or indices of an array.
	 *
	 * @return builder
	 */
	public static ApiMapObjParamBuilder rootMapObjBuilder() {
		return new ApiMapObjParamBuilder(null, (n, m) -> m);
	}

	/**
	 * Use when the map object parameter is nested within another map object and can be retrieved by the parameter name supplied.
	 *
	 * @param innerObjParamName string used to retrieve map from within map
	 * @return builder
	 */
	@SuppressWarnings("unchecked")
	public static ApiMapObjParamBuilder innerMapObjBuilder(String innerObjParamName) {
		return new ApiMapObjParamBuilder(innerObjParamName, (innerObjName, m) -> (Map<String, Object>) m.get(innerObjName));
	}

	public static ApiMapObjParamBuilder copyFrom(ApiMapObjParam mapObjParam) {
		return copyFrom(mapObjParam.paramName, mapObjParam);
	}

	public static ApiMapObjParamBuilder copyFrom(String paramName, ApiMapObjParam mapObjParam) {
		return new ApiMapObjParamBuilder(paramName, mapObjParam);
	}

	public ApiMapObjParamBuilder(String paramName,
	                             ParamRetrievalFunc<Map<String, Object>, Map<String, Object>> retrievalFunction) {
		super(paramName, retrievalFunction);
	}

	public ApiMapObjParamBuilder(String paramName, ApiMapObjParam mapObjParam) {
		super(paramName, mapObjParam);
	}

	public ApiMapObjParamBuilder setContinueOnOptionalFailure(boolean continueOnOptionalFailure) {
		super.setContinueOnOptionalFailure(continueOnOptionalFailure);
		return this;
	}

	public ApiMapObjParamBuilder addRequiredParam(ApiMapParam<?> mapParam) {
		super.addRequiredParam(mapParam);
		return this;
	}

	public ApiMapObjParamBuilder addRequiredParams(ApiMapParam<?>... mapParam) {
		super.addRequiredParams(mapParam);
		return this;
	}

	public ApiMapObjParamBuilder addOptionalParam(ApiMapParam<?> mapParam) {
		super.addOptionalParam(mapParam);
		return this;
	}

	public ApiMapObjParamBuilder addOptionalParams(ApiMapParam<?>... mapParam) {
		super.addOptionalParams(mapParam);
		return this;
	}

	public ApiMapObjParamBuilder addRequiredObjParam(ApiMapObjParam mapObjParam) {
		super.addRequiredObjParam(mapObjParam);
		return this;
	}

	public ApiMapObjParamBuilder addRequiredObjParams(ApiMapObjParam... mapObjParams) {
		super.addRequiredObjParams(mapObjParams);
		return this;
	}

	public ApiMapObjParamBuilder addOptionalObjParam(ApiMapObjParam mapObjParam) {
		super.addOptionalObjParam(mapObjParam);
		return this;
	}

	public ApiMapObjParamBuilder addOptionalObjParams(ApiMapObjParam... mapObjParams) {
		super.addOptionalObjParams(mapObjParams);
		return this;
	}

	public ApiMapObjParamBuilder addRequiredCustomParam(ApiMapCustomParam customParam) {
		super.addRequiredCustomParam(customParam);
		return this;
	}

	public ApiMapObjParamBuilder addRequiredCustomParams(ApiMapCustomParam... customParams) {
		super.addRequiredCustomParams(customParams);
		return this;
	}

	public ApiMapObjParamBuilder addOptionalCustomParam(ApiMapCustomParam customParam) {
		super.addOptionalCustomParam(customParam);
		return this;
	}

	public ApiMapObjParamBuilder addOptionalCustomParams(ApiMapCustomParam... customParams) {
		super.addOptionalCustomParams(customParams);
		return this;
	}

	@SuppressWarnings("unchecked")
	public ApiMapObjParam build() {
		return new ApiMapObjParam(this.paramName,
		                          this.continueOnOptionalFailure,
		                          this.retrievalFunction,
		                          this.requiredParams.toArray(new ApiMapParam[0]),
		                          this.optionalParams.toArray(new ApiMapParam[0]),
		                          this.requiredObjParams.toArray(new ApiMapObjParam[0]),
		                          this.optionalObjParams.toArray(new ApiMapObjParam[0]),
		                          this.requiredCustomParams.toArray(new ApiMapCustomParam[0]),
		                          this.optionalCustomParams.toArray(new ApiMapCustomParam[0]));
	}
}
