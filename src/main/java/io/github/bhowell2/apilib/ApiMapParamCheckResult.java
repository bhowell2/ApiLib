package io.github.bhowell2.apilib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Result of check for {@link ApiMapParam}. Contains all possible return types.
 * @author Blake Howell
 */
public class ApiMapParamCheckResult extends ApiCheckResultBase {

	/**
	 * Name (keyName) of all successfully checked parameters. Note: this contains all
	 * parameters in the providedArrayOfMapsParams and providedMapParams as well.
	 */
	public final Set<String> providedParamNames;

	/**
	 * Name (keyName) of the successfully checked array of maps parameters with
	 * a list of the ApiMapParamCheckResult (i.e., the successfully checked parameters)
	 * corresponding to each index of the checked array.
	 *
	 * Note: entries in the list could be null if
	 * {@link ApiArrayOfMapsParam#indicesCanBeNull} is set to true.
	 */
	public final Map<String, List<ApiMapParamCheckResult>> providedArrayOfMapsParams;

	/*
	 * if objects are nested within this object they should be added here so that their ApiObjectParamTuple
	 * may be obtained to see what parameters were provided
	 * */
	/**
	 *
	 */
	public final Map<String, ApiMapParamCheckResult> providedMapParams;

	/* if an error occurred will be set (all others, besides, potentially, parameterName will be null) */
//	public final ApiParamError apiParamError;

	public ApiMapParamCheckResult(Set<String> providedParamNames,
	                              Map<String, ApiMapParamCheckResult> providedMapParams,
	                              Map<String, List<ApiMapParamCheckResult>> providedArrayOfMapsParams) {
		this(null, providedParamNames, providedMapParams, providedArrayOfMapsParams);
	}

	public ApiMapParamCheckResult(String keyName,
	                              Set<String> providedParamNames,
	                              Map<String, ApiMapParamCheckResult> providedMapParams,
	                              Map<String, List<ApiMapParamCheckResult>> providedArrayOfMapsParams) {
		super(keyName);
		this.providedParamNames = providedParamNames != null ? providedParamNames : new HashSet<>(0);
		this.providedMapParams = providedMapParams != null ? providedMapParams : new HashMap<>(0);
		this.providedArrayOfMapsParams = providedArrayOfMapsParams != null ? providedArrayOfMapsParams : new HashMap<>(0);
	}

	public ApiMapParamCheckResult(ApiParamError apiParamError) {
		super(apiParamError);
		this.providedParamNames = null;
		this.providedMapParams = null;
		this.providedArrayOfMapsParams = null;
	}

	public boolean failed() {
		return apiParamError != null;
	}

	public boolean successful() {
		return apiParamError == null;
	}

	/**
	 * Can be used to check if any given parameter has been supplied. Every p
	 * @param parameterName
	 * @return
	 */
	public boolean containsParameter(String parameterName) {
		return providedParamNames.contains(parameterName);
	}

	/**
	 * @param objectParamName name of the object parameter that was checked
	 * @return the ApiObjParamCheck or null
	 */
	public ApiMapParamCheckResult getMapParamCheck(String objectParamName) {
		return providedMapParams.get(objectParamName);
	}

	/**
	 * Retrieves a list of ApiMapParamCheckResults for the parameter name supplied.
	 * @param paramName
	 * @return
	 */
	public List<ApiMapParamCheckResult> getArrayOfMapParamsCheck(String paramName) {
		return this.providedArrayOfMapsParams.get(paramName);
	}

	/**
	 * Converts the set containing parameter key names to a list.
	 * @return
	 */
	public List<String> getProvidedParamsAsList() {
		return new ArrayList<>(this.providedParamNames);
	}

	/* Static creation methods */

	public static ApiMapParamCheckResult success(String keyName,
	                                             Set<String> providedParamNames,
	                                             Map<String, ApiMapParamCheckResult> providedMapParams,
	                                             Map<String, List<ApiMapParamCheckResult>> providedArrayOfMapParams
	                                             ) {
		return new ApiMapParamCheckResult(keyName, providedParamNames, providedMapParams, providedArrayOfMapParams);
	}

	public static ApiMapParamCheckResult failed(ApiParamError apiParamError) {
		return new ApiMapParamCheckResult(apiParamError);
	}


}
