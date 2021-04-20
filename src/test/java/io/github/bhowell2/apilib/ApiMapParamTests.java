package io.github.bhowell2.apilib;

import io.github.bhowell2.apilib.checks.Check;
import io.github.bhowell2.apilib.checks.StringChecks;
import io.github.bhowell2.apilib.errors.ApiErrorType;
import io.github.bhowell2.apilib.errors.ApiParamError;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This will contain a full example and also test ApiMapParam.
 * Tests for {@link ApiMapParam} to ensure it is working as expected.
 * The tests here will also provide a full example of using all of the features of the library.
 * This will use a real-world example of a customer's account information.
 *
 * This includes the shipping addresses the customer may have on file as well as various other fields.
 *
 * API:
 *
 * @author Blake Howell
 */
public class ApiMapParamTests {

	public static class ShippingAddress {

		public static class BodyParamNames {
			public static final String LINE1 = "line1";
			public static final String LINE2 = "line2";
			public static final String LINE3 = "line3";
			public static final String CITY = "city";
			public static final String STATE = "state";
			public static final String ZIP = "zip";
		}

		public static final ApiSingleParam<String> LINE1 =
			ApiSingleParam.builder(BodyParamNames.LINE1, String.class)
			              .addChecks(StringChecks.IS_NOT_EMPTY_OR_ONLY_WHITESPACE,
			                         StringChecks.lengthGreaterThan(2))
			              .build();

		public static final ApiSingleParam<String> LINE2 =
			ApiSingleParam.builder(BodyParamNames.LINE2, String.class)
			              .addChecks(Check.alwaysPass(String.class))
			              .build();

		public static final ApiSingleParam<String> LINE3 =
			ApiSingleParam.builder(BodyParamNames.LINE3, String.class)
			              .addChecks(Check.alwaysPass(String.class))
			              .build();

		public static final ApiSingleParam<String> CITY =
			ApiSingleParam.builder(BodyParamNames.CITY, String.class)
			              .addChecks(StringChecks.IS_NOT_EMPTY_OR_ONLY_WHITESPACE)
			              .build();

		// only allowing state abbreviation. e.g., TN
		public static final ApiSingleParam<String> STATE =
			ApiSingleParam.builder(BodyParamNames.STATE, String.class)
			              .addChecks(StringChecks.lengthEqualTo(2))
			              .build();

		public static final ApiSingleParam<String> ZIP =
			ApiSingleParam.builder(BodyParamNames.ZIP, String.class)
			              .addChecks(StringChecks.IS_NOT_EMPTY_OR_ONLY_WHITESPACE)
			              .build();

		/*
		 * There is not a single parameter named "shipping_address", but they are part of an array which
		 * is named "shipping_addresses", so it is a root/unnamed parameter.
		 * */
		public static final ApiMapParam SHIPPING_ADDRESS_MAP_PARAM =
			ApiMapParam.builder()
			           // if line2 or line 3 is supplied, but they fail for whatever reason still continue
			           .setContinueOnOptionalFailure(true)
			           .addRequiredSingleParams(LINE1, CITY, STATE, ZIP)
			           .addOptionalSingleParams(LINE2, LINE3)
			           .build();

	}

	public static Map<String, Object> generateShippingAddress(String line1, String city, String state, String zip) {
		Map<String, Object> address = new HashMap<>();
		address.put(ShippingAddress.BodyParamNames.LINE1, line1);
		address.put(ShippingAddress.BodyParamNames.CITY, city);
		address.put(ShippingAddress.BodyParamNames.STATE, state);
		address.put(ShippingAddress.BodyParamNames.ZIP, zip);
		return address;
	}

	/**
	 * Example POST API endpoint.
	 *
	 * Shipping Address (JSON form, used below in customer info):
	 * {
	 *   "line1": "longer than 3 chars",
	 *   "line2": "optionally some text",
	 *   "line3": "optionally some text",
	 *   "city": "some text",
	 *   "state": "some state initial",
	 *   "zip": 12345,
	 * }
	 *
	 * Customer Info:
	 * {
	 *   "username": "longer than 3 chars",
	 *   "password": "longer than 5 chars and contains a 0-9 digit",
	 *   "email": "optionally a valid email",
	 *   "e_billing": true|false,   (if true, must have email provided)
	 *   "phone": "some text",
	 *   "shipping_addresses": [{...}, {...}, ...]
	 * }
	 */
	public static class PostAccountInfo {

