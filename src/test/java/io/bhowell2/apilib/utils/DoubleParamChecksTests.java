package io.bhowell2.apilib.utils;

import io.bhowell2.apilib.CheckFunc;
import io.bhowell2.apilib.CheckFuncResult;
import org.junit.jupiter.api.Test;

import static io.bhowell2.apilib.utils.DoubleParamChecks.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Blake Howell
 */
public class DoubleParamChecksTests {

    @Test
    public void shouldOnlyPassWithValueGreater() {
        CheckFunc<Double> checkTupleFunction = valueGreaterThan(0.0);

        // failing

        CheckFuncResult checkTuple = checkTupleFunction.check(-1.0);
        assertFalse(checkTuple.successful);

        CheckFuncResult checkTuple1 = checkTupleFunction.check(-Double.MIN_VALUE);
        assertFalse(checkTuple1.successful);

        CheckFuncResult checkTuple2 = checkTupleFunction.check(0.0);
        assertFalse(checkTuple2.successful);

        // passing

        CheckFuncResult checkTuple3 = checkTupleFunction.check(Double.MAX_VALUE);
        assertTrue(checkTuple3.successful);

        CheckFuncResult checkTuple4 = checkTupleFunction.check(0.1);
        assertTrue(checkTuple4.successful);
    }

    @Test
    public void shouldPassWithValueGreaterOrEqual() {
        CheckFunc<Double> checkTupleFunction = valueGreaterThanOrEqualTo(0.0);

        // failing

        CheckFuncResult checkTuple = checkTupleFunction.check(-1.0);
        assertFalse(checkTuple.successful);

        CheckFuncResult checkTuple1 = checkTupleFunction.check(-Double.MIN_VALUE);
        assertFalse(checkTuple1.successful);

        // passing

        CheckFuncResult checkTuple2 = checkTupleFunction.check(0.0);
        assertTrue(checkTuple2.successful);

        CheckFuncResult checkTuple3 = checkTupleFunction.check(Double.MAX_VALUE);
        assertTrue(checkTuple3.successful);

        CheckFuncResult checkTuple4 = checkTupleFunction.check(0.1);
        assertTrue(checkTuple4.successful);
    }

    @Test
    public void shouldOnlyPassWithValuesLessThan() {
        CheckFunc<Double> checkTupleFunction = valueLessThan(0.0);

        // passing

        CheckFuncResult checkTuple = checkTupleFunction.check(-1.0);
        assertTrue(checkTuple.successful);

        CheckFuncResult checkTuple1 = checkTupleFunction.check(-Double.MIN_VALUE);
        assertTrue(checkTuple1.successful);

        // failing

        CheckFuncResult checkTuple2 = checkTupleFunction.check(0.0);
        assertFalse(checkTuple2.successful);

        CheckFuncResult checkTuple3 = checkTupleFunction.check(Double.MAX_VALUE);
        assertFalse(checkTuple3.successful);

        CheckFuncResult checkTuple4 = checkTupleFunction.check(0.1);
        assertFalse(checkTuple4.successful);
    }

    @Test
    public void shouldOnlyPassWithValuesLessThanOrEqualTo() {
        CheckFunc<Double> checkTupleFunction = valueLessThanOrEqualTo(0.0);

        // passing

        CheckFuncResult checkTuple = checkTupleFunction.check(-1.0);
        assertTrue(checkTuple.successful);

        CheckFuncResult checkTuple1 = checkTupleFunction.check(-Double.MIN_VALUE);
        assertTrue(checkTuple1.successful);

        CheckFuncResult checkTuple2 = checkTupleFunction.check(0.0);
        assertTrue(checkTuple2.successful);

        // failing

        CheckFuncResult checkTuple3 = checkTupleFunction.check(Double.MAX_VALUE);
        assertFalse(checkTuple3.successful);

        CheckFuncResult checkTuple4 = checkTupleFunction.check(0.1);
        assertFalse(checkTuple4.successful);
    }


    @Test
    public void shouldPassOnlyOnEqualTo() {
        CheckFunc<Double> checkTupleFunction = valueEqualTo(0.0);

        // Failing

        CheckFuncResult checkTuple = checkTupleFunction.check(0.1);
        assertFalse(checkTuple.successful);

        CheckFuncResult checkTuple1 = checkTupleFunction.check(Double.MAX_VALUE);
        assertFalse(checkTuple1.successful);

        CheckFuncResult checkTuple2 = checkTupleFunction.check(-Double.MIN_VALUE);
        assertFalse(checkTuple2.successful);

        // Passing

        CheckFuncResult checkTuple3 = checkTupleFunction.check(0.000);
        assertTrue(checkTuple3.successful);

        CheckFuncResult checkTuple4 = checkTupleFunction.check(0.0);
        assertTrue(checkTuple4.successful);

    }

}
