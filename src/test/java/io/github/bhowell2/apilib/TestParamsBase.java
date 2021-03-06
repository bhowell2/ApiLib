package io.github.bhowell2.apilib;

import io.github.bhowell2.apilib.checks.StringChecks;
import org.junit.jupiter.api.BeforeEach;

import java.util.HashMap;
import java.util.Map;

/**
 * Tests can extend this (or use the static fields) to obtain pre-set parameters for their tests.
 *
 * Provides a full implementation that can be used with
 *
 * The full object looks like this (JSON):
 *
 * <pre></pre>
 *
 * <pre>{@code
 * {
 *   "username": "longer than 3 chars and (arbitrarily) does not begin with 'a','b', or 'c'",
 *   "password": "longer than 5 chars, must have a 0-9 digit",
 *   "contact_email": "must match email format .+@.+\\..+",
 *   "pi": 3.1415962,
 *   "shipping_addresses": [{...}, {...}]
 * }
 * }</pre>
 *
 *
 * @author Blake Howell
 */
public abstract class TestParamsBase {

	/* SINGLE PARAMS */

	public static final String USERNAME_KEY = "username";
	public static final String EMAIL_KEY = "email";
	public static final String PASSWORD_KEY = "password";

	public static final ApiSingleParam<String> USERNAME_PARAM =
		ApiSingleParam.builder(USERNAME_KEY, String.class)
		                     .addChecks(StringChecks.doesNotBeginWithCodePoints("abc"))
		                     .addChecks(StringChecks.lengthGreaterThan(3))
		                     .build();

	public static final ApiSingleParam<String> EMAIL_PARAM =
		ApiSingleParam.builder(EMAIL_KEY, String.class)
		                     .addChecks(StringChecks.MATCHES_BASIC_EMAIL_PATTERN)
		                     .build();

	public static final ApiSingleParam<String> PASSWORD_PARAM =
		ApiSingleParam.builder(PASSWORD_KEY, String.class)
		                     .addChecks(StringChecks.lengthGreaterThan(5),
		                                StringChecks.containsCodePointsInRange(1, "0", "9", false))
		                     .build();


	/* CUSTOM PARAMS */

	public static final String CUSTOM_PARAM_NAME_USERNAME_OR_EMAIL = "username_or_email";

	/* MAP PARAMS */

	// reset for each test
	Map<String, Object> passingParams, failingParams;

	@BeforeEach
	public void beforeEachTestParamsBase() {
		passingParams = new HashMap<>();
		passingParams.put(USERNAME_KEY, "defg");  // cannot begin with 'abc' and must be longer that 3 chars
		passingParams.put(EMAIL_KEY, "length greater than 3");

		failingParams = new HashMap<>();
		failingParams.put(USERNAME_KEY, "azz");   // cannot begin with 'a', 'b', or 'c'. will fail.
		failingParams.put(EMAIL_KEY, "3");
	}

}