		public static class BodyParamNames {
			public static final String USERNAME = "username";
			public static final String PASSWORD = "password";
			public static final String EMAIL = "email";
			public static final String E_BILLING = "e_billing";
			public static final String PHONE = "phone";
			public static final String SHIPPING_ADDRESSES = "shipping_addresses";
		}

		public static final ApiSingleParam<String> USERNAME =
			ApiSingleParam.builder(BodyParamNames.USERNAME, String.class)
			              .addChecks(StringChecks.lengthGreaterThan(3))
			              .build();

		public static final ApiSingleParam<String> PASSWORD =
			ApiSingleParam.builder(BodyParamNames.PASSWORD, String.class)
			              .addChecks(StringChecks.lengthGreaterThan(5),
			                         StringChecks.containsCodePointsInRange(1, "0", "9", false))
			              .build();

		public static final ApiSingleParam<String> EMAIL =
			ApiSingleParam.builder(BodyParamNames.EMAIL, String.class)
			              .addChecks(StringChecks.MATCHES_BASIC_EMAIL_PATTERN)
			              .build();

		public static final ApiSingleParam<Boolean> E_BILLING =
			ApiSingleParam.builder(BodyParamNames.E_BILLING, Boolean.class)
			              .addChecks(Check.alwaysPass(Boolean.class))
			              .build();

		public static final ApiSingleParam<String> PHONE =
			ApiSingleParam.builder(BodyParamNames.PHONE, String.class)
			              .addChecks(StringChecks.lengthGreaterThanOrEqualTo(10))
			              .build();

		public static final ApiMapParamConditionalCheck REQUIRE_EMAIL_IF_E_BILLING_IS_TRUE =
			(params, checkResult) -> {
				// ebilling was provided, make sure that if it is set to true that email is provided
				if (checkResult.checkedKeyNames.contains(BodyParamNames.E_BILLING)) {
					Boolean eBilling = (Boolean) params.get(BodyParamNames.E_BILLING);
					if (eBilling && !checkResult.checkedKeyNames.contains(BodyParamNames.EMAIL)) {
						return ApiMapParamConditionalCheck.Result.failure(BodyParamNames.EMAIL,
						                                                  null,
						                                                  "Email must be provided if e-billing is set.");
					}
				}
				return ApiMapParamConditionalCheck.Result.success();
			};

		/*
		 * There is quite a bit of overhead that goes into creating a custom parameter that checks each
		 * array position for a map. If it is as simple as checking the value in each position then this
		 * */
		public static final ApiListParam<Map<String, Object>, Map<String, Object>> SHIPPING_ADDRESSES =
			ApiListParam.<Map<String, Object>, Map<String, Object>>builder(BodyParamNames.SHIPPING_ADDRESSES)
				.setIndexMapCheck(ShippingAddress.SHIPPING_ADDRESS_MAP_PARAM)
				.build();

		public static final ApiMapParam BODY_MAP_PARAM =
			ApiMapParam.builder()
			           .addRequiredSingleParams(USERNAME, PASSWORD)
			           .addOptionalSingleParams(EMAIL, PHONE, E_BILLING)
			           .addOptionalCollectionParams(SHIPPING_ADDRESSES)
			           .addConditionalChecks(REQUIRE_EMAIL_IF_E_BILLING_IS_TRUE)
			           .build();
	}

