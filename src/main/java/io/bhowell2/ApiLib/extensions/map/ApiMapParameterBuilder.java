package io.bhowell2.ApiLib.extensions.map;

import io.bhowell2.ApiLib.ApiParameterBuilder;
import io.bhowell2.ApiLib.CheckFunction;
import io.bhowell2.ApiLib.ParameterRetrievalFunction;

import java.util.Map;

import static io.bhowell2.ApiLib.extensions.map.MapRequestParameterRetrievalFunctions.*;

/**
 * @author Blake Howell
 */
public class ApiMapParameterBuilder<ParamType> extends ApiParameterBuilder<ParamType, Map<String, Object>> {

    public static <ParamType> ApiMapParameterBuilder<ParamType> builder(String parameterName, Class<ParamType> paramType) {
        return new ApiMapParameterBuilder<>(parameterName, paramType);
    }

    public ApiMapParameterBuilder(String parameterName, Class<ParamType> paramType) {
        super(parameterName, typeFromMap(paramType));
    }

    @SuppressWarnings("unchecked")
    public ApiMapParameter<ParamType> build() {
        return new ApiMapParameter<ParamType>(this.parameterName,
                                              this.retrievalFunction,
                                              this.checkFunctions.toArray((CheckFunction<ParamType>[])new CheckFunction[this.checkFunctions.size()]));
    }

}
