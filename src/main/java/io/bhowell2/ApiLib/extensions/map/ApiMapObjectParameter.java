package io.bhowell2.ApiLib.extensions.map;

import io.bhowell2.ApiLib.ParameterRetrievalFunction;
import io.bhowell2.ApiLib.ApiObjectParameter;

import java.util.Map;

/**
 * @author Blake Howell
 */
public class ApiMapObjectParameter extends ApiObjectParameter<Map<String, Object>, Map<String, Object>> {

    public ApiMapObjectParameter(boolean continueOnOptionalFailure,
                                 ParameterRetrievalFunction<Map<String, Object>, Map<String, Object>> retrievalFunction,
                                 ApiMapParameter<?>[] requiredParameters,
                                 ApiMapParameter<?>[] optionalParameters,
                                 ApiMapObjectParameter[] requiredObjParameters,
                                 ApiMapObjectParameter[] optionalObjParameters,
                                 ApiMapCustomParameters[] requiredCustomParameters,
                                 ApiMapCustomParameters[] optionalCustomParameters) {
        super(continueOnOptionalFailure,
              retrievalFunction,
              requiredParameters,
              optionalParameters,
              requiredObjParameters,
              optionalObjParameters,
              requiredCustomParameters,
              optionalCustomParameters);
    }

    public ApiMapObjectParameter(String parameterName,
                                 boolean continueOnOptionalFailure,
                                 ParameterRetrievalFunction<Map<String, Object>, Map<String, Object>> retrievalFunction,
                                 ApiMapParameter<?>[] requiredParameters,
                                 ApiMapParameter<?>[] optionalParameters,
                                 ApiMapObjectParameter[] requiredObjParameters,
                                 ApiMapObjectParameter[] optionalObjParameters,
                                 ApiMapCustomParameters[] requiredCustomParameters,
                                 ApiMapCustomParameters[] optionalCustomParameters) {
        super(parameterName,
              continueOnOptionalFailure,
              retrievalFunction,
              requiredParameters,
              optionalParameters,
              requiredObjParameters,
              optionalObjParameters,
              requiredCustomParameters,
              optionalCustomParameters);
    }

}
