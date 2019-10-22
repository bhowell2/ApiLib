package io.github.bhowell2.apilib;

import io.github.bhowell2.apilib.checks.ArrayCheck;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Blake Howell
 */
public class ApiArrayParamBuilder<In, Param>
	extends ApiArrayOrListParamBuilderBase<ApiArrayParamBuilder<In, Param>,In, Param, ApiArrayParam<In, Param>> {

	public static <In, Param> ApiArrayParamBuilder<In, Param> builder(String keyName, String displayName) {
		return new ApiArrayParamBuilder<>(keyName, displayName);
	}

	public static <In, Param> ApiArrayParamBuilder<In, Param> copyFrom(String keyName,
	                                                                   String displayName,
	                                                                   ApiArrayParam<In, Param> copyFrom) {
		return new ApiArrayParamBuilder<>(keyName, displayName, copyFrom);
	}

	public static <In, Param> ApiArrayParamBuilder<In, Param> unnamedBuilder() {
		return builder(null, null);
	}


	public static <Param> ApiArrayParamBuilder<Map<String, Object>, Param>
	mapInputBuilder(String keyName, String displayName) {
		return builder(keyName, displayName);
	}

	public static <Param> ApiArrayParamBuilder<Map<String, Object>, Param>
	mapInputBuilder(String keyName, String displayName, Class<Param> paramType) {
		return builder(keyName, displayName);
	}

	public static <Param> ApiArrayParamBuilder<Param[], Param> innerArrayBuilder() {
		return unnamedBuilder();
	}

	public static <Param> ApiArrayParamBuilder<Param[], Param> innerArrayBuilder(Class<Param> paramType) {
		return unnamedBuilder();
	}

	public ApiArrayParamBuilder(String keyName, String displayName) {
		super(keyName, displayName);
	}

	public ApiArrayParamBuilder(String keyName, String displayName, ApiArrayParam<In, Param> copyFrom) {
		super(keyName, displayName, copyFrom);
	}


	@SuppressWarnings("unchecked")
	@Override
	ApiArrayParam<In, Param> build() {
		ArrayCheck<Param>[][] individualIndexChecksAry =
			this.individualIndexChecks.stream()
				.<List<ArrayCheck[]>>collect(ArrayList::new,
				                             (a, b) -> a.add(b.toArray(new ArrayCheck[0])),
				                             List::addAll)
				.toArray(new ArrayCheck[0][]);
		return new ApiArrayParam<>(this.keyName,
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
