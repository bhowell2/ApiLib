package io.github.bhowell2.apilib;

import io.github.bhowell2.apilib.checks.ArrayCheck;
import io.github.bhowell2.apilib.checks.Check;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Blake Howell
 */
abstract class ApiArrayOrListParamBuilderBase<T extends ApiArrayOrListParamBuilderBase,
	Param,
	In,
	C extends ApiArrayOrListParamBase<Param, In>>
extends ApiParamBuilderBase<T> {

	List<ArrayCheck<Param>> indexChecks;
	List<List<ArrayCheck<Param>>> individualIndexChecks;
	ApiMapParam indexMapCheck;
	List<ApiMapParam> individualIndexMapCheck;
	ApiArrayOrListParamBase<?, Param> innerArrayOrListParam;

	public ApiArrayOrListParamBuilderBase(String keyName, String displayName) {
		super(keyName, displayName);
		this.indexChecks = new ArrayList<>();
		this.individualIndexChecks = new ArrayList<>();
	}

	/**
	 * These checks apply to all
	 * @param indexChecks
	 * @return
	 */
	@SafeVarargs
	@SuppressWarnings("unchecked")
	public final T addIndexChecks(Check<Param>... indexChecks) {
		checkVarArgsNotNullAndValuesNotNull(indexChecks);
		return (T) this.addIndexChecks((ArrayCheck<Param>[])
			Arrays.stream(indexChecks)
			      .map(ArrayCheck::wrapCheck)
			      .toArray(ArrayCheck[]::new));
	}

	@SafeVarargs
	@SuppressWarnings("unchecked")
	public final T addIndexChecks(ArrayCheck<Param>... indexChecks) {
		checkVarArgsNotNullAndValuesNotNull(indexChecks);
		for (ArrayCheck<Param> c : indexChecks) {
			if (c == null) {
				throw new IllegalArgumentException("Cannot add null index check");
			}
			this.indexChecks.add(c);
		}
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setIndividualIndexChecks(List<List<ArrayCheck<Param>>> individualIndexChecks) {
		if (this.individualIndexChecks != null) {
			throw new IllegalArgumentException("Individual index checks has already been set.");
		}
		// could set to null if copying from another and wanting to remove it
		this.individualIndexChecks = individualIndexChecks;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setIndexMapCheck(ApiMapParam indexMapCheck) {
		this.indexMapCheck = indexMapCheck;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setIndividualIndexMapChecks(List<ApiMapParam> individualIndexMapChecks) {
		this.individualIndexMapCheck = individualIndexMapChecks;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setInnerListParam(ApiListParam<?, Param> innerListParam) {
		this.innerArrayOrListParam = innerListParam;
		return (T) this;
	}

	/**
	 * Build the {@link ApiListParam} or {@link ApiArrayParam}.
	 * @return
	 */
	abstract C build();

}
