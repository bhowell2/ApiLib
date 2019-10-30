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
public class ApiCollectionParamTests {

	@SuppressWarnings("unchecked")
	@Test
	public void testBuildThrowsExceptionOnInvalidArguments() throws Exception {

		ApiListParam<List<String>, String> listParam =
			ApiListParam.<List<String>, String>unnamedBuilder()
				.addIndexChecks(StringChecks.lengthEqualTo(5),
				                StringChecks.containsCodePoint(2, "\uD83E\uDD13"))
				.build();

		ApiMapParam mapParam = ApiMapParam.builder().build();

		/*
		 * Make sure these cases do not throw as they are used below.
		 * */
		assertDoesNotThrow(() -> {
			// check does not throw because it's used
			ApiListParam.<Map<String, Object>, List<String>>builder("whatever")
				.setIndexMapCheck(mapParam)
				.build();

			ApiListParam.<Map<String, Object>, List<String>>builder("whatever")
				.setInnerCollectionParam(listParam)
				.build();
		});


		assertThrows(IllegalArgumentException.class, () -> {
			ApiListParam.<Map<String, Object>, List<String>>builder("whatever")
				.setIndexMapCheck(mapParam)
				.setInnerCollectionParam(listParam)
				.build();
		}, "Should not be able to create a list with both ApiMapParam (check) and inner list.");

		assertThrows(IllegalArgumentException.class, () -> {
			ApiListParam.<Map<String, Object>, List<String>>builder("whatever")
				.setIndexMapCheck(mapParam)
				.setIndividualIndexMapChecks(Arrays.asList(mapParam))
				.build();
		}, "Should not be able to create a list param with index map checks (i.e., a map check that applies to ALL indices) " +
			             "and individual map checks (i.e., map checks for each index).");

		assertThrows(IllegalArgumentException.class, () -> {
			ApiListParam<List<String>, String> namedListParam =
				ApiListParam.<List<String>, String>builder("name")
					.addIndexChecks(StringChecks.lengthEqualTo(5),
					                StringChecks.containsCodePoint(2, "\uD83E\uDD13"))
					.build();
			ApiListParam.<Map<String, Object>, List<String>>builder("whatever")
				.setIndexMapCheck(mapParam)
				.setInnerCollectionParam(namedListParam)
				.build();
		}, "Should not be able to add inner array/list param that has a key name.");

		assertThrows(IllegalArgumentException.class, () -> {
			ApiListParam.<Map<String, Object>, List<String>>builder("whatever")
				.build();
		}, "Should not be able to create array param without checks.");
	}

	@Test
	public void testArrayParamFailsWithList() throws Exception {
		String keyName = "key";
		ApiArrayParam<Map<String, Object>, String> arrayParam =
			ApiArrayParam.mapInputBuilder(keyName, String.class)
			             .addIndexChecks(Check.alwaysPass(String.class))
			             .build();
		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put(keyName, Arrays.asList("heyyy"));
		ApiCollectionParam.Result result = arrayParam.check(requestParams);
		assertTrue(result.failed());
		assertEquals(keyName, result.error.keyName);
		assertEquals(ApiErrorType.CASTING_ERROR, result.error.errorType);

		requestParams.put(keyName, new String[]{"heyy"});

		ApiCollectionParam.Result passingResult = arrayParam.check(requestParams);
		assertTrue(passingResult.successful());

		ApiArrayParam<String[], String> arrayParamDirect =
			ApiArrayParam.innerArrayBuilder(String.class)
			             .addIndexChecks(Check.alwaysPass(String.class))
			             .build();

		ApiCollectionParam.Result passingResult2 = arrayParamDirect.check(new String[]{"Heyy"});
		assertTrue(passingResult2.successful());

	}

	@Test
	public void testListParamFailsWithArray() throws Exception {
		String keyName = "key";
		ApiListParam<Map<String, Object>, String> listParam =
			ApiListParam.mapInputBuilder(keyName, String.class)
			            .addIndexChecks(Check.alwaysPass(String.class))
			            .build();
		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put(keyName, new String[]{"heyy"});
		ApiCollectionParam.Result result = listParam.check(requestParams);
		assertTrue(result.failed());
		assertEquals(keyName, result.error.keyName);
		assertEquals(ApiErrorType.CASTING_ERROR, result.error.errorType);

		requestParams.put(keyName, Arrays.asList("heyy"));
		ApiCollectionParam.Result passingResult = listParam.check(requestParams);
		assertTrue(passingResult.successful());

		ApiListParam<List<String>, String> listParamDirect =
			ApiListParam.innerListBuilder(String.class)
			            .addIndexChecks(Check.alwaysPass(String.class))
			            .build();

		ApiCollectionParam.Result passingResult2 = listParamDirect.check(Arrays.asList("heyy"));
		assertTrue(passingResult2.successful());

	}

