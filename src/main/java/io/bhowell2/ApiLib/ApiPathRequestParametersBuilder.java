package io.bhowell2.ApiLib;

/**
 *
 * @author Blake Howell
 */
public class ApiPathRequestParametersBuilder<RequestParameters> {

    // generally, for the top level we do not actually retrieve a nested parameter as all of the parameters that are passed in are the top level,
    // so the object is just returned
    public static <RequestParameters> ApiPathRequestParametersBuilder<RequestParameters> builder(ApiVersion apiVersion) {
        ParameterRetrievalFunction<RequestParameters, RequestParameters> retrievalFunction = (paramName, params) -> params;
        return new ApiPathRequestParametersBuilder<>(apiVersion, new ApiNestedParameterBuilder<>(ApiPathRequestParameters.ROOT_LEVEL_NESTED_PARAMETER_NAME,
                                                                                                 retrievalFunction));
    }

    final ApiVersion version;
    final ApiNestedParameterBuilder<RequestParameters, RequestParameters> nestedParameterBuilder;

    public ApiPathRequestParametersBuilder(ApiVersion version,
                                           ApiNestedParameterBuilder<RequestParameters, RequestParameters> nestedParameterBuilder) {
        this.version = version;
        this.nestedParameterBuilder = nestedParameterBuilder;
    }

    public ApiPathRequestParametersBuilder<RequestParameters> setContinueOnOptionalFailure(boolean b) {
        this.nestedParameterBuilder.setContinueOnOptionalFailure(b);
        return this;
    }

    public ApiPathRequestParametersBuilder<RequestParameters> addRequiredParameter(ApiParameter<?, RequestParameters> requiredApiParameter) {
        this.nestedParameterBuilder.addRequiredParameter(requiredApiParameter);
        return this;
    }

    public ApiPathRequestParametersBuilder<RequestParameters> addOptionalParameter(ApiParameter<?, RequestParameters> optionalApiParameter) {
        this.nestedParameterBuilder.addOptionalParameter(optionalApiParameter);
        return this;
    }

    public ApiPathRequestParametersBuilder<RequestParameters> addRequiredNestedParameter(ApiNestedParameter<?, RequestParameters> nestedParameter) {
        this.nestedParameterBuilder.addRequiredNestedParameter(nestedParameter);
        return this;
    }

    public ApiPathRequestParametersBuilder<RequestParameters> addOptionalNestedParameter(ApiNestedParameter<?, RequestParameters> nestedParameter) {
        this.nestedParameterBuilder.addOptionalNestedParameter(nestedParameter);
        return this;
    }

    public ApiPathRequestParametersBuilder<RequestParameters> addRequiredCustomParameters(ApiCustomParameters<RequestParameters> customParameters) {
        this.nestedParameterBuilder.addRequiredCustomParameters(customParameters);
        return this;
    }

    public ApiPathRequestParametersBuilder<RequestParameters> addOptionalCustomParameters(ApiCustomParameters<RequestParameters> customParameters) {
        this.nestedParameterBuilder.addOptionalCustomParameters(customParameters);
        return this;
    }

    public ApiPathRequestParameters<RequestParameters> build() {
        return new ApiPathRequestParameters<>(this.version, this.nestedParameterBuilder.build());
    }

}
