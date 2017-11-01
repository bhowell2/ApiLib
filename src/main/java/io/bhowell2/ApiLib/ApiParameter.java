package io.bhowell2.ApiLib;

/**
 * Generally, this class should be extended to reduce the amount of generics in required in a definition (e.g., if the user has the same ParamObject for all of their application (e.g. string is used to retrieve the ) then they could do: {@code public class ApiParameterExtended<ParamType> extends ApiParameter<ParamType, String, Map<String, Object>>}.
 *
 * @author Blake Howell
 */
public class ApiParameter<ParamType, ParamsObj> {

    final String parameterName;
    private final CheckFunction<ParamType>[] paramCheckFunctions;
    private final ParameterRetrievalFunction<ParamType, ParamsObj> parameterRetrievalFunction;

    public ApiParameter(String parameterName,
                        CheckFunction<ParamType>[] paramCheckFunctions,
                        ParameterRetrievalFunction<ParamType, ParamsObj> parameterRetrievalFunction) {
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
    public ApiParameterCheckTuple check(ParamsObj requestParameters) {
        try {
            ParamType param = parameterRetrievalFunction.retrieveParameter(parameterName, requestParameters);
            if (param == null)
                return ApiParameterCheckTuple.missingParameterFailure(this.parameterName);
            for (CheckFunction<ParamType> functionCheck : paramCheckFunctions) {
                FunctionCheckTuple checkTuple = functionCheck.check(param);

                // short circuit checks and return (if one check fails, it all fails)
                if (checkTuple.failed()) {
                    return checkTuple.hasFailureMessage() ? ApiParameterCheckTuple.invalidParameterFailure(this.parameterName, checkTuple.failureMessage):
                        ApiParameterCheckTuple.invalidParameterFailure(this.parameterName, "Failed to pass parameter check.");
                }

            }
            // all checks have passed, parameter is deemed successfully checked
            return ApiParameterCheckTuple.success(this.parameterName);

        } catch (ClassCastException e) {
            return ApiParameterCheckTuple.parameterCastException(this.parameterName, e.getMessage());
        }
    }

}
