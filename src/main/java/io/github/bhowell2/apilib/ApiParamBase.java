package io.github.bhowell2.apilib;


/**
 * Base that all parameters in ApiLib extend.
 * @author Blake Howell
 */
public abstract class ApiParamBase<In, Result> implements ApiParam<In, Result> {

	public final String keyName, displayName, invalidErrorMessage;
	public final boolean canBeNull;

	public ApiParamBase(String keyName, String displayName, String invalidErrorMessage, boolean canBeNull) {
		this.keyName = keyName;
		this.displayName = displayName;
		this.invalidErrorMessage = invalidErrorMessage;
		this.canBeNull = canBeNull;
	}

}
