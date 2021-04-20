package io.github.bhowell2.apilib.checks;

/**
 * @author Blake Howell
 */
public final class BooleanChecks {

	private BooleanChecks() {}

	/**
	 * Check to ensure the Boolean value is always true.
	 */
	public static final Check<Boolean> ALWAYS_TRUE = b -> b.equals(Boolean.TRUE)
		?
		Check.Result.success()
		:
		Check.Result.failure("Must be 'true'.");

	/**
	 * Check to ensure the Boolean value is always false.
	 */
	public static final Check<Boolean> ALWAYS_FALSE = b -> b.equals(Boolean.FALSE)
		?
		Check.Result.success()
		:
		Check.Result.failure("Must be 'false'.");

	/**
	 * Check to ensure the parameter is a Boolean.
	 */
	public static final Check<Boolean> IS_BOOLEAN = Check.alwaysPass(Boolean.class);

}
