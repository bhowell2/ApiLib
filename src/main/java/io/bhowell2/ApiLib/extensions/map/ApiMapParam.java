package io.bhowell2.ApiLib.extensions.map;

import io.bhowell2.ApiLib.ApiParam;
import io.bhowell2.ApiLib.CheckFunc;
import io.bhowell2.ApiLib.FormatFunc;
import io.bhowell2.ApiLib.FormatInsertFunc;
import io.bhowell2.ApiLib.ParamRetrievalFunc;

import java.util.Map;

/**
 * @author Blake Howell
 */
public class ApiMapParam<ParamType> extends ApiParam<ParamType, Map<String, Object>> {

    public ApiMapParam(String parameterName, boolean isRequired, ParamRetrievalFunc<ParamType,
            Map<String, Object>> retrievalFunction, CheckFunc<ParamType>... checkFunc) {
        super(parameterName, isRequired, retrievalFunction, checkFunc);
    }

    public ApiMapParam(String parameterName, boolean isRequired,
                       Class<ParamType> paramTypeClass, CheckFunc<ParamType>[] checkFunc) {
        this(parameterName, isRequired, paramTypeClass, false, false, checkFunc);
    }

    public ApiMapParam(String parameterName, boolean isRequired,
                       Class<ParamType> paramTypeClass, boolean parseIfString,
                       boolean replaceCastInMap, CheckFunc<ParamType>[] checkFuncs) {
        super(parameterName, isRequired, ApiMapParamRetrievalFunc
            .typeFromMap(paramTypeClass, parseIfString, replaceCastInMap), checkFuncs);
    }

    public ApiMapParam(String parameterName, boolean isRequired,
                       ParamRetrievalFunc<ParamType, Map<String, Object>> retrievalFunction,
                       FormatFunc<ParamType>[] formatFuncs,
                       FormatInsertFunc<ParamType, Map<String, Object>> formatInsertFunc,
                       CheckFunc<ParamType>[] checkFuncs) {
        super(parameterName,
              isRequired,
              retrievalFunction,
              formatFuncs,
              formatInsertFunc,
              checkFuncs);
    }

    public ApiMapParam(String parameterName, boolean isRequired,
                       Class<ParamType> paramTypeClass, boolean parseIfString, boolean replaceCastInMap,
                       FormatFunc<ParamType>[] formatFuncs,
                       FormatInsertFunc<ParamType, Map<String, Object>> formatInsertFunc,
                       CheckFunc<ParamType>[] checkFuncs) {
        this(parameterName,
             isRequired,
             ApiMapParamRetrievalFunc.typeFromMap(paramTypeClass, parseIfString, replaceCastInMap),
             formatFuncs,
             formatInsertFunc,
             checkFuncs);
    }

}
