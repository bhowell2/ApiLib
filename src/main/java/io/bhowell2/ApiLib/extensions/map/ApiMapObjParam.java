package io.bhowell2.ApiLib.extensions.map;

import io.bhowell2.ApiLib.ParamRetrievalFunc;
import io.bhowell2.ApiLib.ApiObjParam;

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
                          ApiMapCustomParam[] customParameters) {
        super(continueOnOptionalFailure,
              retrievalFunction,
              requiredMapParams,
              optionalMapParams,
              requiredMapObjParams,
              optionalMapObjParams,
              customParameters);
    }

    public ApiMapObjParam(String parameterName,
                          boolean continueOnOptionalFailure,
                          ParamRetrievalFunc<Map<String, Object>, Map<String, Object>> retrievalFunction,
                          ApiMapParam<?>[] requiredMapParams,
                          ApiMapParam<?>[] optionalMapParams,
                          ApiMapObjParam[] requiredMapObjParams,
                          ApiMapObjParam[] optionalMapObjParams,
                          ApiMapCustomParam[] customParameters) {
        super(parameterName,
              continueOnOptionalFailure,
              retrievalFunction,
              requiredMapParams,
              optionalMapParams,
              requiredMapObjParams,
              optionalMapObjParams,
              customParameters);
    }

}
