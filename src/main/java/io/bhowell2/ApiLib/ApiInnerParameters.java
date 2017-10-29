package io.bhowell2.ApiLib;

import java.util.List;
import java.util.function.Function;

/**
 * Use when a {@code Map<String, Object>} is nested within a map. This can be chained an unlimited number of times.
 * @author Blake Howell
 */
public final class ApiInnerParameters<T> {

    private final String parameterName;
    private final List<Function<T, FunctionCheckTuple>> paramCheckFunctions;
    private final Class<T> parameterClassType;
    private final Function<? super Object, T> parameterCastFunction;


    public ApiInnerParameters(String parameterName, Class<T> parameterClassType) {

    }

}
