package io.github.bhowell2.apilib;

import io.github.bhowell2.apilib.checks.ArrayCheck;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Blake Howell
 */
public class ApiArrayOrListParamBuilder<In, Param>
	extends ApiArrayOrListParamBuilderBase<
	ApiArrayOrListParamBuilder<In, Param>,
	In,
	Param,
	ApiArrayOrListParam<In, Param>
	> {

	/* STATIC HELPERS */

	public static <In, Param> ApiArrayOrListParamBuilder<In, Param> builder(String keyName, String displayName) {
		return new ApiArrayOrListParamBuilder<>(keyName, displayName);
	}

	public static <In, Param> ApiArrayOrListParamBuilder<In, Param> copyFrom(String keyName, String displayName) {
		return new ApiArrayOrListParamBuilder<>(keyName, displayName);
	}

	public static <In, Param> ApiArrayOrListParamBuilder<In, Param> unnamedBuilder() {
		return builder(null, null);
	}

	public static <Param> ApiArrayOrListParamBuilder<Map<String, Object>, Param>
	mapInputBuilder(String keyName, String displayName) {
		return builder(keyName, displayName);
	}

	public static <Param> ApiArrayOrListParamBuilder<Map<String, Object>, Param>
	mapInputBuilder(String keyName, String displayName, Class<Param> paramType) {
		return builder(keyName, displayName);
	}

	public static <Param> ApiArrayOrListParamBuilder<List<Param>, Param> innerListBuilder() {
		return unnamedBuilder();
	}

	public static <Param> ApiArrayOrListParamBuilder<List<Param>, Param> innerListBuilder(Class<Param> paramType) {
		return unnamedBuilder();
	}

	public static <Param> ApiArrayOrListParamBuilder<Param[], Param> innerArrayBuilder() {
		return unnamedBuilder();
	}

	public static <Param> ApiArrayOrListParamBuilder<Param[], Param> innerArrayBuilder(Class<Param> paramType) {
		return unnamedBuilder();
	}


	/* IMPLEMENTATION */

	/**
	 * Use to set whether or
	 */
	boolean requireArray = false, requireList = false;

	public ApiArrayOrListParamBuilder(String keyName, String displayName) {
		super(keyName, displayName);
		this.indexChecks = new ArrayList<>();
		this.individualIndexChecks = new ArrayList<>();
		this.individualIndexMapCheck = new ArrayList<>();
	}

	@SuppressWarnings("unchecked")
	public ApiArrayOrListParamBuilder(String keyName, String displayName, ApiArrayOrListParam<In, Param> copyFrom) {
		super(keyName, displayName, copyFrom);
		this.requireArray = copyFrom.requireArray;
		this.requireList = copyFrom.requireList;
	}

	public ApiArrayOrListParamBuilder<In, Param> setRequireArray(boolean requireArray) {
		if (this.requireList && requireArray) {
			throw new IllegalArgumentException("Cannot require list and require array");
		}
		this.requireArray = requireArray;
		return this;
	}

	public ApiArrayOrListParamBuilder<In, Param> setRequireList(boolean requireList) {
		if (this.requireArray && requireList) {
			throw new IllegalArgumentException("Cannot require list and require array");
		}
		this.requireList = requireList;
		return this;
	}

	@SuppressWarnings("unchecked")
	public ApiArrayOrListParam<In, Param> build() {
		ArrayCheck<Param>[][] individualIndexChecksAry =
			this.individualIndexChecks.stream()
				.<List<ArrayCheck[]>>collect(ArrayList::new,
				                             (listOfArrayChecksArray, listOfArrayChecks) ->
					                             listOfArrayChecksArray.add(listOfArrayChecks.toArray(new ArrayCheck[0])),
				                             List::addAll)
				.toArray(new ArrayCheck[0][]);
		return new ApiArrayOrListParam<>(this.keyName,
		                                 this.displayName,
		                                 this.invalidErrorMessage,
		                                 this.canBeNull,
		                                 this.indexChecks.toArray(new ArrayCheck[0]),
		                                 individualIndexChecksAry,
		                                 this.indexMapCheck,
		                                 this.individualIndexMapCheck.toArray(new ApiMapParam[0]),
		                                 this.innerArrayOrListParam,
		                                 this.requireArray,
		                                 this.requireList);
	}

}
