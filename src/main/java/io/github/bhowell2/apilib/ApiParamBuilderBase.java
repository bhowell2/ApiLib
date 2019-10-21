package io.github.bhowell2.apilib;

/**
 * Builders extend this to provide some default functionality.
 * @author Blake Howell
 */
public class ApiParamBuilderBase<T extends ApiParamBuilderBase> {

	String keyName, displayName, invalidErrorMessage;
	boolean canBeNull = false;

	protected ApiParamBuilderBase(String keyName, String displayName) {
		this.keyName = keyName;
		this.displayName = displayName;
	}

	@SafeVarargs
	protected static <T> void checkVarArgsNotNullAndValuesNotNull(T... args) {
		if (args == null) {
			throw new IllegalArgumentException("Cannot add null parameters.");
		}
		for (T a : args) {
			if (a == null) {
				throw new IllegalArgumentException("Cannot add null in var args array.");
			}
		}
	}

	/**
	 * Set whether or not the value of the parameter can be SET to null.
	 * Note there is a difference between a parameter being set to null
	 * and just not being provided. In the case of {@link java.util.Map}
	 * {@link java.util.Map#get(Object)} returns null if nothing has been
	 * set for the given key OR if the value for the key has been SET to
	 * null; to determine whether this was SET to null or just does not
	 * exists {@link java.util.Map#containsKey(Object)} is used.
	 */
	@SuppressWarnings("unchecked")
	public T setCanBeNull(boolean canBeNull) {
		this.canBeNull = canBeNull;
		return (T) this;
	}

	/**
	 * Sets an error message to be returned that overrides the failure message
	 * from {@link io.github.bhowell2.apilib.checks.Check.Result} or even the
	 * error message of {@link ApiParamError} in the case of a container
	 * parameter such as {@link ApiMapParam} or
	 * {@link ApiArrayOrListParam}/{@link ApiListParam}. This can be useful when the
	 * user wants to return all of the requirements for a parameter in a single
	 * string, rather than only what caused the failure with the current request.
	 */
	@SuppressWarnings("unchecked")
	public T setInvalidErrorMessage(String errorMessage) {
		this.invalidErrorMessage = errorMessage;
		return (T) this;
	}

}
