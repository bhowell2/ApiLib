package io.bhowell2.ApiLib;

import java.util.ArrayList;
import java.util.List;

/**
 * Facilitates creation of ApiNestedParameter.
 *
 * @author Blake Howell
 */
public final class ApiNestedParameterBuilder<NestedParamType, ParentParamType> {

    public static <NestedParamType, ParentParamType> ApiNestedParameterBuilder<NestedParamType, ParentParamType> builder(String nestedParameterName, ParameterRetrievalFunction<NestedParamType, ParentParamType> retrievalFunction) {
        return new ApiNestedParameterBuilder<>(nestedParameterName, retrievalFunction);
    }

    private String nestedParameterName;
    private ParameterRetrievalFunction<NestedParamType, ParentParamType> retrievalFunction;
    private List<ApiParameter<?, NestedParamType>> requiredApiParameters;
    private List<ApiParameter<?, NestedParamType>> optionalApiParameters;
    private List<ApiNestedParameter<?, NestedParamType>> requiredNestedParameters;
    private List<ApiNestedParameter<?, NestedParamType>> optionalNestedParameters;
    private List<ApiCustomParameters<NestedParamType>> requiredCustomParameters;
    private List<ApiCustomParameters<NestedParamType>> optionalCustomParameters;
    private boolean continueOnOptionalFailure = false;

    public ApiNestedParameterBuilder(String nestedParameterName, ParameterRetrievalFunction<NestedParamType, ParentParamType> retrievalFunction) {
        this.nestedParameterName = nestedParameterName;
        this.retrievalFunction = retrievalFunction;
        this.requiredApiParameters = new ArrayList<>();
        this.optionalApiParameters = new ArrayList<>();
        this.requiredNestedParameters = new ArrayList<>();
        this.optionalNestedParameters = new ArrayList<>();
        this.requiredCustomParameters = new ArrayList<>();
        this.optionalCustomParameters = new ArrayList<>();
    }

    public ApiNestedParameterBuilder<NestedParamType, ParentParamType> setContinueOnOptionalFailure(boolean b) {
        this.continueOnOptionalFailure = b;
        return this;
    }

    public ApiNestedParameterBuilder<NestedParamType, ParentParamType> addRequiredParameter(ApiParameter<?, NestedParamType> requiredApiParameter) {
        this.requiredApiParameters.add(requiredApiParameter);
        return this;
    }

    public ApiNestedParameterBuilder<NestedParamType, ParentParamType> addOptionalParameter(ApiParameter<?, NestedParamType> optionalApiParameter) {
        this.optionalApiParameters.add(optionalApiParameter);
        return this;
    }

    public ApiNestedParameterBuilder<NestedParamType, ParentParamType> addRequiredNestedParameter(ApiNestedParameter<?, NestedParamType> nestedParameter) {
        this.requiredNestedParameters.add(nestedParameter);
        return this;
    }

    public ApiNestedParameterBuilder<NestedParamType, ParentParamType> addOptionalNestedParameter(ApiNestedParameter<?, NestedParamType> nestedParameter) {
        this.optionalNestedParameters.add(nestedParameter);
        return this;
    }

    public ApiNestedParameterBuilder<NestedParamType, ParentParamType> addRequiredCustomParameters(ApiCustomParameters<NestedParamType> customParameters) {
        this.requiredCustomParameters.add(customParameters);
        return this;
    }

    public ApiNestedParameterBuilder<NestedParamType, ParentParamType> addOptionalCustomParameters(ApiCustomParameters<NestedParamType> customParameters) {
        this.optionalCustomParameters.add(customParameters);
        return this;
    }

    @SuppressWarnings("unchecked")
    public ApiNestedParameter<NestedParamType, ParentParamType> build() {
        ApiParameter<?, NestedParamType>[] requiredApiParamsAry = this.requiredApiParameters.toArray((ApiParameter<?, NestedParamType>[])new ApiParameter[this.requiredApiParameters.size()]);

        ApiParameter<?, NestedParamType>[] optionalApiParamsAry = this.optionalApiParameters.toArray((ApiParameter<?, NestedParamType>[])new ApiParameter[this.optionalApiParameters.size()]);

        ApiNestedParameter<?, NestedParamType>[] requiredApiNestedParamsAry = this.requiredNestedParameters.toArray((ApiNestedParameter<?, NestedParamType>[])new ApiNestedParameter[this.requiredNestedParameters.size()]);

        ApiNestedParameter<?, NestedParamType>[] optionalApiNestedParamsAry =   this.optionalNestedParameters.toArray((ApiNestedParameter<?, NestedParamType>[])new ApiNestedParameter[this.optionalNestedParameters.size()]);

        ApiCustomParameters<NestedParamType>[] requiredCustomParamsAry = this.requiredCustomParameters.toArray((ApiCustomParameters<NestedParamType>[])new ApiCustomParameters[this.requiredCustomParameters.size()]);

        ApiCustomParameters<NestedParamType>[] optionalCustomParamsAry = this.optionalCustomParameters.toArray((ApiCustomParameters<NestedParamType>[])new ApiCustomParameters[this.optionalCustomParameters.size()]);

        return new ApiNestedParameter<>(this.nestedParameterName,
                                        this.continueOnOptionalFailure,
                                        this.retrievalFunction,
                                        requiredApiParamsAry,
                                        optionalApiParamsAry,
                                        requiredApiNestedParamsAry,
                                        optionalApiNestedParamsAry,
                                        requiredCustomParamsAry,
                                        optionalCustomParamsAry);
    }

}
