package io.github.bhowell2.apilib;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Blake Howell
 */
public class ApiParamErrorTests {

	@Test
	public void shouldGenerateCorrectErrorMessageWithoutChildError() throws Exception {
		String errMsg = "Something is not right!";
		String keyName = "key_1";
		String displayName = "Key 1";
		ApiParamError apiParamError = new ApiParamError(keyName,
		                                                displayName,
		                                                ApiErrorType.INVALID_PARAMETER,
		                                                errMsg);
		assertEquals(ApiErrorType.INVALID_PARAMETER, apiParamError.errorType);
		assertTrue(apiParamError.hasErrorMessage());
		assertEquals(errMsg, apiParamError.errorMessage);
		assertEquals(keyName, apiParamError.keyName);
		assertEquals(displayName, apiParamError.displayName);
		assertFalse(apiParamError.isNestedError());
	}

	// helper
	private String generateOrder(int count, boolean reversed, String name, String delimiter) {
		StringBuilder builder = new StringBuilder();
		if (reversed) {
			builder.append(name);
			for (int i = 0; i < count; i++) {
				builder.append(delimiter)
				       .append(i);
			}
		} else {
			for (int i = count - 1; i >= 0; i--) {
				builder.append(i)
				.append(delimiter);
			}
			builder.append(name);
		}
		return builder.toString();
	}

	@Test
	public void shouldGenerateCorrectErrorMessageWithChild() throws Exception {
		String errMsg = "Something is not right!";
		String keyName = "key";
		String displayName = "Key";
		ApiParamError apiParamError = new ApiParamError(keyName,
		                                                displayName,
		                                                ApiErrorType.INVALID_PARAMETER,
		                                                errMsg);
		int count = 5;
		/*
		* Emulates what would be created if there were 5 nested map/jsonobjects where the error occurred
		* make apiParamError 5 children deep. Note, the outermost key is 4. The innermost PARENT (not
		* the innermost child) key is 0. The innermost child (where the error occurred) key is "key".
		* */
		for (int i = 0; i < count; i++) {
			apiParamError.addParentKeyAndDisplayNames("" + i , "" + i);
		}
		assertEquals(5, apiParamError.getParentKeyNames().size());
		assertEquals(5, apiParamError.getParentDisplayNames().size());
		for (int i = 0; i < count; i++) {
			assertEquals(Integer.toString(i), apiParamError.getParentKeyNames().get(i));
			assertEquals(Integer.toString(i), apiParamError.getParentDisplayNames().get(i));
		}
	}

}
