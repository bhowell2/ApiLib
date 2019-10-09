package io.github.bhowell2.apilib.checks.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Blake Howell
 */
public class IntegerUtilsTests {

	@Test
	public void testRequireIntGreaterThan() throws Exception {
		assertThrows(IllegalArgumentException.class, () -> {
			IntegerUtils.requireIntGreaterThan(1, 0);
		});
		assertThrows(IllegalArgumentException.class, () -> {
	  	IntegerUtils.requireIntGreaterThan(1, 1);
	  });
	}

}
