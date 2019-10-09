package io.github.bhowell2.apilib.checks;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Blake Howell
 */
public class ArrayChecksTests extends ChecksTestBase {

	@Test
	public void testCheckArray() throws Exception {
		Check<Integer[]> check = ArrayChecks.checkArray(IntegerChecks.valueGreaterThan(0),
		                                                IntegerChecks.valueLessThan(100));

		// successful
		assertCheckSuccessful(check.check(new Integer[]{1, 50, 99}));
		// unsuccessful
		Check.Result failedResult = check.check(new Integer[]{1, 98, 99, 100});
		assertCheckFailed(failedResult);
		assertTrue(failedResult.failureMessage.contains("at index 3"));
	}

	@Test
	public void testCheckList() throws Exception {
		Check<List<String>> check = ArrayChecks.checkList(Arrays.asList(StringChecks.lengthGreaterThan(1),
		                                                                StringChecks.containsCodePoint(1, "i")));
		// successful
		assertCheckSuccessful(check.check(Arrays.asList("heyyy i")));
		assertCheckSuccessful(check.check(Arrays.asList("heyyy i", "ii")));
		// unsuccessful
		Check.Result failedResult = check.check(Arrays.asList("iiii", "yoi", "i"));
		assertCheckFailed(failedResult);
		assertTrue(failedResult.failureMessage.contains("at index 2"));
	}

	@Test
	public void testCheckArrayEvenAndOdd() throws Exception {
		Check<Integer[]> check = ArrayChecks.checkArray(Arrays.asList(IntegerChecks.valueIsEven()),
		                                                Arrays.asList(IntegerChecks.valueIsOdd()));
		// successful
		assertCheckSuccessful(check.check(new Integer[]{0, 1, 2, 3, 4}));
		// unsuccessful
		Check.Result failedResult = check.check(new Integer[]{1, 1, 2, 3, 4});
		assertCheckFailed(failedResult);
		assertTrue(failedResult.failureMessage.contains("at index 0"));
	}

	@Test
	public void testCheckListEvenAndOdd() throws Exception {
		Check<List<String>> check = ArrayChecks.checkList(Arrays.asList(StringChecks.codePointCountGreaterThan(3)),
		                                                  Arrays.asList(StringChecks.beginsWithStrings("one", "two")));
		// successful
		assertCheckSuccessful(check.check(Arrays.asList("codepoint count > 3",
		                                                "one - had to begin with",
		                                                "count",
		                                                "two")));
		// unsuccessful
		Check.Result failedResult = check.check(Arrays.asList("count > 3",
		                                                      "does not begin with 'one'"));
		assertCheckFailed(failedResult);
		assertTrue(failedResult.failureMessage.contains("at index 1"));
	}

	@Test
	public void testCheckArrayOfArrays() throws Exception {
		Check<Integer[][]> check = ArrayChecks.checkArrayOfArrays(IntegerChecks.valueIsEven(),
		                                                          IntegerChecks.valueGreaterThan(10));
		// successful
		assertCheckSuccessful(check.check(new Integer[][]{
			new Integer[]{12, 14, 98},
			new Integer[]{100, 1000, 1234}
		}));
		Check.Result failedResult = check.check(new Integer[][]{
			new Integer[]{12, 14, 98},
			new Integer[]{100, 1000, 123}
		});
		assertCheckFailed(failedResult);
		assertTrue(failedResult.failureMessage.contains("at position (1,2)"));
	}

	@Test
	public void testCheckListOfLists() throws Exception {
		Check<List<List<String>>> check = ArrayChecks.checkListOfLists(
			StringChecks.emptyOrMeetsChecks(
				StringChecks.limitConsecutiveCodePoints(3),
				StringChecks.codePointCountGreaterThan(3)
			)
		);
		assertCheckSuccessful(
			check.check(Arrays.asList(
				Arrays.asList("does not repeat codepoint more than 3 times", "is fine", "length > 3"),
				Arrays.asList("more stuff", "whatever"),
				Arrays.asList("just one"),
				Arrays.asList("") // must be empty or meet checks
			))
		);
		Check.Result failedResult =
			check.check(Arrays.asList(
				Arrays.asList("does not repeat codepoint more than 3 times", "is fine", "length > 3"),
				Arrays.asList("more stuff", "whatever"),
				Arrays.asList("....")  // should cause failure, 4 repeated codepoints
			));
		assertCheckFailed(failedResult);
		assertTrue(failedResult.failureMessage.contains("(consecutively)"));
		assertTrue(failedResult.failureMessage.contains("more than 3"));
		assertTrue(failedResult.failureMessage.contains("at position (2,0)"));
	}

