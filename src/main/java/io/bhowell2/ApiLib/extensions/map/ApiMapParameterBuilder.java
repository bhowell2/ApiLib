package io.bhowell2.ApiLib.extensions.map;

import io.bhowell2.ApiLib.ApiParameter;
import io.bhowell2.ApiLib.ApiParameterBuilder;
import io.bhowell2.ApiLib.CheckFunction;
import io.bhowell2.ApiLib.ParameterRetrievalFunction;

import java.util.List;
import java.util.Map;

import static io.bhowell2.ApiLib.extensions.map.MapRequestParameterRetrievalFunctions.*;

/**
 * @author Blake Howell
 */
public class ApiMapParameterBuilder {

    public static <T> ApiParameterBuilder<T, Map<String, Object>> builder(String parameterName, Class<T> paramType) {
        return new ApiParameterBuilder<>(parameterName, typeFromMap(paramType));
    }

}
