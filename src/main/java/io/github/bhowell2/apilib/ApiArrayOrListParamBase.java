package io.github.bhowell2.apilib;

import io.github.bhowell2.apilib.checks.ArrayCheck;
import io.github.bhowell2.apilib.checks.Check;


/**
 * @author Blake Howell
 */
public abstract class ApiArrayOrListParamBase<Param, In> extends ApiParamBase<In, ApiArrayOrListCheckResult> {

	/**
	 * Checks work just like {@link io.github.bhowell2.apilib.checks.ListChecks}
	 * and {@link io.github.bhowell2.apilib.checks.ListChecks}, but they can be
	 * combined with indexMapCheck(s) or innerArrayParam - if the latter two are
	 * not needed the user should prefer
	 * {@link io.github.bhowell2.apilib.checks.ListChecks}.
	 */
	final ArrayCheck<Param>[] indexChecks;
	final ArrayCheck<Param>[][] individualIndexChecks;
	final ApiMapParam indexMapCheck;
	/**
	 * Used to check each index with the individual ApiMapParam.
	 */
	final ApiMapParam[] individualIndexMapChecks;
	// used for array within array
	final ApiArrayOrListParamBase<?, Param> innerArrayParam;

	public ApiArrayOrListParamBase(String keyName,
	                    String displayName,
	                    String invalidErrorMessage,
	                    boolean canBeNull,
	                    ArrayCheck<Param>[] indexChecks,
	                    ArrayCheck<Param>[][] individualIndexChecks,
	                    ApiMapParam indexMapCheck,
	                    ApiMapParam[] individualIndexMapChecks,
	                    ApiArrayOrListParamBase<?, Param> innerArrayParam) {
		super(keyName, displayName, invalidErrorMessage, canBeNull);
		this.indexChecks = indexChecks;
		this.individualIndexChecks = individualIndexChecks;
		this.indexMapCheck = indexMapCheck;
		this.individualIndexMapChecks = individualIndexMapChecks;
		this.innerArrayParam = innerArrayParam;
		if (innerArrayParam != null && innerArrayParam.keyName != null) {
			throw new IllegalArgumentException("Cannot add inner array param that is named. Use builder to copy existing " +
				                                   "if necessary.");
		}
		/*
		 * These cannot both be supplied since they both return values that would override each other.
		 * */
		if (this.indexMapCheck != null && (this.individualIndexMapChecks != null && this.individualIndexMapChecks.length > 0)) {
			throw new IllegalArgumentException("Cannot set both indexMapChecks and individualIndexMapChecks.");
		}
		/*
		 * Cannot have inner array param set AND map checks. This is because the parameter is
		 * either a list or a map, but not both..
		 * */
		if ((this.indexMapCheck != null ||
			(this.individualIndexMapChecks != null && this.individualIndexMapChecks.length > 0)) &&
			this.innerArrayParam != null) {
			throw new IllegalArgumentException("Cannot set both index map check and inner array param");
		}
	}

}
