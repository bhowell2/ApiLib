package io.github.bhowell2.apilib;

import io.github.bhowell2.apilib.checks.ArrayCheck;
import io.github.bhowell2.apilib.checks.Check;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Blake Howell
 */
public abstract class ApiArrayOrListParamBuilderBase<
	B extends ApiArrayOrListParamBuilderBase,
	In,
	Param,
	C extends ApiArrayOrListParam<In, Param>
	>
	extends ApiParamBuilderBase<ApiArrayOrListParamBuilderBase<B, In, Param, C>> {

	/* IMPLEMENTATION */


	List<ArrayCheck<Param>> indexChecks;
	List<List<ArrayCheck<Param>>> individualIndexChecks;
	ApiMapParam indexMapCheck;
	List<ApiMapParam> individualIndexMapCheck;
	ApiArrayOrListParam<Param, ?> innerArrayOrListParam;

	public ApiArrayOrListParamBuilderBase(String keyName, String displayName) {
		super(keyName, displayName);
		this.indexChecks = new ArrayList<>();
		this.individualIndexChecks = new ArrayList<>();
		this.individualIndexMapCheck = new ArrayList<>();
	}

	/**
	 * These checks apply to all
	 * @param indexChecks
	 * @return
	 */
	@SafeVarargs
	@SuppressWarnings("unchecked")
	public final B addIndexChecks(Check<Param>... indexChecks) {
		checkVarArgsNotNullAndValuesNotNull(indexChecks);
		return this.addIndexChecks((ArrayCheck<Param>[])
			                           Arrays.stream(indexChecks)
			                                 .map(ArrayCheck::wrapCheck)
			                                 .toArray(ArrayCheck[]::new));
	}

	@SuppressWarnings("unchecked")
	@SafeVarargs
	public final B addIndexChecks(ArrayCheck<Param>... indexChecks) {
		checkVarArgsNotNullAndValuesNotNull(indexChecks);
		for (ArrayCheck<Param> c : indexChecks) {
			if (c == null) {
				throw new IllegalArgumentException("Cannot add null index check");
			}
			this.indexChecks.add(c);
		}
		return (B) this;
	}

	@SuppressWarnings("unchecked")
	public B
	setIndividualIndexChecks(List<List<ArrayCheck<Param>>> individualIndexChecks) {
		if (this.individualIndexChecks != null) {
			throw new IllegalArgumentException("Individual index checks has already been set.");
		}
		// could set to null if copying from another and wanting to remove it
		this.individualIndexChecks = individualIndexChecks;
		return (B) this;
	}

	@SuppressWarnings("unchecked")
	public B setIndexMapCheck(ApiMapParam indexMapCheck) {
		this.indexMapCheck = indexMapCheck;
		return (B) this;
	}

	@SuppressWarnings("unchecked")
	public B
	setIndividualIndexMapChecks(List<ApiMapParam> individualIndexMapChecks) {
		this.individualIndexMapCheck = individualIndexMapChecks;
		return (B) this;
	}

	@SuppressWarnings("unchecked")
	public B setInnerArrayOrListParam(ApiArrayOrListParam<Param, ?> innerListParam) {
		if (innerListParam.keyName != null) {
			throw new IllegalArgumentException("Cannot have inner array with a key name. If using an ApiArrayOrListParam " +
				                                   "that was created elsewhere, make a copy of it with the the builder.");
		}
		this.innerArrayOrListParam = innerListParam;
		return (B) this;
	}

	abstract C build();

//	@SuppressWarnings("unchecked")
//	public ApiArrayOrListParam<In, Param> build() {
//		ArrayCheck<Param>[][] individualIndexChecksAry =
//			this.individualIndexChecks.stream()
//				.<List<ArrayCheck[]>>collect(ArrayList::new,
//				                             (a, b) -> a.add(b.toArray(new ArrayCheck[0])),
//				                             List::addAll)
//				.toArray(new ArrayCheck[0][]);
//		return new ApiArrayOrListParam<>(this.keyName,
//		                                 this.displayName,
//		                                 this.invalidErrorMessage,
//		                                 this.canBeNull,
//		                                 this.indexChecks.toArray(new ArrayCheck[0]),
//		                                 individualIndexChecksAry,
//		                                 this.indexMapCheck,
//		                                 this.individualIndexMapCheck.toArray(new ApiMapParam[0]),
//		                                 this.innerArrayOrListParam,
//		                                 this.requireArray,
//		                                 this.requireList);
//	}

}
