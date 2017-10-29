package io.bhowell2.ApiLib;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * All functionality is provided and only {@link #check(Map)} needs to be called. Generally the class type
 * and function checks will be the same for every version, but in the rare case that either of these changes, a new class should be created for the parameter.
 *
 * @author Blake Howell
 */
public final class ApiParameter<T> {

    private final String parameterName;
    private final List<Function<T, FunctionCheckTuple>> paramCheckFunctions;
    private final Class<T> parameterClassType;
    private final Function<? super Object, T> parameterCastFunction;

    public ApiParameter(String parameterName, Class<T> parameterClassType, List<Function<T,
        FunctionCheckTuple>> paramCheckFunctions, Function<? super Object, T> parameterCastFunction) {
        this.parameterName = parameterName;
        this.parameterClassType = parameterClassType;
        this.paramCheckFunctions = paramCheckFunctions;
        this.parameterCastFunction = parameterCastFunction;
    }

    public String getParameterName() {
        return this.parameterName;
    }

    /**
     * Some web frameworks/platforms automatically wrap the parameters for the user in a Map. This will pull the parameter from the map, with
     * the {@link #parameterName} provided in the constructor and check that it meets the requirements specified by the provided check functions.
     * @param requestParameters contains the parameter to be checked, which will be pulled from the map
     * @return a tuple with the name of the successfully checked parameter, or error information
     */
    @SuppressWarnings("unchecked")
    public ApiParameterCheckTuple check(Map<String, Object> requestParameters) {
        if (parameterCastFunction != null) {
            Object param = requestParameters.get(this.parameterName);
            if (param != null) {
                // may fail to convert the parameter
                try {
                    requestParameters.put(this.parameterName, this.parameterCastFunction.apply(param));
                } catch (Exception e) {
                    return ApiParameterCheckTuple.parameterCastException(this.parameterName, this.parameterClassType);
                }
            } else {
                return ApiParameterCheckTuple.missingParameterFailure(this.parameterName);
            }
        }

        T param = (T) requestParameters.get(this.parameterName); // really this is just cast to object due to type erasure
        if (param == null)
            return ApiParameterCheckTuple.missingParameterFailure(this.parameterName);

        if (!this.parameterClassType.isInstance(param))
            return ApiParameterCheckTuple.parameterCastException(this.parameterName, this.parameterClassType);

        for (Function<T, FunctionCheckTuple> functionCheck : paramCheckFunctions) {
            FunctionCheckTuple checkTuple = functionCheck.apply(param);

            // short circuit checks and return (if one check fails, it all fails)
            if (checkTuple.failed()) {
                return checkTuple.hasFailureMessage() ? ApiParameterCheckTuple.invalidParameterFailure(this.parameterName, checkTuple.getFailureMessage()) :
                    ApiParameterCheckTuple.invalidParameterFailure(this.parameterName, "Failed to pass parameter check.");
            }

        }
        // all checks have passed, parameter is deemed successfully checked
        return ApiParameterCheckTuple.success(this.parameterName);
    }

}
