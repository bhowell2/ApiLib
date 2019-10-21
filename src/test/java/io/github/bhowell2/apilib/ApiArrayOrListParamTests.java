package io.github.bhowell2.apilib;

import io.github.bhowell2.apilib.checks.Check;
import io.github.bhowell2.apilib.checks.DoubleChecks;
import io.github.bhowell2.apilib.checks.IntegerChecks;
import io.github.bhowell2.apilib.checks.StringChecks;
import io.github.bhowell2.apilib.formatters.Formatter;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Blake Howell
 */
public class ApiArrayOrListParamTests {

	@SuppressWarnings("unchecked")
	@Test
	public void testConstructorThrowsExceptionOnInvalidArguments() throws Exception {

		ApiListParam<List<String>, String> listParam =
			ApiListParamBuilder.<List<String>, String>unnamedBuilder()
				.addIndexChecks(StringChecks.lengthEqualTo(5),
				                StringChecks.containsCodePoint(2, "\uD83E\uDD13"))
				.build();

		ApiMapParam mapParam = ApiMapParamBuilder.rootBuilder().build();

		assertThrows(IllegalArgumentException.class, () -> {
			new ApiArrayOrListParam("whate", "ver", null, false, null, null, mapParam, null, listParam) {

			};
		}, "Should not be able to create a list with both ApiMapParam (check) and inner list.");

		assertThrows(IllegalArgumentException.class, () -> {
			new ApiArrayOrListParam("whate", "ver", null, false, null, null, mapParam, new ApiMapParam[]{mapParam}, null) {

			};
		}, "Should not be able to create a list param with index map checks (i.e., a map check that applies to ALL indices) " +
			             "and individual map checks (i.e., map checks for each index).");

		assertThrows(IllegalArgumentException.class, () -> {
			ApiListParam<List<String>, String> namedListParam =
				ApiListParamBuilder.<List<String>, String>builder("name", "disp")
					.addIndexChecks(StringChecks.lengthEqualTo(5),
					                StringChecks.containsCodePoint(2, "\uD83E\uDD13"))
					.build();
			new ApiArrayOrListParam("whatever", "", null, false, null, null, null, null, namedListParam) {

			};
		}, "Should not be able to add inner array/list param that has a key name.");

		assertThrows(IllegalArgumentException.class, () -> {
			new ApiArrayOrListParam("whatever", "whatever", "whatever", false, null, null, null, null, null) {

			};
		}, "Should not be able to create array param without checks.");

		assertThrows(IllegalArgumentException.class, () -> {
			new ApiArrayOrListParam("whatever", "whatever", "whatever", false, null, null, mapParam, null, null, true, true) {

			};
		}, "Should not be able to create a parameter that requires type to be an Array and a List");
	}

	@Test
	public void testArrayParamFailsWithList() throws Exception {
		String keyName = "key";
		ApiArrayParam<Map<String, Object>, String> arrayParam =
			ApiArrayParamBuilder.mapInputBuilder(keyName, keyName, String.class)
			                    .addIndexChecks(Check.alwaysPass(String.class))
			                    .build();
		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put(keyName, Arrays.asList("heyyy"));
		ApiArrayOrListCheckResult checkResult = arrayParam.check(requestParams);
		assertTrue(checkResult.failed());
		assertEquals(keyName, checkResult.error.keyName);
		assertEquals(ApiErrorType.CASTING_ERROR, checkResult.error.errorType);

		requestParams.put(keyName, new String[]{"heyy"});

		ApiArrayOrListCheckResult passingCheckResult = arrayParam.check(requestParams);
		assertTrue(passingCheckResult.successful());

		ApiArrayParam<String[], String> arrayParamDirect =
			ApiArrayParamBuilder.innerArrayBuilder(String.class)
			                    .addIndexChecks(Check.alwaysPass(String.class))
			                    .build();

		ApiArrayOrListCheckResult passingCheckResult2 = arrayParamDirect.check(new String[]{"Heyy"});
		assertTrue(passingCheckResult2.successful());

	}

