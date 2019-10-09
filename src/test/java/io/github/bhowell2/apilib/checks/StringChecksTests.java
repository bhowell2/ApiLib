package io.github.bhowell2.apilib.checks;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Ensures each string check works as expected.
 * @author Blake Howell
 */
public class StringChecksTests extends ChecksTestBase {

	@Test
	public void testIsEmpty() throws Exception {
		// successful
		assertCheckSuccessful(StringChecks.IS_EMPTY.check(""));
		// unsuccessful
		assertCheckFailed(StringChecks.IS_EMPTY.check(" "));
		assertCheckFailed(StringChecks.IS_EMPTY.check("1"));
		assertCheckFailed(StringChecks.IS_EMPTY.check("🌋"));
	}

	@Test
	public void testIsNotEmpty() throws Exception {
		// successful
		assertCheckSuccessful(StringChecks.IS_NOT_EMPTY.check(" "));
		assertCheckSuccessful(StringChecks.IS_NOT_EMPTY.check("\u0000"));
		// unsuccessful
		assertCheckFailed(StringChecks.IS_NOT_EMPTY.check(""));
	}

	@Test
	public void testIsEmptyOrOnlyWhitespace() throws Exception {
		// successful
		assertCheckSuccessful(StringChecks.IS_EMPTY_OR_ONLY_WHITESPACE.check(""));        // empty
		assertCheckSuccessful(StringChecks.IS_EMPTY_OR_ONLY_WHITESPACE.check(" "));       // single space
		assertCheckSuccessful(StringChecks.IS_EMPTY_OR_ONLY_WHITESPACE.check("    "));    // multiple spaces
		assertCheckSuccessful(StringChecks.IS_EMPTY_OR_ONLY_WHITESPACE.check("\t"));      // single tab
		assertCheckSuccessful(StringChecks.IS_EMPTY_OR_ONLY_WHITESPACE.check("\t\t"));    // multiple tabs
		assertCheckSuccessful(StringChecks.IS_EMPTY_OR_ONLY_WHITESPACE.check("\n"));      // single new line
		assertCheckSuccessful(StringChecks.IS_EMPTY_OR_ONLY_WHITESPACE.check("\n\n\n"));  // multiple new lines
		assertCheckSuccessful(StringChecks.IS_EMPTY_OR_ONLY_WHITESPACE.check("\t \n"));   // multiple whitespace chars
		// unsuccessful
		assertCheckFailed(StringChecks.IS_EMPTY_OR_ONLY_WHITESPACE.check("\uD83E\uDD13\n"));    // nerd face unicode, end new line
		assertCheckFailed(StringChecks.IS_EMPTY_OR_ONLY_WHITESPACE.check(" \uD83D\uDD25"));     // fire unicode
		assertCheckFailed(StringChecks.IS_EMPTY_OR_ONLY_WHITESPACE.check("\t\uD83D\uDD25"));    // fire start with tab
		assertCheckFailed(StringChecks.IS_EMPTY_OR_ONLY_WHITESPACE.check("\n\uD83D\uDD25"));    // fire start with line feed
	}

	@Test
	public void testNotIsEmptyOrOnlyWhitespace() throws Exception {
		// successful
		assertCheckSuccessful(StringChecks.IS_NOT_EMPTY_OR_ONLY_WHITESPACE.check("\uD83E\uDD13\n"));   // nerd face unicode
		assertCheckSuccessful(StringChecks.IS_NOT_EMPTY_OR_ONLY_WHITESPACE.check(" \uD83D\uDD25"));     // fire unicode
		assertCheckSuccessful(StringChecks.IS_NOT_EMPTY_OR_ONLY_WHITESPACE.check("\t\uD83D\uDD25"));     // fire start with tab
		assertCheckSuccessful(StringChecks.IS_NOT_EMPTY_OR_ONLY_WHITESPACE.check("\n\uD83D\uDD25"));     // fire start with line feed
		// unsuccessful
		assertCheckFailed(StringChecks.IS_NOT_EMPTY_OR_ONLY_WHITESPACE.check(""));        // empty
		assertCheckFailed(StringChecks.IS_NOT_EMPTY_OR_ONLY_WHITESPACE.check(" "));       // single space
		assertCheckFailed(StringChecks.IS_NOT_EMPTY_OR_ONLY_WHITESPACE.check("    "));    // multiple spaces
		assertCheckFailed(StringChecks.IS_NOT_EMPTY_OR_ONLY_WHITESPACE.check("\t"));      // single tab
		assertCheckFailed(StringChecks.IS_NOT_EMPTY_OR_ONLY_WHITESPACE.check("\t\t"));    // multiple tabs
		assertCheckFailed(StringChecks.IS_NOT_EMPTY_OR_ONLY_WHITESPACE.check("\n"));      // single new line
		assertCheckFailed(StringChecks.IS_NOT_EMPTY_OR_ONLY_WHITESPACE.check("\n\n\n"));  // multiple new lines
		assertCheckFailed(StringChecks.IS_NOT_EMPTY_OR_ONLY_WHITESPACE.check("\t \n"));   // many things
	}

	@Test
	public void testOnlyAllowUnreservedUrlChars() throws Exception {
		// successful
		String unreservedLowerCase = "abcdefghijklmnopqrstuvwxyz0123456789-._~";
		assertCheckSuccessful(StringChecks.ONLY_ALLOW_UNRESERVED_URL_CHARS.check("www.google.com"));
		assertCheckSuccessful(StringChecks.ONLY_ALLOW_UNRESERVED_URL_CHARS.check(unreservedLowerCase));
		assertCheckSuccessful(StringChecks.ONLY_ALLOW_UNRESERVED_URL_CHARS.check(unreservedLowerCase.toUpperCase()));
		// unsuccessful
		assertCheckFailed(StringChecks.ONLY_ALLOW_UNRESERVED_URL_CHARS.check("www.google.com/http"));
		assertCheckFailed(StringChecks.ONLY_ALLOW_UNRESERVED_URL_CHARS.check("www.google.com🌋"));
	}

