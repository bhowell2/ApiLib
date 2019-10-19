package io.github.bhowell2.apilib;

/**
 * Provides base functionality for a parameter's check result.
 * @author Blake Howell
 */
public abstract class ApiCheckResultBase {

	/*
	* For ApiSingleParams only the keyName is relevant as it is the "provided param name" if
	* the check was successful.
	*
	* For ApiCustomParams the keyName may or may not be the actual name of a parameter and it
	* is not treated as such, the 'providedParamNames' field is used for this purpose.
	*
	* For ApiMapParams the keyName is the name of the map if it is a nested map, otherwise it
	* is the root_key_name.
	* */
	/**
	 * The key name may be null.
	 */
	public final String keyName;
	public final ApiParamError error;

	protected ApiCheckResultBase(String keyName) {
		this.keyName = keyName;
		this.error = null;
	}

	protected ApiCheckResultBase(ApiParamError error) {
		// must enforce this for successful and failed methods below to return correctly
		if (error == null) {
			throw new IllegalArgumentException("Cannot create failed check result with null.");
		}
		this.error = error;
		// keyName should be obtained in the ApiParamError
		this.keyName = null;
	}

	public boolean failed() {
		return error != null;
	}

	public boolean successful() {
		return error == null;
	}

	/**
	 * Will have an error tuple if the parameter check fails, otherwise it is null.
	 *
	 * @return ErrorTuple containing the parameter name, error message, and error type.
	 */
	public ApiParamError getError() {
		return error;
	}

	/**
	 * @return whether or not the key name has been set (is not null) for this check result
	 */
	public boolean hasKeyName() {
		return this.keyName != null;
	}

	/**
	 * Returns the key name for this check result. The top level
	 * {@link ApiMapCheckResult} will not have a key name (is null)
	 * as the top level {@link ApiMapParam} does not have a name.
	 * Can use {@link #hasKeyName()} to ensure this will not be null
	 * if desired.
	 *
	 * @return name of successfully checked parameter - may be null
	 */
	public String getKeyName() {
		return keyName;
	}

}
