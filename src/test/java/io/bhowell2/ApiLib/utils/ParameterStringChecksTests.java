package io.bhowell2.ApiLib.utils;

import io.bhowell2.ApiLib.CheckFunction;
import io.bhowell2.ApiLib.FunctionCheckTuple;
import org.junit.Test;

import java.util.function.Function;

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

        FunctionCheckTuple checkTuple = checkTupleFunction.check("");
        assertFalse(checkTuple.successful);

        FunctionCheckTuple checkTuple2 = checkTupleFunction.check("1");
        assertFalse(checkTuple2.successful);

        FunctionCheckTuple checkTuple3 = checkTupleFunction.check("12345");
        assertFalse(checkTuple3.successful);

        // passing

        FunctionCheckTuple checkTuple4 = checkTupleFunction.check("12345678");
        assertTrue(checkTuple4.successful);

        // testing greater than 0

        CheckFunction<String> checkTupleFunction2 = lengthGreaterThan(0);

        // failing

        FunctionCheckTuple checkTuple5 = checkTupleFunction2.check("");
        assertFalse(checkTuple5.successful);

        // passing

        FunctionCheckTuple checkTuple6 = checkTupleFunction2.check("0");
        assertTrue(checkTuple6.successful);
    }

    @Test
    public void shouldHaveLengthGreaterThanOrEqualTo() {
        CheckFunction<String> checkTupleFunction = lengthGreaterThanOrEqual(5);

        // failing

        FunctionCheckTuple checkTuple = checkTupleFunction.check("");
        assertFalse(checkTuple.successful);

        FunctionCheckTuple checkTuple2 = checkTupleFunction.check("1");
        assertFalse(checkTuple2.successful);

        // passing

        FunctionCheckTuple checkTuple3 = checkTupleFunction.check("12345");
        assertTrue(checkTuple3.successful);

        FunctionCheckTuple checkTuple4 = checkTupleFunction.check("12345678");
        assertTrue(checkTuple4.successful);

        // testing greater than 0
        CheckFunction<String> checkTupleFunction2 = lengthGreaterThanOrEqual(0);

        // passing

        FunctionCheckTuple checkTuple5 = checkTupleFunction2.check("");
        assertTrue(checkTuple5.successful);

        FunctionCheckTuple checkTuple6 = checkTupleFunction2.check("0");
        assertTrue(checkTuple6.successful);
    }

    @Test
    public void shouldHaveLengthLessThan() {
        CheckFunction<String> checkTupleFunction = lengthLessThan(5);

        // passing

        FunctionCheckTuple checkTuple = checkTupleFunction.check("");
        assertTrue(checkTuple.successful);

        FunctionCheckTuple checkTuple2 = checkTupleFunction.check("1");
        assertTrue(checkTuple2.successful);

        FunctionCheckTuple checkTuple3 = checkTupleFunction.check("1234");
        assertTrue(checkTuple3.successful);

        // Failing

        FunctionCheckTuple checkTuple4 = checkTupleFunction.check("12345");
        assertFalse(checkTuple4.successful);

        FunctionCheckTuple checkTuple5 = checkTupleFunction.check("12345678");
        assertFalse(checkTuple5.successful);

        // testing less than 0

        CheckFunction<String> checkTupleFunction2 = lengthLessThan(0);

        // only failing

        FunctionCheckTuple checkTuple6 = checkTupleFunction2.check("");
        assertFalse(checkTuple6.successful);

        FunctionCheckTuple checkTuple7 = checkTupleFunction2.check("1");
        assertFalse(checkTuple7.successful);
    }

    @Test
    public void shouldHaveLengthLessThanOrEqualTo() {
        CheckFunction<String> checkTupleFunction = lengthLessThanOrEqual(5);

        // passing

        FunctionCheckTuple checkTuple = checkTupleFunction.check("");
        assertTrue(checkTuple.successful);

        FunctionCheckTuple checkTuple2 = checkTupleFunction.check("1");
        assertTrue(checkTuple2.successful);

        FunctionCheckTuple checkTuple3 = checkTupleFunction.check("1234");
        assertTrue(checkTuple3.successful);

        FunctionCheckTuple checkTuple4 = checkTupleFunction.check("12345");
        assertTrue(checkTuple4.successful);

        // Failing

        FunctionCheckTuple checkTuple5 = checkTupleFunction.check("123456");
        assertFalse(checkTuple5.successful);

        // testing less than or equal to 0
        CheckFunction<String> checkTupleFunction2 = lengthLessThanOrEqual(0);

        // passing

        FunctionCheckTuple checkTuple6 = checkTupleFunction2.check("");
        assertTrue(checkTuple6.successful);

        // failing

        FunctionCheckTuple checkTuple7 = checkTupleFunction2.check("1");
        assertFalse(checkTuple7.successful);
    }


    // TODO: Test rest of functions

}
