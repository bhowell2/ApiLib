package io.github.bhowell2.apilib;

import io.github.bhowell2.apilib.checks.ArrayCheck;

/**
 * @author Blake Howell
 */
public class ApiListParam<In, Param> extends ApiArrayOrListParam<In, Param> {

	public ApiListParam(String keyName,
	                     String displayName,
	                     String invalidErrorMessage,
	                     boolean canBeNull,
	                     ArrayCheck<Param>[] indexChecks,
	                     ArrayCheck<Param>[][] individualIndexChecks,
	                     ApiMapParam indexMapCheck,
	                     ApiMapParam[] individualIndexMapChecks,
	                     ApiArrayOrListParam<Param, ?> innerArrayParam) {
		super(keyName,
		      displayName,
		      invalidErrorMessage,
		      canBeNull,
		      indexChecks,
		      individualIndexChecks,
		      indexMapCheck,
		      individualIndexMapChecks,
		      innerArrayParam,
		      false,
		      true);
	}

}
