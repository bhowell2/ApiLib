package io.bhowell2.ApiLib.utils;

import io.bhowell2.ApiLib.CheckFunction;
import io.bhowell2.ApiLib.CheckFunctionTuple;
import org.junit.Test;

import static org.junit.Assert.*;
import static io.bhowell2.ApiLib.utils.ParameterDoubleChecks.*;

/**
 * @author Blake Howell
 */
public class ParameterDoubleChecksTests {

    @Test
    public void shouldOnlyPassWithValueGreater() {
        CheckFunction<Double> checkTupleFunction = valueGreaterThan(0.0);

        // failing

        CheckFunctionTuple checkTuple = checkTupleFunction.check(-1.0);
        assertFalse(checkTuple.successful);

        CheckFunctionTuple checkTuple1 = checkTupleFunction.check(-Double.MIN_VALUE);
        assertFalse(checkTuple1.successful);

        CheckFunctionTuple checkTuple2 = checkTupleFunction.check(0.0);
        assertFalse(checkTuple2.successful);

        // passing

        CheckFunctionTuple checkTuple3 = checkTupleFunction.check(Double.MAX_VALUE);
        assertTrue(checkTuple3.successful);

        CheckFunctionTuple checkTuple4 = checkTupleFunction.check(0.1);
        assertTrue(checkTuple4.successful);
    }

    @Test
    public void shouldPassWithValueGreaterOrEqual() {
        CheckFunction<Double>checkTupleFunction = valueGreaterThanOrEqualTo(0.0);

        // failing

        CheckFunctionTuple checkTuple = checkTupleFunction.check(-1.0);
        assertFalse(checkTuple.successful);

        CheckFunctionTuple checkTuple1 = checkTupleFunction.check(-Double.MIN_VALUE);
        assertFalse(checkTuple1.successful);

        // passing

        CheckFunctionTuple checkTuple2 = checkTupleFunction.check(0.0);
        assertTrue(checkTuple2.successful);

        CheckFunctionTuple checkTuple3 = checkTupleFunction.check(Double.MAX_VALUE);
        assertTrue(checkTuple3.successful);

        CheckFunctionTuple checkTuple4 = checkTupleFunction.check(0.1);
        assertTrue(checkTuple4.successful);
    }

    @Test
    public void shouldOnlyPassWithValuesLessThan() {
        CheckFunction<Double>checkTupleFunction = valueLessThan(0.0);

        // passing

        CheckFunctionTuple checkTuple = checkTupleFunction.check(-1.0);
        assertTrue(checkTuple.successful);

        CheckFunctionTuple checkTuple1 = checkTupleFunction.check(-Double.MIN_VALUE);
        assertTrue(checkTuple1.successful);

        // failing

        CheckFunctionTuple checkTuple2 = checkTupleFunction.check(0.0);
        assertFalse(checkTuple2.successful);

        CheckFunctionTuple checkTuple3 = checkTupleFunction.check(Double.MAX_VALUE);
        assertFalse(checkTuple3.successful);

        CheckFunctionTuple checkTuple4 = checkTupleFunction.check(0.1);
        assertFalse(checkTuple4.successful);
    }

    @Test
    public void shouldOnlyPassWithValuesLessThanOrEqualTo() {
        CheckFunction<Double>checkTupleFunction = valueLessThanOrEqualTo(0.0);

        // passing

        CheckFunctionTuple checkTuple = checkTupleFunction.check(-1.0);
        assertTrue(checkTuple.successful);

        CheckFunctionTuple checkTuple1 = checkTupleFunction.check(-Double.MIN_VALUE);
        assertTrue(checkTuple1.successful);

        CheckFunctionTuple checkTuple2 = checkTupleFunction.check(0.0);
        assertTrue(checkTuple2.successful);

        // failing

        CheckFunctionTuple checkTuple3 = checkTupleFunction.check(Double.MAX_VALUE);
        assertFalse(checkTuple3.successful);

        CheckFunctionTuple checkTuple4 = checkTupleFunction.check(0.1);
        assertFalse(checkTuple4.successful);
    }


    @Test
    public void shouldPassOnlyOnEqualTo() {
        CheckFunction<Double>checkTupleFunction = valueEqualTo(0.0);

        // Failing

        CheckFunctionTuple checkTuple = checkTupleFunction.check(0.1);
        assertFalse(checkTuple.successful);

        CheckFunctionTuple checkTuple1 = checkTupleFunction.check(Double.MAX_VALUE);
        assertFalse(checkTuple1.successful);

        CheckFunctionTuple checkTuple2 = checkTupleFunction.check(-Double.MIN_VALUE);
        assertFalse(checkTuple2.successful);

        // Passing

        CheckFunctionTuple checkTuple3 = checkTupleFunction.check(0.000);
        assertTrue(checkTuple3.successful);

        CheckFunctionTuple checkTuple4 = checkTupleFunction.check(0.0);
        assertTrue(checkTuple4.successful);

    }

}
