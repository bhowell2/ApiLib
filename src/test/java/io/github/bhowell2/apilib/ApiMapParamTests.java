package io.github.bhowell2.apilib;

import io.github.bhowell2.apilib.checks.Check;
import io.github.bhowell2.apilib.checks.ConditionalCheck;
import io.github.bhowell2.apilib.checks.StringChecks;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
//
///**
// * This will contain a full example and also test ApiMapParam.
// * Tests for {@link ApiMapParam} to ensure it is working as expected.
// * The tests here will also provide a full example of using all of the features of the library.
// * This will use a real-world example of a customer's account information.
// *
// * This includes the shipping addresses the customer may have on file as well as various other fields.
// *
// * API:
// *
// * @author Blake Howell
// */
//public class ApiMapParamTests {
//
//	public static class ShippingAddress {
//
//		public static class BodyParamNames {
//			public static final String LINE1 = "line1";
//			public static final String LINE2 = "line2";
//			public static final String LINE3 = "line3";
//			public static final String CITY = "city";
//			public static final String STATE = "state";
//			public static final String ZIP = "zip";
//		}
//
//		public static final ApiSingleParam<String> LINE1 =
//			ApiSingleParamBuilder.builder(BodyParamNames.LINE1, String.class)
//			                     .addChecks(StringChecks.IS_NOT_EMPTY_OR_ONLY_WHITESPACE,
//			                                StringChecks.lengthGreaterThan(2))
//			                     .build();
//
//		public static final ApiSingleParam<String> LINE2 =
//			ApiSingleParamBuilder.builder(BodyParamNames.LINE2, String.class)
//			                     .addChecks(Check.alwaysPass(String.class))
//			                     .build();
//
//		public static final ApiSingleParam<String> LINE3 =
//			ApiSingleParamBuilder.builder(BodyParamNames.LINE3, String.class)
//			                     .addChecks(Check.alwaysPass(String.class))
//			                     .build();
//
//		public static final ApiSingleParam<String> CITY =
//			ApiSingleParamBuilder.builder(BodyParamNames.CITY, String.class)
//			                     .addChecks(StringChecks.IS_NOT_EMPTY_OR_ONLY_WHITESPACE)
//			                     .build();
//
//		// only allowing state abbreviation. e.g., TN
//		public static final ApiSingleParam<String> STATE =
//			ApiSingleParamBuilder.builder(BodyParamNames.STATE, String.class)
//			                     .addChecks(StringChecks.lengthEqualTo(2))
//			                     .build();
//
//		public static final ApiSingleParam<String> ZIP =
//			ApiSingleParamBuilder.builder(BodyParamNames.ZIP, String.class)
//			                     .addChecks(StringChecks.IS_NOT_EMPTY_OR_ONLY_WHITESPACE)
//			                     .build();
//
//		/*
//		 * There is not a single parameter named "shipping_address", but they are part of an array which
//		 * is named "shipping_addresses", so it is a root/unnamed parameter.
//		 * */
//		public static final ApiMapParam SHIPPING_ADDRESS_MAP_PARAM =
//			ApiMapParamBuilder.rootBuilder()
//			                  // if line2 or line 3 is supplied, but they fail for whatever reason still continue
//			                  .setContinueOnOptionalFailure(true)
//			                  .addRequiredSingleParams(LINE1, CITY, STATE, ZIP)
//			                  .addOptionalSingleParams(LINE2, LINE3)
//			                  .build();
//
//	}
//
//	public static Map<String, Object> generateShippingAddress(String line1, String city, String state, String zip) {
//		Map<String, Object> address = new HashMap<>();
//		address.put(ShippingAddress.BodyParamNames.LINE1, line1);
//		address.put(ShippingAddress.BodyParamNames.CITY, city);
//		address.put(ShippingAddress.BodyParamNames.STATE, state);
//		address.put(ShippingAddress.BodyParamNames.ZIP, zip);
//		return address;
//	}
//
//	/**
//	 * Example POST API endpoint.
//	 *
//	 * Shipping Address (JSON form, used below in customer info):
//	 * {
//	 *   "line1": "longer than 3 chars",
//	 *   "line2": "optionally some text",
//	 *   "line3": "optionally some text",
//	 *   "city": "some text",
//	 *   "state": "some state initial",
//	 *   "zip": 12345,
//	 * }
//	 *
//	 * Customer Info:
//	 * {
//	 *   "username": "longer than 3 chars",
//	 *   "password": "longer than 5 chars and contains a 0-9 digit",
//	 *   "email": "optionally a valid email",
//	 *   "e_billing": true|false,   (if true, must have email provided)
//	 *   "phone": "some text",
//	 *   "shipping_addresses": [{...}, {...}, ...]
//	 * }
//	 *
//	 *
//	 */
//	public static class PostAccountInfo {
//
//		public static class BodyParamNames {
//			public static final String USERNAME = "username";
//			public static final String PASSWORD = "password";
//			public static final String EMAIL = "email";
//			public static final String E_BILLING = "e_billing";
//			public static final String PHONE = "phone";
//			public static final String SHIPPING_ADDRESSES = "shipping_addresses";
//		}
//
//		public static final ApiSingleParam<String> USERNAME =
//			ApiSingleParamBuilder.builder(BodyParamNames.USERNAME, String.class)
//			                     .addCheck(StringChecks.lengthGreaterThan(3))
//			                     .build();
//
//		public static final ApiSingleParam<String> PASSWORD =
//			ApiSingleParamBuilder.builder(BodyParamNames.PASSWORD, String.class)
//			                     .addChecks(StringChecks.lengthGreaterThan(5),
//			                                StringChecks.containsCodePointsInRange(1, "0", "9", false))
//			                     .build();
//
//		public static final ApiSingleParam<String> EMAIL =
//			ApiSingleParamBuilder.builder(BodyParamNames.EMAIL, String.class)
//			                     .addChecks(StringChecks.MATCHES_BASIC_EMAIL_PATTERN)
//			                     .build();
//
//		public static final ApiSingleParam<Boolean> E_BILLING =
//			ApiSingleParamBuilder.builder(BodyParamNames.E_BILLING, Boolean.class)
//			                     .addCheck(Check.alwaysPass(Boolean.class))
//			                     .build();
//
//		public static final ApiSingleParam<String> PHONE =
//			ApiSingleParamBuilder.builder(BodyParamNames.PHONE, String.class)
//			                     .addCheck(StringChecks.lengthGreaterThanOrEqualTo(10))
//			                     .build();
//
//		public static final ConditionalCheck REQUIRE_EMAIL_IF_EBILLING_IS_TRUE =
//			(params, keyNames, mapKeys, customKeys) -> {
//			// ebilling was provided, make sure that if it is set to true that email is provided
//				if (keyNames.contains(BodyParamNames.E_BILLING)) {
//					Boolean eBilling = (Boolean) params.get(BodyParamNames.E_BILLING);
//					if (eBilling && !keyNames.contains(BodyParamNames.EMAIL)) {
//						return ConditionalCheck.Result.failure("If e_billing is set to true, email must also be provided");
//					}
//				}
//				return ConditionalCheck.Result.success();
//			};
//
//		/*
//		 * There is quite a bit of overhead that goes into creating a custom parameter that checks each
//		 * array position for a map. If it is as simple as checking the value in each position then this
//		 * */
//		public static final ApiArrayOfMapsParam SHIPPING_ADDRESSES =
//			ApiArrayOfMapsParamBuilder.builder(BodyParamNames.SHIPPING_ADDRESSES)
//			                          .setApiMapParamForAllIndices(ShippingAddress.SHIPPING_ADDRESS_MAP_PARAM)
//			                          .build();
//
//		public static final ApiMapParam BODY_MAP_PARAM =
//			ApiMapParamBuilder.rootBuilder()
//			                  .addRequiredSingleParams(USERNAME, PASSWORD)
//			                  .addOptionalSingleParams(EMAIL, PHONE, E_BILLING)
//			                  .addOptionalArrayOfMapsParam(SHIPPING_ADDRESSES)
//			                  .addConditonalCheck(REQUIRE_EMAIL_IF_EBILLING_IS_TRUE)
//			                  .build();
//	}
//
//	// this also tests the conditional
//	@Test
//	public void shouldSuccessfullyCheckMap() throws Exception {
//		String username = "user1";
//		String password = "password11";
//		Map<String, Object> postAccountInfoMinimalRequired = new HashMap<>();
//		postAccountInfoMinimalRequired.put(PostAccountInfo.BodyParamNames.USERNAME, username);
//		postAccountInfoMinimalRequired.put(PostAccountInfo.BodyParamNames.PASSWORD, password);
//
//		ApiMapParamCheckResult checkResultMinimal = PostAccountInfo.BODY_MAP_PARAM.check(postAccountInfoMinimalRequired);
//		assertTrue(checkResultMinimal.successful());
//		assertEquals(2, checkResultMinimal.providedParamNames.size());
//		assertTrue(checkResultMinimal.containsParameter(PostAccountInfo.BodyParamNames.USERNAME));
//		assertTrue(checkResultMinimal.containsParameter(PostAccountInfo.BodyParamNames.PASSWORD));
//		assertFalse(checkResultMinimal.containsParameter(PostAccountInfo.BodyParamNames.EMAIL));
//		assertFalse(checkResultMinimal.containsParameter(PostAccountInfo.BodyParamNames.E_BILLING));
//		assertFalse(checkResultMinimal.containsParameter(PostAccountInfo.BodyParamNames.PHONE));
//		assertFalse(checkResultMinimal.containsParameter(PostAccountInfo.BodyParamNames.SHIPPING_ADDRESSES));
//		assertEquals(0, checkResultMinimal.providedMapParams.size());
//		assertEquals(0, checkResultMinimal.providedArrayOfMapsParams.size());
//		// check returned params
//
//		String email = "avalidemail@gmail.com";
//		Map<String, Object> postAccountInfoWithEmail = new HashMap<>();
//		postAccountInfoWithEmail.putAll(postAccountInfoMinimalRequired);
//		postAccountInfoWithEmail.put(PostAccountInfo.BodyParamNames.EMAIL, email);
//		ApiMapParamCheckResult checkResultWithEmail = PostAccountInfo.BODY_MAP_PARAM.check(postAccountInfoWithEmail);
//		assertTrue(checkResultWithEmail.successful());
//		assertEquals(3, checkResultWithEmail.providedParamNames.size());
//		assertTrue(checkResultWithEmail.containsParameter(PostAccountInfo.BodyParamNames.USERNAME));
//		assertTrue(checkResultWithEmail.containsParameter(PostAccountInfo.BodyParamNames.PASSWORD));
//		assertTrue(checkResultWithEmail.containsParameter(PostAccountInfo.BodyParamNames.EMAIL));
//		assertFalse(checkResultWithEmail.containsParameter(PostAccountInfo.BodyParamNames.E_BILLING));
//		assertFalse(checkResultWithEmail.containsParameter(PostAccountInfo.BodyParamNames.PHONE));
//		assertFalse(checkResultWithEmail.containsParameter(PostAccountInfo.BodyParamNames.SHIPPING_ADDRESSES));
//		assertEquals(0, checkResultWithEmail.providedMapParams.size());
//		assertEquals(0, checkResultWithEmail.providedArrayOfMapsParams.size());
//
//		Map<String, Object> postAccountInfoWithEbillingFalse = new HashMap<>();
//		postAccountInfoWithEbillingFalse.putAll(postAccountInfoWithEmail);
//		postAccountInfoWithEbillingFalse.put(PostAccountInfo.BodyParamNames.E_BILLING, false);
//		ApiMapParamCheckResult checkResultWithEbillingFalse =
//			PostAccountInfo.BODY_MAP_PARAM.check(postAccountInfoWithEbillingFalse);
//		assertTrue(checkResultWithEbillingFalse.successful());
//		assertEquals(4, checkResultWithEbillingFalse.providedParamNames.size());
//		assertTrue(checkResultWithEbillingFalse.containsParameter(PostAccountInfo.BodyParamNames.USERNAME));
//		assertTrue(checkResultWithEbillingFalse.containsParameter(PostAccountInfo.BodyParamNames.PASSWORD));
//		assertTrue(checkResultWithEbillingFalse.containsParameter(PostAccountInfo.BodyParamNames.EMAIL));
//		assertTrue(checkResultWithEbillingFalse.containsParameter(PostAccountInfo.BodyParamNames.E_BILLING));
//		assertFalse(checkResultWithEbillingFalse.containsParameter(PostAccountInfo.BodyParamNames.PHONE));
//		assertFalse(checkResultWithEbillingFalse.containsParameter(PostAccountInfo.BodyParamNames.SHIPPING_ADDRESSES));
//		assertEquals(0, checkResultWithEbillingFalse.providedMapParams.size());
//		assertEquals(0, checkResultWithEbillingFalse.providedArrayOfMapsParams.size());
//
//		// adding phone number and shipping address here too
//		Map<String, Object> postAccountInfoWithEbillingTrue = new HashMap<>();
//		postAccountInfoWithEbillingTrue.putAll(postAccountInfoWithEmail);
//		postAccountInfoWithEbillingTrue.put(PostAccountInfo.BodyParamNames.E_BILLING, true);
//		postAccountInfoWithEbillingTrue.put(PostAccountInfo.BodyParamNames.PHONE, "1234567890");
//		List<Map<String, Object>> shippingAddresses = new ArrayList<>();
//		shippingAddresses.add(generateShippingAddress("line1", "memphis", "tn", "38117"));
//		postAccountInfoWithEbillingTrue.put(PostAccountInfo.BodyParamNames.SHIPPING_ADDRESSES, shippingAddresses);
//
//		ApiMapParamCheckResult checkResultWithEbillingTrue =
//			PostAccountInfo.BODY_MAP_PARAM.check(postAccountInfoWithEbillingTrue);
//		assertTrue(checkResultWithEbillingTrue.successful());
//		assertEquals(6, checkResultWithEbillingTrue.providedParamNames.size());
//		assertTrue(checkResultWithEbillingTrue.containsParameter(PostAccountInfo.BodyParamNames.USERNAME));
//		assertTrue(checkResultWithEbillingTrue.containsParameter(PostAccountInfo.BodyParamNames.PASSWORD));
//		assertTrue(checkResultWithEbillingTrue.containsParameter(PostAccountInfo.BodyParamNames.EMAIL));
//		assertTrue(checkResultWithEbillingTrue.containsParameter(PostAccountInfo.BodyParamNames.E_BILLING));
//		assertTrue(checkResultWithEbillingTrue.containsParameter(PostAccountInfo.BodyParamNames.PHONE));
//		assertTrue(checkResultWithEbillingTrue.containsParameter(PostAccountInfo.BodyParamNames.SHIPPING_ADDRESSES));
//		assertEquals(0, checkResultWithEbillingTrue.providedMapParams.size());
//		assertEquals(1, checkResultWithEbillingTrue.providedArrayOfMapsParams.size());
//	}
//
//	// the successful conditional check is done in 'shouldSuccessfullyCheckMap'
////	@Test
////	public void shouldFailOnConditionalCheck() throws Exception {
////		String username = "user1";
////		String password = "password11";
////		Map<String, Object> postAccountInfoWithEbillingButNoEmail = new HashMap<>();
////		postAccountInfoWithEbillingButNoEmail.put(PostAccountInfo.BodyParamNames.USERNAME, username);
////		postAccountInfoWithEbillingButNoEmail.put(PostAccountInfo.BodyParamNames.PASSWORD, password);
////		postAccountInfoWithEbillingButNoEmail.put(PostAccountInfo.BodyParamNames.E_BILLING, true);
////
////		ApiMapParamCheckResult failedResult = PostAccountInfo.BODY_MAP_PARAM.check(postAccountInfoWithEbillingButNoEmail);
////		assertTrue(failedResult.failed());
////		assertTrue(failedResult.getApiParamError()
////		                       .getErrorMessageWithKeyName()
////		                       .contains("If e_billing is set to true, email must also be provided"));
////	}
//
//	// TODO test nested error
//
//}

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
			ApiSingleParamBuilder.builder(BodyParamNames.LINE1, String.class)
			                     .addChecks(StringChecks.IS_NOT_EMPTY_OR_ONLY_WHITESPACE,
			                                StringChecks.lengthGreaterThan(2))
			                     .build();

		public static final ApiSingleParam<String> LINE2 =
			ApiSingleParamBuilder.builder(BodyParamNames.LINE2, String.class)
			                     .addChecks(Check.alwaysPass(String.class))
			                     .build();

		public static final ApiSingleParam<String> LINE3 =
			ApiSingleParamBuilder.builder(BodyParamNames.LINE3, String.class)
			                     .addChecks(Check.alwaysPass(String.class))
			                     .build();

		public static final ApiSingleParam<String> CITY =
			ApiSingleParamBuilder.builder(BodyParamNames.CITY, String.class)
			                     .addChecks(StringChecks.IS_NOT_EMPTY_OR_ONLY_WHITESPACE)
			                     .build();

		// only allowing state abbreviation. e.g., TN
		public static final ApiSingleParam<String> STATE =
			ApiSingleParamBuilder.builder(BodyParamNames.STATE, String.class)
			                     .addChecks(StringChecks.lengthEqualTo(2))
			                     .build();

		public static final ApiSingleParam<String> ZIP =
			ApiSingleParamBuilder.builder(BodyParamNames.ZIP, String.class)
			                     .addChecks(StringChecks.IS_NOT_EMPTY_OR_ONLY_WHITESPACE)
			                     .build();

		/*
		 * There is not a single parameter named "shipping_address", but they are part of an array which
		 * is named "shipping_addresses", so it is a root/unnamed parameter.
		 * */
		public static final ApiMapParam SHIPPING_ADDRESS_MAP_PARAM =
			ApiMapParamBuilder.rootBuilder()
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
	 *
	 *
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
			ApiSingleParamBuilder.builder(BodyParamNames.USERNAME, String.class)
			                     .addChecks(StringChecks.lengthGreaterThan(3))
			                     .build();

		public static final ApiSingleParam<String> PASSWORD =
			ApiSingleParamBuilder.builder(BodyParamNames.PASSWORD, String.class)
			                     .addChecks(StringChecks.lengthGreaterThan(5),
			                                StringChecks.containsCodePointsInRange(1, "0", "9", false))
			                     .build();

		public static final ApiSingleParam<String> EMAIL =
			ApiSingleParamBuilder.builder(BodyParamNames.EMAIL, String.class)
			                     .addChecks(StringChecks.MATCHES_BASIC_EMAIL_PATTERN)
			                     .build();

		public static final ApiSingleParam<Boolean> E_BILLING =
			ApiSingleParamBuilder.builder(BodyParamNames.E_BILLING, Boolean.class)
			                     .addChecks(Check.alwaysPass(Boolean.class))
			                     .build();

		public static final ApiSingleParam<String> PHONE =
			ApiSingleParamBuilder.builder(BodyParamNames.PHONE, String.class)
			                     .addChecks(StringChecks.lengthGreaterThanOrEqualTo(10))
			                     .build();

		public static final ConditionalCheck REQUIRE_EMAIL_IF_EBILLING_IS_TRUE =
			(params, checkResult) -> {
				// ebilling was provided, make sure that if it is set to true that email is provided
				if (checkResult.checkedKeyNames.contains(BodyParamNames.E_BILLING)) {
					Boolean eBilling = (Boolean) params.get(BodyParamNames.E_BILLING);
					if (eBilling && !checkResult.checkedKeyNames.contains(BodyParamNames.EMAIL)) {
						return ConditionalCheck.Result.failure("If e_billing is set to true, email must also be provided");
					}
				}
				return ConditionalCheck.Result.success();
			};

		/*
		 * There is quite a bit of overhead that goes into creating a custom parameter that checks each
		 * array position for a map. If it is as simple as checking the value in each position then this
		 * */
		public static final ApiListParam<Map<String, Object>, Map<String, Object>> SHIPPING_ADDRESSES =
			ApiListParamBuilder.<Map<String, Object>, Map<String, Object>>builder(BodyParamNames.SHIPPING_ADDRESSES, "Shipping Addresses")
				.setIndexMapCheck(ShippingAddress.SHIPPING_ADDRESS_MAP_PARAM)
				.build();

		public static final ApiMapParam BODY_MAP_PARAM =
			ApiMapParamBuilder.rootBuilder()
			                  .addRequiredSingleParams(USERNAME, PASSWORD)
			                  .addOptionalSingleParams(EMAIL, PHONE, E_BILLING)
			                  .addOptionalArrayOfMapsParam(SHIPPING_ADDRESSES)
			                  .addConditionalChecks(REQUIRE_EMAIL_IF_EBILLING_IS_TRUE)
			                  .build();
	}

	// this also tests the conditional
	@Test
	public void shouldSuccessfullyCheckMap() throws Exception {
		String username = "user1";
		String password = "password11";
		Map<String, Object> postAccountInfoMinimalRequired = new HashMap<>();
		postAccountInfoMinimalRequired.put(PostAccountInfo.BodyParamNames.USERNAME, username);
		postAccountInfoMinimalRequired.put(PostAccountInfo.BodyParamNames.PASSWORD, password);

		ApiMapCheckResult checkResultMinimal = PostAccountInfo.BODY_MAP_PARAM.check(postAccountInfoMinimalRequired);
		assertTrue(checkResultMinimal.successful());
		assertEquals(2, checkResultMinimal.checkedKeyNames.size());
		assertTrue(checkResultMinimal.containsParameter(PostAccountInfo.BodyParamNames.USERNAME));
		assertTrue(checkResultMinimal.containsParameter(PostAccountInfo.BodyParamNames.PASSWORD));
		assertFalse(checkResultMinimal.containsParameter(PostAccountInfo.BodyParamNames.EMAIL));
		assertFalse(checkResultMinimal.containsParameter(PostAccountInfo.BodyParamNames.E_BILLING));
		assertFalse(checkResultMinimal.containsParameter(PostAccountInfo.BodyParamNames.PHONE));
		assertFalse(checkResultMinimal.containsParameter(PostAccountInfo.BodyParamNames.SHIPPING_ADDRESSES));
		assertFalse(checkResultMinimal.hasCheckedArrayOfMapsParams());
		assertFalse(checkResultMinimal.hasCheckedMapParams());

		String email = "avalidemail@gmail.com";
		Map<String, Object> postAccountInfoWithEmail = new HashMap<>();
		postAccountInfoWithEmail.putAll(postAccountInfoMinimalRequired);
		postAccountInfoWithEmail.put(PostAccountInfo.BodyParamNames.EMAIL, email);
		ApiMapCheckResult checkResultWithEmail = PostAccountInfo.BODY_MAP_PARAM.check(postAccountInfoWithEmail);
		assertTrue(checkResultWithEmail.successful());
		assertEquals(3, checkResultWithEmail.checkedKeyNames.size());
		assertTrue(checkResultWithEmail.containsParameter(PostAccountInfo.BodyParamNames.USERNAME));
		assertTrue(checkResultWithEmail.containsParameter(PostAccountInfo.BodyParamNames.PASSWORD));
		assertTrue(checkResultWithEmail.containsParameter(PostAccountInfo.BodyParamNames.EMAIL));
		assertFalse(checkResultWithEmail.containsParameter(PostAccountInfo.BodyParamNames.E_BILLING));
		assertFalse(checkResultWithEmail.containsParameter(PostAccountInfo.BodyParamNames.PHONE));
		assertFalse(checkResultWithEmail.containsParameter(PostAccountInfo.BodyParamNames.SHIPPING_ADDRESSES));
		assertFalse(checkResultWithEmail.hasCheckedArrayOfMapsParams());
		assertFalse(checkResultWithEmail.hasCheckedMapParams());

		Map<String, Object> postAccountInfoWithEbillingFalse = new HashMap<>();
		postAccountInfoWithEbillingFalse.putAll(postAccountInfoWithEmail);
		postAccountInfoWithEbillingFalse.put(PostAccountInfo.BodyParamNames.E_BILLING, false);
		ApiMapCheckResult checkResultWithEbillingFalse = PostAccountInfo.BODY_MAP_PARAM.check(postAccountInfoWithEbillingFalse);
		assertTrue(checkResultWithEbillingFalse.successful());
		assertEquals(4, checkResultWithEbillingFalse.checkedKeyNames.size());
		assertTrue(checkResultWithEbillingFalse.containsParameter(PostAccountInfo.BodyParamNames.USERNAME));
		assertTrue(checkResultWithEbillingFalse.containsParameter(PostAccountInfo.BodyParamNames.PASSWORD));
		assertTrue(checkResultWithEbillingFalse.containsParameter(PostAccountInfo.BodyParamNames.EMAIL));
		assertTrue(checkResultWithEbillingFalse.containsParameter(PostAccountInfo.BodyParamNames.E_BILLING));
		assertFalse(checkResultWithEbillingFalse.containsParameter(PostAccountInfo.BodyParamNames.PHONE));
		assertFalse(checkResultWithEbillingFalse.containsParameter(PostAccountInfo.BodyParamNames.SHIPPING_ADDRESSES));
		assertFalse(checkResultWithEbillingFalse.hasCheckedArrayOfMapsParams());
		assertFalse(checkResultWithEbillingFalse.hasCheckedMapParams());

		// adding phone number and shipping address here too
		Map<String, Object> postAccountInfoWithEbillingTrue = new HashMap<>();
		postAccountInfoWithEbillingTrue.putAll(postAccountInfoWithEmail);
		postAccountInfoWithEbillingTrue.put(PostAccountInfo.BodyParamNames.E_BILLING, true);
		postAccountInfoWithEbillingTrue.put(PostAccountInfo.BodyParamNames.PHONE, "1234567890");
		List<Map<String, Object>> shippingAddresses = new ArrayList<>();
		shippingAddresses.add(generateShippingAddress("line1", "memphis", "tn", "38117"));
		postAccountInfoWithEbillingTrue.put(PostAccountInfo.BodyParamNames.SHIPPING_ADDRESSES, shippingAddresses);

		ApiMapCheckResult checkResultWithEbillingTrue = PostAccountInfo.BODY_MAP_PARAM.check(postAccountInfoWithEbillingTrue);
		assertTrue(checkResultWithEbillingTrue.successful());
		assertEquals(6, checkResultWithEbillingTrue.checkedKeyNames.size());
		assertTrue(checkResultWithEbillingTrue.containsParameter(PostAccountInfo.BodyParamNames.USERNAME));
		assertTrue(checkResultWithEbillingTrue.containsParameter(PostAccountInfo.BodyParamNames.PASSWORD));
		assertTrue(checkResultWithEbillingTrue.containsParameter(PostAccountInfo.BodyParamNames.EMAIL));
		assertTrue(checkResultWithEbillingTrue.containsParameter(PostAccountInfo.BodyParamNames.E_BILLING));
		assertTrue(checkResultWithEbillingTrue.containsParameter(PostAccountInfo.BodyParamNames.PHONE));
		assertTrue(checkResultWithEbillingTrue.containsParameter(PostAccountInfo.BodyParamNames.SHIPPING_ADDRESSES));
		assertEquals(1, checkResultWithEbillingTrue.checkedArrayOfMapsParams.size());
		assertFalse(checkResultWithEbillingTrue.hasCheckedMapParams());

		ApiArrayOrListCheckResult shippingAddressesCheckResult = checkResultWithEbillingTrue.getCheckedArrayOfMapsParams().get(PostAccountInfo.BodyParamNames.SHIPPING_ADDRESSES);

		assertTrue(shippingAddressesCheckResult.hasMapCheckResults());
		assertEquals(1, shippingAddressesCheckResult.mapCheckResults.size());

		ApiMapCheckResult shippingAddressCheckResult = shippingAddressesCheckResult.getMapCheckResults().get(0);

		assertNull(shippingAddressCheckResult.keyName);
		assertEquals(4, shippingAddressCheckResult.checkedKeyNames.size());
		assertFalse(shippingAddressCheckResult.hasCheckedArrayOfMapsParams());
		assertFalse(shippingAddressCheckResult.hasCheckedMapParams());

	}

