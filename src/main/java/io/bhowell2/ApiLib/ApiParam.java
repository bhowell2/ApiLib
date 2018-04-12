package io.bhowell2.ApiLib;

/**
 *
 * @author Blake Howell
 */
public class ApiParam<ParamType, ParamsObj> {

    public final String parameterName;
    final FormatFunc<ParamType>[] formatFuncs;
    final FormatInsertFunc<ParamType, ParamsObj> formatInsertFunc;
    final CheckFunc<ParamType>[] checkFuncs;
    final ParamRetrievalFunc<ParamType, ParamsObj> paramRetrievalFunc;

    @SuppressWarnings({"varargs", "unchecked"})
    public ApiParam(String parameterName,
                    ParamRetrievalFunc<ParamType, ParamsObj> paramRetrievalFunc,
                    CheckFunc<ParamType>[] checkFuncs) {
        this(parameterName, paramRetrievalFunc, new FormatFunc[0], null, checkFuncs);
    }

    @SuppressWarnings("varargs")
    public ApiParam(String parameterName,
                    ParamRetrievalFunc<ParamType, ParamsObj> paramRetrievalFunc,
                    FormatFunc<ParamType>[] formatFuncs,
                    FormatInsertFunc<ParamType, ParamsObj> formatInsertFunc,
                    CheckFunc<ParamType>[] checkFuncs) {
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
        this.parameterName = parameterName;
        this.paramRetrievalFunc = paramRetrievalFunc;
        this.formatFuncs = formatFuncs;
        this.formatInsertFunc = formatInsertFunc;
        this.checkFuncs = checkFuncs;
    }

//    public CheckFunc<ParamType>[] getCheckFuncs() {
//        return checkFuncs.clone();
//    }
//
//    public FormatFunc<ParamType>[] getFormatFuncs() {
//        return formatFuncs.clone();
//    }
//
//    public FormatInsertFunc<ParamType, ParamsObj> getFormatInsertFunc() {
//        return formatInsertFunc;
//    }
//
//    public ParamRetrievalFunc<ParamType, ParamsObj> getParamRetrievalFunc() {
//        return paramRetrievalFunc;
//    }

    /**
     * Some web frameworks/platforms automatically wrap the parameters for the user in a Map. This will pull the parameter from the map, with
     * the {@link #parameterName} provided in the constructor and check that it meets the requirements specified by the provided check functions.
     * @param requestParameters contains the parameter to be checked, which will be pulled from the map
     * @return a tuple with the name of the successfully checked parameter, or error information
     */
    public ApiParamCheck check(ParamsObj requestParameters) {
        try {
            ParamType param = paramRetrievalFunc.retrieveParameter(parameterName, requestParameters);
            if (param == null)
                return ApiParamCheck.missingParameterFailure(this.parameterName);
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