	/**
	 * This is a pretty full test that checks the PostAccountInfo API above.
	 * This includes a conditional check, ana optional collection check (with
	 * maps as indices).
	 */
	@Test
	public void shouldSuccessfullyCheckMap() throws Exception {
		String username = "user1";
		String password = "password11";
		Map<String, Object> postAccountInfoMinimalRequired = new HashMap<>();
		postAccountInfoMinimalRequired.put(PostAccountInfo.BodyParamNames.USERNAME, username);
		postAccountInfoMinimalRequired.put(PostAccountInfo.BodyParamNames.PASSWORD, password);

		ApiMapParam.Result checkResultMinimal = PostAccountInfo.BODY_MAP_PARAM.check(postAccountInfoMinimalRequired);
		assertTrue(checkResultMinimal.successful());
		assertEquals(2, checkResultMinimal.checkedKeyNames.size());
		assertTrue(checkResultMinimal.containsParameter(PostAccountInfo.BodyParamNames.USERNAME));
		assertTrue(checkResultMinimal.containsParameter(PostAccountInfo.BodyParamNames.PASSWORD));
		assertFalse(checkResultMinimal.containsParameter(PostAccountInfo.BodyParamNames.EMAIL));
		assertFalse(checkResultMinimal.containsParameter(PostAccountInfo.BodyParamNames.E_BILLING));
		assertFalse(checkResultMinimal.containsParameter(PostAccountInfo.BodyParamNames.PHONE));
		assertFalse(checkResultMinimal.containsParameter(PostAccountInfo.BodyParamNames.SHIPPING_ADDRESSES));
		assertFalse(checkResultMinimal.hasCheckedCollectionResults());
		assertFalse(checkResultMinimal.hasCheckedMapResults());

		String email = "avalidemail@gmail.com";
		Map<String, Object> postAccountInfoWithEmail = new HashMap<>();
		postAccountInfoWithEmail.putAll(postAccountInfoMinimalRequired);
		postAccountInfoWithEmail.put(PostAccountInfo.BodyParamNames.EMAIL, email);
		ApiMapParam.Result checkResultWithEmail = PostAccountInfo.BODY_MAP_PARAM.check(postAccountInfoWithEmail);
		assertTrue(checkResultWithEmail.successful());
		assertEquals(3, checkResultWithEmail.checkedKeyNames.size());
		assertTrue(checkResultWithEmail.containsParameter(PostAccountInfo.BodyParamNames.USERNAME));
		assertTrue(checkResultWithEmail.containsParameter(PostAccountInfo.BodyParamNames.PASSWORD));
		assertTrue(checkResultWithEmail.containsParameter(PostAccountInfo.BodyParamNames.EMAIL));
		assertFalse(checkResultWithEmail.containsParameter(PostAccountInfo.BodyParamNames.E_BILLING));
		assertFalse(checkResultWithEmail.containsParameter(PostAccountInfo.BodyParamNames.PHONE));
		assertFalse(checkResultWithEmail.containsParameter(PostAccountInfo.BodyParamNames.SHIPPING_ADDRESSES));
		assertFalse(checkResultWithEmail.hasCheckedCollectionResults());
		assertFalse(checkResultWithEmail.hasCheckedMapResults());

		Map<String, Object> postAccountInfoWithEbillingFalse = new HashMap<>();
		postAccountInfoWithEbillingFalse.putAll(postAccountInfoWithEmail);
		postAccountInfoWithEbillingFalse.put(PostAccountInfo.BodyParamNames.E_BILLING, false);
		ApiMapParam.Result checkResultWithEbillingFalse = PostAccountInfo.BODY_MAP_PARAM.check(postAccountInfoWithEbillingFalse);
		assertTrue(checkResultWithEbillingFalse.successful());
		assertEquals(4, checkResultWithEbillingFalse.checkedKeyNames.size());
		assertTrue(checkResultWithEbillingFalse.containsParameter(PostAccountInfo.BodyParamNames.USERNAME));
		assertTrue(checkResultWithEbillingFalse.containsParameter(PostAccountInfo.BodyParamNames.PASSWORD));
		assertTrue(checkResultWithEbillingFalse.containsParameter(PostAccountInfo.BodyParamNames.EMAIL));
		assertTrue(checkResultWithEbillingFalse.containsParameter(PostAccountInfo.BodyParamNames.E_BILLING));
		assertFalse(checkResultWithEbillingFalse.containsParameter(PostAccountInfo.BodyParamNames.PHONE));
		assertFalse(checkResultWithEbillingFalse.containsParameter(PostAccountInfo.BodyParamNames.SHIPPING_ADDRESSES));
		assertFalse(checkResultWithEbillingFalse.hasCheckedCollectionResults());
		assertFalse(checkResultWithEbillingFalse.hasCheckedMapResults());

		// adding phone number and shipping address here too
		Map<String, Object> postAccountInfoWithEbillingTrue = new HashMap<>();
		postAccountInfoWithEbillingTrue.putAll(postAccountInfoWithEmail);
		postAccountInfoWithEbillingTrue.put(PostAccountInfo.BodyParamNames.E_BILLING, true);
		postAccountInfoWithEbillingTrue.put(PostAccountInfo.BodyParamNames.PHONE, "1234567890");
		List<Map<String, Object>> shippingAddresses = new ArrayList<>();
		shippingAddresses.add(generateShippingAddress("line1", "memphis", "tn", "38117"));
		postAccountInfoWithEbillingTrue.put(PostAccountInfo.BodyParamNames.SHIPPING_ADDRESSES, shippingAddresses);

		ApiMapParam.Result checkResultWithEbillingTrue = PostAccountInfo.BODY_MAP_PARAM.check(postAccountInfoWithEbillingTrue);
		assertTrue(checkResultWithEbillingTrue.successful());
		assertEquals(6, checkResultWithEbillingTrue.checkedKeyNames.size());
		assertTrue(checkResultWithEbillingTrue.containsParameter(PostAccountInfo.BodyParamNames.USERNAME));
		assertTrue(checkResultWithEbillingTrue.containsParameter(PostAccountInfo.BodyParamNames.PASSWORD));
		assertTrue(checkResultWithEbillingTrue.containsParameter(PostAccountInfo.BodyParamNames.EMAIL));
		assertTrue(checkResultWithEbillingTrue.containsParameter(PostAccountInfo.BodyParamNames.E_BILLING));
		assertTrue(checkResultWithEbillingTrue.containsParameter(PostAccountInfo.BodyParamNames.PHONE));
		assertTrue(checkResultWithEbillingTrue.containsParameter(PostAccountInfo.BodyParamNames.SHIPPING_ADDRESSES));
		assertEquals(1, checkResultWithEbillingTrue.checkedCollectionResults.size());
		assertFalse(checkResultWithEbillingTrue.hasCheckedMapResults());

		ApiListParam.Result shippingAddressesCheckResult =
			checkResultWithEbillingTrue.checkedCollectionResults.get(PostAccountInfo.BodyParamNames.SHIPPING_ADDRESSES);

		assertTrue(shippingAddressesCheckResult.hasMapResults());
		assertEquals(1, shippingAddressesCheckResult.mapResults.size());

		ApiMapParam.Result shippingAddressCheckResult = shippingAddressesCheckResult.mapResults.get(0);

		assertNull(shippingAddressCheckResult.keyName);
		assertEquals(4, shippingAddressCheckResult.checkedKeyNames.size());
		assertFalse(shippingAddressCheckResult.hasCheckedCollectionResults());
		assertFalse(shippingAddressCheckResult.hasCheckedMapResults());
	}

