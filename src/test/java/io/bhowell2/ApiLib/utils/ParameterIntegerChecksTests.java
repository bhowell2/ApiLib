package io.bhowell2.ApiLib.utils;

import io.bhowell2.ApiLib.CheckFunction;
import io.bhowell2.ApiLib.CheckFunctionTuple;
import org.junit.Test;

import static org.junit.Assert.*;
import static io.bhowell2.ApiLib.utils.ParameterIntegerChecks.*;

/**
 * @author Blake Howell
 */
public class ParameterIntegerChecksTests {

    @Test
    public void shouldOnlyPassWithValueGreater() {
        CheckFunction<Integer> checkTupleFunction = valueGreaterThan(0);

        // failing

        CheckFunctionTuple checkTuple = checkTupleFunction.check(-1);
        assertFalse(checkTuple.successful);

        CheckFunctionTuple checkTuple1 = checkTupleFunction.check(Integer.MIN_VALUE);
        assertFalse(checkTuple1.successful);

        CheckFunctionTuple checkTuple2 = checkTupleFunction.check(0);
        assertFalse(checkTuple2.successful);

        // passing

        CheckFunctionTuple checkTuple3 = checkTupleFunction.check(Integer.MAX_VALUE);
        assertTrue(checkTuple3.successful);

        CheckFunctionTuple checkTuple4 = checkTupleFunction.check(1);
        assertTrue(checkTuple4.successful);
    }

    @Test
    public void shouldPassWithValueGreaterOrEqual() {
        CheckFunction<Integer> checkTupleFunction = valueGreaterThanOrEqualTo(0);

        // failing

        CheckFunctionTuple checkTuple = checkTupleFunction.check(-1);
        assertFalse(checkTuple.successful);

        CheckFunctionTuple checkTuple1 = checkTupleFunction.check(Integer.MIN_VALUE);
        assertFalse(checkTuple1.successful);

        // passing

        CheckFunctionTuple checkTuple2 = checkTupleFunction.check(0);
        assertTrue(checkTuple2.successful);

        CheckFunctionTuple checkTuple3 = checkTupleFunction.check(Integer.MAX_VALUE);
        assertTrue(checkTuple3.successful);

        CheckFunctionTuple checkTuple4 = checkTupleFunction.check(1);
        assertTrue(checkTuple4.successful);
    }

    @Test
    public void shouldOnlyPassWithValuesLessThan() {
        CheckFunction<Integer> checkTupleFunction = valueLessThan(0);

        // passing

        CheckFunctionTuple checkTuple = checkTupleFunction.check(-1);
        assertTrue(checkTuple.successful);

        CheckFunctionTuple checkTuple1 = checkTupleFunction.check(Integer.MIN_VALUE);
        assertTrue(checkTuple1.successful);

        // failing

        CheckFunctionTuple checkTuple2 = checkTupleFunction.check(0);
        assertFalse(checkTuple2.successful);

        CheckFunctionTuple checkTuple3 = checkTupleFunction.check(Integer.MAX_VALUE);
        assertFalse(checkTuple3.successful);

        CheckFunctionTuple checkTuple4 = checkTupleFunction.check(1);
        assertFalse(checkTuple4.successful);
    }

    @Test
    public void shouldOnlyPassWithValuesLessThanOrEqualTo() {
        CheckFunction<Integer> checkTupleFunction = valueLessThanOrEqualTo(0);

        // passing

        CheckFunctionTuple checkTuple = checkTupleFunction.check(-1);
        assertTrue(checkTuple.successful);

        CheckFunctionTuple checkTuple1 = checkTupleFunction.check(Integer.MIN_VALUE);
        assertTrue(checkTuple1.successful);

        CheckFunctionTuple checkTuple2 = checkTupleFunction.check(0);
        assertTrue(checkTuple2.successful);

        // failing

        CheckFunctionTuple checkTuple3 = checkTupleFunction.check(Integer.MAX_VALUE);
        assertFalse(checkTuple3.successful);

        CheckFunctionTuple checkTuple4 = checkTupleFunction.check(1);
        assertFalse(checkTuple4.successful);
    }


    @Test
    public void shouldPassOnlyOnEqualTo() {
        CheckFunction<Integer> checkTupleFunction = valueEqualTo(0);

        // Failing

        CheckFunctionTuple checkTuple = checkTupleFunction.check(1);
        assertFalse(checkTuple.successful);

        CheckFunctionTuple checkTuple1 = checkTupleFunction.check(Integer.MAX_VALUE);
        assertFalse(checkTuple1.successful);

        CheckFunctionTuple checkTuple2 = checkTupleFunction.check(Integer.MIN_VALUE);
        assertFalse(checkTuple2.successful);

        CheckFunctionTuple checkTuple4 = checkTupleFunction.check(-1);
        assertFalse(checkTuple4.successful);


        // Passing

        CheckFunctionTuple checkTuple3 = checkTupleFunction.check(0);
        assertTrue(checkTuple3.successful);

    }

}

