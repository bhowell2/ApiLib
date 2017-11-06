package io.bhowell2.ApiLib.extensions.map;

import io.bhowell2.ApiLib.ApiParameter;
import io.bhowell2.ApiLib.CheckFunction;
import io.bhowell2.ApiLib.ParameterRetrievalFunction;

import java.util.Map;

/**
 * @author Blake Howell
 */
public class ApiMapParameter<ParamType> extends ApiParameter<ParamType, Map<String, Object>> {

    public ApiMapParameter(String parameterName, CheckFunction<ParamType>[] checkFunction, ParameterRetrievalFunction<ParamType, Map<String, Object>> retrievalFunction) {
        super(parameterName, retrievalFunction, checkFunction);
    }

    public ApiMapParameter(String parameterName, CheckFunction<ParamType>[] checkFunction, Class<ParamType> paramTypeClass) {
        super(parameterName, MapRequestParameterRetrievalFunctions.typeFromMap(paramTypeClass), checkFunction);
    }
    
}
