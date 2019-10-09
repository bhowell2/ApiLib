package io.github.bhowell2.apilib;


/**
 * Base that all parameters should extend.
 * @author Blake Howell
 */
public abstract class ApiParamBase<Result extends ApiCheckResultBase> implements ApiParam<Result> {

	// these are only required if the class extends ApiNamedParamBase
	public final String keyName, displayName;

	public ApiParamBase(String keyName, String displayName) {
		this.keyName = keyName;
		this.displayName = displayName;
	}

}
