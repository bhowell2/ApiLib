package io.bhowell2.apilib.utils;


import io.bhowell2.apilib.utils.formatfuncs.StringFormatFuncs;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Blake Howell
 */
public class StringFormatFuncsTests {

    @Test
    public void shouldRemoveLeadingAndTrailingWhiteSpace() throws Exception {
        String preTrim = "  he y   ";    // 2 leading spaces, 3 trailing
        int preLength = preTrim.length();
        String postTrim = StringFormatFuncs.TRIM.format(preTrim);
        assertEquals(preLength - 5, postTrim.length());

        String preTrim2 = "\t\t\n hey \n\n";
        String postTrim2 = StringFormatFuncs.TRIM.format(preTrim2);
        assertEquals(3, postTrim2.length());
    }

    @Test
    public void shouldOnlyRemoveTrailingWhiteSpace() throws Exception {
        String preTrimTrailing = "\nh e y \t\t\n";
        String postTrimTrailing = StringFormatFuncs.REMOVE_TRAILING_WHITESPACE.format(preTrimTrailing);
        assertEquals(6, postTrimTrailing.length());
    }

    @Test
    public void shouldReplaceMatchingString() throws Exception {
        String preReplace = "this is not the correct string";
        String expectedString = "this is the correct string";
        // note we're replacing 'not' AND the space before it
        String replaced = StringFormatFuncs.replaceStringPartWith(" not", "").format(preReplace);
        assertEquals(expectedString, replaced);
    }


    @Test
    public void shouldUpperCaseString() throws Exception {
        String somelower = "loWERcase";
        String uppered = StringFormatFuncs.TO_UPPER_CASE.format(somelower);
        assertEquals("LOWERCASE", uppered);
    }

}
