package io.bhowell2.apilib.extensions.map;

import io.bhowell2.apilib.ApiParam;
import io.bhowell2.apilib.CheckFunc;
import io.bhowell2.apilib.FormatFunc;
import io.bhowell2.apilib.FormatInsertFunc;
import io.bhowell2.apilib.ParamRetrievalFunc;
import io.bhowell2.apilib.extensions.map.utils.ApiMapNullParamRetrievalCheckFunc;
import io.bhowell2.apilib.extensions.map.utils.ApiMapParamRetrievalFuncs;

import java.util.Map;

/**
 * @author Blake Howell
 */
public class ApiMapParam<ParamType> extends ApiParam<ParamType, Map<String, Object>> {

	public ApiMapParam(String parameterName, ParamRetrievalFunc<ParamType,
		Map<String, Object>> retrievalFunction, CheckFunc<ParamType>[] checkFunc) {
		super(parameterName, retrievalFunction, checkFunc);
	}

	public ApiMapParam(String parameterName,
	                   Class<ParamType> paramTypeClass, CheckFunc<ParamType>[] checkFunc) {
		this(parameterName, paramTypeClass, false, false, checkFunc);
	}

	public ApiMapParam(String parameterName,
	                   Class<ParamType> paramTypeClass, boolean parseIfString,
	                   boolean replaceCastInMap, CheckFunc<ParamType>[] checkFuncs) {
		super(parameterName,
		      ApiMapParamRetrievalFuncs.typeFromMap(paramTypeClass, parseIfString, replaceCastInMap),
		      checkFuncs);
	}

	public ApiMapParam(String parameterName,
	                   ParamRetrievalFunc<ParamType, Map<String, Object>> retrievalFunction,
	                   FormatFunc<? super Object, ParamType>[] formatFuncs,
	                   FormatInsertFunc<ParamType, Map<String, Object>> formatInsertFunc,
	                   CheckFunc<ParamType>[] checkFuncs,
	                   boolean canBeNull) {
		super(parameterName,
		      retrievalFunction,
		      formatFuncs,
		      formatInsertFunc,
		      checkFuncs,
		      canBeNull,
		      ApiMapNullParamRetrievalCheckFunc.NULL_CHECK);
	}

//    public ApiMapParam(String parameterName,
//                       Class<ParamType> paramTypeClass, boolean parseIfString, boolean replaceCastInMap,
//                       FormatFunc<ParamType>[] formatFuncs,
//                       FormatInsertFunc<ParamType, Map<String, Object>> formatInsertFunc,
//                       CheckFunc<ParamType>[] checkFuncs,
//                       boolean canBeNull) {
//        this(parameterName,
//             ApiMapParamRetrievalFuncs.typeFromMap(paramTypeClass, parseIfString, replaceCastInMap),
//             formatFuncs,
//             formatInsertFunc,
//             checkFuncs,
//             canBeNull);
//    }

}
