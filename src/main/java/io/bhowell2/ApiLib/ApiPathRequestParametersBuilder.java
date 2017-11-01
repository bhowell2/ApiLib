package io.bhowell2.ApiLib;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Blake Howell
 */
public class ApiPathRequestParametersBuilder<RequestParameters> {

    // generally, for the top level we do not actually retrieve a nested parameter as all of the parameters that are passed in are the top level,
    // so the object is just returned
    public static <RequestParameters> ApiPathRequestParametersBuilder<RequestParameters> builder(ApiVersion apiVersion) {
        return new ApiPathRequestParametersBuilder<>(apiVersion);
    }

    final ApiVersion version;
    private boolean continueOnOptionalFailure = false;
    private List<ApiParameter<?, RequestParameters>> requiredApiParameters;
    private List<ApiParameter<?, RequestParameters>> optionalApiParameters;
    private List<ApiNestedParameter<?, RequestParameters>> requiredNestedParameters;
    private List<ApiNestedParameter<?, RequestParameters>> optionalNestedParameters;
    private List<ApiCustomParameters<RequestParameters>> requiredCustomParameters;
    private List<ApiCustomParameters<RequestParameters>> optionalCustomParameters;

    public ApiPathRequestParametersBuilder(ApiVersion version) {
        this.version = version;
        this.requiredApiParameters = new ArrayList<>();
        this.optionalApiParameters = new ArrayList<>();
        this.requiredNestedParameters = new ArrayList<>();
        this.optionalNestedParameters = new ArrayList<>();
        this.requiredCustomParameters = new ArrayList<>();
        this.optionalCustomParameters = new ArrayList<>();
    }

    public ApiPathRequestParametersBuilder<RequestParameters> setContinueOnOptionalFailure(boolean b) {
        this.continueOnOptionalFailure = b;
        return this;
    }

    public ApiPathRequestParametersBuilder<RequestParameters> addRequiredParameter(ApiParameter<?, RequestParameters> requiredApiParameter) {
        this.requiredApiParameters.add(requiredApiParameter);
        return this;
    }

    public ApiPathRequestParametersBuilder<RequestParameters> addOptionalParameter(ApiParameter<?, RequestParameters> optionalApiParameter) {
        this.optionalApiParameters.add(optionalApiParameter);
        return this;
    }

    public ApiPathRequestParametersBuilder<RequestParameters> addRequiredNestedParameter(ApiNestedParameter<?, RequestParameters> nestedParameter) {
        this.requiredNestedParameters.add(nestedParameter);
        return this;
    }

    public ApiPathRequestParametersBuilder<RequestParameters> addOptionalNestedParameter(ApiNestedParameter<?, RequestParameters> nestedParameter) {
        this.optionalNestedParameters.add(nestedParameter);
        return this;
    }

    public ApiPathRequestParametersBuilder<RequestParameters> addRequiredCustomParameters(ApiCustomParameters<RequestParameters> customParameters) {
        this.requiredCustomParameters.add(customParameters);
        return this;
    }

    public ApiPathRequestParametersBuilder<RequestParameters> addOptionalCustomParameters(ApiCustomParameters<RequestParameters> customParameters) {
        this.optionalCustomParameters.add(customParameters);
        return this;
    }

    @SuppressWarnings("unchecked")
    public ApiPathRequestParameters<RequestParameters> build() {
        ApiParameter<?, RequestParameters>[] requiredApiParamsAry = this.requiredApiParameters.toArray((ApiParameter<?, RequestParameters>[])new ApiParameter[this.requiredApiParameters.size()]);

        ApiParameter<?, RequestParameters>[] optionalApiParamsAry = this.optionalApiParameters.toArray((ApiParameter<?, RequestParameters>[])new ApiParameter[this.optionalApiParameters.size()]);

        ApiNestedParameter<?, RequestParameters>[] requiredApiNestedParamsAry = this.requiredNestedParameters.toArray((ApiNestedParameter<?, RequestParameters>[])new ApiNestedParameter[this.requiredNestedParameters.size()]);

        ApiNestedParameter<?, RequestParameters>[] optionalApiNestedParamsAry =   this.optionalNestedParameters.toArray((ApiNestedParameter<?, RequestParameters>[])new ApiNestedParameter[this.optionalNestedParameters.size()]);

        ApiCustomParameters<RequestParameters>[] requiredCustomParamsAry = this.requiredCustomParameters.toArray((ApiCustomParameters<RequestParameters>[])new ApiCustomParameters[this.requiredCustomParameters.size()]);

        ApiCustomParameters<RequestParameters>[] optionalCustomParamsAry = this.optionalCustomParameters.toArray((ApiCustomParameters<RequestParameters>[])new ApiCustomParameters[this.optionalCustomParameters.size()]);
        return new ApiPathRequestParameters<>(this.version,
                                              this.continueOnOptionalFailure,
                                              requiredApiParamsAry,
                                              optionalApiParamsAry,
                                              requiredApiNestedParamsAry,
                                              optionalApiNestedParamsAry,
                                              requiredCustomParamsAry,
                                              optionalCustomParamsAry);
    }

}