//	@Test
//	public void testMissingError() throws Exception {
//		String missingParamKeyName = "whatever";
//		ApiSingleParam<String> missingParam = ApiSingleParamBuilder.builder(missingParamKeyName, String.class)
//		                                                           .addChecks(Check.alwaysPass(String.class))
//		                                                           .build();
//
//		ApiMapParam rootMapParam = ApiMapParamBuilder.rootBuilder()
//		                                            .addRequiredParameters(missingParam)
//		                                            .build();
//
//		ApiMapCheckResult result = rootMapParam.check(new HashMap<>());
//		assertTrue(result.failed());
//		assertEquals(ApiErrorType.MISSING_PARAMETER, result.error.errorType);
//		assertEquals(missingParamKeyName, result.error.keyName);
//
//	}
//
//	@Test
//	public void testNestedMissingError() throws Exception {
//		String missingParamKeyName = "whatever";
//		ApiSingleParam<String> missingParam = ApiSingleParamBuilder.builder(missingParamKeyName, String.class)
//		                                                           .addChecks(Check.alwaysPass(String.class))
//		                                                           .build();
//
//		String inner1KeyName = "inner1", inner1DisplayName = "inner map 1";
//		ApiMapParam inner1MapParam =
//			ApiMapParamBuilder.builder(inner1KeyName, inner1DisplayName)
//			                  .addRequiredParameters(missingParam)
//			                  .build();
//
//		ApiMapParam rootMapParam = ApiMapParamBuilder.rootBuilder().addRequiredParameters(inner1MapParam).build();
//
//		Map<String, Object> inner1Map = new HashMap<>();
//		Map<String, Object> rootMap = new HashMap<>();
//		rootMap.put(inner1KeyName, inner1Map);
//
//		ApiMapCheckResult result = rootMapParam.check(rootMap);
//
//		assertTrue(result.failed());
//		assertEquals(ApiErrorType.MISSING_PARAMETER, result.error.errorType);
//		assertEquals(missingParamKeyName, result.error.keyName);
//		assertEquals(1, result.error.getParentKeyNames().size());
//		assertEquals(inner1KeyName, result.error.getParentKeyNames().get(0));
//	}
//
//	@Test
//	public void testNestedError() throws Exception {
//		String inner2KeyName = "inner2", inner2DisplayName = "inner map 2";
//		ApiMapParam inner2 =
//			ApiMapParamBuilder.builder(inner2KeyName, inner2DisplayName)
//			                  .addRequiredParameters(
//				                  p -> ApiMapCheckResult.failure(
//					                  ApiParamError.invalid(null, null, "just fail.")))
//			                  .build();
//		String inner1KeyName = "inner1", inner1DisplayName = "inner map 1";
//		ApiMapParam inner1 =
//			ApiMapParamBuilder.builder(inner1KeyName, inner1DisplayName)
//			                  .addRequiredParameters(inner2)
//			                  .build();
//
//		ApiMapParam rootMapParam = ApiMapParamBuilder.rootBuilder().addRequiredParameters(inner1).build();
//
//		// since inner1 is required, if an empty map is submitted that should be missing
//		ApiMapCheckResult missingInner1CheckResult = rootMapParam.check(new HashMap<>());
//		assertTrue(missingInner1CheckResult.failed());
//		assertEquals(ApiErrorType.MISSING_PARAMETER, missingInner1CheckResult.error.errorType);
//		assertEquals(inner1KeyName, missingInner1CheckResult.error.keyName);
//		assertNull(missingInner1CheckResult.error.getParentKeyNames());
//
//		// provide inner1, but not inner2
//		Map<String, Object> inner1MapWithoutInner2 = new HashMap<>();
//		Map<String, Object> rootMapWithOnlyInner1 = new HashMap();
//		rootMapWithOnlyInner1.put(inner1KeyName, inner1MapWithoutInner2);
//		ApiMapCheckResult missingInner2CheckResult = rootMapParam.check(rootMapWithOnlyInner1);
//		assertTrue(missingInner2CheckResult.failed());
//		assertEquals(ApiErrorType.MISSING_PARAMETER, missingInner2CheckResult.error.errorType);
//		assertEquals(inner2KeyName, missingInner2CheckResult.error.keyName);
//		assertEquals(1, missingInner2CheckResult.error.getParentKeyNames().size());
//		assertEquals(inner1KeyName, missingInner2CheckResult.error.getParentKeyNames().get(0));
//		/*
//		* Need JSON of form:
//		* {
//		*   "inner1": {
//		*     "inner2": {
//		*
//		*     }
//		*   }
//		* }
//		* */
//		Map<String, Object> inner1Params = new HashMap<>();
//		inner1Params.put(inner2KeyName, new HashMap<>());
//		Map<String, Object> params = new HashMap<>();
//		params.put(inner1KeyName, inner1Params);
//
//		ApiMapCheckResult result = rootMapParam.check(params);
//
//		assertFalse(result.successful());
//		assertTrue(result.failed());
//		assertEquals(ApiErrorType.INVALID_PARAMETER, result.error.errorType);
//		assertNull(result.error.keyName, "Invalid error was returned with null keyname/display name");
//		assertNull(result.error.displayName);
//		assertEquals(2, result.error.getParentKeyNames().size());
//		assertEquals(inner2KeyName, result.error.getParentKeyNames().get(1));
//		assertEquals(inner1KeyName, result.error.getParentKeyNames().get(0));
//	}

	// TODO test order. test optional run last

}
