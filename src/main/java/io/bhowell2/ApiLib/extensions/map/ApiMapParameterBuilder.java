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

    public static <ParamType> ApiMapParameterBuilder<ParamType> builder(String parameterName, Class<ParamType> paramType) {
        return new ApiMapParameterBuilder<>(parameterName, paramType);
    }

    /**
     * Provides a builder for the parameter retrieved from a Map<String, Object>.
     * @param parameterName name of the parameter
     * @param paramType the type of the parameter to retrieve (e.g., Double.class, String.class)
     * @param attemptParseFromString attempts to retrieve the parameter from a string if it is provided as such
     * @param <ParamType> type
     * @return a ApiMapParameter
     */
    public static <ParamType> ApiMapParameterBuilder<ParamType> builder(String parameterName, Class<ParamType> paramType, boolean
        attemptParseFromString) {
        return new ApiMapParameterBuilder<ParamType>(parameterName, paramType, attemptParseFromString);
    }

    // must have
    public static <ParamType> ApiMapParameterBuilder<ParamType> mapBuilderWithRetrieval(String parameterName,
                                                                        ParameterRetrievalFunction<ParamType, Map<String, Object>> retrievalFunction) {
        return new ApiMapParameterBuilder<>(parameterName, retrievalFunction);
    }

    public ApiMapParameterBuilder(String parameterName, Class<ParamType> paramType) {
        this(parameterName, paramType, false);
    }

    public ApiMapParameterBuilder(String parameterName, Class<ParamType> paramType, boolean attemptParseFromString) {
        super(parameterName, typeFromMap(paramType, attemptParseFromString));
    }

    public ApiMapParameterBuilder(String parameterName, ParameterRetrievalFunction<ParamType, Map<String, Object>> retrievalFunction) {
        super(parameterName, retrievalFunction);
    }

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
