package io.bhowell2.ApiLib.extensions.map;

import io.bhowell2.ApiLib.ApiObjectParameterBuilder;
import io.bhowell2.ApiLib.ParameterRetrievalFunction;

import java.util.Map;

/**
 * @author Blake Howell
 */
public class ApiMapObjectParameterBuilder extends ApiObjectParameterBuilder<Map<String, Object>, Map<String, Object>> {

    /**
     * With no parameter name then the retrieval function should just return the object passed in
     * @return
     */
    public static ApiMapObjectParameterBuilder builder() {
        return new ApiMapObjectParameterBuilder(null, (s, m) -> m);
    }

    public static ApiMapObjectParameterBuilder builder(String innerObjParamName) {
        return new ApiMapObjectParameterBuilder(innerObjParamName, (innerObjName, m) -> (Map<String, Object>) m.get(innerObjName));
    }

    public ApiMapObjectParameterBuilder(String objectParameterName, ParameterRetrievalFunction<Map<String, Object>, Map<String, Object>> retrievalFunction) {
        super(objectParameterName, retrievalFunction);
    }

    public ApiMapObjectParameterBuilder setContinueOnOptionalFailure(boolean b) {
        this.continueOnOptionalFailure = b;
        return this;
    }

    public ApiMapObjectParameterBuilder addRequiredParameter(ApiMapParameter<?> requiredApiParameter) {
        this.requiredParams.add(requiredApiParameter);
        return this;
    }

    public ApiMapObjectParameterBuilder addOptionalParameter(ApiMapParameter<?> optionalApiParameter) {
        this.optionalParams.add(optionalApiParameter);
        return this;
    }

    public ApiMapObjectParameterBuilder addRequiredObjectParameter(ApiMapObjectParameter objectParameter) {
        this.requiredObjParams.add(objectParameter);
        return this;
    }

    public ApiMapObjectParameterBuilder addOptionalObjectParameter(ApiMapObjectParameter objectParameter) {
        this.optionalObjParams.add(objectParameter);
        return this;
    }

    public ApiMapObjectParameterBuilder addRequiredCustomParameters(ApiMapCustomParameters customParameters) {
        this.requiredCustomParams.add(customParameters);
        return this;
    }

    public ApiMapObjectParameterBuilder addOptionalCustomParameters(ApiMapCustomParameters customParameters) {
        this.optionalCustomParams.add(customParameters);
        return this;
    }


    @SuppressWarnings("unchecked")
    public ApiMapObjectParameter build() {
        ApiMapParameter<?>[] requiredApiParamsAry = this.requiredParams
            .toArray((ApiMapParameter<?>[])new ApiMapParameter[this.requiredParams.size()]);

        ApiMapParameter<?>[] optionalApiParamsAry = this.optionalParams
            .toArray((ApiMapParameter<?>[])new ApiMapParameter[this.optionalParams.size()]);

        ApiMapObjectParameter[] requiredApiMapObjParamsAry = this.requiredObjParams
            .toArray((ApiMapObjectParameter[])new ApiMapObjectParameter[this.requiredObjParams.size()]);

        ApiMapObjectParameter[] optionalApiMapObjParamsAry =   this.optionalObjParams
            .toArray((ApiMapObjectParameter[])new ApiMapObjectParameter[this.optionalObjParams.size()]);

        ApiMapCustomParameters[] requiredCustomParamsAry = this.requiredCustomParams
            .toArray((ApiMapCustomParameters[])new ApiMapCustomParameters[this.requiredCustomParams.size()]);

        ApiMapCustomParameters[] optionalCustomParamsAry = this.optionalCustomParams
            .toArray((ApiMapCustomParameters[])new ApiMapCustomParameters[this.optionalCustomParams.size()]);

        return new ApiMapObjectParameter(this.objectParameterName,
                                         this.continueOnOptionalFailure,
                                         this.retrievalFunction,
                                         requiredApiParamsAry,
                                         optionalApiParamsAry,
                                         requiredApiMapObjParamsAry,
                                         optionalApiMapObjParamsAry,
                                         requiredCustomParamsAry,
                                         optionalCustomParamsAry);
    }

}
