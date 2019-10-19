package io.github.bhowell2.apilib;

import io.github.bhowell2.apilib.checks.ArrayCheck;
import io.github.bhowell2.apilib.checks.Check;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Blake Howell
 */
public class ApiListParamBuilder<Param, In>
//	extends ApiParamBuilderBase<ApiListParamBuilder<Param, In>>
	extends ApiArrayOrListParamBuilderBase<ApiListParamBuilder<Param, In>, Param, In, ApiListParam<Param, In>>
{

	public static <Param, In> ApiListParamBuilder<Param, In> builder(String keyName, String displayName) {
		return new ApiListParamBuilder<>(keyName, displayName);
	}

	public static <Param, In> ApiListParamBuilder<Param, In> unnamedBuilder() {
		return builder(null, null);
	}

	//	String keyName, displayName;
//	List<Check<Param>> indexChecks;
//	List<List<Check<Param>>> individualIndexChecks;
//	ApiMapParam indexMapCheck;
//	List<ApiMapParam> individualIndexMapCheck;
//	ApiListParam<?, Param> innerListParam;

	public ApiListParamBuilder(String keyName, String displayName) {
		super(keyName, displayName);
		this.indexChecks = new ArrayList<>();
		this.individualIndexChecks = new ArrayList<>();
		this.individualIndexMapCheck = new ArrayList<>();
	}

	// TODO: allow for copying?

//	@SafeVarargs
//	public final ApiListParamBuilder<Param, In> addIndexChecks(Check<Param>... indexChecks) {
//		checkVarArgsNotNullAndValuesNotNull(indexChecks);
//		for (Check<Param> c : indexChecks) {
//			if (c == null) {
//				throw new IllegalArgumentException("Cannot add null index check");
//			}
//			this.indexChecks.add(c);
//		}
//		return this;
//	}

//	public ApiListParamBuilder<Param, In> setIndividualIndexChecks(List<List<Check<Param>>> individualIndexChecks) {
//		if (this.individualIndexChecks != null) {
//			throw new IllegalArgumentException("Individual index checks has already been set.");
//		}
//		// could set to null if copying from another and wanting to remove it
//		this.individualIndexChecks = individualIndexChecks;
//		return this;
//	}

//	public ApiListParamBuilder<Param, In> setIndexMapCheck(ApiMapParam indexMapCheck) {
//		this.indexMapCheck = indexMapCheck;
//		return this;
//	}

//	public ApiListParamBuilder<Param, In> setIndividualIndexMapChecks(List<ApiMapParam> individualIndexMapChecks) {
//		this.individualIndexMapCheck = individualIndexMapChecks;
//		return this;
//	}

//	public ApiListParamBuilder<Param, In> setInnerListParam(ApiListParam<?, Param> innerListParam) {
//		this.innerListParam = innerListParam;
//		return this;
//	}

	@SuppressWarnings("unchecked")
	public ApiListParam<Param, In> build() {
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
