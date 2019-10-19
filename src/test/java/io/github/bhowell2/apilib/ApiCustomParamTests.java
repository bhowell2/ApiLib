//package io.github.bhowell2.apilib;
//
//import org.junit.jupiter.api.Test;
//
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * Custom params are really whatever the user makes them. The test here just ensure that the
// * {@link ApiCustomParam} check works as expected.
// * @author Blake Howell
// */
//public class ApiCustomParamTests {
//
//	public static final String KEY1 = "key1";
//	public static final String KEY2 = "key2";
//	public static final String KEY3 = "key3";
//
//	/*
//	 * Require KEY1 and KEY2 to be supplied OR KEY3, but not both. does not check the value, just that it was
//	 * supplied. Could even be set to null in this check. See ApiMapParamTests for a fuller example of using
//	 * ApiCustomParam.
//	 * */
//	public static final ApiCustomParam CUSTOM_PARAM_KEY1_AND_KEY2_XOR_KEY3 =
//		params -> {
//			if (params.containsKey(KEY1) && params.containsKey(KEY2) && !params.containsKey(KEY3)) {
//				return ApiCustomParamCheckResult.success(KEY1, KEY2);
//			} else if (params.containsKey(KEY3) && !(params.containsKey(KEY1) || params.containsKey(KEY2))) {
//				return ApiCustomParamCheckResult.success(KEY3);
//			} else {
//				return ApiCustomParamCheckResult.failure(
//					ApiParamError.invalid("Must contain 'key1' AND 'key2' OR 'key3', but " +
//						                      "not both."));
//			}
//		};
//
//	private static Map<String, Object> generateParamsForCustomParamKey1Key2Key3(boolean key1, boolean key2, boolean key3) {
//		Map<String, Object> map = new HashMap<>();
//		if (key1) {
//			map.put(KEY1, "whatever");
//		}
//		if (key2) {
//			map.put(KEY2, "doesnt matter");
//		}
//		if (key3) {
//			map.put(KEY3, "passing!");
//		}
//		return map;
//	}
//
//	@Test
//	public void shouldPassCheck() throws Exception {
//		// just passes check
//		ApiCustomParam customParam = params -> ApiCustomParamCheckResult.success();
//		ApiCustomParamCheckResult checkResult = customParam.check(new HashMap<>());
//		assertTrue(checkResult.successful());
//		assertFalse(checkResult.failed());
//	}
//
//	@Test
//	public void shouldFailCheck() throws Exception {
//		// just fails check
//		ApiCustomParam customParam = params -> ApiCustomParamCheckResult.failure(ApiParamError.invalid("always invalid"));
//		ApiCustomParamCheckResult checkResult = customParam.check(new HashMap<>());
//		assertTrue(checkResult.failed());
//		assertFalse(checkResult.successful());
//	}
//
//	@Test
//	public void shouldReturnParamNames() throws Exception {
//		ApiCustomParamCheckResult passingCheckResultKey1Key2 =
//			CUSTOM_PARAM_KEY1_AND_KEY2_XOR_KEY3.check(generateParamsForCustomParamKey1Key2Key3(true, true, false));
//		assertTrue(passingCheckResultKey1Key2.successful());
//		assertTrue(passingCheckResultKey1Key2.providedParamKeyNames.contains(KEY1));
//		assertTrue(passingCheckResultKey1Key2.providedParamKeyNames.contains(KEY2));
//		assertFalse(passingCheckResultKey1Key2.providedParamKeyNames.contains(KEY3));
//		assertEquals(2, passingCheckResultKey1Key2.providedParamKeyNames.size());
//		assertEquals(0, passingCheckResultKey1Key2.providedMapParams.size());
//		assertEquals(0, passingCheckResultKey1Key2.providedArrayOfMapParams.size());
//
//
//		ApiCustomParamCheckResult passingCheckResultKey3 =
//			CUSTOM_PARAM_KEY1_AND_KEY2_XOR_KEY3.check(generateParamsForCustomParamKey1Key2Key3(false, false, true));
//		assertTrue(passingCheckResultKey3.successful());
//		assertTrue(passingCheckResultKey3.providedParamKeyNames.contains(KEY3));
//		assertFalse(passingCheckResultKey3.providedParamKeyNames.contains(KEY1));
//		assertFalse(passingCheckResultKey3.providedParamKeyNames.contains(KEY2));
//		assertEquals(1, passingCheckResultKey3.providedParamKeyNames.size());
//		assertEquals(0, passingCheckResultKey3.providedMapParams.size());
//		assertEquals(0, passingCheckResultKey3.providedArrayOfMapParams.size());
//
//		ApiCustomParamCheckResult failingCheckResultAllSupplied =
//			CUSTOM_PARAM_KEY1_AND_KEY2_XOR_KEY3.check(generateParamsForCustomParamKey1Key2Key3(true, true, true));
//		assertTrue(failingCheckResultAllSupplied.failed());
//		assertNull(failingCheckResultAllSupplied.providedParamKeyNames);
//		assertNull(failingCheckResultAllSupplied.providedMapParams);
//		assertNull(failingCheckResultAllSupplied.providedArrayOfMapParams);
//	}
//
//	@Test
//	public void shouldReturnMapParamNames() throws Exception {
//		String innerMapName = "innermap";
//		ApiCustomParam customParam = params -> {
//			Map<String, ApiMapParamCheckResult> customMapParams = new HashMap<>();
//			customMapParams.put(innerMapName,
//			                    ApiMapParamCheckResult.success(innerMapName,
//			                                                   new HashSet<>(Arrays.asList("innerparam1", "innerparam2")),
//			                                                   null,
//			                                                   null));
//			return ApiCustomParamCheckResult.success(
//				new HashSet<>(Collections.singleton("param1")),
//				customMapParams,
//				null);
//		};
//		ApiCustomParamCheckResult result = customParam.check(new HashMap<>());
//		assertTrue(result.successful());
//		assertEquals(1, result.providedParamKeyNames.size());
//		assertEquals(1, result.providedMapParams.size());
//		assertEquals(0, result.providedArrayOfMapParams.size());
//		assertTrue(result.providedParamKeyNames.contains("param1"));
//		assertTrue(result.providedMapParams.get(innerMapName).containsParameter("innerparam1"));
//		assertTrue(result.providedMapParams.get(innerMapName).containsParameter("innerparam2"));
//	}
//
//}