	@Test
	public void testListParamFailsWithArray() throws Exception {
		String keyName = "key";
		ApiListParam<Map<String, Object>, String> listParam =
			ApiListParamBuilder.mapInputBuilder(keyName, keyName, String.class)
			                   .addIndexChecks(Check.alwaysPass(String.class))
			                   .build();
		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put(keyName, new String[]{"heyy"});
		ApiArrayOrListCheckResult checkResult = listParam.check(requestParams);
		assertTrue(checkResult.failed());
		assertEquals(keyName, checkResult.error.keyName);
		assertEquals(ApiErrorType.CASTING_ERROR, checkResult.error.errorType);

		requestParams.put(keyName, Arrays.asList("heyy"));
		ApiArrayOrListCheckResult passingCheckResult = listParam.check(requestParams);
		assertTrue(passingCheckResult.successful());

		ApiListParam<List<String>, String> listParamDirect =
			ApiListParamBuilder.innerListBuilder(String.class)
			                   .addIndexChecks(Check.alwaysPass(String.class))
			                   .build();

		ApiArrayOrListCheckResult passingCheckResult2 = listParamDirect.check(Arrays.asList("heyy"));
		assertTrue(passingCheckResult2.successful());

	}

	/**
	 * This tests taking in a list directly. This usually will not happen unless the
	 * {@link ApiArrayOrListParam} is nested, but testing for good measure.
	 */
	@Test
	public void testListOfOrderOneDirectly() throws Exception {
		// strings must be length 5 and contain 2 nerd faces
		ApiListParam<List<String>, String> listParam =
			ApiListParamBuilder.<List<String>, String>unnamedBuilder()
				.addIndexChecks(StringChecks.lengthEqualTo(5),
				                StringChecks.containsCodePoint(2, "\uD83E\uDD13"))
				.build();
		List<String> list = new ArrayList<>(3);
		list.add("1🤓🤓");
		list.add("a🤓🤓");
		list.add("🤓🤓b");
		ApiArrayOrListCheckResult passingCheckResult = listParam.check(list);
		assertTrue(passingCheckResult.successful());
		assertFalse(passingCheckResult.hasKeyName());
		assertFalse(passingCheckResult.hasMapCheckResults());
		assertFalse(passingCheckResult.hasInnerArrayCheckResults());
	}

	@Test
	public void testArrayOfOrderOneDirectly() throws Exception {
		// strings must be length 5 and contain 2 nerd faces
		ApiArrayParam<String[], String> listParam =
			ApiArrayParamBuilder.<String[], String>unnamedBuilder()
				.addIndexChecks(StringChecks.lengthEqualTo(5),
				                StringChecks.containsCodePoint(2, "\uD83E\uDD13"))
				.build();
		String[] array = new String[]{
			"1🤓🤓",
			"a🤓🤓",
			"🤓🤓b"
		};
		ApiArrayOrListCheckResult passingCheckResult = listParam.check(array);
		assertTrue(passingCheckResult.successful());
		assertFalse(passingCheckResult.hasKeyName());
		assertFalse(passingCheckResult.hasMapCheckResults());
		assertFalse(passingCheckResult.hasInnerArrayCheckResults());
	}

