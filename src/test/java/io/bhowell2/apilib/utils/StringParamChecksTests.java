package io.bhowell2.apilib.utils;

import io.bhowell2.apilib.CheckFunc;
import io.bhowell2.apilib.CheckFuncResult;
import io.bhowell2.apilib.utils.paramchecks.StringParamChecks;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static io.bhowell2.apilib.utils.paramchecks.StringParamChecks.*;
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

        CheckFuncResult check2 = checkFunc.check("123 ");
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

    @Test
    public void testCannotBeginWithCharsString() {
        CheckFunc<String> checkFunc = cannotBeginWithChars("abc");

        // passing
        CheckFuncResult check1 = checkFunc.check("hey");
        assertTrue(check1.successful);
        CheckFuncResult check2 = checkFunc.check("123");
        assertTrue(check2.successful);
        CheckFuncResult check3 = checkFunc.check("1abc23");
        assertTrue(check3.successful);

        // failing
        CheckFuncResult failingCheck1 = checkFunc.check("a123");
        assertTrue(failingCheck1.failed());
        CheckFuncResult failingCheck2 = checkFunc.check("ba123");
        assertTrue(failingCheck2.failed());
        CheckFuncResult failingCheck3 = checkFunc.check("ca123");
        assertTrue(failingCheck3.failed());
    }

    @Test
    public void testCannotBeginWithCharsSet() {
        CheckFunc<String> checkFunc = cannotBeginWithChars(new HashSet<>(Arrays.asList('a', 'b', 'c')));

        // passing
        CheckFuncResult check1 = checkFunc.check("hey");
        assertTrue(check1.successful);
        CheckFuncResult check2 = checkFunc.check("123");
        assertTrue(check2.successful);
        CheckFuncResult check3 = checkFunc.check("1abc23");
        assertTrue(check3.successful);

        // failing
        CheckFuncResult failingCheck1 = checkFunc.check("a123");
        assertTrue(failingCheck1.failed());
        CheckFuncResult failingCheck2 = checkFunc.check("ba123");
        assertTrue(failingCheck2.failed());
        CheckFuncResult failingCheck3 = checkFunc.check("ca123");
        assertTrue(failingCheck3.failed());
    }

    @Test
    public void testEqualsListOfStrings() {
        CheckFunc<String> checkFunc = equalsStringInList(Arrays.asList("autocomplete", "search"));

        // passing
        CheckFuncResult check1 = checkFunc.check("autocomplete");
        assertTrue(check1.successful);
        CheckFuncResult check2 = checkFunc.check("search");
        assertTrue(check2.successful);

        // failing
        CheckFuncResult failingCheck1 = checkFunc.check("fine");
        assertTrue(failingCheck1.failed());
        CheckFuncResult failingCheck2 = checkFunc.check("a");
        assertTrue(failingCheck2.failed());
        CheckFuncResult failingCheck3 = checkFunc.check("");
        assertTrue(failingCheck3.failed());
        CheckFuncResult failingCheck4 = checkFunc.check("1autocomplete");
        assertTrue(failingCheck4.failed());
        CheckFuncResult failingCheck5 = checkFunc.check("autocomplete1");
        assertTrue(failingCheck5.failed());
        CheckFuncResult failingCheck6 = checkFunc.check("autocomplete ");
        assertTrue(failingCheck6.failed());
        CheckFuncResult failingCheck7 = checkFunc.check(" autocomplete");
        assertTrue(failingCheck7.failed());
        CheckFuncResult failingCheck8 = checkFunc.check("searchh");
        assertTrue(failingCheck8.failed());
        CheckFuncResult failingCheck9 = checkFunc.check("1autocomplete");
        assertTrue(failingCheck9.failed());
        CheckFuncResult failingCheck10 = checkFunc.check("AUTOCOMPLETE");
        assertTrue(failingCheck10.failed());
    }

    @Test
    public void testEqualsStringInSet() {
        CheckFunc<String> checkFunc = equalsStringInSet(new HashSet<>(Arrays.asList("autocomplete", "search")));

        // passing
        CheckFuncResult check1 = checkFunc.check("autocomplete");
        assertTrue(check1.successful);
        CheckFuncResult check2 = checkFunc.check("search");
        assertTrue(check2.successful);

        // failing
        CheckFuncResult failingCheck1 = checkFunc.check("fine");
        assertTrue(failingCheck1.failed());
        CheckFuncResult failingCheck2 = checkFunc.check("a");
        assertTrue(failingCheck2.failed());
        CheckFuncResult failingCheck3 = checkFunc.check("");
        assertTrue(failingCheck3.failed());
        CheckFuncResult failingCheck4 = checkFunc.check("1autocomplete");
        assertTrue(failingCheck4.failed());
        CheckFuncResult failingCheck5 = checkFunc.check("autocomplete1");
        assertTrue(failingCheck5.failed());
        CheckFuncResult failingCheck6 = checkFunc.check("autocomplete ");
        assertTrue(failingCheck6.failed());
        CheckFuncResult failingCheck7 = checkFunc.check(" autocomplete");
        assertTrue(failingCheck7.failed());
        CheckFuncResult failingCheck8 = checkFunc.check("searchh");
        assertTrue(failingCheck8.failed());
        CheckFuncResult failingCheck9 = checkFunc.check("1autocomplete");
        assertTrue(failingCheck9.failed());
        CheckFuncResult failingCheck10 = checkFunc.check("AUTOCOMPLETE");
        assertTrue(failingCheck10.failed());
    }

    @Test
    public void testDoesNotEqualListOfStrings() {
        CheckFunc<String> checkFunc = doesNotEqualStringInList(Arrays.asList("autocomplete", "search"));

        // passing
        CheckFuncResult check1 = checkFunc.check("fine");
        assertTrue(check1.successful);
        CheckFuncResult check2 = checkFunc.check("a");
        assertTrue(check2.successful);
        CheckFuncResult check3 = checkFunc.check("");
        assertTrue(check3.successful);
        CheckFuncResult check4 = checkFunc.check("1autocomplete");
        assertTrue(check4.successful);
        CheckFuncResult check5 = checkFunc.check("autocomplete1");
        assertTrue(check5.successful);
        CheckFuncResult check6 = checkFunc.check("autocomplete ");
        assertTrue(check6.successful);
        CheckFuncResult check7 = checkFunc.check(" autocomplete");
        assertTrue(check7.successful);
        CheckFuncResult check8 = checkFunc.check("searchh");
        assertTrue(check8.successful);
        CheckFuncResult check9 = checkFunc.check("1autocomplete");
        assertTrue(check9.successful);
        CheckFuncResult check10 = checkFunc.check("AUTOCOMPLETE");
        assertTrue(check10.successful);


        // failing
        CheckFuncResult failingCheck1 = checkFunc.check("autocomplete");
        assertTrue(failingCheck1.failed());
        CheckFuncResult failingCheck2 = checkFunc.check("search");
        assertTrue(failingCheck2.failed());
    }

    @Test
    public void testDoesNotEqualStringInListIgnoreCase() {
        CheckFunc<String> checkFunc = doesNotEqualStringInListIgnoreCase(Arrays.asList("a", "beE", "GROUP"));

        // passing
        CheckFuncResult check1 = checkFunc.check("aa");
        assertTrue(check1.successful);
        CheckFuncResult check2 = checkFunc.check("Beee");
        assertTrue(check2.successful);
        CheckFuncResult check3 = checkFunc.check("AA");
        assertTrue(check3.successful);
        CheckFuncResult check4 = checkFunc.check("groups");
        assertTrue(check4.successful);
        CheckFuncResult check5 = checkFunc.check("grou");
        assertTrue(check5.successful);
        CheckFuncResult check6 = checkFunc.check("test");
        assertTrue(check6.successful);
        CheckFuncResult check7 = checkFunc.check("WHATEVER");
        assertTrue(check7.successful);

        // failing
        CheckFuncResult failingCheck1 = checkFunc.check("A");
        assertTrue(failingCheck1.failed());
        CheckFuncResult failingCheck2 = checkFunc.check("a");
        assertTrue(failingCheck2.failed());
        CheckFuncResult failingCheck3 = checkFunc.check("BEE");
        assertTrue(failingCheck3.failed());
        CheckFuncResult failingCheck4 = checkFunc.check("bEe");
        assertTrue(failingCheck4.failed());
        CheckFuncResult failingCheck5 = checkFunc.check("group");
        assertTrue(failingCheck5.failed());
        CheckFuncResult failingCheck6 = checkFunc.check("GROUP");
        assertTrue(failingCheck6.failed());
        CheckFuncResult failingCheck7 = checkFunc.check("gRoUp");
        assertTrue(failingCheck7.failed());

    }

    @Test
    public void testDoesNotEqualStringInSet() {
        CheckFunc<String> checkFunc = doesNotEqualStringInSet(new HashSet<>(Arrays.asList("autocomplete", "search")));

        // passing
        CheckFuncResult check1 = checkFunc.check("fine");
        assertTrue(check1.successful);
        CheckFuncResult check2 = checkFunc.check("a");
        assertTrue(check2.successful);
        CheckFuncResult check3 = checkFunc.check("");
        assertTrue(check3.successful);
        CheckFuncResult check4 = checkFunc.check("1autocomplete");
        assertTrue(check4.successful);
        CheckFuncResult check5 = checkFunc.check("autocomplete1");
        assertTrue(check5.successful);
        CheckFuncResult check6 = checkFunc.check("autocomplete ");
        assertTrue(check6.successful);
        CheckFuncResult check7 = checkFunc.check(" autocomplete");
        assertTrue(check7.successful);
        CheckFuncResult check8 = checkFunc.check("searchh");
        assertTrue(check8.successful);
        CheckFuncResult check9 = checkFunc.check("1autocomplete");
        assertTrue(check9.successful);
        CheckFuncResult check10 = checkFunc.check("AUTOCOMPLETE");
        assertTrue(check10.successful);

        // failing
        CheckFuncResult failingCheck1 = checkFunc.check("autocomplete");
        assertTrue(failingCheck1.failed());
        CheckFuncResult failingCheck2 = checkFunc.check("search");
        assertTrue(failingCheck2.failed());
    }

    @Test
    public void testUnreservedUrlCharacters() {
        CheckFunc<String> checkFunc = ONLY_ALLOW_UNRESERVED_URL_CHARS;

        // passing
        CheckFuncResult check1 = checkFunc.check("heyyyoal.123");
        assertTrue(check1.successful);
        CheckFuncResult check2 = checkFunc.check("ABC123...__~--");
        assertTrue(check2.successful);
        CheckFuncResult check3 = checkFunc.check("._-~");
        assertTrue(check3.successful);
        CheckFuncResult check4 = checkFunc.check("1234567890");
        assertTrue(check4.successful);
        CheckFuncResult check5 = checkFunc.check("abcdefghijklmnopqrstuvwxyz");
        assertTrue(check5.successful);
        CheckFuncResult check6 = checkFunc.check("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        assertTrue(check6.successful);
        CheckFuncResult check7 = checkFunc.check("A");
        assertTrue(check7.successful);

        // failing
        CheckFuncResult failingCheck1 = checkFunc.check("!");
        assertTrue(failingCheck1.failed());
        CheckFuncResult failingCheck2 = checkFunc.check("abcd$&?*");
        assertTrue(failingCheck2.failed());
        CheckFuncResult failingCheck3 = checkFunc.check("._-~!");
        assertTrue(failingCheck3.failed());
        CheckFuncResult failingCheck4 = checkFunc.check("nope#");
        assertTrue(failingCheck4.failed());
        CheckFuncResult failingCheck5 = checkFunc.check("@");
        assertTrue(failingCheck5.failed());
        CheckFuncResult failingCheck6 = checkFunc.check("%");
        assertTrue(failingCheck6.failed());
        CheckFuncResult failingCheck7 = checkFunc.check("ABCD ");
        assertTrue(failingCheck7.failed());

    }

    @Test
    public void testIsNotEmptyOrOnlyWhitespace() {
        CheckFunc<String> checkFunc = IS_NOT_EMPTY_OR_ONLY_WHITESPACE;

        // passing
        CheckFuncResult check1 = checkFunc.check("hey");
        assertTrue(check1.successful);
        CheckFuncResult check2 = checkFunc.check("hey ");
        assertTrue(check2.successful);
        CheckFuncResult check3 = checkFunc.check(" okay");
        assertTrue(check3.successful);
        CheckFuncResult check4 = checkFunc.check("! !");
        assertTrue(check4.successful);
        CheckFuncResult check5 = checkFunc.check("hey\t");
        assertTrue(check5.successful);
        CheckFuncResult check6 = checkFunc.check("\t\nhey");
        assertTrue(check6.successful);

        // failing
        CheckFuncResult failingCheck = checkFunc.check("");
        assertTrue(failingCheck.failed());
        CheckFuncResult failingCheck1 = checkFunc.check(" ");
        assertTrue(failingCheck1.failed());
        CheckFuncResult failingCheck2 = checkFunc.check("\t");
        assertTrue(failingCheck2.failed());
        CheckFuncResult failingCheck3 = checkFunc.check("\n");
        assertTrue(failingCheck3.failed());
        CheckFuncResult failingCheck4 = checkFunc.check("\t\n");
        assertTrue(failingCheck4.failed());
        CheckFuncResult failingCheck5 = checkFunc.check("\t\n ");
        assertTrue(failingCheck5.failed());
    }

    @Test
    public void testIsEmptyOrContainsOnlyWhitespace() {
        CheckFunc<String> checkFunc = IS_NOT_EMPTY_OR_ONLY_WHITESPACE;

        // passing
        CheckFuncResult check = checkFunc.check("");
        assertTrue(check.failed());
        CheckFuncResult check1 = checkFunc.check(" ");
        assertTrue(check1.failed());
        CheckFuncResult check2 = checkFunc.check("\t");
        assertTrue(check2.failed());
        CheckFuncResult check3 = checkFunc.check("\n");
        assertTrue(check3.failed());
        CheckFuncResult check4 = checkFunc.check("\t\n");
        assertTrue(check4.failed());
        CheckFuncResult check5 = checkFunc.check("\t\n ");
        assertTrue(check5.failed());


        // failing
        CheckFuncResult failingCheck1 = checkFunc.check("hey");
        assertTrue(failingCheck1.successful);
        CheckFuncResult failingCheck2 = checkFunc.check("hey ");
        assertTrue(failingCheck2.successful);
        CheckFuncResult failingCheck3 = checkFunc.check(" okay");
        assertTrue(failingCheck3.successful);
        CheckFuncResult failingCheck4 = checkFunc.check("! !");
        assertTrue(failingCheck4.successful);
        CheckFuncResult failingCheck5 = checkFunc.check("hey\t");
        assertTrue(failingCheck5.successful);
        CheckFuncResult failingCheck6 = checkFunc.check("\t\nhey");
        assertTrue(failingCheck6.successful);

    }

}
