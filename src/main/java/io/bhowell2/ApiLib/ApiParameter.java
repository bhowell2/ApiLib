package io.bhowell2.ApiLib;

/**
 *
 * @author Blake Howell
 */
public class ApiParameter<ParamType, ParamsObj> {

    final String parameterName;
    protected final CheckFunction<ParamType>[] paramCheckFunctions;
    protected final ParameterRetrievalFunction<ParamType, ParamsObj> parameterRetrievalFunction;

    @SuppressWarnings("varargs")
    public ApiParameter(String parameterName,
                        ParameterRetrievalFunction<ParamType, ParamsObj> parameterRetrievalFunction,
                        CheckFunction<ParamType>... paramCheckFunctions) {
        if (paramCheckFunctions.length == 0) {
            throw new NoFunctionChecksProvidedException(parameterName);
        }
        this.parameterName = parameterName;
        this.paramCheckFunctions = paramCheckFunctions;
        this.parameterRetrievalFunction = parameterRetrievalFunction;
    }

    /**
     * Some web frameworks/platforms automatically wrap the parameters for the user in a Map. This will pull the parameter from the map, with
     * the {@link #parameterName} provided in the constructor and check that it meets the requirements specified by the provided check functions.
     * @param requestParameters contains the parameter to be checked, which will be pulled from the map
     * @return a tuple with the name of the successfully checked parameter, or error information
     */
    public ApiParamTuple check(ParamsObj requestParameters) {
        try {
            ParamType param = parameterRetrievalFunction.retrieveParameter(parameterName, requestParameters);
            if (param == null)
                return ApiParamTuple.missingParameterFailure(this.parameterName);
            for (CheckFunction<ParamType> functionCheck : paramCheckFunctions) {
                CheckFunctionTuple checkTuple = functionCheck.check(param);

                // short circuit checks and return (if one check fails, it all fails)
                if (checkTuple.failed()) {
                    return checkTuple.hasFailureMessage() ? ApiParamTuple.invalidParameterFailure(this.parameterName, checkTuple.failureMessage):
                        ApiParamTuple.invalidParameterFailure(this.parameterName, "Parameter does not meet requirements.");
                }

            }
            // all checks have passed, parameter is deemed successfully checked
            return ApiParamTuple.success(this.parameterName);

        } catch (ClassCastException e) {
            return ApiParamTuple.parameterCastException(this.parameterName, e.getMessage());
        }
    }

    protected static class NoFunctionChecksProvidedException extends RuntimeException {
        public NoFunctionChecksProvidedException(String parameterName) {
            super("No function checks were provided for the parameter " + "'" + parameterName + "'. If this is intentional, just provide a function" +
                      "check that returns success (e.g., FunctionCheckReturnTuple.success()) if you want it to always pass, or failure if it should " +
                      "always fail or is a placeholder that has not yet been implemented.");
        }
    }

}