	@Test
	public void testListOfOrderOneOfMaps() throws Exception {
		ApiMapParam mapParam =
			ApiMapParamBuilder.rootBuilder()
			                  .addRequiredSingleParams(ApiSingleParamBuilder.builder("p1", String.class)
			                                                                .addChecks(Check.alwaysPass(String.class))
			                                                                .build(),
			                                           ApiSingleParamBuilder.builder("p2", Integer.class)
			                                                                .addChecks(IntegerChecks.valueGreaterThan(55),
			                                                                           IntegerChecks.valueLessThan(100))
			                                                                .build())
			                  .addOptionalSingleParams(
				                  ApiSingleParamBuilder.builder("p3", Double.class)
				                                       .addChecks(DoubleChecks.valueEqualTo(3.14))
				                                       // convert string to double
				                                       .addFormatters((String d) -> Formatter.Result.success(Double.valueOf(d)))
				                                       .build())
			                  .build();

		String listKeyName = "listOfMaps";
		ApiArrayOrListParam<Map<String, Object>, Map<String, Object>> listParam =
			ApiArrayOrListParamBuilder.<Map<String, Object>, Map<String, Object>>builder(listKeyName, listKeyName)
				.setIndexMapCheck(mapParam)
				.build();

		List<Map<String, Object>> listOfMaps = new ArrayList<>();
		Map<String, Object> index0 = new HashMap<>();
		index0.put("p1", "always passes");
		index0.put("p2", 56);
		listOfMaps.add(index0);
		Map<String, Object> index1 = new HashMap<>();
		index1.put("p1", "always passes");
		index1.put("p2", 56);
		listOfMaps.add(index1);
		Map<String, Object> index2 = new HashMap<>();
		index2.put("p1", "always passes");
		index2.put("p2", 56);
		// will be converted to double by formatter and must equal 3.14
		index2.put("p3", "3.14");
		listOfMaps.add(index2);

		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put(listKeyName, listOfMaps);
		// just adding another to the request params. shouldnt be checked or anything
		requestParams.put("whatever", 111);

		ApiArrayOrListCheckResult passingCheckResult = listParam.check(requestParams);
		assertTrue(passingCheckResult.successful());
		assertFalse(passingCheckResult.hasInnerArrayCheckResults());
		assertTrue(passingCheckResult.hasMapCheckResults());

		List<ApiMapCheckResult> indexMapCheckResults = passingCheckResult.getMapCheckResults();
		assertEquals(3, indexMapCheckResults.size());

		// index0
		assertEquals(2, indexMapCheckResults.get(0).checkedKeyNames.size());
		assertTrue(indexMapCheckResults.get(0).checkedKeyNames.contains("p1"));
		assertTrue(indexMapCheckResults.get(0).checkedKeyNames.contains("p2"));
		assertFalse(indexMapCheckResults.get(0).checkedKeyNames.contains("p3"));

		// index1
		assertEquals(2, indexMapCheckResults.get(1).checkedKeyNames.size());
		assertTrue(indexMapCheckResults.get(1).checkedKeyNames.contains("p1"));
		assertTrue(indexMapCheckResults.get(1).checkedKeyNames.contains("p2"));
		assertFalse(indexMapCheckResults.get(1).checkedKeyNames.contains("p3"));

		// index2
		assertEquals(3, indexMapCheckResults.get(2).checkedKeyNames.size());
		assertTrue(indexMapCheckResults.get(2).checkedKeyNames.contains("p1"));
		assertTrue(indexMapCheckResults.get(2).checkedKeyNames.contains("p2"));
		assertTrue(indexMapCheckResults.get(2).checkedKeyNames.contains("p3"));
		// ensure formatter inserted
		assertTrue(index2.get("p3") instanceof Double);
		assertTrue(index2.get("p3").equals(3.14));


		/* TEST FAILURE */

		// can just remove one of the required fields. they are checked before the optional that is formatted
		index1.remove("p2");
		ApiArrayOrListCheckResult failingCheckResult = listParam.check(requestParams);
		assertFalse(failingCheckResult.successful());
		ApiParamError listError = failingCheckResult.error;
		assertEquals(ApiErrorType.MISSING_PARAMETER, listError.errorType);
		assertTrue(listError.hasChildError());
		assertEquals(listKeyName, listError.keyName);
		assertEquals(1, listError.index);
		ApiParamError mapError = listError.childParamError;
		assertEquals("p2", mapError.keyName);
		assertEquals(ApiErrorType.MISSING_PARAMETER, mapError.errorType);
		assertFalse(mapError.hasChildError());
		assertNull(mapError.index);
	}

