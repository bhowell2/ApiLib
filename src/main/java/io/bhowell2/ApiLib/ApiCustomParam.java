package io.bhowell2.ApiLib;

/**
 * Allows user to create a custom check for a parameter(s). This could be checking the indices of an array, or ensuring that parameters are
 * conditionally supplied (e.g., (P1 && P2) || P3, but not both (XOR)). A name must be supplied to the ApiCustomParamsTuple (this is so that the
 * user can easily retrieve the results, rather than having to potentially iterate through an array and retrieve the ApiCustomParamsTuple they are
 * looking for). They still do not have to use
 *
 * The ApiCustomParamTuple has been created to allow the user to supply named parameters (providedParamNames or providedParamObjects) in the case of
 * some conditional check, or array object parameters (arrayParamObjects) to allow the user to know what fields have passed checks in the object in
 * each position.
 *
 * @author Blake Howell
 */
@FunctionalInterface
public interface ApiCustomParam<RequestParams> {

    /**
     * @param requestParameters object from which to obtain parameters
     * @return
     */
    ApiCustomParamCheck check(RequestParams requestParameters);

}
