package io.bhowell2.ApiLib;

/**
 * Allows user to create a custom check for a parameter(s). This could be checking the indices of an array, or ensuring that only one or the other
 * of some parameters is supplied. A name must be supplied to the ApiCustomParamsTuple (this is so that the user can easily retrieve the results,
 * rather than having to potentially iterate through an array and retrieve the ApiCustomParamsTuple they are looking for).
 * @author Blake Howell
 */
public interface ApiCustomParameter<RequestParams> {

    /**
     *
     * @param requestParameters object from which to obtain parameters
     * @return
     */
    ApiCustomParamTuple check(RequestParams requestParameters);

}
