package io.github.bhowell2.apilib;

import io.github.bhowell2.apilib.checks.Check;
import io.github.bhowell2.apilib.checks.DoubleChecks;
import io.github.bhowell2.apilib.checks.IntegerChecks;
import io.github.bhowell2.apilib.checks.StringChecks;
import io.github.bhowell2.apilib.formatters.Formatter;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link ApiListParam} performs almost exactly like {@link io.github.bhowell2.apilib.checks.ListChecks},
 * except that it can also check a list of {@link Map} parameters. This allows for returning a check
 * result
 * @author Blake Howell
 */
public class ApiListParamTests {

	@Test
	public void testConstructorThrowsExceptionOnInvalidArguments() throws Exception {

		ApiListParam<String, List<String>> listParam = ApiListParamBuilder.<String, List<String>>unnamedBuilder()
			.addIndexChecks(StringChecks.lengthEqualTo(5),
			                StringChecks.containsCodePoint(2, "\uD83E\uDD13"))
			.build();

		ApiMapParam mapParam = ApiMapParamBuilder.rootBuilder().build();

		assertThrows(IllegalArgumentException.class, () -> {
			new ApiListParam<>("whate", "ver", null, false, null, null, mapParam, null, listParam);
		}, "Should not be able to create a list with both ApiMapParam (check) and inner list.");

		assertThrows(IllegalArgumentException.class, () -> {
			new ApiListParam<>("whate", "ver", null, false, null, null, mapParam, new ApiMapParam[]{mapParam}, null);
		}, "Should not be able to create a list param with index map checks (i.e., a map check that applies to ALL indices) " +
			             "and individual map checks (i.e., map checks for each index).");
		assertThrows(IllegalArgumentException.class, () -> {

			ApiListParam<String, List<String>> namedListParam = ApiListParamBuilder.<String, List<String>>builder("name", "disp")
				.addIndexChecks(StringChecks.lengthEqualTo(5),
				                StringChecks.containsCodePoint(2, "\uD83E\uDD13"))
				.build();
			new ApiListParam<>("whatever", "", null, false, null, null, null, null, namedListParam);
		}, "Should not be able to add inner array/list param that has a key name.");
	}

	/**
	 * This tests taking in a list directly. This usually will not happen unless the
	 * {@link ApiListParam} is nested, but testing for good measure.
	 */
	@Test
	public void testOrderOneListDirectly() throws Exception {
		// strings must be length 5 and contain 2 nerd faces
		ApiListParam<String, List<String>> listParam = ApiListParamBuilder.<String, List<String>>unnamedBuilder()
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
	public void testOrderOneListOfMaps() throws Exception {
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
		ApiListParam<Map<String, Object>, Map<String, Object>> listParam =
			ApiListParamBuilder.<Map<String, Object>, Map<String, Object>>builder(listKeyName, listKeyName)
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
	public void testOrderTwoOfMaps() throws Exception {
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

		ApiListParam<Map<String, Object>, List<Map<String, Object>>> innerListParam =
			ApiListParamBuilder.<Map<String, Object>, List<Map<String, Object>>>unnamedBuilder()
				.setIndexMapCheck(mapParam)
				.build();

		String listOfListsKeyName = "listOfListsOfMaps";
		ApiListParam<List<Map<String, Object>>, Map<String, Object>> listOfListsOfMapsParam =
			ApiListParamBuilder.<List<Map<String, Object>>, Map<String, Object>>builder(listOfListsKeyName, listOfListsKeyName)
				.setInnerListParam(innerListParam)
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



}
