package io.github.bhowell2.apilib;

import io.github.bhowell2.apilib.checks.ArrayCheck;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Blake Howell
 */
public class ApiListParamBuilder<In, Param>
	extends ApiArrayOrListParamBuilderBase<ApiListParamBuilder<In, Param>,
	In,
	Param,
	ApiListParam<In, Param>> {

	public static <In, Param> ApiListParamBuilder<In, Param> builder(String keyName, String displayName) {
		return new ApiListParamBuilder<>(keyName, displayName);
	}

	public static <In, Param> ApiListParamBuilder<In, Param> unnamedBuilder() {
		return builder(null, null);
	}

	public static <Param> ApiListParamBuilder<Map<String, Object>, Param>
	mapInputBuilder(String keyName, String displayName) {
		return builder(keyName, displayName);
	}

	public static <Param> ApiListParamBuilder<Map<String, Object>, Param>
	mapInputBuilder(String keyName, String displayName, Class<Param> paramType) {
		return builder(keyName, displayName);
	}

	public static <Param> ApiListParamBuilder<List<Param>, Param> innerListBuilder() {
		return unnamedBuilder();
	}

	public static <Param> ApiListParamBuilder<List<Param>, Param> innerListBuilder(Class<Param> paramType) {
		return unnamedBuilder();
	}

	public ApiListParamBuilder(String keyName, String displayName) {
		super(keyName, displayName);
	}

	@SuppressWarnings("unchecked")
	@Override
	ApiListParam<In, Param> build() {
		ArrayCheck<Param>[][] individualIndexChecksAry =
			this.individualIndexChecks.stream()
				.<List<ArrayCheck[]>>collect(ArrayList::new,
				                             (a, b) -> a.add(b.toArray(new ArrayCheck[0])),
				                             List::addAll)
				.toArray(new ArrayCheck[0][]);
		return new ApiListParam<>(this.keyName,
		                          this.displayName,
		                          this.invalidErrorMessage,
		                          this.canBeNull,
		                          this.indexChecks.toArray(new ArrayCheck[0]),
		                          individualIndexChecksAry,
		                          this.indexMapCheck,
		                          this.individualIndexMapCheck.toArray(new ApiMapParam[0]),
		                          this.innerArrayOrListParam);
	}

}
