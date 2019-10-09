package io.github.bhowell2.apilib;

import io.github.bhowell2.apilib.checks.StringChecks;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link ApiArrayOfMapsParam}.
 * @author Blake Howell
 */
public class ApiArrayOfMapsParamTests {

	@Test
	public void shouldCheckAllPositionsOfArrayOfMapsWithOneApiMapParam() throws Exception {
		String nameKey = "name";
		String passwordKey = "password";
		ApiSingleParam<String> name = ApiSingleParamBuilder.builder(nameKey, String.class)
		                                                   .addCheck(StringChecks.codePointCountGreaterThan(2))
		                                                   .build();
		ApiSingleParam<String> password = ApiSingleParamBuilder.builder(passwordKey, String.class)
		                                                       .addCheck(StringChecks.codePointCountGreaterThan(5))
		                                                       .build();
		ApiMapParam mapParam = ApiMapParamBuilder.rootBuilder()
		                                         .addRequiredSingleParams(name, password)
		                                         .build();
		// not this one uses arrays, not lists
		String arrayKey = "array";
		ApiArrayOfMapsParam arrayWithMapsParam = ApiArrayOfMapsParamBuilder.builder(arrayKey)
		                                                                   .setApiMapParamForAllIndices(mapParam)
		                                                                   .build();

		Map<String, Object> pos0 = new HashMap<>();
		pos0.put(nameKey, "longer than 2");
		pos0.put(passwordKey, "longer than 5");

		Map<String, Object> pos1 = new HashMap<>();
		pos1.put(nameKey, "longer than 2 again");
		pos1.put(passwordKey, "longer than 5 and one!");

		Map<String, Object> passingParams = new HashMap<>();
		passingParams.put(arrayKey, new Map[]{pos0, pos1});

		ApiArrayOfMapsParamCheckResult passingResult = arrayWithMapsParam.check(passingParams);
		assertTrue(passingResult.successful());
		assertEquals(2, passingResult.arrayMapResults.size());

		Map<String, Object> pos2 = new HashMap<>();
		pos2.put(nameKey, "long enough");
		pos2.put(passwordKey, "short");   // must be greater than 5, is not

		Map<String, Object> failingParams = new HashMap<>();
		failingParams.put(arrayKey, new Map[]{pos0, pos1, pos2});

		ApiArrayOfMapsParamCheckResult failingResult = arrayWithMapsParam.check(failingParams);
		assertTrue(failingResult.failed());
		assertTrue(failingResult.apiParamError.errorMessage.contains("at index 2"));
	}

	@Test
	public void shouldAllowNullParam() throws Exception {

	}

	@Test
	public void shouldAllowNullIndex() throws Exception {

	}


}
