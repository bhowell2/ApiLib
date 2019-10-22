package io.github.bhowell2.apilib;

import io.github.bhowell2.apilib.checks.ArrayCheck;
import io.github.bhowell2.apilib.checks.Check;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Blake Howell
 */
abstract class ApiArrayOrListParamBuilderBase<
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

	public ApiArrayOrListParamBuilderBase(String keyName, String displayName, ApiArrayOrListParam<In, Param> copyFrom) {
		super(keyName, displayName);
		this.indexChecks = copyFrom.indexChecks != null
			? new ArrayList<>(Arrays.asList(copyFrom.indexChecks))
			: new ArrayList<>();
		this.individualIndexChecks = copyFrom.individualIndexChecks != null
			? Arrays.stream(copyFrom.individualIndexChecks)
			        .map(Arrays::asList)
			        .collect(ArrayList::new,
			                 ArrayList::add,
			                 List::addAll)
			: new ArrayList<>();
		this.indexMapCheck = copyFrom.indexMapCheck;
		this.individualIndexMapCheck = copyFrom.individualIndexMapChecks != null
			? Arrays.asList(copyFrom.individualIndexMapChecks)
			: new ArrayList<>();
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
	public B setIndividualIndexChecksWrapCheck(List<List<Check<Param>>> individualIndexChecks) {
		this.individualIndexChecks =
			individualIndexChecks.stream()
			                     .map(checks -> checks.stream()
			                                          .map(ArrayCheck::wrapCheck)
			                                          .collect(Collectors.toList()))
			                     .collect(Collectors.toList());
		return (B) this;
	}

	@SuppressWarnings("unchecked")
	public B setIndividualIndexChecks(List<List<ArrayCheck<Param>>> individualIndexChecks) {
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

}
