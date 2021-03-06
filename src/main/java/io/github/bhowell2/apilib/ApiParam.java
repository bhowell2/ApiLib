package io.github.bhowell2.apilib;

/**
 *
 * @param <In> the input parameter type (usually the check will retrieve
 *             something from this parameter type)
 * @param <Result> the return type
 * @author Blake Howell
 */
@FunctionalInterface
public interface ApiParam<In, Result> {

	/**
	 * Retrieves the parameter to be checked from the supplied Map and returns
	 * the result of the appropriate type. Some results will return only the
	 * name of the parameter (e.g., {@link ApiSingleParam}) and others will
	 * return multiple (e.g., {@link ApiMapParam}) names.
	 *
	 * Generally exceptions should not be thrown and should be handled (all ApiLib
	 * ApiParam implementations handle exceptions) - this helps with performance
	 * if your endpoint receives heavy traffic.
	 *
	 * @param params The map to retrieve this parameter from. For top-level/root/unnamed
	 *               {@link ApiMapParam}s this will not retrieve anything, but use the
	 *               supplied map itself.
	 * @return the result of the check operation
	 */
	Result check(In params);

}
