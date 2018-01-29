package io.bhowell2.ApiLib.extensions.map;

import io.bhowell2.ApiLib.ApiParameterBuilder;
import io.bhowell2.ApiLib.CheckFunction;
import io.bhowell2.ApiLib.ParameterRetrievalFunction;

import java.util.Map;

import static io.bhowell2.ApiLib.extensions.map.MapParameterRetrievalFunctions.*;

/**
 * @author Blake Howell
 */
public class ApiMapParameterBuilder<ParamType> extends ApiParameterBuilder<ParamType, Map<String, Object>> {

    /**
     * Builder that supplies the ParameterRetrievalFunction based on the class type. This does not attempt to parse the parameter from a string (if
     * it is provided as such) or replace the parameter back in the map after casting.
     * @param parameterName name of parameter to retrieve from Map<String, ?>
     * @param paramType the class of the parameter that will be used for casting
     * @param <ParamType>
     * @return the builder to add checks to
     */
    public static <ParamType> ApiMapParameterBuilder<ParamType> builder(String parameterName, Class<ParamType> paramType) {
        return new ApiMapParameterBuilder<>(parameterName, paramType);
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
    public static <ParamType> ApiMapParameterBuilder<ParamType> builder(String parameterName, Class<ParamType> paramType, boolean
        parseIfString, boolean replaceCastInMap) {
        return new ApiMapParameterBuilder<ParamType>(parameterName, paramType, parseIfString);
    }

    /**
     * Allows to pass own retrieval function.
     * @param parameterName name of parameter that will be passed to ParameterRetrievalFunction
     * @param retrievalFunction function to retrieve the correct parameter/paramtype from map
     * @param <ParamType>
     * @return the builder to add checks to
     */
    public static <ParamType> ApiMapParameterBuilder<ParamType> mapBuilderWithRetrieval(String parameterName,
                                                                        ParameterRetrievalFunction<ParamType, Map<String, Object>> retrievalFunction) {
        return new ApiMapParameterBuilder<>(parameterName, retrievalFunction);
    }

    /**
     *
     * @param parameterName
     * @param paramType
     */
    public ApiMapParameterBuilder(String parameterName, Class<ParamType> paramType) {
        this(parameterName, paramType, false);
    }

    /**
     *
     * @param parameterName
     * @param paramType
     * @param attemptParseFromString
     */
    public ApiMapParameterBuilder(String parameterName, Class<ParamType> paramType, boolean attemptParseFromString) {
        super(parameterName, typeFromMap(paramType, attemptParseFromString));
    }

    /**
     *
     * @param parameterName
     * @param retrievalFunction
     */
    public ApiMapParameterBuilder(String parameterName, ParameterRetrievalFunction<ParamType, Map<String, Object>> retrievalFunction) {
        super(parameterName, retrievalFunction);
    }

    /**
     *
     * @param checkFunction
     * @return
     */
    public ApiMapParameterBuilder<ParamType> addCheckFunction(CheckFunction<ParamType> checkFunction) {
        super.addCheckFunction(checkFunction);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ApiMapParameter<ParamType> build() {
        return new ApiMapParameter<>(this.parameterName,
                                     this.retrievalFunction,
                                     this.checkFunctions.toArray((CheckFunction<ParamType>[])new CheckFunction[this.checkFunctions.size()]));
    }

}
