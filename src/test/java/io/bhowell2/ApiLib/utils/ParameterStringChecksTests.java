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
        CheckFunction<String> checkTupleFunction = lengthGreaterThan(5);

        // failing

        CheckFunctionTuple checkTuple = checkTupleFunction.check("");
        assertFalse(checkTuple.successful);

        CheckFunctionTuple checkTuple2 = checkTupleFunction.check("1");
        assertFalse(checkTuple2.successful);

        CheckFunctionTuple checkTuple3 = checkTupleFunction.check("12345");
        assertFalse(checkTuple3.successful);

        // passing

        CheckFunctionTuple checkTuple4 = checkTupleFunction.check("12345678");
        assertTrue(checkTuple4.successful);

        // testing greater than 0

        CheckFunction<String> checkTupleFunction2 = lengthGreaterThan(0);

        // failing

        CheckFunctionTuple checkTuple5 = checkTupleFunction2.check("");
        assertFalse(checkTuple5.successful);

        // passing

        CheckFunctionTuple checkTuple6 = checkTupleFunction2.check("0");
        assertTrue(checkTuple6.successful);
    }

    @Test
    public void shouldHaveLengthGreaterThanOrEqualTo() {
        CheckFunction<String> checkTupleFunction = lengthGreaterThanOrEqual(5);

        // failing

        CheckFunctionTuple checkTuple = checkTupleFunction.check("");
        assertFalse(checkTuple.successful);

        CheckFunctionTuple checkTuple2 = checkTupleFunction.check("1");
        assertFalse(checkTuple2.successful);

        // passing

        CheckFunctionTuple checkTuple3 = checkTupleFunction.check("12345");
        assertTrue(checkTuple3.successful);

        CheckFunctionTuple checkTuple4 = checkTupleFunction.check("12345678");
        assertTrue(checkTuple4.successful);

        // testing greater than 0
        CheckFunction<String> checkTupleFunction2 = lengthGreaterThanOrEqual(0);

        // passing

        CheckFunctionTuple checkTuple5 = checkTupleFunction2.check("");
        assertTrue(checkTuple5.successful);

        CheckFunctionTuple checkTuple6 = checkTupleFunction2.check("0");
        assertTrue(checkTuple6.successful);
    }

    @Test
    public void shouldHaveLengthLessThan() {
        CheckFunction<String> checkTupleFunction = lengthLessThan(5);

        // passing

        CheckFunctionTuple checkTuple = checkTupleFunction.check("");
        assertTrue(checkTuple.successful);

        CheckFunctionTuple checkTuple2 = checkTupleFunction.check("1");
        assertTrue(checkTuple2.successful);

        CheckFunctionTuple checkTuple3 = checkTupleFunction.check("1234");
        assertTrue(checkTuple3.successful);

        // Failing

        CheckFunctionTuple checkTuple4 = checkTupleFunction.check("12345");
        assertFalse(checkTuple4.successful);

        CheckFunctionTuple checkTuple5 = checkTupleFunction.check("12345678");
        assertFalse(checkTuple5.successful);

        // testing less than 0

        CheckFunction<String> checkTupleFunction2 = lengthLessThan(0);

        // only failing

        CheckFunctionTuple checkTuple6 = checkTupleFunction2.check("");
        assertFalse(checkTuple6.successful);

        CheckFunctionTuple checkTuple7 = checkTupleFunction2.check("1");
        assertFalse(checkTuple7.successful);
    }

    @Test
    public void shouldHaveLengthLessThanOrEqualTo() {
        CheckFunction<String> checkTupleFunction = lengthLessThanOrEqual(5);

        // passing

        CheckFunctionTuple checkTuple = checkTupleFunction.check("");
        assertTrue(checkTuple.successful);

        CheckFunctionTuple checkTuple2 = checkTupleFunction.check("1");
        assertTrue(checkTuple2.successful);

        CheckFunctionTuple checkTuple3 = checkTupleFunction.check("1234");
        assertTrue(checkTuple3.successful);

        CheckFunctionTuple checkTuple4 = checkTupleFunction.check("12345");
        assertTrue(checkTuple4.successful);

        // Failing

        CheckFunctionTuple checkTuple5 = checkTupleFunction.check("123456");
        assertFalse(checkTuple5.successful);

        // testing less than or equal to 0
        CheckFunction<String> checkTupleFunction2 = lengthLessThanOrEqual(0);

        // passing

        CheckFunctionTuple checkTuple6 = checkTupleFunction2.check("");
        assertTrue(checkTuple6.successful);

        // failing

        CheckFunctionTuple checkTuple7 = checkTupleFunction2.check("1");
        assertFalse(checkTuple7.successful);
    }


    // TODO: Test rest of functions

}