	@Test
	public void testListOfOrderTwoOfMaps() throws Exception {
		// tests a list of lists of maps. uses same map as orderOne test.
		ApiMapParam mapParam =
			ApiMapParamBuilder.rootBuilder()
			                  .addRequiredSingleParams(ApiSingleParamBuilder.builder("p1", String.class)
			                                                                .addChecks(Check.alwaysPass(String.class))
			                                                                .build(),
			                                           ApiSingleParamBuilder.builder("p2", Integer.class)
			                                                                .addChecks(IntegerChecks.valueGreaterThan(55),
			                                                                           IntegerChecks.valueLessThan(100))
			                                                                .build())
			                  .addOptionalSingleParams(
				                  ApiSingleParamBuilder.builder("p3", Double.class)
				                                       .addChecks(DoubleChecks.valueEqualTo(3.14))
				                                       // convert string to double
				                                       .addFormatters((String d) -> Formatter.Result.success(Double.valueOf(d)))
				                                       .build())
			                  .build();

		ApiArrayOrListParam<List<Map<String, Object>>, Map<String, Object>> innerListParam =
			ApiArrayOrListParamBuilder.<List<Map<String, Object>>, Map<String, Object>>unnamedBuilder()
				.setIndexMapCheck(mapParam)
				.build();

		String listOfListsKeyName = "listOfListsOfMaps";
		ApiArrayOrListParam<Map<String, Object>, List<Map<String, Object>>> listOfListsOfMapsParam =
			ApiArrayOrListParamBuilder.<Map<String, Object>, List<Map<String, Object>>>builder(listOfListsKeyName, listOfListsKeyName)
				.setInnerArrayOrListParam(innerListParam)
				.build();

		List<List<Map<String, Object>>> listOfListsOfMaps = new ArrayList<>();

		List<Map<String, Object>> listOfMapsIndex0 = new ArrayList<>();
		Map<String, Object> mapIndex0_0 = new HashMap<>();
		mapIndex0_0.put("p1", "always passes");
		mapIndex0_0.put("p2", 56);
		listOfMapsIndex0.add(mapIndex0_0);
		Map<String, Object> mapIndex0_1 = new HashMap<>();
		mapIndex0_1.put("p1", "always passes");
		mapIndex0_1.put("p2", 56);
		listOfMapsIndex0.add(mapIndex0_1);
		Map<String, Object> mapIndex0_2 = new HashMap<>();
		mapIndex0_2.put("p1", "always passes");
		mapIndex0_2.put("p2", 56);
		// will be converted to double by formatter and must equal 3.14
		mapIndex0_2.put("p3", "3.14");
		listOfMapsIndex0.add(mapIndex0_2);

		listOfListsOfMaps.add(listOfMapsIndex0);

		List<Map<String, Object>> listOfMapsIndex1 = new ArrayList<>();
		Map<String, Object> mapIndex1_0 = new HashMap<>();
		mapIndex1_0.put("p1", "always passes");
		mapIndex1_0.put("p2", 56);
		listOfMapsIndex1.add(mapIndex1_0);
		Map<String, Object> mapIndex1_1 = new HashMap<>();
		mapIndex1_1.put("p1", "always passes");
		mapIndex1_1.put("p2", 56);
		listOfMapsIndex1.add(mapIndex1_1);
		Map<String, Object> mapIndex1_2 = new HashMap<>();
		mapIndex1_2.put("p1", "always passes");
		mapIndex1_2.put("p2", 56);
		// will be converted to double by formatter and must equal 3.14
		mapIndex1_2.put("p3", "3.14");
		listOfMapsIndex1.add(mapIndex1_2);

		listOfListsOfMaps.add(listOfMapsIndex1);

		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put(listOfListsKeyName, listOfListsOfMaps);

		ApiArrayOrListCheckResult passingOuterCheckResult = listOfListsOfMapsParam.check(requestParams);
		assertTrue(passingOuterCheckResult.successful());
		assertTrue(passingOuterCheckResult.hasInnerArrayCheckResults());
		assertFalse(passingOuterCheckResult.hasMapCheckResults(),
		            "Should have map check results on the INNER array, not this one.");

		List<ApiArrayOrListCheckResult> passingInnerCheckResults = passingOuterCheckResult.innerArrayCheckResults;
		assertEquals(2, passingInnerCheckResults.size());
		for (int i = 0; i < passingInnerCheckResults.size(); i++) {
			ApiArrayOrListCheckResult checkResult = passingInnerCheckResults.get(i);
			assertTrue(checkResult.successful());
			assertFalse(checkResult.hasInnerArrayCheckResults());
			assertTrue(checkResult.hasMapCheckResults());
			assertEquals(3, checkResult.mapCheckResults.size());

			List<ApiMapCheckResult> indexMapCheckResults = checkResult.mapCheckResults;

			// index0
			assertEquals(2, indexMapCheckResults.get(0).checkedKeyNames.size());
			assertTrue(indexMapCheckResults.get(0).checkedKeyNames.contains("p1"));
			assertTrue(indexMapCheckResults.get(0).checkedKeyNames.contains("p2"));
			assertFalse(indexMapCheckResults.get(0).checkedKeyNames.contains("p3"));

			// index1
			assertEquals(2, indexMapCheckResults.get(1).checkedKeyNames.size());
			assertTrue(indexMapCheckResults.get(1).checkedKeyNames.contains("p1"));
			assertTrue(indexMapCheckResults.get(1).checkedKeyNames.contains("p2"));
			assertFalse(indexMapCheckResults.get(1).checkedKeyNames.contains("p3"));

			// index2
			assertEquals(3, indexMapCheckResults.get(2).checkedKeyNames.size());
			assertTrue(indexMapCheckResults.get(2).checkedKeyNames.contains("p1"));
			assertTrue(indexMapCheckResults.get(2).checkedKeyNames.contains("p2"));
			assertTrue(indexMapCheckResults.get(2).checkedKeyNames.contains("p3"));
		}

		// ensure formatter worked and inserted
		assertTrue(mapIndex0_2.get("p3") instanceof Double);
		assertTrue(mapIndex0_2.get("p3").equals(3.14));
		assertTrue(mapIndex1_2.get("p3") instanceof Double);
		assertTrue(mapIndex1_2.get("p3").equals(3.14));

		/* TEST FAILURE */

		// can just remove one of the required fields. they are checked before the optional that is formatted
		mapIndex0_2.put("p3", "3.14");
		mapIndex1_2.remove("p2");
		ApiArrayOrListCheckResult failingCheckResult = listOfListsOfMapsParam.check(requestParams);
		assertTrue(failingCheckResult.failed());

		ApiParamError error = failingCheckResult.error;
		assertEquals(ApiErrorType.MISSING_PARAMETER, error.errorType);
		assertTrue(error.hasChildError());
		assertEquals(listOfListsKeyName, error.keyName);
		assertEquals(1, error.index);

		ApiParamError innerError = error.childParamError;
		assertEquals(ApiErrorType.MISSING_PARAMETER, innerError.errorType);
		assertTrue(innerError.hasChildError());
		assertNull(innerError.keyName);
		assertEquals(2, innerError.index);

		ApiParamError innerErrorMapError = innerError.childParamError;
		assertEquals(ApiErrorType.MISSING_PARAMETER, innerErrorMapError.errorType);
		assertFalse(innerErrorMapError.hasChildError());
		assertEquals("p2", innerErrorMapError.keyName);
		assertNull(innerErrorMapError.index);
	}

