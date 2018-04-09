package io.bhowell2.ApiLib.extensions.map;

import io.bhowell2.ApiLib.ApiObjParamBuilder;
import io.bhowell2.ApiLib.ParamRetrievalFunc;

import java.util.Map;

/**
 * @author Blake Howell
 */
public class ApiMapObjParamBuilder extends ApiObjParamBuilder<Map<String, Object>, Map<String, Object>> {

    /**
     * With no parameter name then the retrieval function should just return the object passed in (i.e., the map). And there is no notion of required.
     * @return
     */
    public static ApiMapObjParamBuilder builder() {
        return builder(false);
    }

    public static ApiMapObjParamBuilder builder(boolean isRequired) {
        return new ApiMapObjParamBuilder(null, isRequired, (s, m) -> m);
    }

    @SuppressWarnings("unchecked")
    public static ApiMapObjParamBuilder builder(String innerObjParamName, boolean isRequired) {
        return new ApiMapObjParamBuilder(innerObjParamName, isRequired, (innerObjName, m) -> (Map<String, Object>) m.get(innerObjName));
    }

    public ApiMapObjParamBuilder(String objectParameterName,
                                 boolean isRequired,
                                 ParamRetrievalFunc<Map<String, Object>, Map<String, Object>> retrievalFunction) {
        super(objectParameterName, isRequired, retrievalFunction);
    }

    public ApiMapObjParamBuilder setContinueOnOptionalFailure(boolean continueOnOptionalFailure) {
        this.continueOnOptionalFailure = continueOnOptionalFailure;
        return this;
    }

    public ApiMapObjParamBuilder addRequiredParameter(ApiMapParam<?> requiredApiParameter) {
        return (ApiMapObjParamBuilder) super.addParameter(requiredApiParameter);
    }

    public ApiMapObjParamBuilder addRequiredParameters(ApiMapParam<?>... requiredApiParameters) {
        return (ApiMapObjParamBuilder) super.addParameters(requiredApiParameters);
    }

    public ApiMapObjParamBuilder addObjectParameter(ApiMapObjParam objectParameter) {
        return (ApiMapObjParamBuilder)super.addObjectParameter(objectParameter);
    }

    public ApiMapObjParamBuilder addObjectParameters(ApiMapObjParam... objectParameters) {
        return (ApiMapObjParamBuilder)super.addObjectParameters(objectParameters);
    }

    public ApiMapObjParamBuilder addCustomParameter(ApiMapCustomParam customParameter) {
        return (ApiMapObjParamBuilder) super.addCustomParameter(customParameter);
    }

    public ApiMapObjParamBuilder addCustomParameters(ApiMapCustomParam... customParameters) {
        return (ApiMapObjParamBuilder) super.addCustomParameters(customParameters);
    }

    @SuppressWarnings("unchecked")
    public ApiMapObjParam build() {
        return new ApiMapObjParam(this.objectParameterName,
                                  this.continueOnOptionalFailure,
                                  this.isRequired,
                                  this.retrievalFunction,
                                  this.parameters.toArray(new ApiMapParam[0]),
                                  this.objectParameters.toArray(new ApiMapObjParam[0]),
                                  this.customParameters.toArray(new ApiMapCustomParam[0]));
    }
}
