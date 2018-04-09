package io.bhowell2.ApiLib;

import java.util.ArrayList;
import java.util.List;

/**
 * Facilitates creation of an ApiObjParam.
 * @author Blake Howell
 */
public class ApiObjParamBuilder<ObjectType, ParentParamType> {

    public static <ObjectType, ParentParamType> ApiObjParamBuilder<ObjectType, ParentParamType> builder(
        boolean isRequired, ParamRetrievalFunc<ObjectType, ParentParamType> retrievalFunction) {
        return new ApiObjParamBuilder<>(null, isRequired, retrievalFunction);
    }

    public static <ObjectType, ParentParamType> ApiObjParamBuilder<ObjectType, ParentParamType> builder(
        String objectParameterName, boolean isRequired, ParamRetrievalFunc<ObjectType, ParentParamType> retrievalFunction) {
        return new ApiObjParamBuilder<>(objectParameterName, isRequired, retrievalFunction);
    }

    public static <ObjectType, ParentParamType> ApiObjParamBuilder<ObjectType, ParentParamType> copyObjParam(boolean isRequired,
                                                                                                             ApiObjParam<ObjectType, ParentParamType> objParamToCopy) {
        return copyObjParam(objParamToCopy.parameterName, isRequired, objParamToCopy);
    }

    public static <ObjectType, ParentParamType> ApiObjParamBuilder<ObjectType, ParentParamType> copyObjParam(
        String paramName, boolean isRequired, ApiObjParam<ObjectType, ParentParamType> objParamToCopy) {
        ApiObjParamBuilder<ObjectType, ParentParamType> builder = new ApiObjParamBuilder<>(paramName, isRequired, objParamToCopy.retrievalFunction);
        builder.addParameters(objParamToCopy.parameters);
        builder.addCustomParameters(objParamToCopy.customParameters);
        builder.addObjectParameters(objParamToCopy.objectParameters);
        builder.setContinueOnOptionalFailure(objParamToCopy.continueOnOptionalFailure);
        return builder;
    }

    protected String objectParameterName;
    protected ParamRetrievalFunc<ObjectType, ParentParamType> retrievalFunction;
    protected List<ApiParam<?, ObjectType>> parameters;
    protected List<ApiObjParam<?, ObjectType>> objectParameters;
    protected List<ApiCustomParam<ObjectType>> customParameters;
    protected boolean isRequired;
    protected boolean continueOnOptionalFailure = false;

    public ApiObjParamBuilder(String objectParameterName, boolean isRequired,
                              ParamRetrievalFunc<ObjectType, ParentParamType> retrievalFunction) {
        this.objectParameterName = objectParameterName;
        this.retrievalFunction = retrievalFunction;
        this.parameters = new ArrayList<>();
        this.objectParameters = new ArrayList<>();
        this.customParameters = new ArrayList<>();
    }

    public ApiObjParamBuilder<ObjectType, ParentParamType> setContinueOnOptionalFailure(boolean continueOnOptionalFailure) {
        this.continueOnOptionalFailure = continueOnOptionalFailure;
        return this;
    }

    public ApiObjParamBuilder<ObjectType, ParentParamType> addParameter(ApiParam<?, ObjectType> parameter) {
        if (parameter == null) {
            throw new RuntimeException("Cannot add null parameters.");
        }
        this.parameters.add(parameter);
        return this;
    }

    public ApiObjParamBuilder<ObjectType, ParentParamType> addParameters(ApiParam<?, ObjectType>... parameters) {
        for (ApiParam<?, ObjectType> parameter : parameters) {
            this.addParameter(parameter);
        }
        return this;
    }

    public ApiObjParamBuilder<ObjectType, ParentParamType> addObjectParameter(ApiObjParam<?, ObjectType> objectParameter) {
        if (objectParameter == null) {
            throw new RuntimeException("Cannot add null object parameters.");
        }
        this.objectParameters.add(objectParameter);
        return this;
    }

    public ApiObjParamBuilder<ObjectType, ParentParamType> addObjectParameters(ApiObjParam<?, ObjectType>... objectParameters) {
        for (ApiObjParam<?, ObjectType> objectParameter : objectParameters) {
            this.addObjectParameter(objectParameter);
        }
        return this;
    }

    public ApiObjParamBuilder<ObjectType, ParentParamType> addCustomParameter(ApiCustomParam<ObjectType> customParameter) {
        if (customParameter == null) {
            throw new RuntimeException("Cannot add null custom parameters.");
        }
        this.customParameters.add(customParameter);
        return this;
    }

    public ApiObjParamBuilder<ObjectType, ParentParamType> addCustomParameters(ApiCustomParam<ObjectType>... customParameters) {
        for (ApiCustomParam<ObjectType> customParameter : customParameters) {
            this.addCustomParameter(customParameter);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public ApiObjParam<ObjectType, ParentParamType> build() {
        return new ApiObjParam<>(this.objectParameterName,
                                 this.continueOnOptionalFailure,
                                 this.isRequired,
                                 this.retrievalFunction,
                                 this.parameters.toArray(new ApiParam[0]),
                                 this.objectParameters.toArray(new ApiObjParam[0]),
                                 this.customParameters.toArray(new ApiCustomParam[0]));
    }

}
