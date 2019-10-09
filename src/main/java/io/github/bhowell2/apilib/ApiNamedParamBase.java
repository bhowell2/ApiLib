package io.github.bhowell2.apilib;

/**
 * Base for parameters that require a keyName. ApiCustomParam
 * @author Blake Howell
 */
public abstract class ApiNamedParamBase<Result extends ApiCheckResultBase> extends ApiParamBase<Result> {

	public ApiNamedParamBase(String keyName, String displayName) {
		super(keyName, displayName);
		if (keyName == null) {
			throw new IllegalArgumentException("Cannot create named parameter with null keyName");
		} else if (displayName == null) {
			throw new IllegalArgumentException("Cannot create named parameter with null displayName");
		}
	}

}
