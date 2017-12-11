package io.bhowell2.ApiLib.utils;

import io.bhowell2.ApiLib.CheckFunction;
import io.bhowell2.ApiLib.CheckFunctionTuple;
import org.junit.Test;

import static org.junit.Assert.*;
import static io.bhowell2.ApiLib.utils.ParameterStringChecks.*;

/**
 * @author Blake Howell
 */
public class ParameterStringChecksTests {

    @Test
    public void shouldHaveLengthGreaterThan() {
        CheckFunction<String> checkFunction = lengthGreaterThan(5);

        // failing

        CheckFunctionTuple checkTuple = checkFunction.check("");
        assertFalse(checkTuple.successful);

        CheckFunctionTuple checkTuple2 = checkFunction.check("1");
        assertFalse(checkTuple2.successful);

        CheckFunctionTuple checkTuple3 = checkFunction.check("12345");
        assertFalse(checkTuple3.successful);

        // passing

        CheckFunctionTuple checkTuple4 = checkFunction.check("12345678");
        assertTrue(checkTuple4.successful);

        // testing greater than 0

        CheckFunction<String> checkFunction2 = lengthGreaterThan(0);

        // failing

        CheckFunctionTuple checkTuple5 = checkFunction2.check("");
        assertFalse(checkTuple5.successful);

        // passing

        CheckFunctionTuple checkTuple6 = checkFunction2.check("0");
        assertTrue(checkTuple6.successful);
    }

    @Test
    public void shouldHaveLengthGreaterThanOrEqualTo() {
        CheckFunction<String> checkFunction = lengthGreaterThanOrEqual(5);

        // failing

        CheckFunctionTuple checkTuple = checkFunction.check("");
        assertFalse(checkTuple.successful);

        CheckFunctionTuple checkTuple2 = checkFunction.check("1");
        assertFalse(checkTuple2.successful);

        // passing

        CheckFunctionTuple checkTuple3 = checkFunction.check("12345");
        assertTrue(checkTuple3.successful);

        CheckFunctionTuple checkTuple4 = checkFunction.check("12345678");
        assertTrue(checkTuple4.successful);

        // testing greater than 0
        CheckFunction<String> checkFunction2 = lengthGreaterThanOrEqual(0);

        // passing

        CheckFunctionTuple checkTuple5 = checkFunction2.check("");
        assertTrue(checkTuple5.successful);

        CheckFunctionTuple checkTuple6 = checkFunction2.check("0");
        assertTrue(checkTuple6.successful);
    }

    @Test
    public void shouldHaveLengthLessThan() {
        CheckFunction<String> checkFunction = lengthLessThan(5);

        // passing

        CheckFunctionTuple checkTuple = checkFunction.check("");
        assertTrue(checkTuple.successful);

        CheckFunctionTuple checkTuple2 = checkFunction.check("1");
        assertTrue(checkTuple2.successful);

        CheckFunctionTuple checkTuple3 = checkFunction.check("1234");
        assertTrue(checkTuple3.successful);

        // Failing

        CheckFunctionTuple checkTuple4 = checkFunction.check("12345");
        assertFalse(checkTuple4.successful);

        CheckFunctionTuple checkTuple5 = checkFunction.check("12345678");
        assertFalse(checkTuple5.successful);

        // testing less than 0

        CheckFunction<String> checkFunction2 = lengthLessThan(0);

        // only failing

        CheckFunctionTuple checkTuple6 = checkFunction2.check("");
        assertFalse(checkTuple6.successful);

        CheckFunctionTuple checkTuple7 = checkFunction2.check("1");
        assertFalse(checkTuple7.successful);
    }

    @Test
    public void shouldHaveLengthLessThanOrEqualTo() {
        CheckFunction<String> checkFunction = lengthLessThanOrEqual(5);

        // passing

        CheckFunctionTuple checkTuple = checkFunction.check("");
        assertTrue(checkTuple.successful);

        CheckFunctionTuple checkTuple2 = checkFunction.check("1");
        assertTrue(checkTuple2.successful);

        CheckFunctionTuple checkTuple3 = checkFunction.check("1234");
        assertTrue(checkTuple3.successful);

        CheckFunctionTuple checkTuple4 = checkFunction.check("12345");
        assertTrue(checkTuple4.successful);

        // Failing

        CheckFunctionTuple checkTuple5 = checkFunction.check("123456");
        assertFalse(checkTuple5.successful);

        // testing less than or equal to 0
        CheckFunction<String> checkFunction2 = lengthLessThanOrEqual(0);

        // passing

        CheckFunctionTuple checkTuple6 = checkFunction2.check("");
        assertTrue(checkTuple6.successful);

        // failing

        CheckFunctionTuple checkTuple7 = checkFunction2.check("1");
        assertFalse(checkTuple7.successful);
    }

    @Test
    public void shouldBeEmptyOrMeetOtherChecks() {
        CheckFunction<String> checkFunction = emptyOrMeetsChecks(lengthGreaterThan(2), lengthLessThanOrEqual(10));

        // passing

        CheckFunctionTuple checkTuple = checkFunction.check("");
        assertTrue(checkTuple.successful);

        CheckFunctionTuple checkTuple2 = checkFunction.check("123");
        assertTrue(checkTuple2.successful);

        CheckFunctionTuple checkTuple3 = checkFunction.check("0123456789");
        assertTrue(checkTuple3.successful);

        // failing

        CheckFunctionTuple checkTuple4 = checkFunction.check("1");
        assertFalse(checkTuple4.successful);

        CheckFunctionTuple checkTuple5 = checkFunction.check("0123456789012345");   // longer than 10
        assertFalse(checkTuple5.successful);

    }

    // TODO: Test rest of functions

}
