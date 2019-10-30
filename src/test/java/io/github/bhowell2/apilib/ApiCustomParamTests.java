package io.github.bhowell2.apilib;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Custom params are really whatever the user makes them. The test here just ensure that the
 * {@link ApiCustomParam} check works as expected.
 * @author Blake Howell
 */
public class ApiCustomParamTests {

	public static final String KEY1 = "key1";
	public static final String KEY2 = "key2";
	public static final String KEY3 = "key3";

	/*
	 * Require KEY1 and KEY2 to be supplied OR KEY3, but not both. does not check the value, just that it was
	 * supplied. Could even be set to null in this check. See ApiMapParamTests for a fuller example of using
	 * ApiCustomParam.
	 * */
	public static final ApiCustomParam CUSTOM_PARAM_KEY1_AND_KEY2_XOR_KEY3 =
		params -> {
			if (params.containsKey(KEY1) && params.containsKey(KEY2) && !params.containsKey(KEY3)) {
				return ApiCustomParam.Result.success(KEY1, KEY2);
			} else if (params.containsKey(KEY3) && !(params.containsKey(KEY1) || params.containsKey(KEY2))) {
				return ApiCustomParam.Result.success(KEY3);
			} else {
				return ApiCustomParam.Result.failure(
					ApiParamError.invalid("Must contain 'key1' AND 'key2' OR 'key3', but " + "not both."));
			}
		};

	private static Map<String, Object> generateParamsForCustomParamKey1Key2Key3(boolean key1, boolean key2, boolean key3) {
		Map<String, Object> map = new HashMap<>();
		if (key1) {
			map.put(KEY1, "whatever");
		}
		if (key2) {
			map.put(KEY2, "doesnt matter");
		}
		if (key3) {
			map.put(KEY3, "passing!");
		}
		return map;
	}

	@Test
	public void testApiCustomResultThrowsException() throws Exception {
		assertThrows(IllegalArgumentException.class, () -> {
			ApiCustomParam.Result.successWithCustomResult(null, "custom value");
		}, "Cannot create an ApiCustomParam Result with a custom value and no key name");
		assertThrows(IllegalArgumentException.class, () -> {
			ApiCustomParam.Result.success(null, null, null, null, "custom value");
		}, "Cannot create an ApiCustomParam Result with a custom value and no key name");
	}

	@Test
	public void shouldPassCheck() throws Exception {
		// just passes check
		ApiCustomParam customParam = params -> ApiCustomParam.Result.success();
		ApiCustomParam.Result checkResult = customParam.check(new HashMap<>());
		assertTrue(checkResult.successful());
		assertFalse(checkResult.failed());
	}

	@Test
	public void shouldFailCheck() throws Exception {
		// just fails check
		ApiCustomParam customParam = params -> ApiCustomParam.Result.failure(ApiParamError.invalid("always invalid"));
		ApiCustomParam.Result checkResult = customParam.check(new HashMap<>());
		assertTrue(checkResult.failed());
		assertFalse(checkResult.successful());
	}

	@Test
	public void shouldReturnParamNames() throws Exception {
		ApiCustomParam.Result passingResultKey1Key2 =
			CUSTOM_PARAM_KEY1_AND_KEY2_XOR_KEY3.check(generateParamsForCustomParamKey1Key2Key3(true, true, false));
		assertTrue(passingResultKey1Key2.successful());
		assertFalse(passingResultKey1Key2.hasKeyName());
		assertTrue(passingResultKey1Key2.hasCheckedKeyNames());
		assertFalse(passingResultKey1Key2.hasCheckedCollectionParams());
		assertFalse(passingResultKey1Key2.hasCheckedMapParams());
		assertFalse(passingResultKey1Key2.hasCustomValue());
		assertTrue(passingResultKey1Key2.checkedKeyNames.contains(KEY1));
		assertTrue(passingResultKey1Key2.checkedKeyNames.contains(KEY2));
		assertFalse(passingResultKey1Key2.checkedKeyNames.contains(KEY3));

		ApiCustomParam.Result passingResultKey3 =
			CUSTOM_PARAM_KEY1_AND_KEY2_XOR_KEY3.check(generateParamsForCustomParamKey1Key2Key3(false, false, true));
		assertTrue(passingResultKey3.successful());
		assertFalse(passingResultKey3.hasKeyName());
		assertTrue(passingResultKey3.hasCheckedKeyNames());
		assertFalse(passingResultKey3.hasCheckedCollectionParams());
		assertFalse(passingResultKey3.hasCheckedMapParams());
		assertFalse(passingResultKey3.hasCustomValue());
		assertTrue(passingResultKey3.checkedKeyNames.contains(KEY3));
		assertFalse(passingResultKey3.checkedKeyNames.contains(KEY1));
		assertFalse(passingResultKey3.checkedKeyNames.contains(KEY2));

		ApiCustomParam.Result failingResultAllSupplied =
			CUSTOM_PARAM_KEY1_AND_KEY2_XOR_KEY3.check(generateParamsForCustomParamKey1Key2Key3(true, true, true));
		assertTrue(failingResultAllSupplied.failed());
		assertFalse(failingResultAllSupplied.hasKeyName());
		assertFalse(failingResultAllSupplied.hasCheckedKeyNames());
		assertFalse(failingResultAllSupplied.hasCheckedMapParams());
		assertFalse(failingResultAllSupplied.hasCheckedCollectionParams());
		assertFalse(failingResultAllSupplied.hasCustomValue());
	}

	@Test
	public void shouldReturnMapParamNames() throws Exception {
		String innerMapName = "innermap";
		ApiCustomParam customParam = params -> {
			Map<String, ApiMapParam.Result> customMapParams = new HashMap<>();
			customMapParams.put(innerMapName,
			                    ApiMapParam.Result.success(innerMapName,
			                                               new HashSet<>(Arrays.asList("innerparam1", "innerparam2")),
			                                               null,
			                                               null,
			                                               null));
			return ApiCustomParam.Result.success(new HashSet<>(Collections.singleton("param1")),
			                                     customMapParams, null
			);
		};
		ApiCustomParam.Result result = customParam.check(new HashMap<>());
		assertTrue(result.successful());
		assertTrue(result.hasCheckedKeyNames());
		assertTrue(result.hasCheckedMapParams());
		assertFalse(result.hasCheckedCollectionParams());
		assertFalse(result.hasCustomValue());
		assertEquals(1, result.checkedKeyNames.size());
		assertEquals(1, result.checkedMapParams.size());
		assertTrue(result.checkedKeyNames.contains("param1"));
		assertTrue(result.checkedMapParams.get(innerMapName).containsParameter("innerparam1"));
		assertTrue(result.checkedMapParams.get(innerMapName).containsParameter("innerparam2"));
	}

}
