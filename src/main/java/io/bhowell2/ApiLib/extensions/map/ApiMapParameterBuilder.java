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

    // must have
    public static <ParamType> ApiMapParameterBuilder<ParamType> mapBuilderWithRetrieval(String parameterName,
                                                                        ParameterRetrievalFunction<ParamType, Map<String, Object>> retrievalFunction) {

        return new ApiMapParameterBuilder<>(parameterName, retrievalFunction);
    }

    public ApiMapParameterBuilder(String parameterName, Class<ParamType> paramType) {
        super(parameterName, typeFromMap(paramType));
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
        if (this.checkFunctions.size() == 0) {
            throw new NoFunctionChecksProvidedException(this.parameterName);
        }
        return new ApiMapParameter<>(this.parameterName,
                                     this.retrievalFunction,
                                     this.checkFunctions.toArray((CheckFunction<ParamType>[])new CheckFunction[this.checkFunctions.size()]));
    }

}
