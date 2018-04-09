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
                          ApiMapParam<?>[] mapParameters,
                          ApiMapObjParam[] objectParameters,
                          ApiMapCustomParam[] customParameters) {
        super(continueOnOptionalFailure,
              retrievalFunction,
              mapParameters,
              objectParameters,
              customParameters);
    }

    public ApiMapObjParam(String parameterName,
                          boolean continueOnOptionalFailure,
                          boolean isRequired,
                          ParamRetrievalFunc<Map<String, Object>, Map<String, Object>> retrievalFunction,
                          ApiMapParam<?>[] mapParameters,
                          ApiMapObjParam[] objectParameters,
                          ApiMapCustomParam[] customParameters) {
        super(parameterName,
              continueOnOptionalFailure,
              isRequired,
              retrievalFunction,
              mapParameters,
              objectParameters,
              customParameters);
    }

}
