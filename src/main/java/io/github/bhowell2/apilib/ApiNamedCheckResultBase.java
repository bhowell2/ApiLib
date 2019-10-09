package io.github.bhowell2.apilib;

/**
 * Just ensures check result comes with a key name.
 * @author Blake Howell
 */
public class ApiNamedCheckResultBase extends ApiCheckResultBase {

	protected ApiNamedCheckResultBase(String keyName) {
		super(keyName);
		if (this.keyName == null) {
			throw new IllegalArgumentException("Cannot created named check result with null keyName.");
		}
	}

	protected ApiNamedCheckResultBase(ApiParamError apiParamError) {
		super(apiParamError);
	}

}
