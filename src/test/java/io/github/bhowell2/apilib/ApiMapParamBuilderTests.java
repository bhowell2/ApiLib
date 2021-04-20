package io.github.bhowell2.apilib;

import io.github.bhowell2.apilib.checks.Check;
import io.github.bhowell2.apilib.checks.StringChecks;
import io.github.bhowell2.apilib.errors.ApiParamError;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link ApiMapParam.Builder}.
 * @author Blake Howell
 */
public class ApiMapParamBuilderTests {

	static String singleKey1 = "sKey1", singleKey2 = "sKey2", singleKey3 = "sKey3";
	static ApiSingleParam<String> single1 = ApiSingleParam.builder(singleKey1, String.class)
	                                                      .addChecks(Check.alwaysPass(String.class))
	                                                      .build();
	static ApiSingleParam<String> single2 = ApiSingleParam.builder(singleKey2, String.class)
	                                                      .addChecks(Check.alwaysPass(String.class))
	                                                      .build();
	static ApiSingleParam<String> single3 = ApiSingleParam.builder(singleKey3, String.class)
	                                                      .addChecks(Check.alwaysPass(String.class))
	                                                      .build();

	static String listKey1 = "lKey1", listKey2 = "lKey2";
	static ApiListParam<Map<String, Object>, String> list1 =
		ApiListParam.<Map<String, Object>, String>builder(listKey1)
			.addIndexChecks(StringChecks.lengthGreaterThanOrEqualTo(2))
			.build();

	static ApiListParam<Map<String, Object>, String> list2 =
		ApiListParam.<Map<String, Object>, String>builder(listKey2)
			.addIndexChecks(StringChecks.lengthGreaterThanOrEqualTo(2))
			.build();

	static String mapKey1 = "mKey1", mapKey2 = "mKey2";
	static ApiMapParam map1 = ApiMapParam.builder(mapKey1)
	                                     .setCanBeNull(true)
	                                     .build();

	static ApiMapParam map2 = ApiMapParam.builder(mapKey2)
	                                     .setCanBeNull(true)
	                                     .build();


	static ApiCustomParam custom1 = map -> ApiCustomParam.Result.failure(ApiParamError.invalid("custom param 1"));

	static ApiCustomParam custom2 = map -> ApiCustomParam.Result.success();

	@Test
	public void shouldCopyFrom() throws Exception {
		ApiMapParam mapParam = ApiMapParam.builder()
		                                  .addRequiredSingleParams(single1)
		                                  .addOptionalSingleParams(single2, single3)
		                                  .addRequiredMapParams(map1)
		                                  .addOptionalMapParams(map2)
		                                  .addRequiredCollectionParams(list1)
		                                  .addOptionalCollectionParams(list2)
		                                  .addRequiredCustomParams(custom1)
		                                  .addOptionalCustomParams(custom2)
		                                  .build();

		assertEquals(1, mapParam.requiredSingleParams.length);
		assertEquals(2, mapParam.optionalSingleParams.length);
		assertEquals(1, mapParam.requiredMapParams.length);
		assertEquals(1, mapParam.optionalMapParams.length);
		assertEquals(1, mapParam.requiredCollectionParams.length);
		assertEquals(1, mapParam.optionalCollectionParams.length);
		assertEquals(1, mapParam.requiredCustomParams.length);
		assertEquals(1, mapParam.optionalCustomParams.length);

		ApiMapParam mapParamWithCopy = ApiMapParam.builder(mapParam)
		                                          .build();

		assertEquals(1, mapParamWithCopy.requiredSingleParams.length);
		assertEquals(2, mapParamWithCopy.optionalSingleParams.length);
		assertEquals(1, mapParamWithCopy.requiredMapParams.length);
		assertEquals(1, mapParamWithCopy.optionalMapParams.length);
		assertEquals(1, mapParamWithCopy.requiredCollectionParams.length);
		assertEquals(1, mapParamWithCopy.optionalCollectionParams.length);
		assertEquals(1, mapParamWithCopy.requiredCustomParams.length);
		assertEquals(1, mapParamWithCopy.optionalCustomParams.length);

		ApiMapParam mapParamWithCopyAndRemove = ApiMapParam.builder(mapParamWithCopy)
		                                                   // should not remove single2, because it's not required
		                                                   .removeRequiredParams(single1, single2)
		                                                   // should not remove map1, because it's not optional
		                                                   .removeOptionalParams(map1, map2)
		                                                   .removeParams(list2)
		                                                   .removeRequiredCustomParams(custom1, custom2)
		                                                   .build();
		
		// check initially copied parameter again to make sure that the params havent been removed from it too
		assertEquals(1, mapParamWithCopy.requiredSingleParams.length);
		assertEquals(2, mapParamWithCopy.optionalSingleParams.length);
		assertEquals(1, mapParamWithCopy.requiredMapParams.length);
		assertEquals(1, mapParamWithCopy.optionalMapParams.length);
		assertEquals(1, mapParamWithCopy.requiredCollectionParams.length);
		assertEquals(1, mapParamWithCopy.optionalCollectionParams.length);
		assertEquals(1, mapParamWithCopy.requiredCustomParams.length);
		assertEquals(1, mapParamWithCopy.optionalCustomParams.length);

		assertNull(mapParamWithCopyAndRemove.requiredSingleParams);
		assertEquals(2, mapParamWithCopyAndRemove.optionalSingleParams.length);
		assertEquals(1, mapParamWithCopyAndRemove.requiredMapParams.length);
		assertNull(mapParamWithCopyAndRemove.optionalMapParams);
		assertEquals(1, mapParamWithCopyAndRemove.requiredCollectionParams.length);
		assertNull(mapParamWithCopyAndRemove.optionalCollectionParams);
		assertNull(mapParamWithCopyAndRemove.requiredCustomParams);
		assertEquals(1, mapParamWithCopyAndRemove.optionalCustomParams.length);
	}

