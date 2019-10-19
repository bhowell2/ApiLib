package io.github.bhowell2.apilib;

import io.github.bhowell2.apilib.checks.ConditionalCheck;

import java.util.HashMap;
import java.util.HashSet;
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
public class ApiMapParam extends ApiParamBase<Map<String, Object>, ApiMapCheckResult> {

	private static void ensureHasKeyNameAndNotNull(ApiParamBase<?, ?>... params) {
		// the array itself could be null, but if it is not make sure no entries are null and nameless
		if (params != null) {
			for (ApiParamBase param : params) {
				if (param == null) {
					throw new IllegalArgumentException("Cannot add parameter to map parameter that is null");
				} else if (param.keyName == null) {
					throw new IllegalArgumentException("Cannot add parameter to map parameter that does not have a key name.");
				}
			}
		}
	}

	final boolean continueOnOptionalFailure;
	final ApiSingleParam<?>[] requiredSingleParams, optionalSingleParams;
	final ApiMapParam[] requiredMapParams, optionalMapParams;
	final ApiArrayOrListParamBase[] requiredArrayParams, optionalArrayParams;
	final ApiCustomParam<Map<String, Object>, ?>[] requiredCustomParams, optionalCustomParams;
	final ConditionalCheck[] conditionalChecks;

	/**
	 *
	 * @param keyName
	 * @param displayName
	 * @param canBeNull
	 * @param continueOnOptionalFailure
	 * @param requiredSingleParams
	 * @param optionalSingleParams
	 * @param requiredMapParams
	 * @param optionalMapParams
	 * @param requiredCustomParams
	 * @param optionalCustomParams
	 */
	public ApiMapParam(String keyName,
	                   String displayName,
	                   String invalidErrorMessage,
	                   boolean canBeNull,
	                   boolean continueOnOptionalFailure,
	                   ApiSingleParam<?>[] requiredSingleParams,
	                   ApiSingleParam<?>[] optionalSingleParams,
	                   ApiMapParam[] requiredMapParams,
	                   ApiMapParam[] optionalMapParams,
	                   ApiArrayOrListParamBase[] requiredArrayParams,
	                   ApiArrayOrListParamBase[] optionalArrayParams,
	                   ApiCustomParam<Map<String, Object>, ?>[] requiredCustomParams,
	                   ApiCustomParam<Map<String, Object>, ?>[] optionalCustomParams,
	                   ConditionalCheck[] conditionalChecks) {
		super(keyName, displayName, invalidErrorMessage, canBeNull);
		this.continueOnOptionalFailure = continueOnOptionalFailure;
		this.requiredSingleParams = requiredSingleParams;
		ensureHasKeyNameAndNotNull(requiredSingleParams);
		this.optionalSingleParams = optionalSingleParams;
		ensureHasKeyNameAndNotNull(optionalSingleParams);
		this.requiredMapParams = requiredMapParams;
		ensureHasKeyNameAndNotNull(requiredMapParams);
		this.optionalMapParams = optionalMapParams;
		ensureHasKeyNameAndNotNull(optionalMapParams);
		this.requiredArrayParams = requiredArrayParams;
		ensureHasKeyNameAndNotNull(requiredArrayParams);
		this.optionalArrayParams = optionalArrayParams;
		ensureHasKeyNameAndNotNull(optionalArrayParams);
		/*
		* Custom parameters are not restricted to having a name since they could be
		* checking multiple parameters or even none at all and simply ensuring that
		* a condition is met - though ConditionalChecks are usually preferable.
		* */
		this.requiredCustomParams = requiredCustomParams;
		this.optionalCustomParams = optionalCustomParams;
		this.conditionalChecks = conditionalChecks;
	}