	@Test
	public void testContinueOnOptionalFailure() throws Exception {
		String keyName = "akey";
		ApiSingleParam optionalParam = ApiSingleParam.builder(keyName)
		                                             .addChecks(Check.alwaysFail())
		                                             .build();
		ApiMapParam mapParam = ApiMapParam.builder()
		                                  .setContinueOnOptionalFailure(true)
		                                  .addOptionalSingleParams(optionalParam)
		                                  .build();

		Map<String, Object> requestParams = new HashMap<>();
		// havent even set the class type for the single parameter above
		requestParams.put(keyName, new Object());

		ApiMapParam.Result result = mapParam.check(requestParams);
		assertTrue(result.successful());
		assertFalse(result.hasKeyName());
		assertFalse(result.hasCheckedKeyNames());
		assertFalse(result.hasCheckedMapResults());
		assertFalse(result.hasCheckedCollectionResults());
	}

	@Test
	public void testFailOnOptionalFailure() throws Exception {
		String keyName = "akey";
		ApiSingleParam optionalParam = ApiSingleParam.builder(keyName)
		                                             .addChecks(Check.alwaysFail())
		                                             .build();
		ApiMapParam mapParam = ApiMapParam.builder()
		                                  .addOptionalSingleParams(optionalParam)
		                                  .build();

		Map<String, Object> requestParams = new HashMap<>();
		// havent even set the class type for the single parameter above
		requestParams.put(keyName, new Object());

		ApiMapParam.Result result = mapParam.check(requestParams);
		assertTrue(result.failed());
		assertEquals(ApiErrorType.INVALID_PARAMETER, result.error.errorType);
		assertEquals(keyName, result.error.keyName);
		assertFalse(result.hasKeyName());
		assertFalse(result.hasCheckedKeyNames());
		assertFalse(result.hasCheckedMapResults());
		assertFalse(result.hasCheckedCollectionResults());
	}