	@Test
	public void shouldFailToAddSameKeyName() throws Exception {
		// it doesnt matter what parameter type the key name is, should not be able to add it anywhere twice
		assertThrows(IllegalArgumentException.class, () -> {
			ApiMapParam.builder()
			           .addRequiredSingleParams(single1, single1)
			           .build();
		});
		assertThrows(IllegalArgumentException.class, () -> {
			ApiMapParam.builder()
			           .addRequiredSingleParams(single1)
			           .addOptionalSingleParams(single1)
			           .build();
		});
		assertThrows(IllegalArgumentException.class, () -> {
			ApiMapParam mapParam = ApiMapParam.builder(singleKey1).build();
			ApiMapParam.builder()
			           .addRequiredSingleParams(single1)
			           .addOptionalMapParams(mapParam)
			           .build();
		});
		assertThrows(IllegalArgumentException.class, () -> {
			ApiMapParam mapParam = ApiMapParam.builder(singleKey1).build();
			ApiListParam<Map<String, Object>, String> listParam =
				ApiListParam.mapInputBuilder(singleKey1, String.class)
				            .addIndexChecks(Check.alwaysPass(String.class))
				            .build();
			ApiMapParam.builder()
			           .addOptionalMapParams(mapParam)
			           .addRequiredCollectionParams(listParam)
			           .build();
		});
	}

	@Test
	public void shouldAddRequiredParamsToCorrectType() throws Exception {
		// uses generic add params. should all be added to correct list
		ApiMapParam param = ApiMapParam.builder()
		                               .addRequiredParams(single1, single2, list1, map1)
		                               .addOptionalParams(single3, list2, map2)
		                               .build();

		assertEquals(2, param.requiredSingleParams.length);
		assertSame(single1, param.requiredSingleParams[0]);
		assertSame(single2, param.requiredSingleParams[1]);
		assertEquals(1, param.optionalSingleParams.length);
		assertSame(single3, param.optionalSingleParams[0]);
		assertEquals(1, param.requiredMapParams.length);
		assertSame(map1, param.requiredMapParams[0]);
		assertEquals(1, param.optionalMapParams.length);
		assertSame(map2, param.optionalMapParams[0]);
		assertEquals(1, param.requiredCollectionParams.length);
		assertSame(list1, param.requiredCollectionParams[0]);
		assertEquals(1, param.optionalCollectionParams.length);
		assertSame(list2, param.optionalCollectionParams[0]);
	}

	@Test
	public void shouldRemoveOptionalOrRequiredParameters() throws Exception {
		ApiMapParam mapParam = ApiMapParam.builder()
		                                  .addRequiredSingleParams(single1)
		                                  .addOptionalSingleParams(single2, single3)
		                                  .addRequiredMapParams(map1)
		                                  .addOptionalMapParams(map2)
		                                  .addRequiredCollectionParams(list1)
		                                  .addOptionalCollectionParams(list2)
		                                  .addRequiredCustomParams(custom1)
		                                  .addOptionalCustomParams(custom2)
		                                  .build();
		// using as expected. generally remove will not be used if copy is not used...
		ApiMapParam copyParam1 = ApiMapParam.builder(mapParam)
		                                    .removeParams(single1, single3)
		                                    .removeParams(map1)

		                                    .build();
	}

	@Test
	public void shouldCopyAndRemoveParameters() throws Exception {
		ApiMapParam mapParam = ApiMapParam.builder()
		                                  .addRequiredSingleParams(single1)
		                                  .addOptionalSingleParams(single2, single3)
		                                  .addRequiredMapParams(map1)
		                                  .addOptionalMapParams(map2)
		                                  .addRequiredCollectionParams(list1)
		                                  .addOptionalCollectionParams(list2)
		                                  .addRequiredCustomParams(custom1)
		                                  .addOptionalCustomParams(custom2)
		                                  .build();


		/* CUSTOM */

		assertNull(ApiMapParam.builder(mapParam)
		                      .removeCustomParams(custom1)
		                      .build().requiredCustomParams);

		assertNull(ApiMapParam.builder(mapParam)
		                      .removeRequiredCustomParams(custom1)
		                      .build().requiredCustomParams);

		assertNotNull(ApiMapParam.builder(mapParam)
		                      .removeRequiredCustomParams(custom2)
		                      .build().requiredCustomParams);

		assertNull(ApiMapParam.builder(mapParam)
		                      .removeCustomParams(custom2)
		                      .build().optionalCustomParams);

		assertNotNull(ApiMapParam.builder(mapParam)
		                      .removeRequiredCustomParams(custom2)
		                      .build().requiredCustomParams);



	}

}
