package io.bhowell2.ApiLib;

import java.util.ArrayList;
import java.util.List;

/**
 * Facilitates creation of ApiObjectParameter.
 *
 * @author Blake Howell
 */
public final class ApiObjectParameterBuilder<ObjectType, ParentParamType> {

    public static <ObjectType, ParentParamType> ApiObjectParameterBuilder<ObjectType, ParentParamType> builder(
        ParameterRetrievalFunction<ObjectType, ParentParamType> retrievalFunction) {
        return new ApiObjectParameterBuilder<>(null, retrievalFunction);
    }

    public static <ObjectType, ParentParamType> ApiObjectParameterBuilder<ObjectType, ParentParamType> builder(String objectParameterName, ParameterRetrievalFunction<ObjectType, ParentParamType> retrievalFunction) {
        return new ApiObjectParameterBuilder<>(objectParameterName, retrievalFunction);
    }

    protected String nestedParameterName;
    protected ParameterRetrievalFunction<ObjectType, ParentParamType> retrievalFunction;
    protected List<ApiParameter<?, ObjectType>> requiredParams;
    protected List<ApiParameter<?, ObjectType>> optionalParams;
    protected List<ApiObjectParameter<?, ObjectType>> requiredObjParams;
    protected List<ApiObjectParameter<?, ObjectType>> optionalObjParams;
    protected List<ApiCustomParameters<ObjectType>> requiredCustomParams;
    protected List<ApiCustomParameters<ObjectType>> optionalCustomParams;
    protected boolean continueOnOptionalFailure = false;

    public ApiObjectParameterBuilder(String nestedParameterName, ParameterRetrievalFunction<ObjectType, ParentParamType> retrievalFunction) {
        this.nestedParameterName = nestedParameterName;
        this.retrievalFunction = retrievalFunction;
        this.requiredParams = new ArrayList<>();
        this.optionalParams = new ArrayList<>();
        this.requiredObjParams = new ArrayList<>();
        this.optionalObjParams = new ArrayList<>();
        this.requiredCustomParams = new ArrayList<>();
        this.optionalCustomParams = new ArrayList<>();
    }

    public ApiObjectParameterBuilder<ObjectType, ParentParamType> setContinueOnOptionalFailure(boolean b) {
        this.continueOnOptionalFailure = b;
        return this;
    }

    public ApiObjectParameterBuilder<ObjectType, ParentParamType> addRequiredParameter(ApiParameter<?, ObjectType> requiredApiParameter) {
        this.requiredParams.add(requiredApiParameter);
        return this;
    }

    public ApiObjectParameterBuilder<ObjectType, ParentParamType> addOptionalParameter(ApiParameter<?, ObjectType> optionalApiParameter) {
        this.optionalParams.add(optionalApiParameter);
        return this;
    }

    public ApiObjectParameterBuilder<ObjectType, ParentParamType> addRequiredObjectParameter(ApiObjectParameter<?, ObjectType> objectParameter) {
        this.requiredObjParams.add(objectParameter);
        return this;
    }

    public ApiObjectParameterBuilder<ObjectType, ParentParamType> addOptionalObjectParameter(ApiObjectParameter<?, ObjectType> objectParameter) {
        this.optionalObjParams.add(objectParameter);
        return this;
    }

    public ApiObjectParameterBuilder<ObjectType, ParentParamType> addRequiredCustomParameters(ApiCustomParameters<ObjectType> customParameters) {
        this.requiredCustomParams.add(customParameters);
        return this;
    }

    public ApiObjectParameterBuilder<ObjectType, ParentParamType> addOptionalCustomParameters(ApiCustomParameters<ObjectType> customParameters) {
        this.optionalCustomParams.add(customParameters);
        return this;
    }

    @SuppressWarnings("unchecked")
    public ApiObjectParameter<ObjectType, ParentParamType> build() {
        ApiParameter<?, ObjectType>[] requiredApiParamsAry = this.requiredParams
            .toArray((ApiParameter<?, ObjectType>[])new ApiParameter[this.requiredParams.size()]);

        ApiParameter<?, ObjectType>[] optionalApiParamsAry = this.optionalParams
            .toArray((ApiParameter<?, ObjectType>[])new ApiParameter[this.optionalParams.size()]);

        ApiObjectParameter<?, ObjectType>[] requiredApiNestedParamsAry = this.requiredObjParams
            .toArray((ApiObjectParameter<?, ObjectType>[])new ApiObjectParameter[this.requiredObjParams.size()]);

        ApiObjectParameter<?, ObjectType>[] optionalApiNestedParamsAry =   this.optionalObjParams
            .toArray((ApiObjectParameter<?, ObjectType>[])new ApiObjectParameter[this.optionalObjParams.size()]);

        ApiCustomParameters<ObjectType>[] requiredCustomParamsAry = this.requiredCustomParams
            .toArray((ApiCustomParameters<ObjectType>[])new ApiCustomParameters[this.requiredCustomParams.size()]);

        ApiCustomParameters<ObjectType>[] optionalCustomParamsAry = this.optionalCustomParams
            .toArray((ApiCustomParameters<ObjectType>[])new ApiCustomParameters[this.optionalCustomParams.size()]);

        return new ApiObjectParameter<>(this.nestedParameterName,
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
