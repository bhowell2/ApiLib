package io.bhowell2.apilib.extensions.map;

import io.bhowell2.apilib.ParamRetrievalFunc;
import io.bhowell2.apilib.ApiObjParam;

import java.util.Map;

/**
 * @author Blake Howell
 */
public class ApiMapObjParam extends ApiObjParam<Map<String, Object>, Map<String, Object>> {

    public ApiMapObjParam(boolean continueOnOptionalFailure,
                          ParamRetrievalFunc<Map<String, Object>, Map<String, Object>> retrievalFunction,
                          ApiMapParam<?>[] requiredMapParams,
                          ApiMapParam<?>[] optionalMapParams,
                          ApiMapObjParam[] requiredMapObjParams,
                          ApiMapObjParam[] optionalMapObjParams,
                          ApiMapCustomParam[] requiredCustomParams,
                          ApiMapCustomParam[] optionalCustomParams) {
        super(continueOnOptionalFailure,
              retrievalFunction,
              requiredMapParams,
              optionalMapParams,
              requiredMapObjParams,
              optionalMapObjParams,
              requiredCustomParams,
              optionalCustomParams);
    }

    public ApiMapObjParam(String parameterName,
                          boolean continueOnOptionalFailure,
                          ParamRetrievalFunc<Map<String, Object>, Map<String, Object>> retrievalFunction,
                          ApiMapParam<?>[] requiredMapParams,
                          ApiMapParam<?>[] optionalMapParams,
                          ApiMapObjParam[] requiredMapObjParams,
                          ApiMapObjParam[] optionalMapObjParams,
                          ApiMapCustomParam[] requiredCustomParams,
                          ApiMapCustomParam[] optionalCustomParams) {
        super(parameterName,
              continueOnOptionalFailure,
              retrievalFunction,
              requiredMapParams,
              optionalMapParams,
              requiredMapObjParams,
              optionalMapObjParams,
              requiredCustomParams,
              optionalCustomParams);
    }

}
