package io.bhowell2.ApiLib;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Blake Howell
 */
public class ApiPathRequestParameters<RequestParameters> {

    public final ApiVersion apiVersion;
    public final ApiObjectParameter<?, RequestParameters> parameters;

    public ApiPathRequestParameters(ApiVersion apiVersion, ApiObjectParameter<?, RequestParameters> parameters) {
        this.apiVersion = apiVersion;
        this.parameters = parameters;
    }

    public ApiObjectParamTuple check(RequestParameters requestParameters) {
        return parameters.check(requestParameters);
    }

}