	@Test
	public void testArrayOfOrderThree() throws Exception {
		ApiArrayOrListParam<String[], String> orderThreeParam =
			ApiArrayOrListParamBuilder.innerArrayBuilder(String.class)
			                          .addIndexChecks(StringChecks.lengthGreaterThan(1),
			                                          StringChecks.lengthLessThan(10))
			                          .build();

		ApiArrayOrListParam<String[][], String[]> orderTwoParam =
			ApiArrayOrListParamBuilder.innerArrayBuilder(String[].class)
			                          .setInnerArrayOrListParam(orderThreeParam)
			                          .build();

		String keyName = "arrayOfArraysOfArraysOfStrings";
		ApiArrayOrListParam<Map<String, Object>, String[][]> orderOneParam =
			ApiArrayOrListParamBuilder.mapInputBuilder(keyName, keyName, String[][].class)
			                          .setInnerArrayOrListParam(orderTwoParam)
			                          .build();

		Map<String, Object> requestParams = new HashMap<>();

		String[][][] arrayWithinArrayStringParams = new String[][][] {
			new String[][] {
				new String[] {
					"12", "34", "56"
				},
				new String[] {
					"abc", "def", "ghi"
				},
				new String[] {
					"jk"
				}
			},
			new String[][] {
				new String[] {
					"one", "two"
				},
				new String[] {
					"three"
				}
			}
		};

		requestParams.put(keyName, arrayWithinArrayStringParams);

		ApiArrayOrListCheckResult passingCheckResult = orderOneParam.check(requestParams);
		assertTrue(passingCheckResult.successful());
		assertEquals(keyName, passingCheckResult.keyName);
		assertFalse(passingCheckResult.hasMapCheckResults());
		assertFalse(passingCheckResult.hasInnerArrayCheckResults());

		/* TEST FAILURE */

		arrayWithinArrayStringParams[1][0][1] = "too long and should fail";

		ApiArrayOrListCheckResult failingCheckResult = orderOneParam.check(requestParams);
		assertTrue(failingCheckResult.failed());
		ApiParamError err = failingCheckResult.error;
		assertEquals(keyName, err.keyName);
		assertEquals(keyName, err.displayName);
		assertEquals(ApiErrorType.INVALID_PARAMETER, err.errorType);
		assertTrue(err.hasChildError());
		assertEquals(1, err.index);
		ApiParamError childErr1 = err.childParamError;
		assertNull(childErr1.keyName);
		assertNull(childErr1.displayName);
		assertEquals(ApiErrorType.INVALID_PARAMETER, childErr1.errorType);
		assertTrue(childErr1.hasChildError());
		assertEquals(0, childErr1.index);
		ApiParamError childErr2 = childErr1.childParamError;
		assertNull(childErr2.keyName);
		assertNull(childErr2.displayName);
		assertEquals(ApiErrorType.INVALID_PARAMETER, childErr2.errorType);
		assertFalse(childErr2.hasChildError());
		assertEquals(1, childErr2.index);
	}

