package io.github.bhowell2.apilib;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Allows the user to return any combination of successfully checked parameters (i.e., one or
 * more individual parameters, one or more map (JsonObject) parameters, and one or more arrays of maps).
 *
 * @author Blake Howell
 */
public class ApiCustomParamCheckResult extends ApiCheckResultBase {

	public final Set<String> providedParamKeyNames;
	public final Map<String, ApiMapCheckResult> providedMapParams; // name and result of successfully checked Maps
	public final Map<String, List<ApiMapCheckResult>> providedArrayOfMapParams;       // for Maps that are the indices of arrays
	public final ApiParamError apiParamError;

	/**
	 * The custom parameter itself does not have a name, but if it checks parameters where their
	 * name should be returned they should be returned int he
	 * @param providedParamKeyNames the keyNames of the parameters that were successfully checked
	 * @param providedMapParams the keyNames and results of successfully checked ApiMapParams
	 * @param providedArrayOfMapParams if an array is being checked and each index of the array contains a Map/JsonObject
	 */
	public ApiCustomParamCheckResult(Set<String> providedParamKeyNames,
	                                 Map<String, ApiMapCheckResult> providedMapParams,
	                                 Map<String, List<ApiMapCheckResult>> providedArrayOfMapParams) {
		super((String) null);
		/*
		 * Setting these to empty sets/lists to avoid user accidentally getting null pointers when attempting to access
		 * unfortunately takes up more space than using null or even using Optional, but using optionals could be more
		 * inconvenient than just using empty collections.
		 * */
		this.providedParamKeyNames = providedParamKeyNames != null ? providedParamKeyNames : new HashSet<>(0);
		this.providedMapParams = providedMapParams != null ? providedMapParams : new HashMap<>(0);
		this.providedArrayOfMapParams = providedArrayOfMapParams != null ? providedArrayOfMapParams : new HashMap<>(0);
		this.apiParamError = null;
	}

	public ApiCustomParamCheckResult(ApiParamError apiParamError) {
		super(apiParamError);
		this.apiParamError = apiParamError;
		this.providedArrayOfMapParams = null;
		this.providedParamKeyNames = null;
		this.providedMapParams = null;
	}

	public boolean failed() {
		return apiParamError != null;
	}

	public boolean successful() {
		return apiParamError == null;
	}

	/**
	 * @param objName
	 * @return the ApiObjParamCheck or null
	 */
	public ApiMapCheckResult getMapParamCheck(String objName) {
		return this.providedMapParams.get(objName);
	}

	/* Static creation methods */

	/**
	 * In the case that it needs to be stated the check passed, but there is really no information to return other than a successful check.
	 * <p>
	 * E.g., the user wants to know whether all values in an array pass a check:
	 * 1. The user could simply implement a custom parameter and return the name of the array.
	 * 2. The user could make an ApiArrayParameter, add an ApiCustomParameter check which would just pass.
	 *
	 * @return
	 */
	public static ApiCustomParamCheckResult success() {
		return new ApiCustomParamCheckResult(null, null, null);
	}

	public static ApiCustomParamCheckResult success(String... providedParamNames) {
		return success(new HashSet<>(Arrays.asList(providedParamNames)));
	}

	public static ApiCustomParamCheckResult success(Set<String> providedParamNames) {
		return success(providedParamNames, null, null);
	}

	public static ApiCustomParamCheckResult successForMaps(Map<String, ApiMapCheckResult> providedMapParams) {
		return success(null, providedMapParams, null);
	}

	public static ApiCustomParamCheckResult successForArraysOfMaps(Map<String, List<ApiMapCheckResult>> providedArrayOfMapsParams) {
		return success(null, null, providedArrayOfMapsParams);
	}

	public static ApiCustomParamCheckResult success(Set<String> providedParamNames,
	                                                Map<String, ApiMapCheckResult> providedMapParams,
	                                                Map<String, List<ApiMapCheckResult>> providedArrayOfMapsParams) {
		return new ApiCustomParamCheckResult(providedParamNames, providedMapParams, providedArrayOfMapsParams);
	}

	public static ApiCustomParamCheckResult failure(ApiParamError apiParamError) {
		return new ApiCustomParamCheckResult(apiParamError);
	}
}
