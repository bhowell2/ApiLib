package io.bhowell2.ApiLib.utils;

import io.bhowell2.ApiLib.CheckFunc;
import io.bhowell2.ApiLib.CheckFuncResult;
import org.junit.jupiter.api.Test;

import static io.bhowell2.ApiLib.utils.IntegerParamChecks.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Blake Howell
 */
public class IntegerParamChecksTests {

    @Test
    public void shouldOnlyPassWithValueGreater() {
        CheckFunc<Integer> checkTupleFunction = valueGreaterThan(0);

        // failing

        CheckFuncResult checkTuple = checkTupleFunction.check(-1);
        assertFalse(checkTuple.successful);

        CheckFuncResult checkTuple1 = checkTupleFunction.check(Integer.MIN_VALUE);
        assertFalse(checkTuple1.successful);

        CheckFuncResult checkTuple2 = checkTupleFunction.check(0);
        assertFalse(checkTuple2.successful);

        // passing

        CheckFuncResult checkTuple3 = checkTupleFunction.check(Integer.MAX_VALUE);
        assertTrue(checkTuple3.successful);

        CheckFuncResult checkTuple4 = checkTupleFunction.check(1);
        assertTrue(checkTuple4.successful);
    }

    @Test
    public void shouldPassWithValueGreaterOrEqual() {
        CheckFunc<Integer> checkTupleFunction = valueGreaterThanOrEqualTo(0);

        // failing

        CheckFuncResult checkTuple = checkTupleFunction.check(-1);
        assertFalse(checkTuple.successful);

        CheckFuncResult checkTuple1 = checkTupleFunction.check(Integer.MIN_VALUE);
        assertFalse(checkTuple1.successful);

        // passing

        CheckFuncResult checkTuple2 = checkTupleFunction.check(0);
        assertTrue(checkTuple2.successful);

        CheckFuncResult checkTuple3 = checkTupleFunction.check(Integer.MAX_VALUE);
        assertTrue(checkTuple3.successful);

        CheckFuncResult checkTuple4 = checkTupleFunction.check(1);
        assertTrue(checkTuple4.successful);
    }

    @Test
    public void shouldOnlyPassWithValuesLessThan() {
        CheckFunc<Integer> checkTupleFunction = valueLessThan(0);

        // passing

        CheckFuncResult checkTuple = checkTupleFunction.check(-1);
        assertTrue(checkTuple.successful);

        CheckFuncResult checkTuple1 = checkTupleFunction.check(Integer.MIN_VALUE);
        assertTrue(checkTuple1.successful);

        // failing

        CheckFuncResult checkTuple2 = checkTupleFunction.check(0);
        assertFalse(checkTuple2.successful);

        CheckFuncResult checkTuple3 = checkTupleFunction.check(Integer.MAX_VALUE);
        assertFalse(checkTuple3.successful);

        CheckFuncResult checkTuple4 = checkTupleFunction.check(1);
        assertFalse(checkTuple4.successful);
    }

    @Test
    public void shouldOnlyPassWithValuesLessThanOrEqualTo() {
        CheckFunc<Integer> checkTupleFunction = valueLessThanOrEqualTo(0);

        // passing

        CheckFuncResult checkTuple = checkTupleFunction.check(-1);
        assertTrue(checkTuple.successful);

        CheckFuncResult checkTuple1 = checkTupleFunction.check(Integer.MIN_VALUE);
        assertTrue(checkTuple1.successful);

        CheckFuncResult checkTuple2 = checkTupleFunction.check(0);
        assertTrue(checkTuple2.successful);

        // failing

        CheckFuncResult checkTuple3 = checkTupleFunction.check(Integer.MAX_VALUE);
        assertFalse(checkTuple3.successful);

        CheckFuncResult checkTuple4 = checkTupleFunction.check(1);
        assertFalse(checkTuple4.successful);
    }


    @Test
    public void shouldPassOnlyOnEqualTo() {
        CheckFunc<Integer> checkTupleFunction = valueEqualTo(0);

        // Failing

        CheckFuncResult checkTuple = checkTupleFunction.check(1);
        assertFalse(checkTuple.successful);

        CheckFuncResult checkTuple1 = checkTupleFunction.check(Integer.MAX_VALUE);
        assertFalse(checkTuple1.successful);

        CheckFuncResult checkTuple2 = checkTupleFunction.check(Integer.MIN_VALUE);
        assertFalse(checkTuple2.successful);

        CheckFuncResult checkTuple4 = checkTupleFunction.check(-1);
        assertFalse(checkTuple4.successful);


        // Passing

        CheckFuncResult checkTuple3 = checkTupleFunction.check(0);
        assertTrue(checkTuple3.successful);

    }

}

