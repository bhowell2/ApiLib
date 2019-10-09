package io.github.bhowell2.apilib;

import io.github.bhowell2.apilib.checks.Check;
import io.github.bhowell2.apilib.checks.DoubleChecks;
import io.github.bhowell2.apilib.checks.StringChecks;
import io.github.bhowell2.apilib.formatters.Formatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to ensure {@link ApiSingleParam} is working as expected.
 *
 * @author Blake Howell
 */
public class ApiSingleParamTests {

	public static final String USERNAME_KEY = "username";

	// arbitrary username
	public static final ApiSingleParam<String> USERNAME_PARAM =
		ApiSingleParamBuilder.builder(USERNAME_KEY, String.class)
		                     .addCheck(StringChecks.doesNotBeginWithCodePoints("abc"))
		                     .addCheck(StringChecks.lengthGreaterThan(3))
		                     .build();

	Map<String, Object> passingParams, failingParams;

	@BeforeEach
	public void beforeEachApiSingleParamTest() {
		passingParams = new HashMap<>();
		passingParams.put(USERNAME_KEY, "defg");  // cannot begin with 'abc' and must be longer that 3 chars

		failingParams = new HashMap<>();
		failingParams.put(USERNAME_KEY, "azz");   // cannot begin with 'a', 'b', or 'c'. will fail.
	}

	@Test
	public void shouldThrowExceptionCreatingOrBuilding() throws Exception {
		assertThrows(RuntimeException.class, () -> {
			ApiSingleParamBuilder.builder(null, String.class)
			                     .addCheck(Check.alwaysPass(String.class))
			                     .build();
		});
		assertThrows(RuntimeException.class, () -> {
			ApiSingleParamBuilder.builder("whatever", String.class)
			                     .addCheck(null)
			                     .build();
		});
		assertThrows(RuntimeException.class, () -> {
			ApiSingleParamBuilder.builder("whatever", String.class)
			                     .build();
		});
	}

	@Test
	public void shouldSuccessfullyCheckParameter() throws Exception {
		ApiSingleParamCheckResult checkResult = USERNAME_PARAM.check(passingParams);
		assertTrue(checkResult.successful());
		assertFalse(checkResult.failed());
		assertEquals(checkResult.keyName, USERNAME_KEY);
	}

	@Test
	public void shouldFailToCheckParameter() throws Exception {
		ApiSingleParamCheckResult checkResult = USERNAME_PARAM.check(failingParams);
		assertTrue(checkResult.failed());
		assertFalse(checkResult.successful());
		assertEquals(USERNAME_KEY, checkResult.apiParamError.keyName);
		assertEquals(USERNAME_KEY, checkResult.apiParamError.displayName,
		             "Display name should be same as key name if no display name was provided.");
	}

	@Test
	public void shouldCopyParamWithBuilder() throws Exception {
		// Copy the original test parameter and add on a check to make sure it works as intended.
		String localTestKey = "thistestkey";
		ApiSingleParam<String> copiedParam = ApiSingleParamBuilder.builder(localTestKey, "diff", USERNAME_PARAM)
		                                                          .addCheck(StringChecks.doesNotBeginWithCodePoints("defg"))
		                                                          .build();

		// should fail with this (passed in above tests, but added 'defg' to cannot begin with
		Map<String, Object> localFailingParams = new HashMap<>();
		localFailingParams.put(localTestKey, "dzz");
		ApiSingleParamCheckResult checkResultFailing = copiedParam.check(localFailingParams);
		assertTrue(checkResultFailing.failed());
		assertFalse(checkResultFailing.successful());
		assertEquals(localTestKey, checkResultFailing.apiParamError.keyName);
		assertEquals("diff", checkResultFailing.apiParamError.displayName);
		assertEquals(ApiErrorType.INVALID_PARAMETER, checkResultFailing.apiParamError.errorType);

		Map<String, Object> localPassingParams = new HashMap<>();
		localPassingParams.put(localTestKey, "hijk");
		ApiSingleParamCheckResult checkResultPassing = copiedParam.check(localPassingParams);
		assertTrue(checkResultPassing.successful());
		assertFalse(checkResultPassing.failed());
		assertEquals(localTestKey, checkResultPassing.keyName);


		// should ignore key1 (just a sanity check)
		ApiSingleParamCheckResult checkResultFailing2 = copiedParam.check(passingParams);
		assertTrue(checkResultFailing2.failed());
		assertFalse(checkResultFailing2.successful());
		assertEquals(localTestKey, checkResultFailing2.apiParamError.keyName);
		assertEquals("diff", checkResultFailing2.apiParamError.displayName);
		assertEquals(ApiErrorType.MISSING_PARAMETER, checkResultFailing2.apiParamError.errorType);
	}