	@Test
	public void testContinueOnOptionalFailureWithNestedMap() throws Exception {
		String keyName = "akey";
		ApiSingleParam optionalParam = ApiSingleParam.builder(keyName)
		                                             .addChecks(Check.alwaysFail())
		                                             .build();
		String innerMapKey = "innerMapKey";
		ApiMapParam innerMapParam = ApiMapParam.builder(innerMapKey)
		                                       .setContinueOnOptionalFailure(true)
		                                       .addOptionalSingleParams(optionalParam)
		                                       .build();

		/*
		 * Note here that the inner map is required, but it has no parameters that will
		 * be successfully checked. However, the inner map will still return as being
		 * successfully checked. This means that it should be added to the root map
		 * as a successfully checked parameter.
		 * */
		ApiMapParam rootMapParam = ApiMapParam.builder()
		                                      .addRequiredMapParams(innerMapParam)
		                                      .build();

		Map<String, Object> innerMap = new HashMap<>();
		innerMap.put(keyName, new Object());

		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put(innerMapKey, innerMap);

		ApiMapParam.Result result = rootMapParam.check(requestParams);
		assertTrue(result.successful());
		assertTrue(result.hasCheckedKeyNames());
		assertTrue(result.hasCheckedMapResults());
		assertFalse(result.hasCheckedCollectionResults());
		assertFalse(result.hasCustomValues());
		assertEquals(1, result.checkedKeyNames.size());
		assertEquals(1, result.checkedMapResults.size());
		assertTrue(result.containsParameter(innerMapKey));

		// inner map should not have successfully checked any parameters
		ApiMapParam.Result innerMapResult = result.getMapResult(innerMapKey);
		assertTrue(innerMapResult.successful());
		assertEquals(innerMapKey, innerMapResult.keyName);
		assertFalse(innerMapResult.hasCheckedKeyNames());
		assertFalse(innerMapResult.hasCheckedMapResults());
		assertFalse(innerMapResult.hasCheckedCollectionResults());
		assertFalse(innerMapResult.hasCustomValues());
	}

	@Test
	public void testFailOnContinueOnOptionalFailureWithNestedMap() throws Exception {
		String keyName = "akey";
		ApiSingleParam optionalParam = ApiSingleParam.builder(keyName)
		                                             .addChecks(Check.alwaysFail())
		                                             .build();
		String innerMapKey = "innerMapKey";
		ApiMapParam innerMapParam = ApiMapParam.builder(innerMapKey)
		                                       .addOptionalSingleParams(optionalParam)
		                                       .build();
		/*
		 * Note here that the inner map is required, but it has no parameters that will
		 * be successfully checked. However, the inner map will still return as being
		 * successfully checked. This means that it should be added to the root map
		 * as a successfully checked parameter.
		 * */
		ApiMapParam rootMapParam = ApiMapParam.builder()
		                                      .addRequiredMapParams(innerMapParam)
		                                      .build();

		Map<String, Object> innerMap = new HashMap<>();
		innerMap.put(keyName, new Object());

		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put(innerMapKey, innerMap);

		ApiMapParam.Result result = rootMapParam.check(requestParams);
		assertTrue(result.failed());
		ApiParamError rootError = result.error;
		assertEquals(innerMapKey, rootError.keyName);
		assertEquals(ApiErrorType.INVALID_PARAMETER, rootError.errorType);
		assertTrue(rootError.hasChildError());

		ApiParamError innerError = rootError.childParamError;
		assertEquals(keyName, innerError.keyName);
		assertEquals(ApiErrorType.INVALID_PARAMETER, innerError.errorType);
		assertFalse(innerError.hasChildError());
	}

