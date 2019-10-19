package io.github.bhowell2.apilib.checks;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Blake Howell
 */
public class ListChecksTests extends ChecksTestBase {

	@Test
	public void testCheckList() throws Exception {
		Check<List<String>> check = ListChecks.checkList(Arrays.asList(StringChecks.lengthGreaterThan(1),
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
	public void testCheckListEvenAndOdd() throws Exception {
		Check<List<String>> check = ListChecks.checkList(Arrays.asList(StringChecks.codePointCountGreaterThan(3)),
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
	public void testCheckListOfLists() throws Exception {
		Check<List<List<String>>> check = ListChecks.checkListOfLists(
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
	public void testCheckListAtIndex() throws Exception {
		Check<List<String>> check = ListChecks.checkListWithIndividualIndexChecks(
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

}
