package io.github.bhowell2.apilib;

import io.github.bhowell2.apilib.formatters.Formatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder to help create an {@link ApiArrayOfMapsParam}.
 * @author Blake Howell
 */
public class ApiArrayOfMapsParamBuilder {

	public static ApiArrayOfMapsParamBuilder builder(String keyName) {
		return builder(keyName, keyName);
	}

	public static ApiArrayOfMapsParamBuilder builder(String keyName, String displayName) {
		return new ApiArrayOfMapsParamBuilder(keyName, displayName);
	}

	private String keyName, displayName, invalidErrMsg;
	private boolean canBeNull = false, indicesCanBeNull = false;
	private List<Formatter<?, ?>> formatters;
	/*
	 * The user may want to supply a check for each position in the array or use one check
	 * for all positions in the array. One of these should be set and the other should be null.
	 * */
	private ApiMapParam check;
	private ApiMapParam[] checks;

	public ApiArrayOfMapsParamBuilder(String keyName, String displayName) {
		this.keyName = keyName;
		this.displayName = displayName;
		this.formatters = new ArrayList<>();
	}

	public ApiArrayOfMapsParamBuilder setInvalidErrorMessage(String invalidErrorMessage) {
		this.invalidErrMsg = invalidErrorMessage;
	  return this;
	}

	/**
	 * Declares whether or not the array can be null. Does not have anything to do
	 * with the indices of the array (see {@link #setIndicesCanBeNull(boolean)} for
	 * that case).
	 * @param canBeNull
	 * @return
	 */
	public ApiArrayOfMapsParamBuilder setCanBeNull(boolean canBeNull) {
		this.canBeNull = canBeNull;
		return this;
	}

	/**
	 * Declares whether or not null is an acceptable value at an index in the array.
	 * @param indicesCanBeNull
	 * @return
	 */
	public ApiArrayOfMapsParamBuilder setIndicesCanBeNull(boolean indicesCanBeNull) {
		this.indicesCanBeNull = indicesCanBeNull;
		return this;
	}


	/**
	 * This is meant to provide one {@link ApiMapParam} that will be used to check
	 * all indices of the array parameter.
	 * @param check
	 * @return
	 */
	public ApiArrayOfMapsParamBuilder setApiMapParamForAllIndices(ApiMapParam check) {
		if (this.check != null) {
			throw new IllegalArgumentException("Check has already been set.");
		} else if (this.checks != null) {
			throw new IllegalArgumentException("Cannot set both check and checks. If (single) check is set it will be " +
				                                   "used for all indices of the array parameter. If (array) checks is set " +
				                                   "the ApiMapParam at each index will be used to check the corresponding " +
				                                   "map at each position in the array parameter.");
		}
		this.check = check;
		return this;
	}

	/**
	 * This is meant to provide a {@link ApiMapParam} that will be used to check each 
	 * index of the array with the corresponding ApiMapParam. This requires that the 
	 * array parameter be the same size as the array supplied here - if they are not 
	 * an error will be returned when checking.
	 * @param checks
	 * @return
	 */
	public ApiArrayOfMapsParamBuilder setApiMapParamForEachIndex(ApiMapParam... checks) {
	  if (this.checks != null) {
		  throw new IllegalArgumentException("Checks has already been set.");
	  } else if (this.check != null) {
		  throw new IllegalArgumentException("Cannot set both check and checks. If (single) check is set it will be " +
			                                     "used for all indices of the array parameter. If (array) checks is set " +
			                                     "the ApiMapParam at each index will be used to check the corresponding " +
			                                     "map at each position in the array parameter.");
	  }
		this.checks = checks;
	  return this;
	}

	public ApiArrayOfMapsParamBuilder addFormatter(Formatter<?,?> formatter) {
		if (formatter == null) {
			throw new RuntimeException("Cannot add null Formatter");
		}
		this.formatters.add(formatter);
	  return this;
	}

	public ApiArrayOfMapsParamBuilder addFormatters(Formatter<?,?>... formatters) {
		for (Formatter<?, ?> f : formatters) {
			this.addFormatter(f);
		}
	  return this;
	}

	@SuppressWarnings("unchecked")
	public ApiArrayOfMapsParam build() {
		if (this.check != null) {
			return new ApiArrayOfMapsParam(this.keyName,
			                               this.displayName,
			                               this.canBeNull,
			                               this.indicesCanBeNull,
			                               this.formatters.toArray(new Formatter[0]),
			                               this.check,
			                               this.invalidErrMsg);
		} else {
			return new ApiArrayOfMapsParam(this.keyName,
			                               this.displayName,
			                               this.canBeNull,
			                               this.indicesCanBeNull,
			                               this.formatters.toArray(new Formatter[0]),
			                               this.checks,
			                               this.invalidErrMsg);
		}
	}

}
