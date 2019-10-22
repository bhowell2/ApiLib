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
				return ApiCustomCheckResult.success(KEY1, KEY2);
			} else if (params.containsKey(KEY3) && !(params.containsKey(KEY1) || params.containsKey(KEY2))) {
				return ApiCustomCheckResult.success(KEY3);
			} else {
				return ApiCustomCheckResult.failure(
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
	public void shouldPassCheck() throws Exception {
		// just passes check
		ApiCustomParam customParam = params -> ApiCustomCheckResult.success();
		ApiCustomCheckResult checkResult = customParam.check(new HashMap<>());
		assertTrue(checkResult.successful());
		assertFalse(checkResult.failed());
	}

	@Test
	public void shouldFailCheck() throws Exception {
		// just fails check
		ApiCustomParam customParam = params -> ApiCustomCheckResult.failure(ApiParamError.invalid("always invalid"));
		ApiCustomCheckResult checkResult = customParam.check(new HashMap<>());
		assertTrue(checkResult.failed());
		assertFalse(checkResult.successful());
	}

	@Test
	public void shouldReturnParamNames() throws Exception {
		ApiCustomCheckResult passingCheckResultKey1Key2 =
			CUSTOM_PARAM_KEY1_AND_KEY2_XOR_KEY3.check(generateParamsForCustomParamKey1Key2Key3(true, true, false));
		assertTrue(passingCheckResultKey1Key2.successful());
		assertFalse(passingCheckResultKey1Key2.hasKeyName());
		assertTrue(passingCheckResultKey1Key2.hasCheckedKeyNames());
		assertFalse(passingCheckResultKey1Key2.hasCheckedArrayOfMapsParams());
		assertFalse(passingCheckResultKey1Key2.hasCheckedMapParams());
		assertFalse(passingCheckResultKey1Key2.hasCustomValue());
		assertTrue(passingCheckResultKey1Key2.checkedKeyNames.contains(KEY1));
		assertTrue(passingCheckResultKey1Key2.checkedKeyNames.contains(KEY2));
		assertFalse(passingCheckResultKey1Key2.checkedKeyNames.contains(KEY3));

		ApiCustomCheckResult passingCheckResultKey3 =
			CUSTOM_PARAM_KEY1_AND_KEY2_XOR_KEY3.check(generateParamsForCustomParamKey1Key2Key3(false, false, true));
		assertTrue(passingCheckResultKey3.successful());
		assertFalse(passingCheckResultKey3.hasKeyName());
		assertTrue(passingCheckResultKey3.hasCheckedKeyNames());
		assertFalse(passingCheckResultKey3.hasCheckedArrayOfMapsParams());
		assertFalse(passingCheckResultKey3.hasCheckedMapParams());
		assertFalse(passingCheckResultKey3.hasCustomValue());
		assertTrue(passingCheckResultKey3.checkedKeyNames.contains(KEY3));
		assertFalse(passingCheckResultKey3.checkedKeyNames.contains(KEY1));
		assertFalse(passingCheckResultKey3.checkedKeyNames.contains(KEY2));

		ApiCustomCheckResult failingCheckResultAllSupplied =
			CUSTOM_PARAM_KEY1_AND_KEY2_XOR_KEY3.check(generateParamsForCustomParamKey1Key2Key3(true, true, true));
		assertTrue(failingCheckResultAllSupplied.failed());
		assertFalse(failingCheckResultAllSupplied.hasKeyName());
		assertFalse(failingCheckResultAllSupplied.hasCheckedKeyNames());
		assertFalse(failingCheckResultAllSupplied.hasCheckedMapParams());
		assertFalse(failingCheckResultAllSupplied.hasCheckedArrayOfMapsParams());
		assertFalse(failingCheckResultAllSupplied.hasCustomValue());
	}

	@Test
	public void shouldReturnMapParamNames() throws Exception {
		String innerMapName = "innermap";
		ApiCustomParam customParam = params -> {
			Map<String, ApiMapCheckResult> customMapParams = new HashMap<>();
			customMapParams.put(innerMapName,
			                    ApiMapCheckResult.success(innerMapName,
			                                              new HashSet<>(Arrays.asList("innerparam1", "innerparam2")),
			                                              null,
			                                              null,
			                                              null));
			return ApiCustomCheckResult.success(new HashSet<>(Collections.singleton("param1")),
			                                    null,
			                                    customMapParams);
		};
		ApiCustomCheckResult result = customParam.check(new HashMap<>());
		assertTrue(result.successful());
		assertTrue(result.hasCheckedKeyNames());
		assertTrue(result.hasCheckedMapParams());
		assertFalse(result.hasCheckedArrayOfMapsParams());
		assertFalse(result.hasCustomValue());
		assertEquals(1, result.checkedKeyNames.size());
		assertEquals(1, result.checkedMapParams.size());
		assertTrue(result.checkedKeyNames.contains("param1"));
		assertTrue(result.checkedMapParams.get(innerMapName).containsParameter("innerparam1"));
		assertTrue(result.checkedMapParams.get(innerMapName).containsParameter("innerparam2"));
	}

}
