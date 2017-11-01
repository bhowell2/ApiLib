package io.bhowell2.ApiLib;

/**
 * For this base case, {@link ApiNestedParameter#parameterRetrievalFunction} will just return the object to be checked (not pulling out a nested
 * parameter) -- use {@link ApiPathRequestParametersBuilder}) to do this.
 *
 * The
 *
 * @author Blake Howell
 */
public class ApiPathRequestParameters<RequestParameters> {

    // this constant is used for
    public static final String ROOT_LEVEL_NESTED_PARAMETER_NAME = "ROOT_NESTED_PARAM";

    public final ApiVersion apiVersion;
    final ApiNestedParameter<RequestParameters, RequestParameters> nestedParameter;

    public ApiPathRequestParameters(ApiVersion apiVersion,
                                    ApiNestedParameter<RequestParameters, RequestParameters> nestedParameter) {
        this.apiVersion = apiVersion;
        this.nestedParameter = nestedParameter;
    }

    public ApiNestedParamCheckTuple check(RequestParameters requestParameters) {
        return nestedParameter.check(requestParameters);
    }

}
