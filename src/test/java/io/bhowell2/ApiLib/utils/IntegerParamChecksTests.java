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
        CheckFunc<Integer> checkFunction = valueGreaterThan(0);

        // failing

        CheckFuncResult check = checkFunction.check(-1);
        assertFalse(check.successful);

        CheckFuncResult check1 = checkFunction.check(Integer.MIN_VALUE);
        assertFalse(check1.successful);

        CheckFuncResult check2 = checkFunction.check(0);
        assertFalse(check2.successful);

        // passing

        CheckFuncResult check3 = checkFunction.check(Integer.MAX_VALUE);
        assertTrue(check3.successful);

        CheckFuncResult check4 = checkFunction.check(1);
        assertTrue(check4.successful);
    }

    @Test
    public void shouldPassWithValueGreaterOrEqual() {
        CheckFunc<Integer> checkFunction = valueGreaterThanOrEqualTo(0);

        // failing

        CheckFuncResult check = checkFunction.check(-1);
        assertFalse(check.successful);

        CheckFuncResult check1 = checkFunction.check(Integer.MIN_VALUE);
        assertFalse(check1.successful);

        // passing

        CheckFuncResult check2 = checkFunction.check(0);
        assertTrue(check2.successful);

        CheckFuncResult check3 = checkFunction.check(Integer.MAX_VALUE);
        assertTrue(check3.successful);

        CheckFuncResult check4 = checkFunction.check(1);
        assertTrue(check4.successful);
    }

    @Test
    public void shouldOnlyPassWithValuesLessThan() {
        CheckFunc<Integer> checkFunction = valueLessThan(0);

        // passing

        CheckFuncResult check = checkFunction.check(-1);
        assertTrue(check.successful);

        CheckFuncResult check1 = checkFunction.check(Integer.MIN_VALUE);
        assertTrue(check1.successful);

        // failing

        CheckFuncResult check2 = checkFunction.check(0);
        assertFalse(check2.successful);

        CheckFuncResult check3 = checkFunction.check(Integer.MAX_VALUE);
        assertFalse(check3.successful);

        CheckFuncResult check4 = checkFunction.check(1);
        assertFalse(check4.successful);
    }

    @Test
    public void shouldOnlyPassWithValuesLessThanOrEqualTo() {
        CheckFunc<Integer> checkFunction = valueLessThanOrEqualTo(0);

        // passing

        CheckFuncResult check = checkFunction.check(-1);
        assertTrue(check.successful);

        CheckFuncResult check1 = checkFunction.check(Integer.MIN_VALUE);
        assertTrue(check1.successful);

        CheckFuncResult check2 = checkFunction.check(0);
        assertTrue(check2.successful);

        // failing

        CheckFuncResult check3 = checkFunction.check(Integer.MAX_VALUE);
        assertFalse(check3.successful);

        CheckFuncResult check4 = checkFunction.check(1);
        assertFalse(check4.successful);
    }


    @Test
    public void shouldPassOnlyOnEqualTo() {
        CheckFunc<Integer> checkFunction = valueEqualTo(0);

        // Failing

        CheckFuncResult check = checkFunction.check(1);
        assertFalse(check.successful);

        CheckFuncResult check1 = checkFunction.check(Integer.MAX_VALUE);
        assertFalse(check1.successful);

        CheckFuncResult check2 = checkFunction.check(Integer.MIN_VALUE);
        assertFalse(check2.successful);

        CheckFuncResult check4 = checkFunction.check(-1);
        assertFalse(check4.successful);

        // Passing

        CheckFuncResult check3 = checkFunction.check(0);
        assertTrue(check3.successful);
    }

    @Test
    public void shouldPassOnlyOnEqualToArray() {
        CheckFunc<Integer> checkFunction = valueEqualTo(new int[]{1, 2, 3});

        // passing
        CheckFuncResult check = checkFunction.check(1);
        assertTrue(check.successful);
        CheckFuncResult check1 = checkFunction.check(2);
        assertTrue(check1.successful);
        CheckFuncResult check2 = checkFunction.check(3);
        assertTrue(check2.successful);

        CheckFuncResult failingCheck = checkFunction.check(0);
        assertTrue(failingCheck.failed());
        CheckFuncResult failingCheck2 = checkFunction.check(Integer.MIN_VALUE);
        assertTrue(failingCheck2.failed());
        CheckFuncResult failingCheck3 = checkFunction.check(Integer.MAX_VALUE);
        assertTrue(failingCheck3.failed());
        CheckFuncResult failingCheck4 = checkFunction.check(5);
        assertTrue(failingCheck4.failed());
    }
    
}

