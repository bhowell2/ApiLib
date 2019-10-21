package io.github.bhowell2.apilib;

import io.github.bhowell2.apilib.checks.ArrayCheck;


/**
 * Requires that the parameter be of type Array. {@link ApiArrayOrListParam} handles
 * either case, but it is abstract since it is assumed the user will know whether or
 * not the parameter will be of type Array or List.
 * @author Blake Howell
 */
public class ApiArrayParam<In, Param> extends ApiArrayOrListParam<In, Param> {

	public ApiArrayParam(String keyName,
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
		      true,
		      false);
	}

}
