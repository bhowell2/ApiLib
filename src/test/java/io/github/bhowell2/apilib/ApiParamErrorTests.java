package io.github.bhowell2.apilib;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Blake Howell
 */
public class ApiParamErrorTests {

	@Test
	public void shouldGenerateApiParamErrorWithoutChildError() throws Exception {
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
		assertFalse(apiParamError.hasChildError());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldGenerateApiParamErrorForChildError() throws Exception {
		String errMsg = "Something is not right!";
		String keyName = "key";
		String displayName = "Key";
		ApiParamError apiParamError = new ApiParamError(keyName,
		                                                displayName,
		                                                ApiErrorType.INVALID_PARAMETER,
		                                                errMsg);
		int depth = 4;
		/*
		* Emulates what would be created if there were 5 nested map/jsonobjects where the error occurred
		* making ApiParamError 5 children deep. Note, the first position in the array is the outermost
		* depth ("0") and the last position in the array is the innermost depth ("4"). The innermost
		* child where the error occurred is "key".
		* */
		for (int i = depth; i >= 0; i--) {
			apiParamError = new ApiParamError("" + i,
			                                  "" + i,
			                                  apiParamError.errorType,
			                                  apiParamError.errorMessage,
			                                  apiParamError.exception,
			                                  apiParamError);
		}

		Map<String, Object> errorAsMap = apiParamError.toMap();
		int mapDepthCounter = 0;
		while (errorAsMap.get(ApiLibSettings.ErrorMessageParamNames.CHILD_ERROR) != null) {
			assertEquals("" + mapDepthCounter, errorAsMap.get(ApiLibSettings.ErrorMessageParamNames.KEY_NAME));
			errorAsMap = (Map<String, Object>) errorAsMap.get(ApiLibSettings.ErrorMessageParamNames.CHILD_ERROR);
			mapDepthCounter++;
		}

		assertEquals(keyName, errorAsMap.get(ApiLibSettings.ErrorMessageParamNames.KEY_NAME));
		assertEquals(5, mapDepthCounter); // depth is 6, but starts at 0 so goes counter goes to 5...

		for (int i = 0; i <= depth; i++) {
			assertTrue(apiParamError.hasChildError());
			assertEquals("" + i, apiParamError.keyName);
			assertEquals("" + i, apiParamError.displayName);
			assertEquals(errMsg, apiParamError.errorMessage);
			// all should have same error type
			assertEquals(ApiErrorType.INVALID_PARAMETER, apiParamError.errorType);
			apiParamError = apiParamError.childParamError;
		}
		assertFalse(apiParamError.hasChildError());
		assertEquals(keyName, apiParamError.keyName);
		assertEquals(displayName, apiParamError.displayName);

	}

}