	// checks that parameter is formatted when the checks pass and NOT formatted when the checks fail
	@Test
	public void shouldFormatAndCheckParameter() throws Exception {
		// make sure format was re-inserted into map
		String localKey = "alocalkey";
		Map<String, Object> failingParams = new HashMap<>();
		failingParams.put(localKey, "14.99");

		/*
		* This will format the parameter by first converting the string to a double and then adding 0.02 to
		* the double. This ensures that multiple formatters are run and that they are run in the order they
		* are added.
		* */
		ApiSingleParam<Double> doubleParam = ApiSingleParamBuilder
			.builder(localKey, "disp name", Double.class)
			.addFormatter((String s) -> Formatter.Result.success(Double.parseDouble(s)))
			.addCheck(DoubleChecks.valueGreaterThan(15.01))
			.addFormatter((Double d) -> Formatter.Result.success(d + 0.02))
			.build();

		ApiSingleParamCheckResult checkResultFailing = doubleParam.check(failingParams);
		assertTrue(checkResultFailing.failed());
		assertFalse(checkResultFailing.successful());
		assertEquals(localKey, checkResultFailing.apiParamError.keyName);
		assertEquals("disp name", checkResultFailing.apiParamError.displayName);
		assertEquals(ApiErrorType.INVALID_PARAMETER, checkResultFailing.apiParamError.errorType);
		assertEquals("14.99", failingParams.get(localKey),
		             "Parameter should not have been put back in map after formatting. Only after successful check.");

		Map<String, Object> passingParams = new HashMap<>();
		passingParams.put(localKey, "15.00");

		ApiSingleParamCheckResult checkResultPassing = doubleParam.check(passingParams);
		assertTrue(checkResultPassing.successful());
		assertFalse(checkResultPassing.failed());
		assertEquals(localKey, checkResultPassing.keyName);
		assertEquals(15.02, passingParams.get(localKey),
		             "After formatting parameter should have been added back to map when check was successful.");
	}

	@Test
	public void shouldFailFormattingParameter() throws Exception {
		String key = "akey";
		String formatErrMsg = "always fail formatting!";
		Map<String, Object> params = new HashMap<>();
		params.put(key, "whatever");

		ApiSingleParam<String> singleParam = ApiSingleParamBuilder.builder(key, String.class)
		                                                          .addFormatter(o -> Formatter.Result.failure(formatErrMsg))
		                                                          .addCheck(Check.alwaysPass(String.class))
		                                                          .build();

		ApiSingleParamCheckResult checkResult = singleParam.check(params);
		assertTrue(checkResult.failed());
		assertFalse(checkResult.successful());
		assertEquals(key, checkResult.apiParamError.keyName);
		assertEquals(key, checkResult.apiParamError.displayName,
		             "Display name should be same as key name if no display name was provided.");
		assertEquals(ApiErrorType.FORMAT_ERROR, checkResult.apiParamError.errorType);
		assertTrue(checkResult.apiParamError.hasErrorMessage());
		assertEquals(formatErrMsg, checkResult.apiParamError.errorMessage);
	}

	@Test
	public void shouldRunChecksInOrder() throws Exception {
		String localKey = "localKey";

		// will run 3 checks
		List<Integer> checkOrderVarArgs = new ArrayList<>(3);

		BiFunction<List<Integer>, Integer, Check<String>> createCheck = (list, i) ->
			checkValue -> {
				list.add(i);
				return Check.Result.success();
			};

		Check<String> check1VarArg = createCheck.apply(checkOrderVarArgs, 1);
		Check<String> check2VarArg = createCheck.apply(checkOrderVarArgs, 2);
		Check<String> check3VarArg = createCheck.apply(checkOrderVarArgs, 3);

		ApiSingleParam<String> localParamVarArgChecks =
			ApiSingleParamBuilder.builder(localKey, String.class)
			                     .addChecks(check1VarArg, check2VarArg, check3VarArg)
			                     .build();

		Map<String, Object> params = new HashMap<>();
		params.put(localKey, "whatever");

		ApiSingleParamCheckResult checkResultVarArgs = localParamVarArgChecks.check(params);
		assertTrue(checkResultVarArgs.successful());
		assertEquals(1, checkOrderVarArgs.get(0));
		assertEquals(2, checkOrderVarArgs.get(1));
		assertEquals(3, checkOrderVarArgs.get(2));

		// Doing same as above here, but not using var args and adding individually

		List<Integer> checkOrder = new ArrayList<>(3);

		Check<String> check1 = createCheck.apply(checkOrder, 1);
		Check<String> check2 = createCheck.apply(checkOrder, 2);
		Check<String> check3 = createCheck.apply(checkOrder, 3);

		ApiSingleParam<String> localParam =
			ApiSingleParamBuilder.builder(localKey, String.class)
			                     .addCheck(check2)
			                     .addCheck(check1)
			                     .addCheck(check3)
			                     .build();

		ApiSingleParamCheckResult checkResult = localParam.check(params);
		assertTrue(checkResult.successful());
		// note this order is different than what was added above!! should be 2,1,3
		assertEquals(2, checkOrder.get(0));
		assertEquals(1, checkOrder.get(1));
		assertEquals(3, checkOrder.get(2));

	}

