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
	public void testNestedArrays() throws Exception {
		Check<String[]> thirdLevelArray = ArrayChecks.checkArray(StringChecks.lengthGreaterThan(5));
		Check<String[][]> secondLevelArrayChecks = ArrayChecks.checkArray(thirdLevelArray);
		Check<String[][][]> topLevelArrayCheck = ArrayChecks.checkArray(secondLevelArrayChecks);
		String[][][] threeDimensionalPassingArray = new String[][][] {
			new String[][] {
				new String[] {
					"123456", "abc123"
				},
				new String[] {
					"this will pass"
				},
				new String[] {
					"this passes too"
				}
			}
		};
		assertCheckSuccessful(topLevelArrayCheck.check(threeDimensionalPassingArray));

		String[][][] threeDimensionalFailingArray1 = new String[][][] {
			new String[][] {
				new String[] {
					"123456", "fail"
				},
				// should fail from "fail" above
				new String[] {
					"this will pass"
				},
				new String[] {
					"this passes too"
				}
			}
		};
		assertCheckFailed(topLevelArrayCheck.check(threeDimensionalFailingArray1));

		String[][][] threeDimensionalFailingArray2 = new String[][][] {
			new String[][] {
				new String[] {
					"123456", "abc123"
				},
				new String[] {
					"fail"
				},
				new String[] {
					"this passes too"
				}
			}
		};
		assertCheckFailed(topLevelArrayCheck.check(threeDimensionalFailingArray2));

		String[][][] threeDimensionalFailingArray3 = new String[][][] {
			new String[][] {
				new String[] {
					"123456", "abc123"
				},
				new String[] {
					"passing"
				},
				new String[] {
					"this passes too"
				},
				new String[] {
					"fail"
				}
			}
		};
		assertCheckFailed(topLevelArrayCheck.check(threeDimensionalFailingArray3));
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

}
