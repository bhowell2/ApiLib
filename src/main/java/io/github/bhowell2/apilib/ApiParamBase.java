package io.github.bhowell2.apilib;


/**
 * Base that all parameters in ApiLib extend.
 * @author Blake Howell
 */
abstract class ApiParamBase<In, Result> implements ApiParam<In, Result> {

	public final String keyName, displayName, invalidErrorMessage;
	public final boolean canBeNull;

	public static class Builder<B extends Builder<B>> {

		String keyName, displayName, invalidErrorMessage;
		boolean canBeNull = false;

		public Builder(String keyName) {
			this.keyName = keyName;
		}

		/**
		 * Used by extending classes to check varargs.
		 */
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
		 *
		 * @param displayName
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public B setDisplayName(String displayName) {
			this.displayName = displayName;
			return (B) this;
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
		public B setCanBeNull(boolean canBeNull) {
			this.canBeNull = canBeNull;
			return (B) this;
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
		public B setInvalidErrorMessage(String errorMessage) {
			this.invalidErrorMessage = errorMessage;
			return (B) this;
		}

	}

	protected ApiParamBase(Builder builder) {
		this.keyName = builder.keyName;
		this.displayName = builder.displayName;
		this.invalidErrorMessage = builder.invalidErrorMessage;
		this.canBeNull = builder.canBeNull;
	}

	public ApiParamBase(String keyName, String displayName, String invalidErrorMessage, boolean canBeNull) {
		this.keyName = keyName;
		this.displayName = displayName;
		this.invalidErrorMessage = invalidErrorMessage;
		this.canBeNull = canBeNull;
	}

}
