package io.github.bhowell2.apilib.checks.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Blake Howell
 */
public class StringUtilsTests {

	@Test
	public void testRequireNonEmptyString() throws Exception {
		assertThrows(IllegalArgumentException.class, () -> {
			StringUtils.requireNonEmptyString("");
		});
	}

	@Test
	public void testRequireNoRepeatingCodePoints() throws Exception {
		assertThrows(IllegalArgumentException.class, () -> {
			StringUtils.requireUniqueCodePoints("aa");
		});
		assertThrows(IllegalArgumentException.class, () -> {
			StringUtils.requireUniqueCodePoints("aba");
		});
		assertThrows(IllegalArgumentException.class, () -> {
			StringUtils.requireUniqueCodePoints("\uD83E\uDD13b🤓");
		});
		assertDoesNotThrow(() -> {
			StringUtils.requireUniqueCodePoints("a");
			StringUtils.requireUniqueCodePoints("b🤓");
			StringUtils.requireUniqueCodePoints("\uD83E\uDD13bcdefghijklmnopqrstuv");
		});
	}

	@Test
	public void testRequireNonSupplementaryCodePoints() throws Exception {
	  assertThrows(IllegalArgumentException.class, () -> {
		  StringUtils
			  .requireNonSupplementaryCodePoints("abcdefghijklmnopqrstuvwxyz\uD83E\uDD13!@#$%^&*()1234567890 \t\n");
	  });
		assertThrows(IllegalArgumentException.class, () -> {
			StringUtils.requireNonSupplementaryCodePoints("\uD83E\uDD13");
		});
		assertThrows(IllegalArgumentException.class, () -> {
			StringUtils.requireNonSupplementaryCodePoints("🤓");
		});
		assertDoesNotThrow(() -> {
		  StringUtils.requireNonSupplementaryCodePoints("abcdefghijklmnopqrstuvwxyz!@#$%^&*()1234567890 \t\n");
	  });
	}

	@Test
	public void testRequireCodePointCountGreaterThan() throws Exception {
		assertThrows(IllegalArgumentException.class, () -> {
			StringUtils.requireCodePointCountGreaterThanOrEqualTo(0, "");
		});
		assertThrows(IllegalArgumentException.class, () -> {
			StringUtils.requireCodePointCountGreaterThanOrEqualTo(5, "");
		});
		assertThrows(IllegalArgumentException.class, () -> {
	  	StringUtils.requireCodePointCountGreaterThanOrEqualTo(5, "1234");
	  });
	  assertDoesNotThrow(() -> {
		  StringUtils.requireCodePointCountGreaterThanOrEqualTo(5, "12345");
	  });
		assertDoesNotThrow(() -> {
			StringUtils.requireCodePointCountGreaterThanOrEqualTo(5, "123456");
		});
	}

	@Test
	public void testRequireNCodePoints() throws Exception {
	  assertThrows(IllegalArgumentException.class, () -> {
	  	StringUtils.requireCodePointCountEqualTo(1, "11");
	  });
		assertThrows(IllegalArgumentException.class, () -> {
			StringUtils.requireCodePointCountEqualTo(1, "");
		});
		assertThrows(IllegalArgumentException.class, () -> {
			StringUtils.requireCodePointCountEqualTo(2, "🤓🤓🤓");
		});
		assertDoesNotThrow(() -> {
			StringUtils.requireCodePointCountEqualTo(1, "🤓");
		});
	}

}
