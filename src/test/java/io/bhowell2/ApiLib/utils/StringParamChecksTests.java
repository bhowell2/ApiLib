package io.bhowell2.ApiLib.utils;

import io.bhowell2.ApiLib.CheckFunc;
import io.bhowell2.ApiLib.CheckFuncResult;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static io.bhowell2.ApiLib.utils.StringParamChecks.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Blake Howell
 */
public class StringParamChecksTests {

    @Test
    public void testLengthGreaterThan() {
        CheckFunc<String> checkFunc = lengthGreaterThan(5);

        // failing

        CheckFuncResult check = checkFunc.check("");
        assertFalse(check.successful);

        CheckFuncResult check2 = checkFunc.check("1");
        assertFalse(check2.successful);

        CheckFuncResult check3 = checkFunc.check("12345");
        assertFalse(check3.successful);

        // passing

        CheckFuncResult check4 = checkFunc.check("12345678");
        assertTrue(check4.successful);

        // testing greater than 0

        CheckFunc<String> checkFunc2 = lengthGreaterThan(0);

        // failing

        CheckFuncResult check5 = checkFunc2.check("");
        assertFalse(check5.successful);

        // passing

        CheckFuncResult check6 = checkFunc2.check("0");
        assertTrue(check6.successful);
    }

    @Test
    public void testLengthGreaterThanOrEqualTo() {
        CheckFunc<String> checkFunc = lengthGreaterThanOrEqual(5);

        // failing

        CheckFuncResult check = checkFunc.check("");
        assertFalse(check.successful);

        CheckFuncResult check2 = checkFunc.check("1");
        assertFalse(check2.successful);

        // passing

        CheckFuncResult check3 = checkFunc.check("12345");
        assertTrue(check3.successful);

        CheckFuncResult check4 = checkFunc.check("12345678");
        assertTrue(check4.successful);

        // testing greater than 0
        CheckFunc<String> checkFunc2 = lengthGreaterThanOrEqual(0);

        // passing

        CheckFuncResult check5 = checkFunc2.check("");
        assertTrue(check5.successful);

        CheckFuncResult check6 = checkFunc2.check("0");
        assertTrue(check6.successful);
    }

    @Test
    public void testLengthLessThan() {
        CheckFunc<String> checkFunc = lengthLessThan(5);

        // passing

        CheckFuncResult check = checkFunc.check("");
        assertTrue(check.successful);

        CheckFuncResult check2 = checkFunc.check("1");
        assertTrue(check2.successful);

        CheckFuncResult check3 = checkFunc.check("1234");
        assertTrue(check3.successful);

        // Failing

        CheckFuncResult check4 = checkFunc.check("12345");
        assertFalse(check4.successful);

        CheckFuncResult check5 = checkFunc.check("12345678");
        assertFalse(check5.successful);

        // testing less than 0

        CheckFunc<String> checkFunc2 = lengthLessThan(0);

        // only failing

        CheckFuncResult check6 = checkFunc2.check("");
        assertFalse(check6.successful);

        CheckFuncResult check7 = checkFunc2.check("1");
        assertFalse(check7.successful);
    }

    @Test
    public void testLengthLessThanOrEqualTo() {
        CheckFunc<String> checkFunc = lengthLessThanOrEqual(5);

        // passing

        CheckFuncResult check = checkFunc.check("");
        assertTrue(check.successful);

        CheckFuncResult check2 = checkFunc.check("1");
        assertTrue(check2.successful);

        CheckFuncResult check3 = checkFunc.check("1234");
        assertTrue(check3.successful);

        CheckFuncResult check4 = checkFunc.check("12345");
        assertTrue(check4.successful);

        // Failing

        CheckFuncResult check5 = checkFunc.check("123456");
        assertFalse(check5.successful);

        // testing less than or equal to 0
        CheckFunc<String> checkFunc2 = lengthLessThanOrEqual(0);

        // passing

        CheckFuncResult check6 = checkFunc2.check("");
        assertTrue(check6.successful);

        // failing

        CheckFuncResult check7 = checkFunc2.check("1");
        assertFalse(check7.successful);
    }
    
