package io.bhowell2.apilib;

/**
 * @author Blake Howell
 */
@FunctionalInterface
public interface NullParamRetrievalCheckFunc<ParamsObj> {

	/**
	 * Check whether or not the parameter of null was actually provided.
	 * E.g., {@link java.util.Map#get(Object)} will return null, whether or not the parameter was provided as null or if it does not exists; therefore,
	 * must check that it was actually provided as null by checking if the parameter exists or not using {@link java.util.Map#containsKey(Object)}.
	 *
	 * @param parameterName name of parameter being retrieved
	 * @return whether or not the parameter was actually provided
	 */
	boolean checkIfNullProvided(String parameterName, ParamsObj paramsObj);

}
