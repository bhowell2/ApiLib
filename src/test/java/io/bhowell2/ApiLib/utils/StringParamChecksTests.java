package io.bhowell2.ApiLib.utils;

import io.bhowell2.ApiLib.CheckFunc;
import io.bhowell2.ApiLib.CheckFuncResult;
import org.junit.jupiter.api.Test;

import static io.bhowell2.ApiLib.utils.StringParamChecks.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Blake Howell
 */
public class StringParamChecksTests {

    @Test
    public void shouldHaveLengthGreaterThan() {
        CheckFunc<String> checkFunc = lengthGreaterThan(5);

        // failing

        CheckFuncResult checkTuple = checkFunc.check("");
        assertFalse(checkTuple.successful);

        CheckFuncResult checkTuple2 = checkFunc.check("1");
        assertFalse(checkTuple2.successful);

        CheckFuncResult checkTuple3 = checkFunc.check("12345");
        assertFalse(checkTuple3.successful);

        // passing

        CheckFuncResult checkTuple4 = checkFunc.check("12345678");
        assertTrue(checkTuple4.successful);

        // testing greater than 0

        CheckFunc<String> checkFunc2 = lengthGreaterThan(0);

        // failing

        CheckFuncResult checkTuple5 = checkFunc2.check("");
        assertFalse(checkTuple5.successful);

        // passing

        CheckFuncResult checkTuple6 = checkFunc2.check("0");
        assertTrue(checkTuple6.successful);
    }

    @Test
    public void shouldHaveLengthGreaterThanOrEqualTo() {
        CheckFunc<String> checkFunc = lengthGreaterThanOrEqual(5);

        // failing

        CheckFuncResult checkTuple = checkFunc.check("");
        assertFalse(checkTuple.successful);

        CheckFuncResult checkTuple2 = checkFunc.check("1");
        assertFalse(checkTuple2.successful);

        // passing

        CheckFuncResult checkTuple3 = checkFunc.check("12345");
        assertTrue(checkTuple3.successful);

        CheckFuncResult checkTuple4 = checkFunc.check("12345678");
        assertTrue(checkTuple4.successful);

        // testing greater than 0
        CheckFunc<String> checkFunc2 = lengthGreaterThanOrEqual(0);

        // passing

        CheckFuncResult checkTuple5 = checkFunc2.check("");
        assertTrue(checkTuple5.successful);

        CheckFuncResult checkTuple6 = checkFunc2.check("0");
        assertTrue(checkTuple6.successful);
    }

    @Test
    public void shouldHaveLengthLessThan() {
        CheckFunc<String> checkFunc = lengthLessThan(5);

        // passing

        CheckFuncResult checkTuple = checkFunc.check("");
        assertTrue(checkTuple.successful);

        CheckFuncResult checkTuple2 = checkFunc.check("1");
        assertTrue(checkTuple2.successful);

        CheckFuncResult checkTuple3 = checkFunc.check("1234");
        assertTrue(checkTuple3.successful);

        // Failing

        CheckFuncResult checkTuple4 = checkFunc.check("12345");
        assertFalse(checkTuple4.successful);

        CheckFuncResult checkTuple5 = checkFunc.check("12345678");
        assertFalse(checkTuple5.successful);

        // testing less than 0

        CheckFunc<String> checkFunc2 = lengthLessThan(0);

        // only failing

        CheckFuncResult checkTuple6 = checkFunc2.check("");
        assertFalse(checkTuple6.successful);

        CheckFuncResult checkTuple7 = checkFunc2.check("1");
        assertFalse(checkTuple7.successful);
    }

    @Test
    public void shouldHaveLengthLessThanOrEqualTo() {
        CheckFunc<String> checkFunc = lengthLessThanOrEqual(5);

        // passing

        CheckFuncResult checkTuple = checkFunc.check("");
        assertTrue(checkTuple.successful);

        CheckFuncResult checkTuple2 = checkFunc.check("1");
        assertTrue(checkTuple2.successful);

        CheckFuncResult checkTuple3 = checkFunc.check("1234");
        assertTrue(checkTuple3.successful);

        CheckFuncResult checkTuple4 = checkFunc.check("12345");
        assertTrue(checkTuple4.successful);

        // Failing

        CheckFuncResult checkTuple5 = checkFunc.check("123456");
        assertFalse(checkTuple5.successful);

        // testing less than or equal to 0
        CheckFunc<String> checkFunc2 = lengthLessThanOrEqual(0);

        // passing

        CheckFuncResult checkTuple6 = checkFunc2.check("");
        assertTrue(checkTuple6.successful);

        // failing

        CheckFuncResult checkTuple7 = checkFunc2.check("1");
        assertFalse(checkTuple7.successful);
    }

    @Test
    public void shouldBeEmptyOrMeetOtherChecks() {
        CheckFunc<String> checkFunc = emptyOrMeetsChecks(lengthGreaterThan(2), lengthLessThanOrEqual(10));

        // passing

        CheckFuncResult checkTuple = checkFunc.check("");
        assertTrue(checkTuple.successful);

        CheckFuncResult checkTuple2 = checkFunc.check("123");
        assertTrue(checkTuple2.successful);

        CheckFuncResult checkTuple3 = checkFunc.check("0123456789");
        assertTrue(checkTuple3.successful);

        // failing

        CheckFuncResult checkTuple4 = checkFunc.check("1");
        assertFalse(checkTuple4.successful);

        CheckFuncResult checkTuple5 = checkFunc.check("0123456789012345");   // longer than 10
        assertFalse(checkTuple5.successful);

    }

    // TODO: Test rest of functions

}