	@Test
	public void testRequireList() throws Exception {
		String keyName = "key";
		ApiArrayOrListParam<Map<String, Object>, Integer> listParam =
			ApiArrayOrListParamBuilder.mapInputBuilder(keyName, keyName, Integer.class)
			                          .addIndexChecks(IntegerChecks.valueGreaterThan(10))
			                          .setRequireList(true)
			                          .build();

		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put(keyName, new ArrayList<>(Arrays.asList(15,20)));

		ApiArrayOrListCheckResult passingCheckResult = listParam.check(requestParams);
		assertTrue(passingCheckResult.successful());
		assertEquals(keyName, passingCheckResult.keyName);

		requestParams.put(keyName, new Integer[]{15,20});
		ApiArrayOrListCheckResult failingCheckResult = listParam.check(requestParams);
		assertTrue(failingCheckResult.failed());
		assertEquals(keyName, failingCheckResult.error.keyName);
		assertEquals(ApiErrorType.CASTING_ERROR, failingCheckResult.error.errorType);
	}

	@Test
	public void testRequireArray() throws Exception {
		String keyName = "key";
		ApiArrayOrListParam<Map<String, Object>, Integer> arrayParam =
			ApiArrayOrListParamBuilder.mapInputBuilder(keyName, keyName, Integer.class)
			                          .addIndexChecks(IntegerChecks.valueGreaterThan(10))
			                          // just testing changing it..
			                          .setRequireList(true)
			                          .setRequireList(false)
			                          .setRequireArray(true)
			                          .build();

		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put(keyName, new Integer[]{15,20});

		ApiArrayOrListCheckResult passingCheckResult = arrayParam.check(requestParams);
		assertTrue(passingCheckResult.successful());
		assertEquals(keyName, passingCheckResult.keyName);

		requestParams.put(keyName, new ArrayList<>(Arrays.asList(15,20)));

		ApiArrayOrListCheckResult failingCheckResult = arrayParam.check(requestParams);
		assertTrue(failingCheckResult.failed());
		assertEquals(keyName, failingCheckResult.error.keyName);
		assertEquals(ApiErrorType.CASTING_ERROR, failingCheckResult.error.errorType);
	}


