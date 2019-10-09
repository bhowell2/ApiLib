package io.github.bhowell2.apilib.checks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Ensures Checks work as intended.
 * @author Blake Howell
 */
public class CheckTests {

	@SuppressWarnings("unchecked")
	@Test
	public void shouldAlwaysPass() throws Exception {
		assertTrue(Check.alwaysPass(Object.class).check(new Object()).successful());
		assertTrue(Check.alwaysPass(String.class).check("pass me!").successful());
		// fails when class is of different type
		Check failingCastCheck = Check.alwaysPass(Integer.class);
		assertFalse(failingCastCheck.check("will fail b/c not integer").successful());
	}

	@Test
	public void shouldAlwaysFail() throws Exception {
	  assertTrue(Check.alwaysFail().check(new Object()).failed());
		assertTrue(Check.alwaysFail().check("fail always").failed());
	}

	@Test
	public void shouldFailWithMessage() throws Exception {
		String failureMessage = "this always fails";
		Check<String> alwaysFailStringCheck = Check.alwaysFail(failureMessage);
		Check.Result result = alwaysFailStringCheck.check("ehh");
		assertTrue(result.failed());
		assertEquals(failureMessage, result.failureMessage);
	}

}