	/**
	 * This tests taking in a list directly. This usually will not happen unless the
	 * {@link ApiCollectionParam} is nested, but testing for good measure.
	 */
	@Test
	public void testListOfOrderOneDirectly() throws Exception {
		// strings must be length 5 and contain 2 nerd faces
		ApiListParam<List<String>, String> listParam =
			ApiListParam.<List<String>, String>unnamedBuilder()
				.addIndexChecks(StringChecks.lengthEqualTo(5),
				                StringChecks.containsCodePoint(2, "\uD83E\uDD13"))
				.build();
		List<String> list = new ArrayList<>(3);
		list.add("1🤓🤓");
		list.add("a🤓🤓");
		list.add("🤓🤓b");
		ApiCollectionParam.Result passingResult = listParam.check(list);
		assertTrue(passingResult.successful());
		assertFalse(passingResult.hasKeyName());
		assertFalse(passingResult.hasMapResults());
		assertFalse(passingResult.hasInnerCollectionResults());
	}

	@Test
	public void testArrayOfOrderOneDirectly() throws Exception {
		// strings must be length 5 and contain 2 nerd faces
		ApiArrayParam<String[], String> listParam =
			ApiArrayParam.<String[], String>unnamedBuilder()
				.addIndexChecks(StringChecks.lengthEqualTo(5),
				                StringChecks.containsCodePoint(2, "\uD83E\uDD13"))
				.build();
		String[] array = new String[]{
			"1🤓🤓",
			"a🤓🤓",
			"🤓🤓b"
		};
		ApiCollectionParam.Result passingResult = listParam.check(array);
		assertTrue(passingResult.successful());
		assertFalse(passingResult.hasKeyName());
		assertFalse(passingResult.hasMapResults());
		assertFalse(passingResult.hasInnerCollectionResults());
	}