	/**
	 * Wraps the error if it occurred in a "named" (i.e., not a top-level/root/array index)
	 * ApiMapParam so that the failed parameter can be traced back to the source.
	 *
	 * E.g.,
	 * Take the following JsonObject:
	 * <pre>
	 * {@code
	 * {
	 *   "innerjson1": {
	 *     "key1": "some failing value"
	 *   },
	 *   "innerjson2": {
	 *     "key1": "non failing value"
	 *   },
	 *   "key1": "non failing value"
	 * }
	 * }
	 * </pre>
	 * If the ApiMapParam for "innerjson1" failed and just returned "'key1' failed for x reason"
	 * it would be ambiguous whether or not it was "key1" in the root JsonObject or whether or
	 * not it was "key1" for "innerjson1" or "innerjson2".
	 *
	 * This will return an {@link ApiParamError} for (keyName) "innerjson1" which contains an
	 * ApiParamError for (keyName) "key1" (as {@link ApiParamError#childApiParamError}) so
	 * that it can be traced. ApiParamError provides
	 * {@link ApiParamError#getErrorMessageWithDisplayName(String, boolean)}
	 * and {@link ApiParamError#getErrorMessageWithKeyName(String, boolean)} so that this nested
	 * error message structure can provide a detailed error message such as "innerjson1.key1
	 * failed for X reason".
	 *
	 * @param apiParamError the original ApiParamError if in the root
	 * @return
	 */
	private ApiParamError wrapCheckError(ApiParamError apiParamError) {
		if (this.keyName == null) {
			return apiParamError;
		}
		return new ApiParamError(this.keyName,
		                         this.displayName,
		                         apiParamError.errorType,
		                         apiParamError.errorMessage,
		                         apiParamError.exception,
		                         apiParamError);
	}

	/**
	 * Use this to handle thrown errors with the various checks. This logic is repeated for all
	 * of the checks, so can just call and return this in each catch block.
	 * @param param the parameter that failed
	 * @param exception the thrown exception...
	 * @return an ApiMapCheckResult to be returned to the caller of {@link #check(Map)}
	 */
	private ApiMapCheckResult returnFailedCheckResult(ApiParamBase<?, ?> param, Exception exception) {
		ApiParamError apiParamError = null;
		if (exception instanceof ClassCastException) {
			apiParamError = ApiParamError.cast(param, exception);
		} else {
			apiParamError = ApiParamError.exceptional(param, exception);
		}
		return ApiMapCheckResult.failure(wrapCheckError(apiParamError));
	}

	/**
	 * Creates an {@link ApiMapCheckResult} to return an error. This will wrap the
	 * error if it this is a named map.
	 * @param failedCheckError
	 * @return
	 */
	private ApiMapCheckResult returnFailedCheckResult(ApiParamError failedCheckError) {
		return ApiMapCheckResult.failure(wrapCheckError(failedCheckError));
	}

