package io.bhowell2.ApiLib;

/**
 * @author Blake Howell
 */
@FunctionalInterface
public interface FormatInsertFunc<ParamType, ParamsObj> {

    /**
     * Use to put formatted values back into requestParams.
     * @param parameterName name of parameter being formatted
     * @param requestParams object to put formatted parameter back into
     * @param formattedParam the parameter that was formatted
     */
    void injectIntoRequestParameters(String parameterName, ParamsObj requestParams, ParamType formattedParam);

}