	@Test
	public void testArrayOfLists() throws Exception {
		ApiArrayOrListParam<List<Integer>, Integer> innerListParam =
			ApiArrayOrListParamBuilder.innerListBuilder(Integer.class)
			                          .addIndexChecks(IntegerChecks.valueGreaterThan(10))
			                          .build();

		String keyName = "arrayOfLists";
		ApiArrayOrListParam<Map<String, Object>, List<Integer>> arrayParam =
			ApiArrayOrListParamBuilder.<List<Integer>>mapInputBuilder(keyName, keyName)
				.setInnerArrayOrListParam(innerListParam)
				.build();

		Map<String, Object> requestParams = new HashMap<>();
		List[] arrayOfListsOfIntegers = new List[] {
			Arrays.asList(11,12,13),
			Arrays.asList(20,9999999)
		};
		requestParams.put(keyName, arrayOfListsOfIntegers);

		ApiArrayOrListCheckResult passingCheckResult = arrayParam.check(requestParams);
		assertTrue(passingCheckResult.successful());
		assertEquals(keyName, passingCheckResult.keyName);
		assertFalse(passingCheckResult.hasMapCheckResults());
		assertFalse(passingCheckResult.hasInnerArrayCheckResults());

		/* TEST ARRAY OF ARRAYS */

		/*
		 * Since requireArray or requireList is not set, a list or an array will be fine.
		 * */
		List<List<Integer>> listOfListsOfIntegers = Arrays.asList(
			Arrays.asList(11, 12, 13),
			Arrays.asList(2001, 91919190)
		);

		Map<String, Object> requestParams2 = new HashMap<>();
		requestParams2.put(keyName, listOfListsOfIntegers);

		ApiArrayOrListCheckResult passingCheckResult2 = arrayParam.check(requestParams);
		assertTrue(passingCheckResult2.successful());
		assertEquals(keyName, passingCheckResult2.keyName);
		assertFalse(passingCheckResult2.hasMapCheckResults());
		assertFalse(passingCheckResult2.hasInnerArrayCheckResults());

		/* TEST FAILURE */

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testListOfArraysOfLists() throws Exception {
		ApiListParam<List<String>, String> innermostListParam =
			ApiListParamBuilder.innerListBuilder(String.class)
			                   .addIndexChecks(Check.alwaysPass(String.class))
			                   .build();

		ApiArrayParam<List<String>[], List<String>> arrayParam =
			ApiArrayParamBuilder.<List<String>>innerArrayBuilder()
			                    .setInnerArrayOrListParam(innermostListParam)
			                    .build();

		String keyName = "listOfArraysOfListsOfStrings";
		ApiListParam<Map<String, Object>, List<String>[]> listParam =
			ApiListParamBuilder.<List<String>[]>mapInputBuilder(keyName, keyName)
			                   .setInnerArrayOrListParam(arrayParam)
			                   .build();

		List<List<String>[]> listOfArraysOfListsOfStrings =
			Arrays.asList(
				new List[] {
					Arrays.asList("heyy", "yooo", "noo")
				},
				new List[] {
					Arrays.asList("check", "123")
				},
				new List[] {
					Arrays.asList("hmm")
				}
			);

		Map<String, Object> requestParams = new HashMap();
		requestParams.put(keyName, listOfArraysOfListsOfStrings);

		ApiArrayOrListCheckResult passingCheckResult = listParam.check(requestParams);
		assertTrue(passingCheckResult.successful());

	}

}
