package io.bhowell2.ApiLib.extensions.map;

import io.bhowell2.ApiLib.ApiParamBuilder;
import io.bhowell2.ApiLib.CheckFunc;
import io.bhowell2.ApiLib.FormatFunc;
import io.bhowell2.ApiLib.FormatInsertFunc;
import io.bhowell2.ApiLib.ParamRetrievalFunc;

import java.util.Map;

import static io.bhowell2.ApiLib.extensions.map.ApiMapParamRetrievalFuncs.*;

/**
 * @author Blake Howell
 */
public class ApiMapParamBuilder<ParamType> extends ApiParamBuilder<ParamType, Map<String, Object>> {

    /**
     * Builder that supplies the ParameterRetrievalFunction based on the class type. This does not attempt to parse the parameter from a string (if
     * it is provided as such) or replace the parameter back in the map after casting.
     * @param paramName name of parameter to retrieve from Map<String, ?>
     * @param paramType the class of the parameter that will be used for casting
     * @param <ParamType>
     * @return the rootObjBuilder to add checks to
     */
    public static <ParamType> ApiMapParamBuilder<ParamType> builder(String paramName, Class<ParamType> paramType) {
        return new ApiMapParamBuilder<>(paramName, paramType);
    }

    /**
     * Builder that supplies the ParameterRetrievalFunction based on the class type.
     * @param paramName name of parameter to retrieve from Map<String, ?>
     * @param paramType the class of the parameter that will be used for casting
     * @param parseIfString if the parameter is a string, will try to parse it (e.g., Integer#parseInt)
     * @param replaceCastInMap if the parameter is casted it will be replaced in the map with the casted value/class
     * @param <ParamType>
     * @return the rootObjBuilder to add checks to
     */
    public static <ParamType> ApiMapParamBuilder<ParamType> builder(String paramName,  Class<ParamType> paramType,
                                                                    boolean parseIfString, boolean replaceCastInMap) {
        return new ApiMapParamBuilder<ParamType>(paramName, paramType, parseIfString, replaceCastInMap);
    }

    /**
     * Copies the name, retrieval function, Format/FormatInsert funcs, and check functions. Mainly created to allow the user to duplicate a
     * parameter, but provide additional checks (or format functions).
     * @param mapParam the parameter to copy
     * @param <ParamType> the type of the parameter to be checked
     * @return a builder to add additional checks and format functions to
     */
    public static <ParamType> ApiMapParamBuilder<ParamType> copyFrom(ApiMapParam<ParamType> mapParam) {
        return copyFrom(mapParam.parameterName, mapParam);
    }

    /**
     * Allows the copying of a parameter, but allows for changing the name. Copies the retrieval function, Format/FormatInsert funcs, and check
     * functions. Mainly created to allow the user to duplicate a parameter, but provide additional checks (or format functions).
     * @param paramName name of parameter
     * @param mapParam the parameter to copy
     * @param <ParamType>
     * @return a builder to add additional checks and format functions to
     */
    public static <ParamType> ApiMapParamBuilder<ParamType> copyFrom(String paramName,
                                                                     ApiMapParam<ParamType> mapParam) {
        return new ApiMapParamBuilder<ParamType>(paramName, mapParam);
    }

    /**
     * Allows to pass custom retrieval function.
     * @param paramName     name of parameter that will be passed to ParameRetrievalFunc
     * @param retrievalFunc function to retrieve the correct parameter/paramtype from map
     * @param <ParamType>
     * @return the rootObjBuilder to add checks to
     */
    public static <ParamType> ApiMapParamBuilder<ParamType> mapBuilderWithRetrieval(String paramName,
                                                                                    ParamRetrievalFunc<ParamType, Map<String, Object>> retrievalFunc) {
        return new ApiMapParamBuilder<>(paramName, retrievalFunc);
    }

    /**
     * Provides retrieval function based on paramType provided.
     * @param paramName name of parameter. will be passed to retrieval function.
     * @param paramType determines what retrieval function will be used. does not attempt to parse from string or cast and replace in map
     */
    public ApiMapParamBuilder(String paramName, Class<ParamType> paramType) {
        this(paramName, paramType, false, false);
    }

    /**
     * Provides retrieval function based on paramType provided.
     * @param paramName name of parameter. will be passed to retrieval function
     * @param paramType determines what retrieval function will be used
     * @param attemptParseFromString whether or not to attempt to parse a double/integer from a string
     * @param replaceParseInMap whether or not to replace the value in the map if it needed to be retrieved/casted
     */
    public ApiMapParamBuilder(String paramName,
                              Class<ParamType> paramType,
                              boolean attemptParseFromString,
                              boolean replaceParseInMap) {
        this(paramName, typeFromMap(paramType, attemptParseFromString, replaceParseInMap));
    }

    public ApiMapParamBuilder(String paramName, ApiMapParam<ParamType> mapParam) {
        super(paramName, mapParam);
    }

    public ApiMapParamBuilder(String paramName,
                              ParamRetrievalFunc<ParamType, Map<String, Object>> retrievalFunction) {
        super(paramName, retrievalFunction);
    }

    Class<ParamType> paramTypeClass;

    public ApiMapParamBuilder<ParamType> addCheckFunction(CheckFunc<ParamType> checkFunc) {
        super.addCheckFunction(checkFunc);
        return this;
    }

    @SuppressWarnings({"varargs"})
    public ApiMapParamBuilder<ParamType> addCheckFunctions(CheckFunc<ParamType>... checkFuncs) {
        super.addCheckFunctions(checkFuncs);
        return this;
    }

    public ApiMapParamBuilder<ParamType> addFormatFunction(FormatFunc<ParamType> formatFunc) {
        super.addFormatFunction(formatFunc);
        return this;
    }

    public ApiMapParamBuilder<ParamType> addFormatFunctions(FormatFunc<ParamType>... formatFuncs) {
        super.addFormatFunctions(formatFuncs);
        return this;
    }

    @Override
    public ApiMapParamBuilder<ParamType> addFormatInjectionFunction(FormatInsertFunc<ParamType, Map<String, Object>>
                                                                                               formatInsertFunc) {
        super.addFormatInjectionFunction(formatInsertFunc);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ApiMapParam<ParamType> build() {
        return new ApiMapParam<>(this.paramName,
                                 this.retrievalFunc,
                                 this.formatFuncs.toArray(new FormatFunc[0]),
                                 this.formatInsertFunc,
                                 this.checkFuncs.toArray(new CheckFunc[0]));
    }

}
