package io.github.bhowell2.apilib.checks.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * @author Blake Howell
 */
public class CollectionUtilsTests {

	@Test
	public void testRequireNonNullEntries() throws Exception {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			CollectionUtils.requireNonNullEntries(Arrays.asList((String)null));
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			CollectionUtils.requireNonNullEntries(Arrays.asList(null, null));
		});
		Assertions.assertDoesNotThrow(() -> {
			CollectionUtils.requireNonNullEntries(Arrays.asList("yo"));
		});
		Assertions.assertDoesNotThrow(() -> {
			CollectionUtils.requireNonNullEntries(Arrays.asList(3));
		});
		Assertions.assertDoesNotThrow(() -> {
			CollectionUtils.requireNonNullEntries(Arrays.asList("hey", 3));
		});
	}

	@Test
	public void testRequireNonEmptyStrings() throws Exception {
	  Assertions.assertThrows(IllegalArgumentException.class, () -> {
	  	CollectionUtils.requireNonEmptyStrings(Arrays.asList("watt", ""));
	  });
	}

	@Test
	public void testRequireSizeGreater() throws Exception {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			CollectionUtils.requireSizeGreaterThan(5, Arrays.asList());
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			CollectionUtils.requireSizeGreaterThan(5, Arrays.asList(1, 2, 3, 4, 5));
		});
		Assertions.assertDoesNotThrow(() -> {
			CollectionUtils.requireSizeGreaterThan(4, Arrays.asList(1, 2, 3, 4, 5));
		});
	}

}
