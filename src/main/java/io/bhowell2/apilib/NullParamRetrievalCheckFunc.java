package io.bhowell2.apilib;

/**
 * It is not required that
 * @author Blake Howell
 */
@FunctionalInterface
public interface NullParamRetrievalCheckFunc<ParamsObj> {

    /**
     * Check whether or not the parameter of null was actually provided.
     * E.g., Map.getValue() will return null, whether or not the parameter was provided as null or if it does not exists; therefore,
     * must check that it was actually provided as null.
     * @param parameterName
     * @return
     */
    boolean checkIfNullProvided(String parameterName, ParamsObj paramsObj);

}
