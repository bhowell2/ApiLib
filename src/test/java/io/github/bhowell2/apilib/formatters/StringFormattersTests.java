package io.github.bhowell2.apilib.formatters;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Blake Howell
 */
public class StringFormattersTests {

	@Test
	public void shouldTrimLeadingAndTrailingWhitespace() throws Exception {
		String s = "  some leading and trailing white space  \n\t";
		Formatter.Result<String> result = StringFormatters.TRIM_LEADING_AND_TRAILING_WHITESPACE.format(s);
		assertTrue(result.successful());
		assertFalse(result.failed());
	  assertEquals("some leading and trailing white space", result.formattedValue);
	}

	@Test
	public void shouldTrimOnlyLeadingWhitespace() throws Exception {
		String s = "\n\t  some leading and trailing whitespace \n";
		Formatter.Result<String> result = StringFormatters.TRIM_LEADING_WHITESPACE.format(s);
		assertTrue(result.successful());
		assertFalse(result.failed());
		assertEquals("some leading and trailing whitespace \n", result.formattedValue);
	}

	@Test
	public void shouldTrimOnlyTrailingWhitespace() throws Exception {
		String s = "\n\t  some leading and trailing whitespace \n";
		Formatter.Result<String> result = StringFormatters.TRIM_TRAILING_WHITESPACE.format(s);
		assertTrue(result.successful());
		assertFalse(result.failed());
		assertEquals("\n\t  some leading and trailing whitespace", result.formattedValue);
	}

	@Test
	public void shouldUppercaseCharactersOfString() throws Exception {
		String s = "lower cased";
		String uppercasedS = s.toUpperCase();
		Formatter.Result<String> result = StringFormatters.TO_UPPERCASE.format(s);
		assertTrue(result.successful());
		assertEquals(uppercasedS, result.formattedValue);
	}

}
