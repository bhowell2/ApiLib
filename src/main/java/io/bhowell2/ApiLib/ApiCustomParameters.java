package io.bhowell2.ApiLib;

/**
 * Allows users to create a custom check for a parameter(s).
 * @author Blake Howell
 */
public interface ApiCustomParameters<RequestParams> {

    /**
     *
     * @param requestParameters object from which to obtain parameters
     * @return
     */
    ApiCustomParametersCheckTuple check(RequestParams requestParameters);

}