	@Test
	public void testRequiredMissingError() throws Exception {
		String missingParamKeyName = "whatever";
		ApiSingleParam<String> missingParam = ApiSingleParam.builder(missingParamKeyName, String.class)
		                                                    .addChecks(Check.alwaysPass(String.class))
		                                                    .build();

		ApiMapParam rootMapParam = ApiMapParam.builder()
		                                      .addRequiredSingleParams(missingParam)
		                                      .build();

		ApiMapParam.Result result = rootMapParam.check(new HashMap<>());
		assertTrue(result.failed());
		assertEquals(ApiErrorType.MISSING_PARAMETER, result.error.errorType);
		assertEquals(missingParamKeyName, result.error.keyName);
	}

	@Test
	public void testOptionalMissingParameter() throws Exception {
		String missingParamKeyName = "whatever";
		ApiSingleParam<String> missingParam = ApiSingleParam.builder(missingParamKeyName, String.class)
		                                                    .addChecks(Check.alwaysPass(String.class))
		                                                    .build();

		ApiMapParam rootMapParam = ApiMapParam.builder()
		                                      .addOptionalSingleParams(missingParam)
		                                      .build();

		ApiMapParam.Result result = rootMapParam.check(new HashMap<>());
		assertTrue(result.successful());
		assertFalse(result.hasCheckedKeyNames());
		assertFalse(result.hasCheckedMapResults());
		assertFalse(result.hasCheckedCollectionResults());
		assertFalse(result.hasCustomValues());
	}

	@Test
	public void testRequiredNestedMissingError() throws Exception {
		String missingParamKeyName = "whatever";
		ApiSingleParam<String> missingParam = ApiSingleParam.builder(missingParamKeyName, String.class)
		                                                    .addChecks(Check.alwaysPass(String.class))
		                                                    .build();

		String inner1KeyName = "inner1", inner1DisplayName = "inner map 1";
		ApiMapParam inner1MapParam =
			ApiMapParam.builder(inner1KeyName)
			           .addRequiredSingleParams(missingParam)
			           .build();

		ApiMapParam rootMapParam = ApiMapParam.builder().addRequiredMapParams(inner1MapParam).build();

		Map<String, Object> inner1Map = new HashMap<>();
		Map<String, Object> rootMap = new HashMap<>();
		rootMap.put(inner1KeyName, inner1Map);

		ApiMapParam.Result result = rootMapParam.check(rootMap);

		assertTrue(result.failed());
		ApiParamError topLevelError = result.error;
		assertEquals(ApiErrorType.MISSING_PARAMETER, topLevelError.errorType);
		assertEquals(inner1KeyName, topLevelError.keyName);
		assertTrue(topLevelError.hasChildError());
		assertNull(topLevelError.index);
		assertEquals(2, topLevelError.getErrorsAsList().size());

		ApiParamError innerError = topLevelError.childParamError;
		assertEquals(ApiErrorType.MISSING_PARAMETER, innerError.errorType);
		assertFalse(innerError.hasChildError());
		assertEquals(missingParamKeyName, innerError.keyName);
		assertNull(innerError.index);
		assertEquals(1, innerError.getErrorsAsList().size());
	}

	@Test
	public void testOptionalNestedMissingParameter() throws Exception {
		String missingParamKeyName = "whatever";
		ApiSingleParam<String> missingParam = ApiSingleParam.builder(missingParamKeyName, String.class)
		                                                    .addChecks(Check.alwaysPass(String.class))
		                                                    .build();

		String inner1KeyName = "inner1", inner1DisplayName = "inner map 1";
		ApiMapParam inner1MapParam = ApiMapParam.builder(inner1KeyName)
		                                        .addOptionalSingleParams(missingParam)
		                                        .build();

		ApiMapParam rootMapParam = ApiMapParam.builder().addRequiredMapParams(inner1MapParam).build();

		Map<String, Object> inner1Map = new HashMap<>();
		Map<String, Object> rootMap = new HashMap<>();
		rootMap.put(inner1KeyName, inner1Map);

		ApiMapParam.Result result = rootMapParam.check(rootMap);
		assertTrue(result.successful());
		assertTrue(result.hasCheckedKeyNames());
		assertEquals(1, result.checkedKeyNames.size());
		assertTrue(result.containsParameter(inner1KeyName));
		assertTrue(result.hasCheckedMapResults());
		assertFalse(result.hasCheckedCollectionResults());
		assertFalse(result.hasCustomValues());

		ApiMapParam.Result innerResult = result.getMapResult(inner1KeyName);
		assertTrue(innerResult.successful());
		assertFalse(innerResult.hasCheckedKeyNames());
		assertFalse(innerResult.containsParameter(inner1KeyName));
		assertFalse(innerResult.hasCheckedMapResults());
		assertFalse(innerResult.hasCheckedCollectionResults());
		assertFalse(innerResult.hasCustomValues());
	}