    @Test
    public void testLengthEqualTo() {
        CheckFunc<String> checkFunc = lengthEqualTo(5);
        CheckFuncResult check1 = checkFunc.check("12345");
        assertTrue(check1.successful);

        // failing
        CheckFuncResult failingCheck1 = checkFunc.check("");
        assertTrue(failingCheck1.failed());

        CheckFuncResult failingCheck2 = checkFunc.check("1111");
        assertTrue(failingCheck2.failed());

        CheckFuncResult failingCheck3 = checkFunc.check("111111");
        assertTrue(failingCheck3.failed());

        CheckFuncResult failingCheck4 = checkFunc.check("11111111111111111111111111111111111111111897kljdfjkA*(&^!+)&*");
        assertTrue(failingCheck4.failed());
    }

    @Test
    public void testMatchRegex() {
        CheckFunc<String> checkFunc = StringParamChecks.matchesRegex(Pattern.compile("aa.+"));
        // passing
        CheckFuncResult check1 = checkFunc.check("aaheyy");
        assertTrue(check1.successful);

        CheckFuncResult failingCheck1 = checkFunc.check("aheyy");
        assertTrue(failingCheck1.failed());
    }

    @Test
    public void testEmptyOrMeetOtherChecks() {
        CheckFunc<String> checkFunc = emptyOrMeetsChecks(lengthGreaterThan(2), lengthLessThanOrEqual(10));

        // passing

        CheckFuncResult check = checkFunc.check("");
        assertTrue(check.successful);

        CheckFuncResult check2 = checkFunc.check("123");
        assertTrue(check2.successful);

        CheckFuncResult check3 = checkFunc.check("0123456789");
        assertTrue(check3.successful);

        // failing

        CheckFuncResult check4 = checkFunc.check("1");
        assertFalse(check4.successful);

        CheckFuncResult check5 = checkFunc.check("0123456789012345");   // longer than 10
        assertFalse(check5.successful);
    }

    @Test
    public void testMustContainAtLeastNChars() {
        CheckFunc<String> checkFunc = mustContainAtLeastNChars(3, "abc");

        // passing

        CheckFuncResult check1 = checkFunc.check("abc");
        assertTrue(check1.successful);
        CheckFuncResult check2 = checkFunc.check("aaa");
        assertTrue(check2.successful);
        CheckFuncResult check3 = checkFunc.check("whtever-abb");
        assertTrue(check3.successful);

        // failing

        CheckFuncResult failingCheck1 = checkFunc.check("");
        assertTrue(failingCheck1.failed());

        CheckFuncResult failingCheck2 = checkFunc.check("aa-nope");
        assertTrue(failingCheck2.failed());
    }

    @Test
    public void testMustContainAtLeastNCharsSet() {
        Set<Character> characterSet = new HashSet<>();
        characterSet.add('a');
        characterSet.add('b');
        characterSet.add('c');
        CheckFunc<String> checkFunc = mustContainAtLeastNChars(3, characterSet);

        // passing

        CheckFuncResult check1 = checkFunc.check("abc");
        assertTrue(check1.successful);
        CheckFuncResult check2 = checkFunc.check("aaa");
        assertTrue(check2.successful);
        CheckFuncResult check3 = checkFunc.check("whtever-abb");
        assertTrue(check3.successful);

        // failing

        CheckFuncResult failingCheck1 = checkFunc.check("");
        assertTrue(failingCheck1.failed());

        CheckFuncResult failingCheck2 = checkFunc.check("aa-nope");
        assertTrue(failingCheck2.failed());
    }

    @Test
    public void testEmptyWhitespaceOrMeetsChecks() {
        CheckFunc<String> checkFunc = emptyWhitespaceOrMeetsChecks(lengthGreaterThanOrEqual(3), lengthLessThan(7));

        // passing
        CheckFuncResult check1 = checkFunc.check("\t\t");
        assertTrue(check1.successful);
        CheckFuncResult check2 = checkFunc.check("\t\t  \n");
        assertTrue(check2.successful);
        CheckFuncResult check3 = checkFunc.check("yep");
        assertTrue(check3.successful);
        CheckFuncResult check4 = checkFunc.check("\tyep");
        assertTrue(check4.successful);

        // failing
        CheckFuncResult failingCheck1 = checkFunc.check("toolonghere");
        assertTrue(failingCheck1.failed());
        CheckFuncResult failingCheck2 = checkFunc.check("\t\ttoolonghere");
        assertTrue(failingCheck2.failed());
    }

}
