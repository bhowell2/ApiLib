package io.github.bhowell2.apilib.formatters;

import io.github.bhowell2.apilib.ApiLibSettings;

/**
 * @author Blake Howell
 */
@FunctionalInterface
public interface Formatter<In, Out> {

	final class Result<Out> {

		String failureMessage;    // cannot be null, will cause library to use default
		Out formattedValue;

		/**
		 * Requiring use of static creation methods below, because it can be ambiguous whether or
		 * not the format was successful if the Out type is a String.
		 */
		private Result() {}

		public boolean successful() {
			return this.failureMessage == null;
		}

		public boolean failed() {
			return this.failureMessage != null;
		}

		public boolean hasFailureMessage() {
			return this.failureMessage != null;
		}

		public Out getFormattedValue() {
			return formattedValue;
		}

		public String getFailureMessage() {
			return failureMessage;
		}

		/* Static Creation Methods */

		public static <T> Result<T> success(T formattedValue) {
			// need to set this way to avoid ambiguity if <T> is of type String
			Result<T> r = new Result<>();
			r.formattedValue = formattedValue;
			return r;
		}

		// uses type <T> to avoid casting issues for user
		public static <T> Result<T> failure() {
			return failure(ApiLibSettings.DEFAULT_FORMATTING_ERROR_MESSAGE);
		}

		/**
		 * Creates a failed format result with the provided failure message.
		 * If the failure message is null it will be set to
		 * {@link ApiLibSettings#DEFAULT_FORMATTING_ERROR_MESSAGE}.
		 */
		public static <T> Result<T> failure(String failureMessage) {
			if (failureMessage == null) {
				/*
				 * Cannot be null or would need to add a boolean to differentiate a failure
				 * from success, due to how successful and failure methods are implemented.
				 * */
				failureMessage = ApiLibSettings.DEFAULT_FORMATTING_ERROR_MESSAGE;
			}
			Result<T> r = new Result<>();
			r.failureMessage = failureMessage;
			return r;
		}

	}

	/**
	 * Takes in a parameter of type In and formats it in someway, possibly changing
	 * it to a different type of type Out.
	 * @param param the parameter to format. this will never be null.
	 * @return the successful result of formatting
	 */
	Result<Out> format(In param);

}
