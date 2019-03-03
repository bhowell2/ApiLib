package io.bhowell2.apilib;

/**
 * @author Blake Howell
 */
@FunctionalInterface
public interface FormatInsertFunc<ParamType, ParamsObj> {

	/**
	 * Use to put formatted values back into requestParams.
	 *
	 * @param paramName      name of parameter being formatted
	 * @param requestParams  object to put formatted parameter back into
	 * @param formattedParam the parameter that was formatted and should be put back into requestParams
	 */
	void insertIntoRequestParams(String paramName, ParamsObj requestParams, ParamType formattedParam);

}
