package io.github.bhowell2.apilib;


import io.github.bhowell2.apilib.errors.ApiParamError;

import java.util.List;

/**
 * Base that all parameters in ApiLib extend, provides common functionality.
 * @author Blake Howell
 */
public abstract class ApiParamBase<In, Result extends ApiParamBase.Result> implements ApiParam<In, Result> {

	public final String keyName, displayName, invalidErrorMessage;
	public final boolean canBeNull;

	/**
	 * Common builder for all implemented parameter types.
	 * @param <B> the extending builder. needed to provide common fluent functionality
	 */
	public static abstract class Builder<
		P extends ApiParamBase<?,?>,
		B extends Builder<P, B>
		> {

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
		 * Sets the name of the parameter that will be returned in {@link ApiParamError}
		 * if an error occurs with the parameter. This allows for easily retrieving a
		 * name that can be displayed to the user with an informative error message.
		 *
		 * @param displayName display name to use. default is null
		 * @return this builder
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
		 *
		 * @return this builder
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
		 * parameter such as {@link ApiMapParam} or {@link ApiCollectionParam}.
		 * This can be useful when the user wants to return all of the requirements
		 * for a parameter in a single string, rather than only what caused the
		 * failure with the current request.
		 *
		 * @return this builder
		 */
		@SuppressWarnings("unchecked")
		public B setInvalidErrorMessage(String errorMessage) {
			this.invalidErrorMessage = errorMessage;
			return (B) this;
		}

		/**
		 * Build the parameter of type {@code P}.
		 */
		public abstract P build();

	}

	protected static <T> boolean arrayIsNotNullOrEmpty(T[] array) {
		return array != null && array.length > 0;
	}

	/**
	 * Used by various extending classes to test whether or not a list is
	 * empty and/or empty to avoid accessing null or empty lists.
	 * @return true of the list is not null and not empty
	 */
	protected static boolean listIsNotNullOrEmpty(List<?> list) {
		return list != null && list.size() > 0;
	}

	@SuppressWarnings("unchecked")
	protected static <T> T[] getArrayIfNotNullOrEmpty(List<T> list, T[] emptyCollection) {
		return listIsNotNullOrEmpty(list)
			? list.toArray(emptyCollection)
			: null;
	}

	protected ApiParamBase(Builder<?, ?> builder) {
		this.keyName = builder.keyName;
		this.displayName = builder.displayName;
		this.invalidErrorMessage = builder.invalidErrorMessage;
		this.canBeNull = builder.canBeNull;
	}

	/**
	 * The result returned from {@link ApiParam#check(Object)}. This is just common
	 * functionality shared by all param results. Basically a keyName should be returned
	 * (except, perhaps, for the result of {@link ApiCustomParam}) or an
	 * {@link ApiParamError} should be returned which, of course, indicates an error
	 * occurred.
	 */
	public static abstract class Result {

		public final String keyName;
		public final ApiParamError error;

		protected Result(String keyName) {
			this.keyName = keyName;
			this.error = null;
		}

		protected Result(ApiParamError error) {
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
		 * Returns the key name for this check result. Not all parameters
		 * results will have a key name (e.g., root level ApiMapParams or
		 * inner ApiCollectionParams).
		 * Can use {@link #hasKeyName()} to ensure this will not be null
		 * if desired.
		 *
		 * @return name of successfully checked parameter - may be null
		 */
		public String getKeyName() {
			return keyName;
		}

	}

}
