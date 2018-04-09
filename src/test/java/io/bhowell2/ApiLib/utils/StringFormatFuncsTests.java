package io.bhowell2.ApiLib.utils;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Blake Howell
 */
public class StringFormatFuncsTests {

    @Test
    public void shouldRemoveLeadingAndTrailingWhiteSpace() {
        String preTrim = "  he y   ";    // 2 leading spaces, 3 trailing
        int preLength = preTrim.length();
        String postTrim = StringFormatFuncs.TRIM.format(preTrim);
        Assertions.assertEquals(preLength - 5, postTrim.length());

        String preTrim2 = "\t\t\n hey \n\n";
        String postTrim2 = StringFormatFuncs.TRIM.format(preTrim2);
        Assertions.assertEquals(3, postTrim2.length());
    }

    @Test
    public void shouldOnlyRemoveTrailingWhiteSpace() {
        String preTrimTrailing = "\nh e y \t\t\n";
        String postTrimTrailing = StringFormatFuncs.REMOVE_TRAILING_WHITESPACE.format(preTrimTrailing);
        Assertions.assertEquals(6, postTrimTrailing.length());
    }

    @Test
    public void shouldReplaceMatchingString() {
        String preReplace = "this is not the correct string";
        String expectedString = "this is the correct string";
        // note we're replacing 'not' AND the space before it
        String replaced = StringFormatFuncs.replaceStringPartWith(" not", "").format(preReplace);
        Assertions.assertEquals(expectedString, replaced);
    }

}