	@Test
	public void testMatchesBasicEmailPattern() throws Exception {
	  // successful
		assertCheckSuccessful(StringChecks.MATCHES_BASIC_EMAIL_PATTERN.check("\u0000@b.co"));
		assertCheckSuccessful(StringChecks.MATCHES_BASIC_EMAIL_PATTERN.check("a@b.co"));
		assertCheckSuccessful(StringChecks.MATCHES_BASIC_EMAIL_PATTERN.check("a@b.com"));
		assertCheckSuccessful(StringChecks.MATCHES_BASIC_EMAIL_PATTERN.check("a🤓@🤓b.com"));
		assertCheckSuccessful(StringChecks.MATCHES_BASIC_EMAIL_PATTERN.check("apilib@gmail.com"));
		// unsuccessful
		assertCheckFailed(StringChecks.MATCHES_BASIC_EMAIL_PATTERN.check("@be.cc"));
		assertCheckFailed(StringChecks.MATCHES_BASIC_EMAIL_PATTERN.check("a@b.c"));
		assertCheckFailed(StringChecks.MATCHES_BASIC_EMAIL_PATTERN.check("is@invalid"));
		assertCheckFailed(StringChecks.MATCHES_BASIC_EMAIL_PATTERN.check("@invalid.com"));
		assertCheckFailed(StringChecks.MATCHES_BASIC_EMAIL_PATTERN.check("@invalid.com"));
	}