	@Test
	public void shouldRunFormattersInOrder() throws Exception {
		String localKey = "localKey";

		// will run 3 checks
		List<Integer> formatOrderVarArgs = new ArrayList<>(3);

		BiFunction<List<Integer>, Integer, Formatter<?, String>> createFormatter = (list, i) ->
			checkValue -> {
				list.add(i);
				// just return the value. no changes
				return Formatter.Result.success((String)checkValue);
			};

		Formatter<?, String> formatter1VarArg = createFormatter.apply(formatOrderVarArgs, 1);
		Formatter<?, String> formatter2VarArg = createFormatter.apply(formatOrderVarArgs, 2);
		Formatter<?, String> formatter3VarArg = createFormatter.apply(formatOrderVarArgs, 3);

		ApiSingleParam<String> localParamVarArgFormatter =
			ApiSingleParamBuilder.builder(localKey, String.class)
			                     .addFormatters(formatter1VarArg, formatter3VarArg, formatter2VarArg)
			                     .addChecks(Check.alwaysPass(String.class))
			                     .build();

		Map<String, Object> params = new HashMap<>();
		params.put(localKey, "whatever");

		ApiSingleParamCheckResult checkResultVarArgFormatters = localParamVarArgFormatter.check(params);
		assertTrue(checkResultVarArgFormatters.successful());
		// note order here. formatters added in order: 1,3,2
		assertEquals(1, formatOrderVarArgs.get(0));
		assertEquals(3, formatOrderVarArgs.get(1));
		assertEquals(2, formatOrderVarArgs.get(2));

		List<Integer> formatOrder = new ArrayList<>(3);

		Formatter<?, String> formatter1 = createFormatter.apply(formatOrder, 1);
		Formatter<?, String> formatter2 = createFormatter.apply(formatOrder, 2);
		Formatter<?, String> formatter3 = createFormatter.apply(formatOrder, 3);

		ApiSingleParam<String> localParam =
			ApiSingleParamBuilder.builder(localKey, String.class)
			                     .addFormatter(formatter3)
			                     .addFormatter(formatter2)
			                     .addFormatter(formatter1)
			                     .addChecks(Check.alwaysPass(String.class))
			                     .build();

		ApiSingleParamCheckResult checkResult = localParam.check(params);
		assertTrue(checkResult.successful());
		// note order here. added backwards
		assertEquals(3, formatOrder.get(0));
		assertEquals(2, formatOrder.get(1));
		assertEquals(1, formatOrder.get(2));
	}


	@Test
	public void shouldFailWithLaterChecks() throws Exception {
		ApiSingleParam<String> localParam =
			ApiSingleParamBuilder.builder(USERNAME_PARAM)
			                     .addCheck(Check.alwaysFail())
			                     .build();

		ApiSingleParamCheckResult checkResult = localParam.check(passingParams);
		assertTrue(checkResult.failed());
	}

	@Test
	public void shouldPassWithNull() throws Exception {
		String localKey = "localKey";
		ApiSingleParam<String> localParam = ApiSingleParamBuilder.builder(localKey, String.class)
		                                                         .setCanBeNull(true)
		                                                         .addCheck(Check.alwaysPass(String.class))
		                                                         .build();

		Map<String, Object> passingNullParams = new HashMap<>();
		passingNullParams.put(localKey, null);

		ApiSingleParamCheckResult checkResult = localParam.check(passingNullParams);
		assertTrue(checkResult.successful());
		assertEquals(localKey, checkResult.keyName);
	}

	@Test
	public void shouldFailWithNull() throws Exception {
		String localKey = "localKey";
		ApiSingleParam<String> localParam = ApiSingleParamBuilder.builder(localKey, String.class)
		                                                         .setCanBeNull(false)
		                                                         .addCheck(Check.alwaysPass(String.class))
		                                                         .build();

		Map<String, Object> failingNullParams = new HashMap<>();
		failingNullParams.put(localKey, null);

		ApiSingleParamCheckResult checkResultInvalidFromNull = localParam.check(failingNullParams);
		assertTrue(checkResultInvalidFromNull.failed());
		assertEquals(localKey, checkResultInvalidFromNull.apiParamError.keyName);
		assertEquals(ApiErrorType.INVALID_PARAMETER, checkResultInvalidFromNull.apiParamError.errorType);

		failingNullParams.remove(localKey);
		ApiSingleParamCheckResult checkResultMissing = localParam.check(failingNullParams);
		assertTrue(checkResultMissing.failed());
		assertEquals(localKey, checkResultMissing.apiParamError.keyName);
		assertEquals(ApiErrorType.MISSING_PARAMETER, checkResultMissing.apiParamError.errorType);

	}

}
