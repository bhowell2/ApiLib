package io.bhowell2.ApiLib.extensions.map;

import io.bhowell2.ApiLib.ApiParamBuilder;
import io.bhowell2.ApiLib.CheckFunc;
import io.bhowell2.ApiLib.FormatFunc;
import io.bhowell2.ApiLib.ParamRetrievalFunc;

import java.util.Map;

import static io.bhowell2.ApiLib.extensions.map.ApiMapParamRetrievalFunc.*;

/**
 * @author Blake Howell
 */
public class ApiMapParamBuilder<ParamType> extends ApiParamBuilder<ParamType, Map<String, Object>> {

    /**
     * Builder that supplies the ParameterRetrievalFunction based on the class type. This does not attempt to parse the parameter from a string (if
     * it is provided as such) or replace the parameter back in the map after casting.
     * @param parameterName name of parameter to retrieve from Map<String, ?>
     * @param paramType the class of the parameter that will be used for casting
     * @param <ParamType>
     * @return the builder to add checks to
     */
    public static <ParamType> ApiMapParamBuilder<ParamType> builder(String parameterName, boolean isRequired, Class<ParamType> paramType) {
        return new ApiMapParamBuilder<>(parameterName, isRequired, paramType);
    }

    /**
     * Builder that supplies the ParameterRetrievalFunction based on the class type.
     * @param parameterName name of parameter to retrieve from Map<String, ?>
     * @param paramType the class of the parameter that will be used for casting
     * @param parseIfString if the parameter is a string, will try to parse it (e.g., Integer#parseInt)
     * @param replaceCastInMap if the parameter is casted it will be replaced in the map with the casted value/class
     * @param <ParamType>
     * @return the builder to add checks to
     */
    public static <ParamType> ApiMapParamBuilder<ParamType> builder(String parameterName, boolean isRequired, Class<ParamType> paramType,
                                                                    boolean parseIfString, boolean replaceCastInMap) {
        return new ApiMapParamBuilder<ParamType>(parameterName, isRequired, paramType, parseIfString, replaceCastInMap);
    }

    /**
     * Allows to pass own retrieval function.
     * @param parameterName     name of parameter that will be passed to ParameRetrievalFunc
     * @param retrievalFunction function to retrieve the correct parameter/paramtype from map
     * @param <ParamType>
     * @return the builder to add checks to
     */
    public static <ParamType> ApiMapParamBuilder<ParamType> mapBuilderWithRetrieval(String parameterName,
                                                                                    boolean isRequired,
                                                                                    ParamRetrievalFunc<ParamType, Map<String, Object>> retrievalFunction) {
        return new ApiMapParamBuilder<>(parameterName, isRequired, retrievalFunction);
    }

    /**
     * Provides retrieval function based on paramType provided.
     * @param parameterName name of parameter. will be passed to retrieval function.
     * @param isRequired whether or not parameter is required
     * @param paramType determines what retrieval function will be used. does not attempt to parse from string or cast and replace in map
     */
    public ApiMapParamBuilder(String parameterName, boolean isRequired, Class<ParamType> paramType) {
        this(parameterName, isRequired, paramType, false, false);
    }

    /**
     * Provides retrieval function based on paramType provided.
     * @param parameterName name of parameter. will be passed to retrieval function
     * @param isRequired whether or not parameter is required
     * @param paramType determines what retrieval function will be used
     * @param attemptParseFromString whether or not to attempt to parse a double/integer from a string
     * @param replaceParseInMap whether or not to replace the value in the map if it needed to be retrieved/casted
     */
    public ApiMapParamBuilder(String parameterName, boolean isRequired,
                              Class<ParamType> paramType, boolean attemptParseFromString,
                              boolean replaceParseInMap) {
        this(parameterName, isRequired, typeFromMap(paramType, attemptParseFromString, replaceParseInMap));
    }

    public ApiMapParamBuilder(String parameterName, boolean isRequired,
                              ParamRetrievalFunc<ParamType, Map<String, Object>> retrievalFunction) {
        super(parameterName, isRequired, retrievalFunction);
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
        return (ApiMapParamBuilder<ParamType>) super.addFormatFunction(formatFunc);
    }

    public ApiMapParamBuilder<ParamType> addFormatFunctions(FormatFunc<ParamType>... formatFuncs) {
        return (ApiMapParamBuilder<ParamType>) super.addFormatFunctions(formatFuncs);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ApiMapParam<ParamType> build() {
        return new ApiMapParam<>(this.parameterName,
                                 this.isRequired,
                                 this.retrievalFunction,
                                 this.formatFuncs.toArray(new FormatFunc[0]),
                                 this.formatInsertFunc,
                                 this.checkFuncs.toArray(new CheckFunc[0]));
    }

}
