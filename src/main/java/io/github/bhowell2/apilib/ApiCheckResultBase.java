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
	public final String keyName;
	public final ApiParamError apiParamError;

	protected ApiCheckResultBase(String keyName) {
		this.keyName = keyName;
		this.apiParamError = null;
	}

	protected ApiCheckResultBase(ApiParamError apiParamError) {
		// must enforce this for successful and failed methods below to return correctly
		if (apiParamError == null) {
			throw new IllegalArgumentException("Cannot create failed check result with null.");
		}
		this.apiParamError = apiParamError;
		// keyName should be obtained in the ApiParamError
		this.keyName = null;
	}

	public boolean failed() {
		return apiParamError != null;
	}

	public boolean successful() {
		return apiParamError == null;
	}

	/**
	 * Will have an error tuple if the parameter check fails, otherwise it is null.
	 *
	 * @return ErrorTuple containing the parameter name, error message, and error type.
	 */
	public ApiParamError getApiParamError() {
		return apiParamError;
	}


	/**
	 * In the case of success a keyName is only guaranteed if this extends
	 * {@link ApiNamedCheckResultBase}
	 * @return name of successfully checked parameter
	 */
	public String getKeyName() {
		return keyName;
	}

}
