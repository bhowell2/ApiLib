package io.github.bhowell2.apilib;

import io.github.bhowell2.apilib.checks.ConditionalCheck;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An ApiMapParam is a parameter that contains other, named, parameters. This can be viewed
 * as a JsonObject. All parameters (strings, doubles, arrays, even nested Maps/JsonObjects,
 * etc.) exist within a Map and can be obtained by a string (the parameter's key name). Each
 * parameter will be checked by the provided {@link ApiSingleParam}, {@link ApiArrayOfMapsParam},
 * {@link ApiMapParam}, or {@link ApiCustomParam} (with the map being passed to each one so that
 * the parameter can be retrieved from it and re-inserted into the map if it is formatted).
 *
 * Required Parameters:
 * -  If the required parameter is NOT provided or the check fails it will immediately fail the
 *    ApiMapParam check.
 *
 * -  If the required parameter is provided and the check passes the parameter's name will be
 *    added to the list of successfully checked parameters. If the parameter is of type
 *    {@link ApiMapParam} or {@link ApiArrayOfMapsParam} the parameter's name will still be
 *    added to the list of successfully checked parameters, but will also return the result of
 *    that each check so the user will know what parameters were supplied and correctly checked
 *    in each of them.
 *
 * Optional Parameters:
 * -  If the optional parameter is NOT provided then it will be skipped (thus not failing this
 *    ApiMapParam check).
 *
 * -  If the optional parameter is provided but the check fails (excluding missing failure) the
 *    ApiMapParam check will fail if {@link ApiMapParam#continueOnOptionalFailure} is set to false
 *    (the default), otherwise it will ignore the failed parameter (not adding it to the list of
 *    successfully checked parameters).
 *
 * -  If the optional parameter is provided and the check succeeds the parameter's name will be
 *    added to the list of successfully checked parameters. If the parameter is of type
 *    {@link ApiMapParam} or {@link ApiArrayOfMapsParam} the parameter's name will still be
 *    added to the list of successfully checked parameters, but will also return the result of
 *    that each check so the user will know what parameters were supplied and correctly checked
 *    in each of them.
 *
 * @author Blake Howell
 */
public class ApiMapParam extends ApiParamBase<ApiMapParamCheckResult> {

	final boolean canBeNull, continueOnOptionalFailure;
	final ApiSingleParam<?>[] requiredParams;
	final ApiSingleParam<?>[] optionalParams;
	final ApiMapParam[] requiredMapParams;
	final ApiMapParam[] optionalMapParams;
	final ApiArrayOfMapsParam[] requiredArrayOfMapsParam;
	final ApiArrayOfMapsParam[] optionalArrayOfMapsParam;
	final ApiCustomParam[] requiredCustomParams;
	final ApiCustomParam[] optionalCustomParams;
	final ConditionalCheck[] conditionalChecks;

	/**
	 *
	 * @param keyName
	 * @param displayName
	 * @param canBeNull
	 * @param continueOnOptionalFailure
	 * @param requiredParams
	 * @param optionalParams
	 * @param requiredMapParams
	 * @param optionalMapParams
	 * @param requiredCustomParams
	 * @param optionalCustomParams
	 */
	public ApiMapParam(String keyName,
	                   String displayName,
	                   boolean canBeNull,
	                   boolean continueOnOptionalFailure,
	                   ApiSingleParam<?>[] requiredParams,
	                   ApiSingleParam<?>[] optionalParams,
	                   ApiMapParam[] requiredMapParams,
	                   ApiMapParam[] optionalMapParams,
	                   ApiArrayOfMapsParam[] requiredArrayOfMapsParam,
	                   ApiArrayOfMapsParam[] optionalArrayOfMapsParam,
	                   ApiCustomParam[] requiredCustomParams,
	                   ApiCustomParam[] optionalCustomParams,
	                   ConditionalCheck[] conditionalChecks) {
		super(keyName, displayName);
		this.canBeNull = canBeNull;
		this.continueOnOptionalFailure = continueOnOptionalFailure;
		this.requiredParams = requiredParams != null ? requiredParams : new ApiSingleParam[0];
		this.optionalParams = optionalParams != null ? optionalParams : new ApiSingleParam[0];
		this.requiredMapParams = requiredMapParams != null ? requiredMapParams : new ApiMapParam[0];
		this.optionalMapParams = optionalMapParams != null ? optionalMapParams : new ApiMapParam[0];
		this.requiredArrayOfMapsParam = requiredArrayOfMapsParam != null ? requiredArrayOfMapsParam : new ApiArrayOfMapsParam[0];
		this.optionalArrayOfMapsParam = optionalArrayOfMapsParam != null ? optionalArrayOfMapsParam : new ApiArrayOfMapsParam[0];
		this.requiredCustomParams = requiredCustomParams != null ? requiredCustomParams : new ApiCustomParam[0];
		this.optionalCustomParams = optionalCustomParams != null ? optionalCustomParams : new ApiCustomParam[0];
		this.conditionalChecks = conditionalChecks != null ? conditionalChecks : new ConditionalCheck[0];
	}

	private void injectParentKeyAndDisplayName(ApiParamError apiParamError) {
		if (this.keyName != null) {
			apiParamError.addParentKeyAndDisplayNames(this.keyName, this.displayName);
		}
	}

//	/**
//	 * Wraps the error if it occurred in a "named" (i.e., not a top-level/root/array index)
//	 * ApiMapParam so that the failed parameter can be traced back to the source.
//	 *
//	 * E.g.,
//	 * Take the following JsonObject:
//	 * <pre>
//	 * {@code
//	 * {
//	 *   "innerjson1": {
//	 *     "key1": "some failing value"
//	 *   },
//	 *   "innerjson2": {
//	 *     "key1": "non failing value"
//	 *   },
//	 *   "key1": "non failing value"
//	 * }
//	 * }
//	 * </pre>
//	 * If the ApiMapParam for "innerjson1" failed and just returned "'key1' failed for x reason"
//	 * it would be ambiguous whether or not it was "key1" in the root JsonObject or whether or
//	 * not it was "key1" for "innerjson1" or "innerjson2".
//	 *
//	 * This will return an {@link ApiParamError} for (keyName) "innerjson1" which contains an
//	 * ApiParamError for (keyName) "key1" (as {@link ApiParamError#childApiParamError}) so
//	 * that it can be traced. ApiParamError provides
//	 * {@link ApiParamError#getErrorMessageWithDisplayName(String, boolean)}
//	 * and {@link ApiParamError#getErrorMessageWithKeyName(String, boolean)} so that this nested
//	 * error message structure can provide a detailed error message such as "innerjson1.key1
//	 * failed for X reason".
//	 *
//	 * @param apiParamError the original ApiParamError if in the root
//	 * @return
//	 */
//	private ApiParamError wrapCheckError(ApiParamError apiParamError) {
//		if (this.keyName == null) {
//			return apiParamError;
//		}
//		return new ApiParamError(this.keyName,
//		                         this.displayName,
//		                         apiParamError.errorType,
//		                         apiParamError.errorMessage,
//		                         apiParamError,
//		                         apiParamError.exception);
//	}

	/**
	 * Use this to handle thrown errors with the various checks. This logic is repeated for all
	 * of the checks, so can just call and return this in each catch block.
	 * @param param the parameter that failed
	 * @param exception the thrown exception...
	 * @return an ApiMapParamCheckResult to be returned to the caller of {@link #check(Map)}
	 */
	private ApiMapParamCheckResult returnFailedCheckResult(ApiParamBase<?> param, Exception exception) {
		ApiParamError apiParamError = null;
		if (exception instanceof ClassCastException) {
			apiParamError = ApiParamError.cast(param, exception);
		} else {
			apiParamError = ApiParamError.exceptional(param, exception);
		}
		injectParentKeyAndDisplayName(apiParamError);
		return ApiMapParamCheckResult.failed(apiParamError);
	}

	private ApiMapParamCheckResult returnFailedCheckResult(ApiParamError failedCheckError) {
		injectParentKeyAndDisplayName(failedCheckError);
		return ApiMapParamCheckResult.failed(failedCheckError);
	}

	@Override
	@SuppressWarnings({"unchecked", "ConstantConditions"})
	public ApiMapParamCheckResult check(Map<String, Object> params) {
		/*
		 * All parameters are passed down the map containing their values. If this map does
		 * not contain a parameter name then it is a top-level/root map (i.e., the parent map
		 * or a map that is the index of an array and thus has no name).
		 * */
		try {
			Map<String, Object> mapParamToCheck;
			if (this.keyName == null) {
				mapParamToCheck = params;
			} else {
				mapParamToCheck = (Map<String, Object>) params.get(this.keyName);
			}

			if (mapParamToCheck == null) {
				if (this.canBeNull) {
					return ApiMapParamCheckResult.success(this.keyName, null, null, null);
				} else {
					return ApiMapParamCheckResult.failed(ApiParamError.missing(this));
				}
			}

			/*
			* Currently there are no formatters/re-insertions for ApiMapParam. This likely is
			* not a problem since all other parameters can re-insert their modified values
			* back into the map and maps are just references, so any sub-map that has its
			* parameters formatted and re-inserted into it will appear in the retrieve map
			* */

			Set<String> providedParamKeyNames = new HashSet<>(this.requiredParams.length);
			Map<String, ApiMapParamCheckResult> providedMapParams = new HashMap<>(this.requiredMapParams.length);
			Map<String, List<ApiMapParamCheckResult>> providedArrayOfMapsParams = new HashMap<>(this.requiredArrayOfMapsParam.length);

			// check all required first. will fail faster if something is not provided.

			/* REQUIRED PARAMS */

			for (ApiSingleParam<?> param : this.requiredParams) {
				// catch any fall-through exceptions (should never happen for this)
				try {
					ApiSingleParamCheckResult checkResult = param.check(mapParamToCheck);
					if (checkResult.failed()) {
						return returnFailedCheckResult(checkResult.apiParamError);
					}
					providedParamKeyNames.add(checkResult.keyName);
				} catch (Exception e) {
					return returnFailedCheckResult(param, e);
				}
			}
			for (ApiMapParam param : this.requiredMapParams) {
				// catch any fall-through exceptions (should never happen for this)
				try {
					ApiMapParamCheckResult checkResult = param.check(mapParamToCheck);
					if (checkResult.failed()) {
						return returnFailedCheckResult(checkResult.apiParamError);
					}
					providedParamKeyNames.add(checkResult.keyName);
					providedMapParams.put(checkResult.keyName, checkResult);
				} catch (Exception e) {
					return returnFailedCheckResult(param, e);
				}
			}

			for (ApiArrayOfMapsParam param : this.requiredArrayOfMapsParam) {
				// catch any fall-through exceptions (should never happen for this)
				try {
					ApiArrayOfMapsParamCheckResult checkResult = param.check(mapParamToCheck);
					if (checkResult.failed()) {
						return returnFailedCheckResult(checkResult.apiParamError);
					}
					providedParamKeyNames.add(checkResult.keyName);
					providedArrayOfMapsParams.put(checkResult.keyName, checkResult.arrayMapResults);
				} catch (Exception e) {
					return returnFailedCheckResult(param, e);
				}
			}

			for (ApiCustomParam param : this.requiredCustomParams) {
				try {
					ApiCustomParamCheckResult checkResult = param.check(mapParamToCheck);
					if (checkResult.failed()) {
						return returnFailedCheckResult(checkResult.apiParamError);
					}
					// keyName is used to retrieve the provided custom params check result, but is not added to providedParamNames
					providedParamKeyNames.addAll(checkResult.providedParamKeyNames);
					providedMapParams.putAll(checkResult.providedMapParams);
					providedArrayOfMapsParams.putAll(checkResult.providedArrayOfMapParams);
				} catch (Exception e) {
					// custom parameters do not have names, so error is considered in
					return returnFailedCheckResult(this, e);
				}
			}


			/* OPTIONAL PARAMS */

			for (ApiSingleParam<?> param : this.optionalParams) {
				// catch any fall-through exceptions (should never happen for this)
				try {
					ApiSingleParamCheckResult checkResult = param.check(mapParamToCheck);
					if (checkResult.failed()) {
						if (this.continueOnOptionalFailure || checkResult.apiParamError.errorType == ApiErrorType.MISSING_PARAMETER) {
							continue;
						}
						return returnFailedCheckResult(checkResult.apiParamError);
					}
					providedParamKeyNames.add(checkResult.keyName);
				} catch (Exception e) {
					return returnFailedCheckResult(param, e);
				}
			}

			for (ApiMapParam param : this.optionalMapParams) {
				// catch any fall-through exceptions (should never happen for this)
				try {
					ApiMapParamCheckResult checkResult = param.check(mapParamToCheck);
					if (checkResult.failed()) {
						if (this.continueOnOptionalFailure || checkResult.apiParamError.errorType == ApiErrorType.MISSING_PARAMETER) {
							continue;
						}
						return returnFailedCheckResult(checkResult.apiParamError);
					}
					providedParamKeyNames.add(checkResult.keyName);
					providedMapParams.put(checkResult.keyName, checkResult);
				} catch (Exception e) {
					return returnFailedCheckResult(param, e);
				}
			}

			for (ApiArrayOfMapsParam param : this.optionalArrayOfMapsParam) {
				// catch any fall-through exceptions (should never happen for this)
				try {
					ApiArrayOfMapsParamCheckResult checkResult = param.check(mapParamToCheck);
					if (checkResult.failed()) {
						if (this.continueOnOptionalFailure || checkResult.apiParamError.errorType == ApiErrorType.MISSING_PARAMETER) {
							continue;
						}
						return returnFailedCheckResult(checkResult.apiParamError);
					}
					providedParamKeyNames.add(checkResult.keyName);
					providedArrayOfMapsParams.put(checkResult.keyName, checkResult.arrayMapResults);
				} catch (Exception e) {
					return returnFailedCheckResult(param, e);
				}
			}

			for (ApiCustomParam param : this.optionalCustomParams) {
				try {
					ApiCustomParamCheckResult checkResult = param.check(mapParamToCheck);
					if (checkResult.failed()) {
						if (this.continueOnOptionalFailure || checkResult.apiParamError.errorType == ApiErrorType.MISSING_PARAMETER) {
							continue;
						}
						return returnFailedCheckResult(checkResult.apiParamError);
					}
					// keyName is used to retrieve the provided custom params check result, but is not added to providedParamNames
					providedParamKeyNames.addAll(checkResult.providedParamKeyNames);
					providedMapParams.putAll(checkResult.providedMapParams);
					providedArrayOfMapsParams.putAll(checkResult.providedArrayOfMapParams);
				} catch (Exception e) {
					return returnFailedCheckResult(this, e);
				}
			}

			for (ConditionalCheck cc : this.conditionalChecks) {
				ConditionalCheck.Result checkResult = cc.check(mapParamToCheck,
				                                               providedParamKeyNames,
				                                               providedMapParams,
				                                               providedArrayOfMapsParams);
				if (checkResult.failed()) {
					return returnFailedCheckResult(ApiParamError.conditional(checkResult.failureMessage));
				}
			}

			return ApiMapParamCheckResult.success(this.keyName,
			                                      providedParamKeyNames,
			                                      providedMapParams,
			                                      providedArrayOfMapsParams);
		} catch (ClassCastException e) {
			return ApiMapParamCheckResult.failed(ApiParamError.cast(this, e));
		} catch (Exception e) {
			return ApiMapParamCheckResult.failed(ApiParamError.exceptional(this, e));
		}
	}

}
