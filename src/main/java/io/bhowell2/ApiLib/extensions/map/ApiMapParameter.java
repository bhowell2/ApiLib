package io.bhowell2.ApiLib.extensions.map;

import io.bhowell2.ApiLib.ApiParameter;
import io.bhowell2.ApiLib.CheckFunction;
import io.bhowell2.ApiLib.ParameterRetrievalFunction;

import java.util.Map;

/**
 * @author Blake Howell
 */
public class ApiMapParameter<ParamType> extends ApiParameter<ParamType, Map<String, Object>> {

    public ApiMapParameter(String parameterName, ParameterRetrievalFunction<ParamType,
        Map<String, Object>> retrievalFunction, CheckFunction<ParamType>... checkFunction) {
        super(parameterName, retrievalFunction, checkFunction);
    }

    public ApiMapParameter(String parameterName,  Class<ParamType> paramTypeClass, CheckFunction<ParamType>[] checkFunction) {
        this(parameterName, paramTypeClass, false, checkFunction);
    }

    public ApiMapParameter(String parameterName,  Class<ParamType> paramTypeClass, boolean attemptStringRetrieval, CheckFunction<ParamType>[]
        checkFunction) {
        super(parameterName, MapParameterRetrievalFunctions.typeFromMap(paramTypeClass, attemptStringRetrieval), checkFunction);
    }
}