	@Test
	public void testLengthGreaterThan() throws Exception {
		Check<String> check = StringChecks.lengthGreaterThan(5);
		// successful
		assertCheckSuccessful(check.check("longer"));
		assertCheckSuccessful(check.check("much longer than a length of 5...!"));
		assertCheckSuccessful(check.check("🤓3456"));
		// unsuccessful
		assertCheckFailed(check.check(""));
		assertCheckFailed(check.check("1234"));
		assertCheckFailed(check.check("🤓345"));
		assertCheckFailed(check.check("123🤓"));

		Check<String> check0 = StringChecks.lengthGreaterThan(0);
		// successful
		assertCheckSuccessful(check0.check("1"));
		// unsuccessful
		assertCheckFailed(check0.check(""));

		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.lengthGreaterThan(-1);
		});
	}

	@Test
	public void testCodePointLengthGreaterThan() throws Exception {
		Check<String> check = StringChecks.codePointCountGreaterThan(5);
		assertCheckSuccessful(check.check("longer"));
		assertCheckSuccessful(check.check("much longer than a length of 5...!"));
		assertCheckSuccessful(check.check("🤓23456"));   // codepoint for nerd face is 1, then each ASCII char is 1 as well
		// unsuccessful
		assertCheckFailed(check.check(""));
		assertCheckFailed(check.check("1234"));
		assertCheckFailed(check.check("🤓345"));
		assertCheckFailed(check.check("123🤓"));

		Check<String> check0 = StringChecks.codePointCountGreaterThan(0);
		// successful
		assertCheckSuccessful(check0.check("1"));
		assertCheckSuccessful(check0.check("🤓"));
		// unsuccessful
		assertCheckFailed(check0.check(""));

		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.codePointCountGreaterThan(-1);
		});
	}

	@Test
	public void testLengthGreaterThanOrEqualTo() throws Exception {
		Check<String> check = StringChecks.lengthGreaterThanOrEqualTo(7);
		// successful
		assertCheckSuccessful(check.check("1234567"));
		assertCheckSuccessful(check.check("1234567 and a bunch more text here"));
		assertCheckSuccessful(check.check("🤓34567"));
		// unsuccessful
		assertCheckFailed(check.check(""));
		assertCheckFailed(check.check("123456"));
		assertCheckFailed(check.check("🤓3456"));

		Check<String> check0 = StringChecks.lengthGreaterThanOrEqualTo(0);
		// successful
		assertCheckSuccessful(check0.check("1"));
		assertCheckSuccessful(check0.check(""));

		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.lengthGreaterThanOrEqualTo(-1);
		});
	}

	@Test
	public void testCodePointLengthGreaterThanOrEqualTo() throws Exception {
		Check<String> check = StringChecks.codePointCountGreaterThanOrEqualTo(2);
		// successful
		assertCheckSuccessful(check.check("12"));
		assertCheckSuccessful(check.check("🤓🤓"));
		// unsuccessful
		assertCheckFailed(check.check("1"));
		assertCheckFailed(check.check("🤓"));

		Check<String> check0 = StringChecks.codePointCountGreaterThanOrEqualTo(0);
		assertCheckSuccessful(check0.check(""));
		assertCheckSuccessful(check0.check("🤓"));

		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.codePointCountGreaterThanOrEqualTo(-1);
		});
	}

	@Test
	public void testLengthLessThan() throws Exception {
		Check<String> check = StringChecks.lengthLessThan(6);
		// successful
		assertCheckSuccessful(check.check("12345"));
		assertCheckSuccessful(check.check(""));
		// unsuccessful
		assertCheckFailed(check.check("1234567"));
		assertCheckFailed(check.check("much longer than 6 characters"));

		Check<String> check1 = StringChecks.lengthLessThan(1);
		// successful
		assertCheckSuccessful(check1.check(""));
		// unsuccessful
		assertCheckFailed(check1.check("1"));
		assertCheckFailed(check1.check("𩸽"));

		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.lengthLessThan(0);
		});
	}

	@Test
	public void testCodePointLengthLessThan() throws Exception {
		Check<String> check = StringChecks.codePointCountLessThan(6);
		// successful
		assertCheckSuccessful(check.check("12345"));
		assertCheckSuccessful(check.check("\uD867\uDE3D345")); // 𩸽 (2 chars)
		assertCheckSuccessful(check.check("𩸽𩸽1"));
		assertCheckSuccessful(check.check("𩸽345"));
		// unsuccessful
		assertCheckFailed(check.check("𩸽𩸽𩸽\uD83E\uDD1356"));
		assertCheckFailed(check.check("𩸽𩸽𩸽𩸽𩸽𩸽"));
		assertCheckFailed(check.check("1234567"));

		Check<String> check1 = StringChecks.codePointCountLessThan(1);
		// successful
		assertCheckSuccessful(check1.check(""));
		// unsuccessful
		assertCheckFailed(check1.check("1"));

		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.codePointCountLessThan(0);
		});
	}

	@Test
	public void testLengthLessThanOrEqualTo() throws Exception {
		Check<String> check = StringChecks.lengthLessThanOrEqualTo(15);
		// successful
		assertCheckSuccessful(check.check("123456789123456"));
		assertCheckSuccessful(check.check("12345678912345"));
		assertCheckSuccessful(check.check(""));
		// unsuccessful
		assertCheckFailed(check.check("1234567891234567"));
		assertCheckFailed(check.check("123456789123456 and some more to fail it"));
		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.lengthLessThanOrEqualTo(-1);
		});
		assertDoesNotThrow(() -> {
			StringChecks.lengthLessThanOrEqualTo(0);
			StringChecks.lengthLessThanOrEqualTo(1);
		});
	}

	@Test
	public void testCodePointLengthLessThanOrEqualTo() throws Exception {
		Check<String> check = StringChecks.codePointCountLessThanOrEqualTo(4);
		// successful
		assertCheckSuccessful(check.check("1234"));
		assertCheckSuccessful(check.check("𩸽🌋"));
		assertCheckSuccessful(check.check("🌋🌋"));
		assertCheckSuccessful(check.check("🌋🌋🌋🌋"));
		// unsuccessful
		assertCheckFailed(check.check("12345"));
		assertCheckFailed(check.check("🌋🌋🌋🌋𩸽"));
		assertCheckFailed(check.check("🌋🌋345"));

		Check<String> check0 = StringChecks.codePointCountLessThanOrEqualTo(0);
		// successful
		assertCheckSuccessful(check0.check(""));
		// unsuccessful
		assertCheckFailed(check0.check("🌋"));
		assertCheckFailed(check0.check("1"));

		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.codePointCountLessThanOrEqualTo(-1);
		});
	}

	@Test
	public void testLengthEqualTo() throws Exception {
		Check<String> check = StringChecks.lengthEqualTo(0);
		// successful
		assertCheckSuccessful(check.check(""));
		// unsuccessful
		assertCheckFailed(check.check("l"));
		assertCheckFailed(check.check("🌋"));

		Check<String> check5 = StringChecks.lengthEqualTo(5);
		// successful
		assertCheckSuccessful(check5.check("12345"));
		assertCheckSuccessful(check5.check("🌋🌋5"));
		assertCheckSuccessful(check5.check("1🌋🌋"));
		// unsuccessful
		assertCheckFailed(check5.check("123456"));
		assertCheckFailed(check5.check("1234"));
		assertCheckFailed(check5.check("1🌋🌋🌋"));

		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.lengthEqualTo(-1);
		});
	}

	@Test
	public void testCodePointLengthEqualTo() throws Exception {
		Check<String> check = StringChecks.codePointCountEqualTo(0);
		// successful
		assertCheckSuccessful(check.check(""));
		//  unsuccessful
		assertCheckFailed(check.check("1"));
		assertCheckFailed(check.check("🌋"));

		Check<String> check5 = StringChecks.codePointCountEqualTo(5);
		// successful
		assertCheckSuccessful(check5.check("12345"));
		assertCheckSuccessful(check5.check("1🌋345"));         // code point count 5
		// unsuccessful
		assertCheckFailed(check5.check("123456"));         // code point count 6
		assertCheckFailed(check5.check("1234"));           // code point count 4
		assertCheckFailed(check5.check("🌋🌋🌋"));        // code point count 3
		assertCheckFailed(check5.check("12345🌋🌋🌋"));   // code point count 8

		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.codePointCountEqualTo(-1);
		});
	}

	@Test
	public void testMatchesRegex() throws Exception {
		Check<String> check = StringChecks.matchesRegex(Pattern.compile("hey|horses"));
		// successful
		assertCheckSuccessful(check.check("hey"));
		assertCheckSuccessful(check.check("horses"));
		// unsuccessful
		assertCheckFailed(check.check("horse"));
		assertCheckFailed(check.check("he"));
		assertCheckFailed(check.check(""));

		// begins with zxy and ends with abc, may have chars between the two.
		Check<String> check1 = StringChecks.matchesRegex(Pattern.compile("^[zyx].*[abc]$"));
		// successful
		assertCheckSuccessful(check1.check("za"));
		assertCheckSuccessful(check1.check("xjdiuq8912983b"));
		assertCheckSuccessful(check1.check("x-c"));
		// unsuccessful
		assertCheckFailed(check1.check("nope"));
		assertCheckFailed(check1.check("\uD83E\uDD14"));
		assertCheckFailed(check1.check(" "));

		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.matchesRegex(Pattern.compile(""));
		});
	}

	@Test
	public void testBeginsWithCodePoints() throws Exception {
		Check<String> check = StringChecks.beginsWithCodePoints("aBc!🤓 ");
		// successful
		assertCheckSuccessful(check.check("a yo!"));
		assertCheckSuccessful(check.check("Bdd"));
		assertCheckSuccessful(check.check("cadd"));
		assertCheckSuccessful(check.check("!yo!"));
		assertCheckSuccessful(check.check("🤓  doesn't matter"));
		assertCheckSuccessful(check.check("  noo"));
		// unsuccessful
		assertCheckFailed(check.check(""));
		assertCheckFailed(check.check("^🤓  noo"));
		assertCheckFailed(check.check("dd"));
		assertCheckFailed(check.check(""));

		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.beginsWithCodePoints("");
		});
		assertThrows(IllegalArgumentException.class, () -> {
			// does not allow repeated codepoints
			StringChecks.beginsWithCodePoints("  ");
		});
	}

	@Test
	public void testBeginsWithCodePointsRange() throws Exception {
		Check<String> check = StringChecks.beginsWithCodePointsInRange(0, 126);
		// successful
		assertCheckSuccessful(check.check("a81897"));
		assertCheckSuccessful(check.check("B"));
		assertCheckSuccessful(check.check("!"));
		assertCheckSuccessful(check.check("\u0000"));
		assertCheckSuccessful(check.check("\u007E"));
		// unsuccessful
		assertCheckFailed(check.check("🤓"));
		assertCheckFailed(check.check("\u9999"));
		assertCheckFailed(check.check(""));

		// not all emojis actually, but a test of some!
		Check<String> checkEmojiOnly = StringChecks.beginsWithCodePointsInRange("\uD83D\uDE00",
		                                                                        "\uD83D\uDE44");
		// successful
		assertCheckSuccessful(checkEmojiOnly.check("\uD83D\uDE00123"));
		assertCheckSuccessful(checkEmojiOnly.check("\uD83D\uDE2399"));
		assertCheckSuccessful(checkEmojiOnly.check("\uD83D\uDE44Abc71"));
		// unsuccessful
		assertCheckFailed(checkEmojiOnly.check("1"));
		assertCheckFailed(checkEmojiOnly.check("Ackj🙄"));

		Check<String> checkSinglePoint = StringChecks.beginsWithCodePointsInRange(0, 0);
		// successful
		assertCheckSuccessful(checkSinglePoint.check("\u0000"));
		assertCheckSuccessful(checkSinglePoint.check("\u0000\u0000"));
		// unsuccessful
		assertCheckFailed(checkSinglePoint.check(" "));
		assertCheckFailed(checkSinglePoint.check("\u0001"));

		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.beginsWithCodePointsInRange(-1, 10);
		});
		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.beginsWithCodePointsInRange("Z", "A");
		});
	}

	@Test
	public void testDoesNotBeginWithCodePoints() throws Exception {
		Check<String> check = StringChecks.doesNotBeginWithCodePoints("🤓!^");
		// successful
		assertCheckSuccessful(check.check("123"));
		assertCheckSuccessful(check.check("Abc"));
		assertCheckSuccessful(check.check("_bc"));
		assertCheckSuccessful(check.check(""));
		// unsuccessful
		assertCheckFailed(check.check("!"));
		assertCheckFailed(check.check("^Acb"));
		assertCheckFailed(check.check("🤓a"));

		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.doesNotBeginWithCodePoints("");
		});
		assertThrows(NullPointerException.class, () -> {
			StringChecks.doesNotBeginWithCodePoints(null);
		});
		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.doesNotBeginWithCodePoints("!!");
		});
	}

	@Test
	public void testDoesNotBeginWithCodePointsRange() throws Exception {
		Check<String> check = StringChecks.doesNotBeginWithCodePointsInRange("\uD83D\uDE00".codePointAt(0),
		                                                                     "\uD83D\uDE44".codePointAt(0));
		// successful
		assertCheckSuccessful(check.check(" "));
		assertCheckSuccessful(check.check(""));
		assertCheckSuccessful(check.check("hey"));
		assertCheckSuccessful(check.check("!hey"));
		// unsuccessful
		assertCheckFailed(check.check("\uD83D\uDE00"));
		assertCheckFailed(check.check("\uD83D\uDE44"));

		Check<String> check1 = StringChecks.doesNotBeginWithCodePointsInRange("A", "Z");
		// successful
		assertCheckSuccessful(check1.check(""));
		assertCheckSuccessful(check1.check("abcdefghijhl"));
		assertCheckSuccessful(check1.check("0"));
		assertCheckSuccessful(check1.check(" "));
		assertCheckSuccessful(check1.check("bACdef"));
		// unsuccessful
		assertCheckFailed(check1.check("Abcdeg"));
		assertCheckFailed(check1.check("A01"));

		Check<String> checkSingleCodePoint = StringChecks.doesNotBeginWithCodePointsInRange(0, 0);
		// successful
		assertCheckSuccessful(checkSingleCodePoint.check("\u0001abhcu "));
		assertCheckSuccessful(checkSingleCodePoint.check("0 "));
		assertCheckSuccessful(checkSingleCodePoint.check(" "));
		// unsuccessful
		assertCheckFailed(checkSingleCodePoint.check("\u0000whatever"));
		assertCheckFailed(checkSingleCodePoint.check("\u0000\u0000"));

		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.doesNotBeginWithCodePointsInRange("z", "a");
		});
		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.doesNotBeginWithCodePointsInRange(9, 8);
		});
	}

	@Test
	public void testBeginsWithStrings() throws Exception {
		Check<String> check = StringChecks.beginsWithStrings("one", "abc", "33");
		// successful
		assertCheckSuccessful(check.check("oneheyy!79"));
		assertCheckSuccessful(check.check("abc 123"));
		assertCheckSuccessful(check.check("333456"));
		// unsuccessful
		assertCheckFailed(check.check(""));
		assertCheckFailed(check.check("3 33456"));
		assertCheckFailed(check.check("cabc333456"));
		assertCheckFailed(check.check("\uD83E\uDD13one"));

		Check<String> checkEmoji = StringChecks.beginsWithStrings("\uD83E\uDD13", "\uD83E\uDD14");
		// successful
		assertCheckSuccessful(checkEmoji.check("🤓123"));
		assertCheckSuccessful(checkEmoji.check("\uD83E\uDD14 whatever"));
		// unsuccessful
		assertCheckFailed(checkEmoji.check(""));
		assertCheckFailed(checkEmoji.check("123🤓"));
		assertCheckFailed(checkEmoji.check("whatever"));

		Check<String> checkOne = StringChecks.beginsWithStrings("hmm");
		// successful
		assertCheckSuccessful(checkOne.check("hmm999"));
		assertCheckSuccessful(checkOne.check("hmm and one!"));
		// unsuccessful
		assertCheckFailed(checkOne.check("anything else"));
		assertCheckFailed(checkOne.check("ahmm "));

		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.beginsWithStrings((String)null);
		});
	}

	@Test
	public void testDoesNotBeginWithStrings() throws Exception {
		Check<String> check = StringChecks.doesNotBeginWithStrings("one", "abc", "33");
		// successful
		assertCheckSuccessful(check.check(""));
		assertCheckSuccessful(check.check(" "));
		assertCheckSuccessful(check.check("3 33456"));
		assertCheckSuccessful(check.check("cabc333456"));
		assertCheckSuccessful(check.check("\uD83E\uDD13one"));
		// unsuccessful
		assertCheckFailed(check.check("oneheyy!79"));
		assertCheckFailed(check.check("abc 123"));
		assertCheckFailed(check.check("333456"));

		Check<String> checkEmoji = StringChecks.doesNotBeginWithStrings("\uD83E\uDD13", "\uD83E\uDD14");
		// successful
		assertCheckSuccessful(checkEmoji.check("123🤓"));
		assertCheckSuccessful(checkEmoji.check("whatever"));
		// unsuccessful
		assertCheckFailed(checkEmoji.check("🤓123"));
		assertCheckFailed(checkEmoji.check("\uD83E\uDD14 whatever"));

		Check<String> checkOne = StringChecks.doesNotBeginWithStrings("hmm");
		// successful
		assertCheckSuccessful(checkOne.check("anything else"));
		assertCheckSuccessful(checkOne.check("ahmm "));
		// unsuccessful
		assertCheckFailed(checkOne.check("hmm999"));
		assertCheckFailed(checkOne.check("hmm and one!"));

		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.beginsWithStrings((String)null);
		});
	}

	@Test
	public void testLimitCodePointsToRange() throws Exception {
		Check<String> check = StringChecks.limitCodePointsToRange("A", "z", false);
		// successful
		assertCheckSuccessful(check.check("BcdwhateverzA"));
		assertCheckSuccessful(check.check("^"));
		// unsuccessful
		assertCheckFailed(check.check(""));
		assertCheckFailed(check.check("the exclaimation is wrong!"));
		assertCheckFailed(check.check(" "), "Spaces are not within that range, and whitespace is not allowed.");

		Check<String> checkAscii = StringChecks.limitCodePointsToRange(0, 126, false);
		StringBuilder asciiBuilder = new StringBuilder();
		for (int i = 0; i < 127; i++) {
			asciiBuilder.append((char) i);
		}
		// successful
		assertCheckSuccessful(checkAscii.check(asciiBuilder.toString()));
		assertCheckSuccessful(checkAscii.check("make sure space is still valid when it is part of range, but " +
			                                       "allow white space is false"));
		// unsuccessful
		assertCheckFailed(checkAscii.check("\uD83E\uDE00 whatever"));

		Check<String> checkNoRange = StringChecks.limitCodePointsToRange("A".codePointAt(0), "A".codePointAt(0), true);
		// successful
		assertCheckSuccessful(checkNoRange.check("A"));
		assertCheckSuccessful(checkNoRange.check(" \n"));
		assertCheckSuccessful(checkNoRange.check("A\tA"));
		// unsuccessful
		assertCheckFailed(checkNoRange.check("B"));
		assertCheckFailed(checkNoRange.check("AB"));
		assertCheckFailed(checkNoRange.check(""));
		assertCheckFailed(checkNoRange.check("\u0000"));

		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.limitCodePointsToRange("aa", "b", true);
		}, "Only a single codepoint is allowed.");
		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.limitCodePointsToRange("a", "bb", true);
		}, "Only a single codepoint is allowed.");
		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.limitCodePointsToRange("z", "a", true);
		}, "min must be less than or equal to max");
		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.limitCodePointsToRange(-1, 0, true);
		}, "cannot have negative code point.");
	}

	@Test
	public void testContainsCodePoint() throws Exception {
		Check<String> check = StringChecks.containsCodePoint(1, "\uD83E\uDD13");
		// successful
		assertCheckSuccessful(check.check("abcdefg ! 🤓 j*1 🤓"));
		assertCheckSuccessful(check.check("abcdefg ! j*1 🤓"));
		assertCheckSuccessful(check.check(" 🤓 "));
		assertCheckSuccessful(check.check("🤓"));
		// unsuccessful
		assertCheckFailed(check.check(""));
		assertCheckFailed(check.check("891723!kjad"));
		assertCheckFailed(check.check("\uD83E"));

		Check<String> checkBackSlash = StringChecks.containsCodePoint(2, "\\");
		// successful
		assertCheckSuccessful(checkBackSlash.check("\\\\"));
		assertCheckSuccessful(checkBackSlash.check("\\aw31987!\\"));
		// unsuccessful
		assertCheckFailed(checkBackSlash.check(""));
		assertCheckFailed(checkBackSlash.check("\\"));
		assertCheckFailed(checkBackSlash.check("nope"));

		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.containsCodePoint(0, "\uD83E\uDD13");
		}, "min um must be greater than 0");
		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.containsCodePoint(-1, "\uD83E\uDD13");
		}, "min um must be greater than 0");
		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.containsCodePoint(1, "🤓🤓");
		}, "Cannot have duplicate codepoints.");
	}

	@Test
	public void testContainsUniqueCodePointsInRange() throws Exception {
		Check<String> check = StringChecks.containsCodePointsInRange(3, "A", "Z", true);
		// successful
		assertCheckSuccessful(check.check("AbcdEfghI"));
		assertCheckSuccessful(check.check("ABC"));
		assertCheckSuccessful(check.check("!a73907^(81_ABC"));
		assertCheckSuccessful(check.check("D \tM \nZ"));
		// unsuccessful
		assertCheckFailed(check.check(""));
		assertCheckFailed(check.check("ZZZ"));
		assertCheckFailed(check.check("ABcdefg"));
		assertCheckFailed(check.check("12374"));
		assertCheckFailed(check.check(" \t \n"));

		// this is the smallest N possible for the range.
		Check<String> checkMinimumNPossible = StringChecks.containsCodePointsInRange(3,
		                                                                             "A".codePointAt(0),
		                                                                             "C".codePointAt(0),
		                                                                             true);
		// successful
		assertCheckSuccessful(checkMinimumNPossible.check("CBA"));
		assertCheckSuccessful(checkMinimumNPossible.check("!abcD_ACB"));
		// unsuccessful
		assertCheckFailed(checkMinimumNPossible.check(""));
		assertCheckFailed(checkMinimumNPossible.check("AABcde"));
		assertCheckFailed(checkMinimumNPossible.check("C"));
		assertCheckFailed(checkMinimumNPossible.check(" \n \t"));

		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.containsCodePointsInRange(0, 55, 99, true);
		});
		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.containsCodePointsInRange(6, 1, 5, true);
		}, "Minimum is larger than range and is unique. This is not possible.");
	}

	@Test
	public void testContainsNonUniqueCodePointsInRange() throws Exception {
		Check<String> check = StringChecks.containsCodePointsInRange(5, "A", "A", false);
		// successful
		assertCheckSuccessful(check.check("AbAbAbAbAb"));
		assertCheckSuccessful(check.check("AAAAA"));
		assertCheckSuccessful(check.check("\nAAAAA\n"));
		// unsuccessful
		assertCheckFailed(check.check(""));
		assertCheckFailed(check.check("AAAA"));
		assertCheckFailed(check.check("BBBBB"));

		Check<String> check1 = StringChecks.containsCodePointsInRange(3,
		                                                              "A".codePointAt(0),
		                                                              "z".codePointAt(0),
		                                                              false);
		// successful
		assertCheckSuccessful(check1.check("aBZ"));
		assertCheckSuccessful(check1.check("\t !@#$%8 aaa \n"));
		// unsuccessful
		assertCheckFailed(check1.check(""));
		assertCheckFailed(check1.check("ab"));

		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.containsCodePointsInRange(3, -5, 6, false);
		});
		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.containsCodePointsInRange(3, 7, 6, false);
		});
	}

	@Test
	public void testContainsUniqueCodePoints() throws Exception {
		Check<String> check = StringChecks.containsCodePoints(2, "aB🤓", true);
		// successful
		assertCheckSuccessful(check.check("aB"));
		assertCheckSuccessful(check.check("a🤓"));
		assertCheckSuccessful(check.check("a    🤓"));
		assertCheckSuccessful(check.check("acdefgh!B"));
		// unsuccessful
		assertCheckFailed(check.check(""));
		assertCheckFailed(check.check("ab"));
		assertCheckFailed(check.check("    🤓"));
		assertCheckFailed(check.check("\uD83E\uDD13"));
		assertCheckFailed(check.check("\uD83E\uDD13🤓"));   // codepoints are unique

		Check<String> check1 = StringChecks.containsCodePoints(1, "\uD83E\uDD13", true);
		// successful
		assertCheckSuccessful(check1.check("\uD83E\uDD13"));
		assertCheckSuccessful(check1.check(" 🤓 \t\n"));
		// unsuccessful
		assertCheckFailed(check1.check("abc"));
		assertCheckFailed(check1.check("\uD83E\uDD14"));

		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.containsCodePoints(0, "a1", true);
		}, "Cannot check for 0 codepoints.");
		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.containsCodePoints(1, "", true);
		}, "Cannot check for more codepoints than exists in string.");
		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.containsCodePoints(1, "  ", true);  // 2 spaces
		}, "String cannot contain duplicate codepoints.");
		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.containsCodePoints(1, "\uD83E\uDD14🤔", true);  // 2 codepoints
		}, "String cannot contain duplicate codepoints.");
	}

	@Test
	public void testContainsNonUniqueCodePoints() throws Exception {
		Check<String> check = StringChecks.containsCodePoints(2, "ab🤓", false);
		// successful
		assertCheckSuccessful(check.check("ab"));
		assertCheckSuccessful(check.check("a🤓"));
		assertCheckSuccessful(check.check("a    🤓"));
		assertCheckSuccessful(check.check("acdefgh!b"));
		assertCheckSuccessful(check.check("\uD83E\uDD13🤓"));   // codepoints are unique
		// unsuccessful
		assertCheckFailed(check.check(""));
		assertCheckFailed(check.check("    🤓"));
		assertCheckFailed(check.check("\uD83E\uDD13"));

		Check<String> check1 = StringChecks.containsCodePoints(1, "\uD83E\uDD13", false);
		// successful
		assertCheckSuccessful(check1.check("\uD83E\uDD13"));
		assertCheckSuccessful(check1.check(" 🤓 \t\n"));
		// unsuccessful
		assertCheckFailed(check1.check("abc"));
		assertCheckFailed(check1.check("\uD83E\uDD14"));

		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.containsCodePoints(0, "a1", false);
		}, "Cannot ");
		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.containsCodePoints(1, "", false);
		});
		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.containsCodePoints(1, "  ", false);  // 2 spaces
		}, "String cannot contain duplicate codepoints.");
		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.containsCodePoints(1, "\uD83E\uDD14🤔", false);  // 2 codepoints
		}, "String cannot contain duplicate codepoints.");
	}

	@Test
	public void testContainsString() throws Exception {
		Check<String> check = StringChecks.containsString("!!!");
		// successful
		assertCheckSuccessful(check.check("hey!!!"));
		assertCheckSuccessful(check.check("!!!"));
		// unsuccessful
		assertCheckFailed(check.check(""));
		assertCheckFailed(check.check("! ! !"));
		assertCheckFailed(check.check("hey!!"));

		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.containsString(null);
		});
	}

	@Test
	public void testContainsStrings() throws Exception {
		Set<String> strings = new HashSet<>();
		strings.add("🤓🤓");
		strings.add("ab");
		strings.add("!23");
		Check<String> check = StringChecks.containsStrings(2, strings);
		// successful
		assertCheckSuccessful(check.check("ab-hijk-!23"));
		assertCheckSuccessful(check.check("hijk   !23 ab"));
		assertCheckSuccessful(check.check("def🤓🤓abc"));
		// unsuccessful
		assertCheckFailed(check.check("def ac"));
		assertCheckFailed(check.check(" 🤓 "));
		assertCheckFailed(check.check(" 🤓🤓 🤓🤓"));
		assertCheckFailed(check.check(""));
		assertCheckFailed(check.check("nothing contained in set!!!!!!!999"));

		Check<String> check1 = StringChecks.containsStrings(1, strings);
		// successful
		assertCheckSuccessful(check1.check("def ab"));
		assertCheckSuccessful(check1.check(" 🤓🤓 "));
		// unsuccessful
		assertCheckFailed(check1.check(""));
		assertCheckFailed(check1.check(" \t\n "));
		assertCheckFailed(check1.check("🤓"));
		assertCheckFailed(check1.check("nothing in set!"));

		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.containsStrings(0, strings);
		});
		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.containsStrings(5, new HashSet<>());
		});
	}

	@Test
	public void testLimitConsecutiveCodePoints() throws Exception {
		Check<String> check = StringChecks.limitConsecutiveCodePoints(2);
		// successful
		assertCheckSuccessful(check.check(""));
		assertCheckSuccessful(check.check("ababababa"));
		assertCheckSuccessful(check.check("aa"));
		assertCheckSuccessful(check.check("🤓🤓"));
		assertCheckSuccessful(check.check("nothing repeating at al!"));
		// unsuccessful
		assertCheckFailed(check.check("aaa"));
		assertCheckFailed(check.check("🤓🤓🤓"));
		assertCheckFailed(check.check("   ")); // 3 spaces
		assertCheckFailed(check.check("!!!!!!"));

		Check<String> check1 = StringChecks.limitConsecutiveCodePoints(1);
		// successful
		assertCheckSuccessful(check1.check(""));
		assertCheckSuccessful(check1.check("ababababa"));
		assertCheckSuccessful(check1.check("a a🤓a"));
		assertCheckSuccessful(check1.check("not repeating at al!"));
		// unsuccessful
		assertCheckFailed(check1.check("aa"));
		assertCheckFailed(check1.check("  ")); // 2 spaces
		assertCheckFailed(check1.check("🤓🤓 "));
		assertCheckFailed(check1.check("!!!!!!"));

		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.limitConsecutiveCodePoints(0);
		});
	}

	@Test
	public void testEmptyOrMeetsChecks() throws Exception {
		Check<String> check = StringChecks.emptyOrMeetsChecks(StringChecks.doesNotBeginWithCodePoints("!&*"),
		                                                      StringChecks.lengthGreaterThan(5));
		// successful
		assertCheckSuccessful(check.check(""));
		assertCheckSuccessful(check.check("longer than 5 chars"));
		assertCheckSuccessful(check.check("d!oesnt begin with illegal chars"));
		assertCheckSuccessful(check.check("d&oesnt begin with illegal chars"));
		assertCheckSuccessful(check.check("d*oesnt begin with illegal chars"));
		// unsuccessful
		assertCheckFailed(check.check("!does begin with illegal chars"));
		assertCheckFailed(check.check("&does begin with illegal chars"));
		assertCheckFailed(check.check("*does begin with illegal chars"));
		assertCheckFailed(check.check("short"));   // doesnt start with illegal chars, but is too short
		assertCheckFailed(check.check(" "));   // doesnt start with illegal chars, but is too short
		assertCheckFailed(check.check("\t"));   // doesnt start with illegal chars, but is too short
		assertCheckFailed(check.check("\n "));   // doesnt start with illegal chars, but is too short
	}

	@Test
	public void testEmptyOrWhitespaceOrMeetsChecks() throws Exception {
		Check<String> check = StringChecks.emptyOrWhitespaceOrMeetsChecks(StringChecks.doesNotBeginWithCodePoints("!&*"),
		                                                                  StringChecks.lengthGreaterThan(5));
		// successful
		assertCheckSuccessful(check.check(""));
		assertCheckSuccessful(check.check("longer than 5 chars"));
		assertCheckSuccessful(check.check("d!oesnt begin with illegal chars"));
		assertCheckSuccessful(check.check("d&oesnt begin with illegal chars"));
		assertCheckSuccessful(check.check("d*oesnt begin with illegal chars"));
		assertCheckSuccessful(check.check(" "));   // doesnt start with illegal chars, but is too short
		assertCheckSuccessful(check.check("\t"));   // doesnt start with illegal chars, but is too short
		assertCheckSuccessful(check.check("\n "));   // doesnt start with illegal chars, but is too short
		// unsuccessful
		assertCheckFailed(check.check("!does begin with illegal chars"));
		assertCheckFailed(check.check("&does begin with illegal chars"));
		assertCheckFailed(check.check("*does begin with illegal chars"));
		assertCheckFailed(check.check("short"));   // doesnt start with illegal chars, but is too short
	}

	@Test
	public void testEqualsStringInList() throws Exception {
		Check<String> check = StringChecks.equalsString("!@3");
		// successful
		assertCheckSuccessful(check.check("!@3"));
		// unsuccessful
		assertCheckFailed(check.check(""));
		assertCheckFailed(check.check("!@"));
		assertCheckFailed(check.check("3!@"));

		Check<String> checkMany = StringChecks.equalsString("Hey", "no", "🤓🤓", "");
		// successful
		assertCheckSuccessful(checkMany.check("🤓🤓"));
		assertCheckSuccessful(checkMany.check("Hey"));
		assertCheckSuccessful(checkMany.check("no"));
		assertCheckSuccessful(checkMany.check(""));
		// unsuccessful
		assertCheckFailed(checkMany.check("\t"));
		assertCheckFailed(checkMany.check("🤓"));
		assertCheckFailed(checkMany.check(" "));
		assertCheckFailed(checkMany.check("hey"));
		assertCheckFailed(checkMany.check("he"));

		Check<String> checkEmptyEtAl = StringChecks.equalsString("", "not empty");
		// successful
		assertCheckSuccessful(checkEmptyEtAl.check(""));
		assertCheckSuccessful(checkEmptyEtAl.check("not empty"));
		// unsuccessful
		assertCheckFailed(checkEmptyEtAl.check(" "));
		assertCheckFailed(checkEmptyEtAl.check("\t"));
		assertCheckFailed(checkEmptyEtAl.check(" not empty"));

		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.equalsString((String)null);
		});
		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.equalsString("", null);
		});
	}

	@Test
	public void testEqualsStringInSet() throws Exception {
		Set<String> set = new HashSet<>(Arrays.asList(" ", "Yyy"));
		Check<String> check = StringChecks.equalsString(set);
		// successful
		assertCheckSuccessful(check.check(" "));
		assertCheckSuccessful(check.check("Yyy"));
		// unsuccessful
		assertCheckFailed(check.check(""));
		assertCheckFailed(check.check("  "));
		assertCheckFailed(check.check("\n"));
		assertCheckFailed(check.check("yyy"));
		assertCheckFailed(check.check("yy"));
		assertCheckFailed(check.check("🤔"));

		Set<String> setEmptyEtAl = new HashSet<>(Arrays.asList("hey!", "", "third", "🤓"));
		Check<String> checkEmptyEtAl = StringChecks.equalsString(setEmptyEtAl);
		// successful
		assertCheckSuccessful(checkEmptyEtAl.check("third"));
		assertCheckSuccessful(checkEmptyEtAl.check("hey!"));
		assertCheckSuccessful(checkEmptyEtAl.check("🤓"));
		assertCheckSuccessful(checkEmptyEtAl.check(""));
		// unsuccessful
		assertCheckFailed(checkEmptyEtAl.check("🤓🤓"));
		assertCheckFailed(checkEmptyEtAl.check(" "));
		assertCheckFailed(checkEmptyEtAl.check("3"));

		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.equalsString(new HashSet<>(5));   // is still empty
		});
	}
	
	@Test
	public void testEqualsStringIgnoreCase() throws Exception {
		Check<String> check = StringChecks.equalsStringIgnoreCase("ONE", "two", "33", "🤔");
		// successful
		assertCheckSuccessful(check.check("one"));
		assertCheckSuccessful(check.check("33"));
		assertCheckSuccessful(check.check("🤔"));
		assertCheckSuccessful(check.check("TwO"));
		// unsuccessful
		assertCheckFailed(check.check(""));
		assertCheckFailed(check.check("  "));
		assertCheckFailed(check.check("one "));
		assertCheckFailed(check.check("two!"));
		assertCheckFailed(check.check("333"));
		assertCheckFailed(check.check("🤔🤔"));
	}

	@Test
	public void testDoesNotEqualStringsInList() throws Exception {
		Check<String> check = StringChecks.doesNotEqualStrings("not equal", "whatever");
		// successful
		assertCheckSuccessful(check.check(""));
		assertCheckSuccessful(check.check("\u0000not equal"));
		assertCheckSuccessful(check.check("not equal to this"));
		assertCheckSuccessful(check.check("anything really"));
		// unsuccessful
		assertCheckFailed(check.check("not equal"));
		assertCheckFailed(check.check("whatever"));

		Check<String> checkWithEmpty = StringChecks.doesNotEqualStrings("", "other");
		// successful
		assertCheckSuccessful(checkWithEmpty.check(" "));
		assertCheckSuccessful(checkWithEmpty.check("something else"));
		// unsuccessful
		assertCheckFailed(checkWithEmpty.check(""));
		assertCheckFailed(checkWithEmpty.check("other"));

		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.doesNotEqualStrings((String) null);
		});
		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.doesNotEqualStrings(new String[10]);
		});
	}

	@Test
	public void testDoesNotEqualStringsInSet() throws Exception {
		Set<String> strings = new HashSet<>();
		strings.add("something");
		strings.add("!");
		Check<String> check = StringChecks.doesNotEqualStrings(strings);
		// successful
		assertCheckSuccessful(check.check(""));
		assertCheckSuccessful(check.check("something else"));
		// unsuccessful
		assertCheckFailed(check.check("!"));
		assertCheckFailed(check.check("something"));

		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.doesNotEqualStrings(new HashSet<>());
		});
		assertThrows(NullPointerException.class, () -> {
			StringChecks.doesNotEqualStrings((Set)null);
		});
	}

	@Test
	public void testDoesNotEqualStringsInListIgnoreCase() throws Exception {
		Check<String> check = StringChecks.doesNotEqualStringsIgnoreCase("HEYY", "\uD83E\uDD13");
		// successful
		assertCheckSuccessful(check.check(""));
		// unsuccessful
		assertCheckFailed(check.check("heyy"));
		assertCheckFailed(check.check("heYy"));
		assertCheckFailed(check.check("HEYY"));
		assertCheckFailed(check.check("\uD83E\uDD13"));

		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.doesNotEqualStringsIgnoreCase((String) null);
		});
		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.doesNotEqualStringsIgnoreCase(new String[10]);
		});
	}

	@Test
	public void testDoesNotEqualStringsInSetIgnoreCase() throws Exception {
		Set<String> stringSet = new HashSet<>();
		stringSet.add("");
		stringSet.add("  "); // 2 spaces
		stringSet.add("!");
		stringSet.add("\uD83E\uDD55");
		Check<String> check = StringChecks.doesNotEqualStringsIgnoreCase(stringSet);
		// successful
		assertCheckSuccessful(check.check(" "));   // 1 space
		assertCheckSuccessful(check.check("anything!"));
		// unsuccessful
		assertCheckFailed(check.check(""));
		assertCheckFailed(check.check("!"));
		assertCheckFailed(check.check("🥕"));

		assertThrows(IllegalArgumentException.class, () -> {
			StringChecks.doesNotEqualStringsIgnoreCase(new HashSet<>());
		});
	}

}