	@Test
	public void testListOfOrderOneOfMaps() throws Exception {
		ApiMapParam mapParam =
			ApiMapParam.builder()
			           .addRequiredSingleParams(ApiSingleParam.builder("p1", String.class)
			                                                  .addChecks(Check.alwaysPass(String.class))
			                                                  .build(),
			                                    ApiSingleParam.builder("p2", Integer.class)
			                                                  .addChecks(IntegerChecks.valueGreaterThan(55),
			                                                             IntegerChecks.valueLessThan(100))
			                                                  .build())
			           .addOptionalSingleParams(
				           ApiSingleParam.builder("p3", Double.class)
				                         .addChecks(DoubleChecks.valueEqualTo(3.14))
				                         // convert string to double
				                         .addFormatters((String d) -> Formatter.Result.success(Double.valueOf(d)))
				                         .build())
			           .build();

		String listKeyName = "listOfMaps";
		ApiListParam<Map<String, Object>, Map<String, Object>> listParam =
			ApiListParam.<Map<String, Object>, Map<String, Object>>builder(listKeyName)
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

		ApiCollectionParam.Result passingResult = listParam.check(requestParams);
		assertTrue(passingResult.successful());
		assertFalse(passingResult.hasInnerCollectionResults());
		assertTrue(passingResult.hasMapResults());

		List<ApiMapParam.Result> indexMapResults = passingResult.mapResults;
		assertEquals(3, indexMapResults.size());

		// index0
		assertEquals(2, indexMapResults.get(0).checkedKeyNames.size());
		assertTrue(indexMapResults.get(0).checkedKeyNames.contains("p1"));
		assertTrue(indexMapResults.get(0).checkedKeyNames.contains("p2"));
		assertFalse(indexMapResults.get(0).checkedKeyNames.contains("p3"));

		// index1
		assertEquals(2, indexMapResults.get(1).checkedKeyNames.size());
		assertTrue(indexMapResults.get(1).checkedKeyNames.contains("p1"));
		assertTrue(indexMapResults.get(1).checkedKeyNames.contains("p2"));
		assertFalse(indexMapResults.get(1).checkedKeyNames.contains("p3"));

		// index2
		assertEquals(3, indexMapResults.get(2).checkedKeyNames.size());
		assertTrue(indexMapResults.get(2).checkedKeyNames.contains("p1"));
		assertTrue(indexMapResults.get(2).checkedKeyNames.contains("p2"));
		assertTrue(indexMapResults.get(2).checkedKeyNames.contains("p3"));
		// ensure formatter inserted
		assertTrue(index2.get("p3") instanceof Double);
		assertTrue(index2.get("p3").equals(3.14));


		/* TEST FAILURE */

		// can just remove one of the required fields. they are checked before the optional that is formatted
		index1.remove("p2");
		ApiCollectionParam.Result failingResult = listParam.check(requestParams);
		assertFalse(failingResult.successful());
		ApiParamError listError = failingResult.error;
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

	/**
	 * Tests that a list of lists of maps checks as expected. This will include
	 * @throws Exception
	 */
	@Test
	public void testListOfOrderTwoOfMaps() throws Exception {
		// tests a list of lists of maps. uses same map as orderOne test.
		ApiMapParam mapParam =
			ApiMapParam.builder()
			           .addRequiredSingleParams(ApiSingleParam.builder("p1", String.class)
			                                                  .addChecks(Check.alwaysPass(String.class))
			                                                  .build(),
			                                    ApiSingleParam.builder("p2", Integer.class)
			                                                  .addChecks(IntegerChecks.valueGreaterThan(55),
			                                                             IntegerChecks.valueLessThan(100))
			                                                  .build())
			           .addOptionalSingleParams(
				           ApiSingleParam.builder("p3", Double.class)
				                         .addChecks(DoubleChecks.valueEqualTo(3.14))
				                         // convert string to double
				                         .addFormatters((String d) -> Formatter.Result.success(Double.valueOf(d)))
				                         .build())
			           .build();

		ApiListParam<List<Map<String, Object>>, Map<String, Object>> innerListParam =
			ApiListParam.<List<Map<String, Object>>, Map<String, Object>>unnamedBuilder()
				.setIndexMapCheck(mapParam)
				.build();

		String listOfListsKeyName = "listOfListsOfMaps";
		ApiListParam<Map<String, Object>, List<Map<String, Object>>> listOfListsOfMapsParam =
			ApiListParam.<Map<String, Object>, List<Map<String, Object>>>builder(listOfListsKeyName)
				.setInnerCollectionParam(innerListParam)
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

		ApiCollectionParam.Result passingOuterResult = listOfListsOfMapsParam.check(requestParams);
		assertTrue(passingOuterResult.successful());
		assertTrue(passingOuterResult.hasInnerCollectionResults());
		assertFalse(passingOuterResult.hasMapResults(),
		            "Should have map check results on the INNER array, not this one.");

		List<ApiCollectionParam.Result> passingInnerResults = passingOuterResult.innerCollectionResults;
		assertEquals(2, passingInnerResults.size());
		for (int i = 0; i < passingInnerResults.size(); i++) {
			ApiCollectionParam.Result result = passingInnerResults.get(i);
			assertTrue(result.successful());
			assertFalse(result.hasInnerCollectionResults());
			assertTrue(result.hasMapResults());
			assertEquals(3, result.mapResults.size());

			List<ApiMapParam.Result> indexMapResults = result.mapResults;

			// index0
			assertEquals(2, indexMapResults.get(0).checkedKeyNames.size());
			assertTrue(indexMapResults.get(0).checkedKeyNames.contains("p1"));
			assertTrue(indexMapResults.get(0).checkedKeyNames.contains("p2"));
			assertFalse(indexMapResults.get(0).checkedKeyNames.contains("p3"));

			// index1
			assertEquals(2, indexMapResults.get(1).checkedKeyNames.size());
			assertTrue(indexMapResults.get(1).checkedKeyNames.contains("p1"));
			assertTrue(indexMapResults.get(1).checkedKeyNames.contains("p2"));
			assertFalse(indexMapResults.get(1).checkedKeyNames.contains("p3"));

			// index2
			assertEquals(3, indexMapResults.get(2).checkedKeyNames.size());
			assertTrue(indexMapResults.get(2).checkedKeyNames.contains("p1"));
			assertTrue(indexMapResults.get(2).checkedKeyNames.contains("p2"));
			assertTrue(indexMapResults.get(2).checkedKeyNames.contains("p3"));
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
		ApiCollectionParam.Result failingResult = listOfListsOfMapsParam.check(requestParams);
		assertTrue(failingResult.failed());

		ApiParamError error = failingResult.error;
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
		ApiArrayParam<String[], String> orderThreeParam =
			ApiArrayParam.innerArrayBuilder(String.class)
			             .addIndexChecks(StringChecks.lengthGreaterThan(1),
			                             StringChecks.lengthLessThan(10))
			             .build();

		ApiArrayParam<String[][], String[]> orderTwoParam =
			ApiArrayParam.innerArrayBuilder(String[].class)
			             .setInnerCollectionParam(orderThreeParam)
			             .build();

		String keyName = "arrayOfArraysOfArraysOfStrings";
		ApiArrayParam<Map<String, Object>, String[][]> orderOneParam =
			ApiArrayParam.mapInputBuilder(keyName, String[][].class)
			             .setDisplayName(keyName)
			             .setInnerCollectionParam(orderTwoParam)
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

		ApiCollectionParam.Result passingResult = orderOneParam.check(requestParams);
		assertTrue(passingResult.successful());
		assertEquals(keyName, passingResult.keyName);
		assertFalse(passingResult.hasMapResults());
		assertFalse(passingResult.hasInnerCollectionResults());

		/* TEST FAILURE */

		arrayWithinArrayStringParams[1][0][1] = "too long and should fail";

		ApiCollectionParam.Result failingResult = orderOneParam.check(requestParams);
		assertTrue(failingResult.failed());
		ApiParamError err = failingResult.error;
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
		ApiListParam<Map<String, Object>, Integer> listParam =
			ApiListParam.mapInputBuilder(keyName, Integer.class)
			            .addIndexChecks(IntegerChecks.valueGreaterThan(10))
			            .build();

		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put(keyName, new ArrayList<>(Arrays.asList(15,20)));

		ApiCollectionParam.Result passingResult = listParam.check(requestParams);
		assertTrue(passingResult.successful());
		assertEquals(keyName, passingResult.keyName);

		requestParams.put(keyName, new Integer[]{15,20});
		ApiCollectionParam.Result failingResult = listParam.check(requestParams);
		assertTrue(failingResult.failed());
		assertEquals(keyName, failingResult.error.keyName);
		assertEquals(ApiErrorType.CASTING_ERROR, failingResult.error.errorType);
	}

	@Test
	public void testRequireArray() throws Exception {
		String keyName = "key";
		ApiArrayParam<Map<String, Object>, Integer> arrayParam =
			ApiArrayParam.mapInputBuilder(keyName, Integer.class)
			             .addIndexChecks(IntegerChecks.valueGreaterThan(10))
			             .build();

		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put(keyName, new Integer[]{15,20});

		ApiCollectionParam.Result passingResult = arrayParam.check(requestParams);
		assertTrue(passingResult.successful());
		assertEquals(keyName, passingResult.keyName);

		requestParams.put(keyName, new ArrayList<>(Arrays.asList(15,20)));

		ApiCollectionParam.Result failingResult = arrayParam.check(requestParams);
		assertTrue(failingResult.failed());
		assertEquals(keyName, failingResult.error.keyName);
		assertEquals(ApiErrorType.CASTING_ERROR, failingResult.error.errorType);
	}


	@Test
	public void testArrayOfLists() throws Exception {
		ApiListParam<List<Integer>, Integer> innerListParam =
			ApiListParam.innerListBuilder(Integer.class)
			            .addIndexChecks(IntegerChecks.valueGreaterThan(10))
			            .build();

		String keyName = "arrayOfLists";
		ApiArrayParam<Map<String, Object>, List<Integer>> arrayParam =
			ApiArrayParam.<List<Integer>>mapInputBuilder(keyName)
				.setInnerCollectionParam(innerListParam)
				.build();

		Map<String, Object> requestParams = new HashMap<>();
		List[] arrayOfListsOfIntegers = new List[] {
			Arrays.asList(11,12,13),
			Arrays.asList(20,9999999)
		};
		requestParams.put(keyName, arrayOfListsOfIntegers);

		ApiCollectionParam.Result passingResult = arrayParam.check(requestParams);
		assertTrue(passingResult.successful());
		assertEquals(keyName, passingResult.keyName);
		assertFalse(passingResult.hasMapResults());
		assertFalse(passingResult.hasInnerCollectionResults());

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

		ApiCollectionParam.Result passingResult2 = arrayParam.check(requestParams);
		assertTrue(passingResult2.successful());
		assertEquals(keyName, passingResult2.keyName);
		assertFalse(passingResult2.hasMapResults());
		assertFalse(passingResult2.hasInnerCollectionResults());

		/* TEST FAILURE */

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testListOfArraysOfLists() throws Exception {
		ApiListParam<List<String>, String> innermostListParam =
			ApiListParam.innerListBuilder(String.class)
			            .addIndexChecks(Check.alwaysPass(String.class))
			            .build();

		ApiArrayParam<List<String>[], List<String>> arrayParam =
			ApiArrayParam.<List<String>>innerArrayBuilder()
				.setInnerCollectionParam(innermostListParam)
				.build();

		String keyName = "listOfArraysOfListsOfStrings";
		ApiListParam<Map<String, Object>, List<String>[]> listParam =
			ApiListParam.<List<String>[]>mapInputBuilder(keyName)
				.setInnerCollectionParam(arrayParam)
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

		ApiCollectionParam.Result passingResult = listParam.check(requestParams);
		assertTrue(passingResult.successful());

	}

	@Test
	public void testIndividualIndexChecks() throws Exception {
		String keyName = "key";
		ApiListParam<Map<String, Object>, String> arrayOrListParam =
			ApiListParam.mapInputBuilder(keyName, String.class)
			            .setIndividualIndexChecksWrapCheck(
				            Arrays.asList(
					            Arrays.asList(
						            StringChecks.lengthEqualTo(1)
					            ),
					            Arrays.asList(
						            StringChecks.lengthEqualTo(2)
					            ),
					            Arrays.asList(
						            StringChecks.lengthEqualTo(3)
					            )
				            ))
			            .build();
		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put(keyName, Arrays.asList("1", "12", "123"));
		ApiCollectionParam.Result passingResult = arrayOrListParam.check(requestParams);
		assertTrue(passingResult.successful());
		assertEquals(keyName, passingResult.keyName);
		assertFalse(passingResult.hasInnerCollectionResults());
		assertFalse(passingResult.hasMapResults());

		// should cause failure
		requestParams.put(keyName, Arrays.asList("1", "123", "123"));
		ApiCollectionParam.Result failingResult = arrayOrListParam.check(requestParams);
		assertTrue(failingResult.failed());
		assertEquals(keyName, failingResult.error.keyName);
		assertEquals(ApiErrorType.INVALID_PARAMETER, failingResult.error.errorType);
		assertEquals(1, failingResult.error.index);
	}

	@Test
	public void shouldFailWhenLengthIsInvalid() throws Exception {
		String key = "key";
	  ApiArrayParam<Map<String, Object>, String> arrayParam =
		  ApiArrayParam.<Map<String, Object>, String>builder(key)
			  .setSizeCheck(IntegerChecks.valueGreaterThan(3))
			  .addIndexChecks(StringChecks.lengthGreaterThan(5))
			  .build();

	  Map<String, Object> requestParams = new HashMap<>();
		requestParams.put(key, new String[]{"index string is longer than 5", "but collecion size too small"});

		ApiCollectionParam.Result result = arrayParam.check(requestParams);
		assertTrue(result.failed());

		requestParams.put(key, new String[]{"index string is longer than 5", "but collecion size too small",
		                                    "still too small collection size"});

		ApiCollectionParam.Result result2 = arrayParam.check(requestParams);
		assertTrue(result2.failed());

		requestParams.put(key, new String[]{"index string is longer than 5", "but collecion size too small",
		                                    "still too small collection size", "this will work."});

		ApiCollectionParam.Result result3 = arrayParam.check(requestParams);
		assertTrue(result3.successful());

		// this will fail because string length is too short
		requestParams.put(key, new String[]{"index string is longer than 5", "but collecion size too small",
		                                    "still too small collection size", "this will work.", "fail"});

		ApiCollectionParam.Result result4 = arrayParam.check(requestParams);
		assertTrue(result4.failed());
	}

}