	@Test
	public void testNestedMapsCollection() throws Exception {
		String nestedMapKey = "nestedMap", listKey = "listKey";

		ApiListParam<Map<String, Object>, String> list =
			ApiListParam.<Map<String, Object>, String>builder(listKey)
				.addIndexChecks(StringChecks.lengthGreaterThan(5))
				.build();

		ApiMapParam nestedMap = ApiMapParam.builder(nestedMapKey)
		                                   .addRequiredCollectionParams(list)
		                                   .build();

		ApiMapParam rootMap = ApiMapParam.builder()
		                                 .addRequiredMapParams(nestedMap)
		                                 .build();

		/* SUCCESS */

		Map<String, Object> nestedMapParams = new HashMap<>();
		nestedMapParams.put(listKey, Arrays.asList("passing", "length greater than 5"));

		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put(nestedMapKey, nestedMapParams);

		ApiMapParam.Result successfulResult = rootMap.check(requestParams);
		assertTrue(successfulResult.successful());
		assertTrue(successfulResult.hasCheckedKeyNames());
		assertEquals(1, successfulResult.checkedKeyNames.size());
		assertTrue(successfulResult.hasCheckedMapResults());
		assertEquals(1, successfulResult.checkedMapResults.size());
		// should not have collection at this leve. should be inner map
		assertFalse(successfulResult.hasCheckedCollectionResults());

		ApiMapParam.Result innerResult = successfulResult.getMapResult(nestedMapKey);
		assertTrue(innerResult.successful());
		assertTrue(innerResult.hasCheckedKeyNames());
		assertEquals(1, innerResult.checkedKeyNames.size());
		assertTrue(innerResult.containsParameter(listKey));
		assertFalse(innerResult.hasCheckedMapResults());
		// should not have collection params, because they did not actually return anything, just that it was successful
		assertFalse(innerResult.hasCheckedCollectionResults());


		/* FAILURE */

		nestedMapParams.put(listKey, Arrays.asList("passing", "fail"));

		ApiMapParam.Result failingResult = rootMap.check(requestParams);
		assertTrue(failingResult.failed());
		ApiParamError error = failingResult.error;
		assertTrue(error.hasKeyName());
		assertEquals(nestedMapKey, error.keyName);
		assertTrue(error.hasChildError());
		ApiParamError nestedError = error.childParamError;
		assertTrue(nestedError.hasKeyName());
		assertEquals(listKey, nestedError.keyName);
		assertFalse(nestedError.hasChildError());
		assertTrue(nestedError.hasIndex());
		assertEquals(1, nestedError.index);
	}

	@Test
	public void shouldPassOnEmptyMapCheck() throws Exception {
		ApiMapParam innerMapParam = ApiMapParam.builder("inner")
		                                       .build();
		ApiMapParam parentMapParam = ApiMapParam.builder()
		                                        .addRequiredMapParams(innerMapParam)
		                                        .build();
		Map<String, Object> param = new HashMap<>();
		param.put("inner", new HashMap<>());
		ApiMapParam.Result checkResult = parentMapParam.check(param);
		assertTrue(checkResult.hasCheckedKeyNames());
		assertTrue(checkResult.hasCheckedMapResults());
		assertFalse(checkResult.hasCheckedCollectionResults());
		assertEquals(1, checkResult.checkedKeyNames.size());
		assertTrue(checkResult.checkedKeyNames.contains("inner"));
		ApiMapParam.Result innerCheckResult = checkResult.getMapResult("inner");
		assertFalse(innerCheckResult.hasCheckedKeyNames());
		assertFalse(innerCheckResult.hasCheckedMapResults());
		assertFalse(innerCheckResult.hasCheckedCollectionResults());
	}

}
