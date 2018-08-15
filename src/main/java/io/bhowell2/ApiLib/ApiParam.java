package io.bhowell2.ApiLib;

/**
 *
 * @author Blake Howell
 */
public class ApiParam<ParamType, ParamsObj> {

    public final String parameterName;
    boolean canBeNull = false;
    final FormatFunc<ParamType>[] formatFuncs;
    final FormatInsertFunc<ParamType, ParamsObj> formatInsertFunc;
    final CheckFunc<ParamType>[] checkFuncs;
    final ParamRetrievalFunc<ParamType, ParamsObj> paramRetrievalFunc;
    final NullParamRetrievalCheckFunc<ParamsObj> nullRetrievalCheckFunc;

    @SuppressWarnings({"varargs", "unchecked"})
    public ApiParam(String parameterName,
                    ParamRetrievalFunc<ParamType, ParamsObj> paramRetrievalFunc,
                    CheckFunc<ParamType>[] checkFuncs) {
        this(parameterName, paramRetrievalFunc, new FormatFunc[0], null, checkFuncs, false, null);
    }

    @SuppressWarnings("varargs")
    public ApiParam(String parameterName,
                    ParamRetrievalFunc<ParamType, ParamsObj> paramRetrievalFunc,
                    FormatFunc<ParamType>[] formatFuncs,
                    FormatInsertFunc<ParamType, ParamsObj> formatInsertFunc,
                    CheckFunc<ParamType>[] checkFuncs,
                    boolean canBeNull,
                    NullParamRetrievalCheckFunc<ParamsObj> nullRetrievalCheckFunc) {
        if (paramRetrievalFunc == null) {
            throw new RuntimeException("ParameterRetrievalFunction must be provided to ApiParameter.");
        }
        if (checkFuncs == null || checkFuncs.length == 0) {
            throw new RuntimeException("No function checks were provided for the parameter " + "'" + parameterName + "'. If this is intentional, just provide a function" +
                                           "check that returns success (e.g., CheckFuncResult.success()) if you want it to always pass, or failure " +
                                           "if it should always fail or is a placeholder that has not yet been implemented. Also, you can use " +
                                           "ParamChecks.alwaysPass() or ParamChecks.alwaysFail().");
        }
        for (CheckFunc<ParamType> checkFunc : checkFuncs) {
            if (checkFunc == null) {
                throw new RuntimeException("Check funcs argument cannot have null values in array.");
            }
        }
        // only required to have format injection function IF format functions are provided
        if (formatFuncs != null && formatFuncs.length > 0 && formatInsertFunc == null) {
            throw new RuntimeException("Cannot have format functions without a format insertion function provided.");
        }
        if (canBeNull && nullRetrievalCheckFunc == null) {
            throw new RuntimeException("canBeNull cannot be set to true with no nullRetrievalCheckFunction provided");
        }
        this.parameterName = parameterName;
        this.paramRetrievalFunc = paramRetrievalFunc;
        this.formatFuncs = formatFuncs;
        this.formatInsertFunc = formatInsertFunc;
        this.checkFuncs = checkFuncs;
        this.canBeNull = canBeNull;
        this.nullRetrievalCheckFunc = nullRetrievalCheckFunc;
    }

    /**
     * Takes in the request parameters and uses functions provided to check whether or not it was successful.
     * @param requestParameters contains the parameter to be checked, will be passed to paramRetrievalFunc which will retrieve the parameter
     * @return object that tells whether or not the parameter was successfully checked or what the error was
     */
    public ApiParamCheck check(ParamsObj requestParameters) {
        try {
            ParamType param = paramRetrievalFunc.retrieveParameter(parameterName, requestParameters);
            if (param == null) {
                if (this.canBeNull && this.nullRetrievalCheckFunc.checkIfNullProvided(this.parameterName, requestParameters)) {
                    return ApiParamCheck.success(this.parameterName);
                } else {
                    // need to determine if the parameter is null or if it is
                    return ApiParamCheck.missingParameterFailure(this.parameterName);
                }
            }
            // check after making sure param is not null
            if (formatInsertFunc != null && formatFuncs != null && formatFuncs.length > 0) {
                for (FormatFunc<ParamType> formatFunc : formatFuncs) {
                    param = formatFunc.format(param);
                }
                formatInsertFunc.insertIntoRequestParams(parameterName, requestParameters, param);
            }
            // do not need to check for null here as constructor will not allow CheckFunc to be null
            for (CheckFunc<ParamType> functionCheck : checkFuncs) {
                CheckFuncResult checkTuple = functionCheck.check(param);

                // short circuit checks and return (if one check fails, it all fails)
                if (checkTuple.failed()) {
                    return checkTuple.hasFailureMessage() ? ApiParamCheck.invalidParameterFailure(this.parameterName, checkTuple.failureMessage):
                        ApiParamCheck.invalidParameterFailure(this.parameterName, "Parameter does not meet requirements.");
                }

            }
            // all checks have passed, parameter is deemed successfully checked
            return ApiParamCheck.success(this.parameterName);
        } catch (ClassCastException e) {
            return ApiParamCheck.parameterCastException(this.parameterName, e.getMessage());
        }
    }

}