	@SuppressWarnings({"unchecked", "ConstantConditions"})
	public ApiMapCheckResult check(Map<String, Object> params) {
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
					return ApiMapCheckResult.success(this.keyName);
				} else {
					return ApiMapCheckResult.failure(ApiParamError.missing(this));
				}
			}

			/*
			* Currently there are no formatters/re-insertions for ApiMapParam. This likely is
			* not a problem since all other parameters can re-insert their modified values
			* back into the map and maps are just references, so any sub-map that has its
			* parameters formatted and re-inserted into it will appear in the retrieve map
			* */

			Set<String> checkedKeyNames = null;
			Map<String, ApiMapCheckResult> checkedMapParams = null;
			Map<String, ApiArrayOrListCheckResult> checkedArrayParams = null;
			Map<String, Object> customValues = null;

			// check all required first. will fail faster if something is not provided.

			/* REQUIRED PARAMS */

			if (this.requiredSingleParams != null) {
				for (ApiSingleParam<?> param : this.requiredSingleParams) {
					// catch any fall-through exceptions (should never happen for this)
					try {
						ApiSingleCheckResult checkResult = param.check(mapParamToCheck);
						if (checkResult.failed()) {
							return returnFailedCheckResult(checkResult.error);
						} else if (checkedKeyNames == null) {
							checkedKeyNames = new HashSet<>(this.requiredSingleParams.length);
						}
						/*
						 * A single param will always return the key name. So, may just
						 * add it here.
						 * */
						checkedKeyNames.add(checkResult.keyName);
					} catch (Exception e) {
						return returnFailedCheckResult(param, e);
					}
				}
			}

			if (this.requiredMapParams != null) {
				for (ApiMapParam param : this.requiredMapParams) {
					// catch any fall-through exceptions (should never happen for this)
					try {
						ApiMapCheckResult checkResult = param.check(mapParamToCheck);
						if (checkResult.failed()) {
							return returnFailedCheckResult(checkResult.error);
						}
						if (checkedKeyNames == null) {
							checkedKeyNames = new HashSet<>(this.requiredMapParams.length);
						}
						if (checkedMapParams == null) {
							checkedMapParams = new HashMap<>(this.requiredMapParams.length);
						}
						/*
						* All maps within maps are required to have key-names (per constructor),
						* so do not need to check for this here.
						* */
						checkedKeyNames.add(checkResult.keyName);
						checkedMapParams.put(checkResult.keyName, checkResult);
					} catch (Exception e) {
						return returnFailedCheckResult(param, e);
					}
				}
			}

			if (this.requiredArrayParams != null) {
				for (ApiArrayOrListParamBase<?, Map<String, Object>> param : this.requiredArrayParams) {
					// catch any fall-through exceptions (should never happen for this)
					try {
						ApiArrayOrListCheckResult checkResult = param.check(mapParamToCheck);
						if (checkResult.failed()) {
							return returnFailedCheckResult(checkResult.error);
						}
						if (checkedKeyNames == null) {
							checkedKeyNames = new HashSet<>(this.requiredArrayParams.length);
						}
						if (checkedArrayParams == null) {
							checkedArrayParams = new HashMap<>(this.requiredArrayParams.length);
						}
						/*
						* All arrays within maps are required to have key names (per constructor),
						* so do not need to check for name here.
						* */
						checkedKeyNames.add(checkResult.keyName);
						/*
						 * Do not want to add if the array did not provide an inner array (which
						 * also requires either another inner array or map check results - per
						 * ApiArrayParam/ApiListParam)
						 * */
						if (checkResult.hasInnerArrayCheckResults() || checkResult.hasMapCheckResults()) {
							checkedArrayParams.put(checkResult.keyName, checkResult);
						}
					} catch (Exception e) {
						return returnFailedCheckResult(param, e);
					}
				}
			}

			if (this.requiredCustomParams != null) {
				for (ApiCustomParam<Map<String, Object>, ?> param : this.requiredCustomParams) {
					try {
						ApiCustomCheckResult<?> checkResult = param.check(mapParamToCheck);
						if (checkResult.failed()) {
							return returnFailedCheckResult(checkResult.error);
						}
						if (checkResult.hasKeyName()) {
							if (checkedKeyNames == null) {
								checkedKeyNames = new HashSet<>();
							}
							checkedKeyNames.add(checkResult.keyName);
						}
						if (checkResult.hasCheckedKeyNames()) {
							if (checkedKeyNames == null) {
								checkedKeyNames = new HashSet<>();
							}
							checkedKeyNames.addAll(checkResult.checkedKeyNames);
						}
						if (checkResult.hasCheckedArrayOfMapsParams()) {
							if (checkedArrayParams == null) {
								checkedArrayParams = new HashMap<>();
							}
							checkedArrayParams.putAll(checkResult.checkedArrayOfMapsParams);
						}
						if (checkResult.hasCheckedMapParams()) {
							if (checkedMapParams == null) {
								checkedMapParams = new HashMap<>();
							}
							checkedMapParams.putAll(checkResult.checkedMapParams);
						}
						if (checkResult.hasCustomValue()) {
							if (customValues == null) {
								customValues = new HashMap<>();
							}
							/*
							 * Must have keyName if returning a custom value or else would not be
							 * able to retrieve the custom value.
							 * */
							customValues.put(checkResult.keyName, checkResult.customValue);
						}
					} catch (Exception e) {
						// custom parameters do not have names, so error is considered in
						return returnFailedCheckResult(this, e);
					}
				}

			}

			/* OPTIONAL PARAMS */

			if (this.optionalSingleParams != null) {
				for (ApiSingleParam<?> param : this.optionalSingleParams) {
					// catch any fall-through exceptions (should never happen for this)
					try {
						ApiSingleCheckResult checkResult = param.check(mapParamToCheck);
						if (checkResult.failed()) {
							if (this.continueOnOptionalFailure || checkResult.error.errorType == ApiErrorType.MISSING_PARAMETER) {
								continue;
							}
							return returnFailedCheckResult(checkResult.error);
						} else if (checkedKeyNames == null) {
							checkedKeyNames = new HashSet<>();
						}
						/*
						 * A single param will always return the key name. So, may just
						 * add it here.
						 * */
						checkedKeyNames.add(checkResult.keyName);
					} catch (Exception e) {
						return returnFailedCheckResult(param, e);
					}
				}
			}

			if (this.optionalMapParams != null) {
				for (ApiMapParam param : this.optionalMapParams) {
					// catch any fall-through exceptions (should never happen for this)
					try {
						ApiMapCheckResult checkResult = param.check(mapParamToCheck);
						if (checkResult.failed()) {
							if (this.continueOnOptionalFailure || checkResult.error.errorType == ApiErrorType.MISSING_PARAMETER) {
								continue;
							}
							return returnFailedCheckResult(checkResult.error);
						}
						if (checkedKeyNames == null) {
							checkedKeyNames = new HashSet<>();
						}
						if (checkedMapParams == null) {
							checkedMapParams = new HashMap<>();
						}
						/*
						 * All maps within maps are required to have key-names (per constructor),
						 * so do not need to check for this here.
						 * */
						checkedKeyNames.add(checkResult.keyName);
						checkedMapParams.put(checkResult.keyName, checkResult);
					} catch (Exception e) {
						return returnFailedCheckResult(param, e);
					}
				}
			}


			if (this.optionalArrayParams != null) {
				for (ApiArrayOrListParamBase<?, Map<String, Object>> param : this.optionalArrayParams) {
					// catch any fall-through exceptions (should never happen for this)
					try {
						ApiArrayOrListCheckResult checkResult = param.check(mapParamToCheck);
						if (checkResult.failed()) {
							if (this.continueOnOptionalFailure || checkResult.error.errorType == ApiErrorType.MISSING_PARAMETER) {
								continue;
							}
							return returnFailedCheckResult(checkResult.error);
						}
						if (checkedKeyNames == null) {
							checkedKeyNames = new HashSet<>();
						}
						if (checkedArrayParams == null) {
							checkedArrayParams = new HashMap<>();
						}
						/*
						 * All arrays within maps are required to have key names (per constructor),
						 * so do not need to check for name here.
						 * */
						checkedKeyNames.add(checkResult.keyName);
						/*
						 * Do not want to add if the array did not provide an inner array (which
						 * also requires either another inner array or map check results - per
						 * ApiArrayParam/ApiListParam)
						 * */
						if (checkResult.hasInnerArrayCheckResults() || checkResult.hasMapCheckResults()) {
							checkedArrayParams.put(checkResult.keyName, checkResult);
						}
					} catch (Exception e) {
						return returnFailedCheckResult(param, e);
					}
				}
			}

			if (this.optionalCustomParams != null) {
				for (ApiCustomParam<Map<String, Object>, ?> param : this.optionalCustomParams) {
					try {
						ApiCustomCheckResult<?> checkResult = param.check(mapParamToCheck);
						if (checkResult.failed()) {
							if (this.continueOnOptionalFailure || checkResult.error.errorType == ApiErrorType.MISSING_PARAMETER) {
								continue;
							}
							return returnFailedCheckResult(checkResult.error);
						}
						if (checkResult.hasKeyName()) {
							if (checkedKeyNames == null) {
								checkedKeyNames = new HashSet<>();
							}
							checkedKeyNames.add(checkResult.keyName);
						}
						if (checkResult.hasCheckedKeyNames()) {
							if (checkedKeyNames == null) {
								checkedKeyNames = new HashSet<>();
							}
							checkedKeyNames.addAll(checkResult.checkedKeyNames);
						}
						if (checkResult.hasCheckedArrayOfMapsParams()) {
							if (checkedArrayParams == null) {
								checkedArrayParams = new HashMap<>();
							}
							checkedArrayParams.putAll(checkResult.checkedArrayOfMapsParams);
						}
						if (checkResult.hasCheckedMapParams()) {
							if (checkedMapParams == null) {
								checkedMapParams = new HashMap<>();
							}
							checkedMapParams.putAll(checkResult.checkedMapParams);
						}
						if (checkResult.hasCustomValue()) {
							if (customValues == null) {
								customValues = new HashMap<>();
							}
							/*
							 * Must have keyName if returning a custom value or else would not be
							 * able to retrieve the custom value.
							 * */
							customValues.put(checkResult.keyName, checkResult.customValue);
						}
					} catch (Exception e) {
						// custom parameters do not have names, so error is considered in
						return returnFailedCheckResult(this, e);
					}
				}
			}

			// create here to pass to conditional checks if exist
			ApiMapCheckResult thisMapCheckResult = ApiMapCheckResult.success(this.keyName,
			                                                          checkedKeyNames,
			                                                          checkedMapParams,
			                                                          checkedArrayParams,
			                                                          customValues);

			if (this.conditionalChecks != null) {
				for (ConditionalCheck cc : this.conditionalChecks) {
					ConditionalCheck.Result checkResult = cc.check(mapParamToCheck, thisMapCheckResult);
					if (checkResult.failed()) {
						return returnFailedCheckResult(ApiParamError.conditional(checkResult.failureMessage));
					}
				}
			}

			return thisMapCheckResult;
		} catch (ClassCastException e) {
			return ApiMapCheckResult.failure(ApiParamError.cast(this, e));
		} catch (Exception e) {
			return ApiMapCheckResult.failure(ApiParamError.exceptional(this, e));
		}
	}

}
//package io.github.bhowell2.apilib;
//
//import io.github.bhowell2.apilib.checks.ConditionalCheck;
//
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//
///**
// * Allows for checking any type of parameter.
// * @author Blake Howell
// */
//public class ApiMapParam extends ApiParamBase<Map<String, Object>, ApiMapCheckResult> {
//
//	final boolean continueOnOptionalFailure;
//	final ApiParam<Map<String, Object>, ?>[] requiredParams;
//	final ApiParam<Map<String, Object>, ?>[] optionalParams;
//	final ConditionalCheck[] conditionalChecks;
//
//	public ApiMapParam(String keyName,
//	                   String displayName,
//	                   String invalidErrorMessage,
//	                   boolean canBeNull,
//	                   boolean continueOnOptionalFailure,
//	                   ApiParam<Map<String, Object>, ?>[] requiredParams,
//	                   ApiParam<Map<String, Object>, ?>[] optionalParams,
//	                   ConditionalCheck[] conditionalChecks) {
//		super(keyName, displayName, invalidErrorMessage, canBeNull);
//		this.continueOnOptionalFailure = continueOnOptionalFailure;
//		this.requiredParams = requiredParams;
//		this.optionalParams = optionalParams;
//		this.conditionalChecks = conditionalChecks;
//	}
//
//	/**
//	 * If this map is named (i.e., not root level or a map in an array index) the
//	 * name of the map needs to be passed back so that the user can determine
//	 * which map the error actually occurred in.
//	 * @param error
//	 */
//	private void injectMapKeyNameAndDisplayNameIntoError(ApiParamError error) {
//		if (this.keyName != null) {
//			error.addParentKeyAndDisplayNames(this.keyName, this.displayName);
//		}
//	}
//
//	/**
//	 * @param params The map to retrieve this parameter from. For top-level/root/unnamed
//	 *               {@link ApiMapParam}s this will not retrieve anything, but use the
//	 *               supplied map itself.
//	 * @return if the parameter is provided at all it will be
//	 */
//	@SuppressWarnings("unchecked")
//	public ApiMapCheckResult check(Map<String, Object> params) {
//		Map<String, Object> paramToCheck = params;
//		if (this.keyName != null) {
//			paramToCheck = (Map<String, Object>) params.get(this.keyName);
//		}
//
//		if (paramToCheck == null) {
//			if (this.canBeNull) {
//				return ApiMapCheckResult.success(this.keyName);
//			} else {
//				return ApiMapCheckResult.failure(ApiParamError.missing(this));
//			}
//		}
//
//		MapApiMapCheckResultBuilder checkResultBuilder = MapApiMapCheckResultBuilder.builder();
//		if (this.requiredParams != null && this.requiredParams.length > 0) {
//			for (ApiParam<Map<String, Object>, ?> apiParam : this.requiredParams) {
//				ApiMapCheckResult checkResult = apiParam.check(paramToCheck);
//				if (checkResult.failed()) {
//					injectMapKeyNameAndDisplayNameIntoError(checkResult.error);
//					return checkResult;
//				}
//				checkResultBuilder.mergeCheckResult(checkResult);
//			}
//		}
//		if (this.optionalParams != null && this.optionalParams.length > 0) {
//			for (ApiParam<Map<String, Object>> apiParam : this.optionalParams) {
//				ApiMapCheckResult checkResult = apiParam.check(paramToCheck);
//				if (checkResult.failed()) {
//					if (this.continueOnOptionalFailure || checkResult.error.errorType == ApiErrorType.MISSING_PARAMETER) {
//						continue;
//					} else {
//						injectMapKeyNameAndDisplayNameIntoError(checkResult.error);
//						return checkResult;
//					}
//				}
//				checkResultBuilder.mergeCheckResult(checkResult);
//			}
//		}
//
//		ApiMapCheckResult checkResult = checkResultBuilder.setKeyName(this.keyName).build();
//		if (this.conditionalChecks != null && this.conditionalChecks.length > 0) {
//			for (int i = 0; i < this.conditionalChecks.length; i++) {
//				ConditionalCheck.Result result = this.conditionalChecks[i].check(paramToCheck, checkResult);
//				if (result.failed()) {
//					return ApiMapCheckResult.failure(ApiParamError.conditional(this.keyName,
//					                                                        this.displayName,
//					                                                        result.failureMessage));
//				}
//			}
//		}
//
//		return checkResult;
//	}
//
//	/**
//	 * Provides functionality to generate the checked key names and checked inner parameters
//	 * (map or array of maps) for the map where this builder is run. Any ApiMapCheckResult that
//	 * returns only the keyName (all other fields set to null or empty collections) is added
//	 * to the ApiMapParam's {@link ApiMapCheckResult#checkedKeyNames}, but if the keyName is set
//	 * AND other fields are set, the keyName is added to the ApiMapParam's
//	 * {@link ApiMapCheckResult#checkedKeyNames} and {@link ApiMapCheckResult#innerCheckResults}. If
//	 * NO keyName is set, but other fields are returned they will be merged. An ApiMapParam does
//	 * not set {@link ApiMapCheckResult#arrayCheckResults} at all.
//	 */
//	private static class MapApiMapCheckResultBuilder {
//
//		public static MapApiMapCheckResultBuilder builder() {
//			return new MapApiMapCheckResultBuilder();
//		}
//
//		// the maps name itself
//		String keyName;
//		// these are keys that were successfully checked for the current map
//		Set<String> checkedKeyNames;
//		// for maps or arrays of maps (within the map)
//		Map<String, ApiMapCheckResult> innerCheckResults;
//
//		public MapApiMapCheckResultBuilder() {}
//
//		/**
//		 * Used for the name of the current map.
//		 * @param keyName
//		 * @return
//		 */
//		MapApiMapCheckResultBuilder setKeyName(String keyName) {
//			this.keyName = keyName;
//			return this;
//		}
//
//		MapApiMapCheckResultBuilder addToCheckedKeyNames(String keyName) {
//			if (keyName != null) {
//				if (this.checkedKeyNames == null) {
//					this.checkedKeyNames = new HashSet<>();
//				}
//				this.checkedKeyNames.add(keyName);
//			}
//			return this;
//		}
//
//		MapApiMapCheckResultBuilder mergeCheckedKeyNames(Set<String> checkedKeyNames) {
//			if (checkedKeyNames != null) {
//				if (this.checkedKeyNames == null) {
//					this.checkedKeyNames = new HashSet<>();
//				}
//				this.checkedKeyNames.addAll(checkedKeyNames);
//			}
//			return this;
//		}
//
//		MapApiMapCheckResultBuilder addInnerCheckResult(String keyName, ApiMapCheckResult checkResult) {
//			if (keyName != null && checkResult != null) {
//				if (this.innerCheckResults == null) {
//					this.innerCheckResults = new HashMap<>();
//				}
//				this.innerCheckResults.put(keyName, checkResult);
//			}
//			return this;
//		}
//
//		MapApiMapCheckResultBuilder mergeInnerCheckResults(Map<String, ApiMapCheckResult> innerCheckResults) {
//			if (innerCheckResults != null) {
//				this.innerCheckResults.putAll(innerCheckResults);
//			}
//			return this;
//		}
//
//		/**
//		 * Checks whether or not ONLY the key name has been set. If any of the other
//		 * fields are set (even {@link ApiMapCheckResult#customReturnValue} - which is
//		 * not set by any parameters in ApiLib, but may be set by the user with their
//		 * own implementation of {@link ApiParam}
//		 *
//		 * @param checkResult
//		 * @return
//		 */
//		private static boolean onlyKeyNameIsSet(ApiMapCheckResult checkResult) {
//			return checkResult.keyName != null &&
//				checkResult.checkedKeyNames == null &&
//				checkResult.innerCheckResults == null &&
//				checkResult.arrayCheckResults == null &&
//				checkResult.customReturnValue == null;
////				!(checkResult.checkedKeyNames != null && checkResult.checkedKeyNames.size() > 0) &&
////				!(checkResult.arrayCheckResults != null && checkResult.arrayCheckResults.size() > 0) &&
////				!(checkResult.innerCheckResults != null && checkResult.innerCheckResults.size() > 0) &&
////				// the user may set this with their own implementation of ApiParam, so it should be
////				// returned if it is set.
////				checkResult.customReturnValue == null;
//		}
//
//		/**
//		 * Any ApiMapCheckResult that returns only the keyName (all other fields set to
//		 * null or empty collections) is added to the ApiMapParam's
//		 * {@link ApiMapCheckResult#checkedKeyNames}, but if the keyName is set AND other
//		 * fields are set, the keyName is added to the ApiMapParam's
//		 * {@link ApiMapCheckResult#checkedKeyNames} and {@link ApiMapCheckResult#innerCheckResults}.
//		 * If NO keyName is set, but other fields are returned they will be merged. An
//		 * ApiMapParam does not set {@link ApiMapCheckResult#arrayCheckResults} at all, so it is
//		 * ignored completely - the only way that {@link ApiMapCheckResult#arrayCheckResults} will
//		 * be obtained by something returned from ApiMapParam is via
//		 * {@link ApiMapCheckResult#innerCheckResults}.
//		 * @param checkResult
//		 * @return
//		 */
//		MapApiMapCheckResultBuilder mergeCheckResult(ApiMapCheckResult checkResult) {
//			if (onlyKeyNameIsSet(checkResult)) {
//				this.addToCheckedKeyNames(checkResult.keyName);
//			} else if (checkResult.keyName != null) {
//				this.addToCheckedKeyNames(checkResult.keyName);
//				this.addInnerCheckResult(checkResult.keyName, checkResult);
//			} else {
//				mergeCheckedKeyNames(checkResult.checkedKeyNames);
//				mergeInnerCheckResults(checkResult.innerCheckResults);
//				// again, there are no arrayCheckResults for a Map.
//			}
//			return this;
//		}
//
//		public ApiMapCheckResult build() {
//			return new ApiMapCheckResult(this.keyName, this.checkedKeyNames, this.innerCheckResults, null);
//		}
//
//	}
//
//}
//