	@Test
	public void testCheckArrayAtIndex() throws Exception {
		Check<String[]> check = ArrayChecks.checkArrayAtIndex(
			Arrays.asList(
				Arrays.asList(StringChecks.lengthGreaterThan(0), StringChecks.lengthLessThan(5)),
				Arrays.asList(StringChecks.lengthLessThanOrEqualTo(3)),
				Arrays.asList(StringChecks.emptyOrWhitespaceOrMeetsChecks(StringChecks.containsCodePoint(1, "!")))
			)
		);
		// successful
		assertCheckSuccessful(check.check(new String[]{"1234", "123", " "}));
		assertCheckSuccessful(check.check(new String[]{"1234", "123", ""}));
		assertCheckSuccessful(check.check(new String[]{"1234", "123", "! and anything else"}));
		// unsuccessful
		Check.Result failedResult = check.check(new String[]{"", "123", " "});
		assertCheckFailed(failedResult);
		assertTrue(failedResult.failureMessage.contains("at index 0"));
		Check.Result failedResult2 = check.check(new String[]{"123", "123", "does not contain exclimation"});
		assertCheckFailed(failedResult2);
		assertTrue(failedResult2.failureMessage.contains("at index 2"));

		Check.Result failFromIndexTooLarge = check.check(new String[]{"", "123", " ", "one too many"});
		assertCheckFailed(failFromIndexTooLarge);
		assertTrue(failFromIndexTooLarge.failureMessage.contains("Array is not of proper size"));
	}

	@Test
	public void testCheckListAtIndex() throws Exception {
		Check<List<String>> check = ArrayChecks.checkListAtIndex(
			Arrays.asList(
				Arrays.asList(StringChecks.lengthGreaterThan(0), StringChecks.lengthLessThan(5)),
				Arrays.asList(StringChecks.lengthLessThanOrEqualTo(3)),
				Arrays.asList(StringChecks.emptyOrWhitespaceOrMeetsChecks(StringChecks.containsCodePoint(1, "!")))
			)
		);
		// successful
		assertCheckSuccessful(check.check(Arrays.asList("1234", "123", " ")));
		assertCheckSuccessful(check.check(Arrays.asList("1234", "123", "")));
		assertCheckSuccessful(check.check(Arrays.asList("1234", "123", "! and anything else")));
		// unsuccessful
		Check.Result failedResult = check.check(Arrays.asList("", "123", " "));
		assertCheckFailed(failedResult);
		assertTrue(failedResult.failureMessage.contains("at index 0"));
		Check.Result failedResult2 = check.check(Arrays.asList("123", "123", "does not contain exclimation"));
		assertCheckFailed(failedResult2);
		assertTrue(failedResult2.failureMessage.contains("at index 2"));

		Check.Result failFromIndexTooLarge = check.check(Arrays.asList("", "123", " ", "one too many"));
		assertCheckFailed(failFromIndexTooLarge);
		assertTrue(failFromIndexTooLarge.failureMessage.contains(" is not of proper size"));
	}

//	@Test
//	public void testCheckArrayWithMapParams() throws Exception {
//		String nameKey = "name";
//		String passwordKey = "password";
//		ApiSingleParam<String> name = ApiSingleParamBuilder.builder(nameKey, String.class)
//		                                                   .addCheck(StringChecks.codePointCountGreaterThan(2))
//		                                                   .build();
//		ApiSingleParam<String> password = ApiSingleParamBuilder.builder(passwordKey, String.class)
//		                                                       .addCheck(StringChecks.codePointCountGreaterThan(5))
//		                                                       .build();
//		ApiMapParam mapParam = ApiMapParamBuilder.rootBuilder()
//		                                         .addRequiredSingleParams(name, password)
//		                                         .build();
//		// not this one uses arrays, not lists
//		String arrayKey = "array";
//		ApiCustomParam arrayWithMapsParam = ArrayChecks.checkArrayWithMapParams(arrayKey, "An array", mapParam);
//
//		Map<String, Object> pos0 = new HashMap<>();
//		pos0.put(nameKey, "longer than 2");
//		pos0.put(passwordKey, "longer than 5");
//
//		Map<String, Object> pos1 = new HashMap<>();
//		pos1.put(nameKey, "longer than 2 again");
//		pos1.put(passwordKey, "longer than 5 and one!");
//
//		Map<String, Object> passingParams = new HashMap<>();
//		passingParams.put(arrayKey, new Map[]{pos0, pos1});
//
//		ApiCustomParamCheckResult passingResult = arrayWithMapsParam.check(passingParams);
//		assertTrue(passingResult.successful());
//		assertTrue(passingResult.providedParamKeyNames.contains(arrayKey));
//		assertEquals(2, passingResult.arrayMapParams.size());
//
//		Map<String, Object> pos2 = new HashMap<>();
//		pos2.put(nameKey, "long enough");
//		pos2.put(passwordKey, "short");   // must be greater than 5, is not
//
//		Map<String, Object> failingParams = new HashMap<>();
//		failingParams.put(arrayKey, new Map[]{pos0, pos1, pos2});
//
//		ApiCustomParamCheckResult failingResult = arrayWithMapsParam.check(failingParams);
//		assertTrue(failingResult.failed());
//		assertTrue(failingResult.apiParamError.templatedErrorMessage.contains("at index 2"));
//	}
//
//	@Test
//	public void testCheckListWithMapParams() throws Exception {
//		String nameKey = "name";
//		String passwordKey = "password";
//		ApiSingleParam<String> name = ApiSingleParamBuilder.builder(nameKey, String.class)
//		                                                   .addCheck(StringChecks.codePointCountGreaterThan(2))
//		                                                   .build();
//		ApiSingleParam<String> password = ApiSingleParamBuilder.builder(passwordKey, String.class)
//		                                                       .addCheck(StringChecks.codePointCountGreaterThan(5))
//		                                                       .build();
//		ApiMapParam mapParam = ApiMapParamBuilder.rootBuilder()
//		                                         .addRequiredSingleParams(name, password)
//		                                         .build();
//		// not this one uses arrays, not lists
//		String arrayKey = "array";
//		ApiCustomParam listWithMapsParam = ArrayChecks.checkListWithMapParams(arrayKey, "An array", mapParam);
//
//		Map<String, Object> pos0 = new HashMap<>();
//		pos0.put(nameKey, "longer than 2");
//		pos0.put(passwordKey, "longer than 5");
//
//		Map<String, Object> pos1 = new HashMap<>();
//		pos1.put(nameKey, "longer than 2 again");
//		pos1.put(passwordKey, "longer than 5 and one!");
//
//		Map<String, Object> passingParams = new HashMap<>();
//		passingParams.put(arrayKey, Arrays.asList(pos0, pos1));
//
//		ApiCustomParamCheckResult passingResult = listWithMapsParam.check(passingParams);
//		assertTrue(passingResult.successful());
//		assertTrue(passingResult.providedParamKeyNames.contains(arrayKey));
//		assertEquals(2, passingResult.arrayMapParams.size());
//
//		Map<String, Object> pos2 = new HashMap<>();
//		pos2.put(nameKey, "long enough");
//		pos2.put(passwordKey, "short");   // must be greater than 5, is not
//
//		Map<String, Object> failingParams = new HashMap<>();
//		failingParams.put(arrayKey, Arrays.asList(pos0, pos1, pos2));
//
//		ApiCustomParamCheckResult failingResult = listWithMapsParam.check(failingParams);
//		assertTrue(failingResult.failed());
//		assertTrue(failingResult.apiParamError.templatedErrorMessage.contains("at index 2"));
//	}

}
