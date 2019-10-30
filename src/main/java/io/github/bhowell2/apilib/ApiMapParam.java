package io.github.bhowell2.apilib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An ApiMapParam is a parameter that contains other, named, parameters. This can be viewed
 * as a JsonObject. All parameters (strings, doubles, arrays, even nested Maps/JsonObjects,
 * etc.) exist within a Map and can be obtained by a string (the parameter's key name). Each
 * parameter will be checked by the provided {@link ApiSingleParam}, {@link ApiCollectionParam},
 * {@link ApiMapParam}, or {@link ApiCustomParam} (with the map being passed to each one so that
 * the parameter can be retrieved from it and re-inserted into the map if it is formatted).
 *
 * Required Parameters:
 * -  If the required parameter is NOT provided or the check fails it will immediately fail the
 *    ApiMapParam check.
 *
 * -  If the required parameter is provided and the check passes the parameter's name will be
 *    added to the list of successfully checked parameters. If the parameter is of type
 *    {@link ApiMapParam} or {@link ApiCollectionParam} the parameter's name will still be
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
 *    {@link ApiMapParam} or {@link ApiCollectionParam} the parameter's name will still be
 *    added to the list of successfully checked parameters, but will also return the result of
 *    that each check so the user will know what parameters were supplied and correctly checked
 *    in each of them.
 *
 * @author Blake Howell
 */
public class ApiMapParam extends ApiParamBase<Map<String, Object>, ApiMapParam.Result> {

	/**
	 * Creates a builder for a top-level Map or a Map that is in an array
	 * (i.e., Maps that are not retrieved from other Maps by name).
	 * canBeNull and continueOnOptionalFailure default to {@code false}. All
	 * parameters are empty and must be added as desired. If the ApiMapParam
	 * has a name (not null), then a map of the given name will be retrieved
	 * from the Map passed into {@link #check(Map)}; otherwise the Map passed
	 * into check will itself be checked.
	 *
	 * @return
	 */
	public static Builder builder() {
		return builder((String) null);
	}


	/**
	 * Creates a builder for an {@link ApiMapParam} with the given key name.
	 * canBeNull and continueOnOptionalFailure default to {@code false}. All
	 * parameters are empty and must be added as desired. If the ApiMapParam
	 * has a name, then a map of the given name must be retrievable from the
	 * Map passed into {@link #check(Map)}.
	 *
	 * @param keyName name of the map that the {@link ApiMapParam} built by this builder
	 *                should check. a value of null would not attempt to retrieve a
	 *                map from the Map input to {@link #check(Map)}, but check the input
	 *                map itself.
	 * @return a builder
	 */
	public static Builder builder(String keyName) {
		return new Builder(keyName);
	}

	/**
	 * Copies everything from the provided {@link ApiMapParam}. This includes
	 * key name, display name, can be null, continue on optional failure, and all
	 * required/optional parameters and conditional checks.
	 *
	 * @param copyFrom the parameter to copy
	 * @return a builder with pre-set fields from the copied ApiMapParam
	 */
	public static Builder builder(ApiMapParam copyFrom) {
		return new Builder(copyFrom.keyName, copyFrom);
	}

	/**
	 * Copies everything from the provided {@link ApiMapParam}. This includes
	 * display name, can be null, continue on optional failure, and all
	 * required/optional parameters and conditional checks.
	 *
	 * @param keyName the key name to use for this parameter
	 * @param copyFrom the parameter to copy
	 * @return a builder with pre-set fields from the copied ApiMapParam
	 */
	public static Builder builder(String keyName, ApiMapParam copyFrom) {
		return new Builder(keyName, copyFrom);
	}


	/*
	 *
	 * FIELDS
	 *
	 * */

	final boolean continueOnOptionalFailure;
	final ApiSingleParam<?>[] requiredSingleParams, optionalSingleParams;
	final ApiMapParam[] requiredMapParams, optionalMapParams;
	final ApiCollectionParam<Map<String, Object>, ?, ?>[] requiredCollectionParams, optionalCollectionParams;
	final ApiCustomParam[] requiredCustomParams, optionalCustomParams;
	final ApiMapParamConditionalCheck[] conditionalChecks;


	/*
	 *
	 * BUILDER
	 *
	 * */

	/**
	 * Facilitates building an {@link ApiMapParam}. Generally greatly reduces
	 * the boilerplate needed to create the map parameter and ensures that a
	 * parameter (by key name) has not already been added to another parameter
	 * type.
	 */
	public static class Builder extends ApiParamBase.Builder<ApiMapParam, Builder> {

		// default to false, avoids potential problems for user down the line
		private boolean continueOnOptionalFailure = false;

		// using maps to easily keep track of already added params (by key name)
		private Map<String, ApiSingleParam<?>> requiredSingleParams, optionalSingleParams;
		private Map<String, ApiMapParam> requiredMapParams, optionalMapParams;
		private Map<String, ApiCollectionParam<Map<String, Object>, ?, ?>> requiredCollectionParams, optionalCollectionParams;

		// custom parameters and conditional checks do not have names
		private List<ApiCustomParam> requiredCustomParams, optionalCustomParams;
		private List<ApiMapParamConditionalCheck> conditionalChecks;

		private static <T extends ApiParamBase<?,?>> Map<String, T> makeMapForCopyFromParamArray(T[] copyFromArray) {
			Map<String, T> copyFromMap = new HashMap<>();
			if (copyFromArray != null) {
				for (int i = 0; i < copyFromArray.length; i++) {
					// these should never be null, but checking for good measure...
					T param = copyFromArray[i];
					if (param != null) {
						copyFromMap.put(param.keyName, param);
					}
				}
			}
			return copyFromMap;
		}

		private static <T> List<T> makeListForCopyFromParamArray(T[] copyFromArray) {
			List<T> list = new ArrayList<>();
			if (copyFromArray != null) {
				for (int i = 0; i < copyFromArray.length; i++) {
					T param = copyFromArray[i];
					if (param != null) {
						list.add(param);
					}
				}
			}
			return list;
		}


		public Builder(String keyName) {
			super(keyName);
			this.requiredSingleParams = new HashMap<>();
			this.optionalSingleParams = new HashMap<>();
			this.requiredMapParams = new HashMap<>();
			this.optionalMapParams = new HashMap<>();
			this.requiredCollectionParams = new HashMap<>();
			this.optionalCollectionParams = new HashMap<>();
			this.requiredCustomParams = new ArrayList<>();
			this.optionalCustomParams = new ArrayList<>();
			this.conditionalChecks = new ArrayList<>();
		}

		/**
		 * Copies everything from the provided {@link ApiMapParam}. This includes the
		 * displayName, canBeNull, invalidErrorMessage, continueOnOptionalFailure, and
		 * all required/optional parameters and conditional checks.
		 * @param keyName
		 * @param copyFrom
		 */
		public Builder(String keyName, ApiMapParam copyFrom) {
			super(keyName);
			this.displayName = copyFrom.displayName;
			this.canBeNull = copyFrom.canBeNull;
			this.invalidErrorMessage = copyFrom.invalidErrorMessage;
			this.continueOnOptionalFailure = copyFrom.continueOnOptionalFailure;
			this.requiredSingleParams = makeMapForCopyFromParamArray(copyFrom.requiredSingleParams);
			this.optionalSingleParams = makeMapForCopyFromParamArray(copyFrom.optionalSingleParams);
			this.requiredMapParams = makeMapForCopyFromParamArray(copyFrom.requiredMapParams);
			this.optionalMapParams = makeMapForCopyFromParamArray(copyFrom.optionalMapParams);
			this.requiredCollectionParams = makeMapForCopyFromParamArray(copyFrom.requiredCollectionParams);
			this.optionalCollectionParams = makeMapForCopyFromParamArray(copyFrom.optionalCollectionParams);
			this.requiredCustomParams = makeListForCopyFromParamArray(copyFrom.requiredCustomParams);
			this.optionalCustomParams = makeListForCopyFromParamArray(copyFrom.optionalCustomParams);
			if (arrayIsNotNullOrEmpty(copyFrom.conditionalChecks)) {
				this.conditionalChecks.addAll(Arrays.asList(copyFrom.conditionalChecks));
			}
		}

		/**
		 * Set whether or not the {@link ApiMapParam} should continue checking if an
		 * optional parameter fails in any way other than {@link ApiErrorType#MISSING_PARAMETER}.
		 *
		 * If {@link ApiMapParam#continueOnOptionalFailure} is {@link true} an
		 * optional parameter that fails in any way will NOT fail the check and will
		 * NOT be added to the list of {@link ApiMapParam.Result#checkedKeyNames}.
		 *
		 * @param continueOnOptionalFailure whether or not to continue on an optional failure
		 * @return this builder
		 */
		public Builder setContinueOnOptionalFailure(boolean continueOnOptionalFailure) {
			this.continueOnOptionalFailure = continueOnOptionalFailure;
			return this;
		}

		// checks that the parameter by the given name has not been added to
		private void checkHasNotBeenAdded(ApiParamBase<?, ?> param) {
			// redundancy check since checkVarArgs... is used everywhere
			if (param == null) {
				throw new IllegalArgumentException("Cannot add null parameter.");
			}
			String keyName = param.keyName;
			if (keyName == null) {
				throw new IllegalArgumentException("Cannot add parameter with null key name to ApiMapParam");
			}
			if (this.requiredSingleParams.containsKey(keyName)) {
				throw new IllegalArgumentException("Parameter of keyName=" + keyName +
					                                   " has already been added to required single params.");
			} else if (this.optionalSingleParams.containsKey(keyName)) {
				throw new IllegalArgumentException("Parameter of keyName=" + keyName +
					                                   " has already been added to optional single params.");
			} else if (this.requiredCollectionParams.containsKey(keyName)) {
				throw new IllegalArgumentException("Parameter of keyName=" + keyName +
					                                   " has already been added to required array params.");
			} else if (this.optionalCollectionParams.containsKey(keyName)) {
				throw new IllegalArgumentException("Parameter of keyName=" + keyName +
					                                   " has already been added to optional array params.");
			} else if (this.requiredMapParams.containsKey(keyName)) {
				throw new IllegalArgumentException("Parameter of keyName=" + keyName +
					                                   " has already been added to required map params.");
			} else if (this.optionalMapParams.containsKey(keyName)) {
				throw new IllegalArgumentException("Parameter of keyName=" + keyName +
					                                   " has already been added to optional map params.");
			}
		}

		@SafeVarargs
		@SuppressWarnings("unchecked")
		public final Builder addRequiredParams(ApiParam<Map<String, Object>, ?>... params) {
			checkVarArgsNotNullAndValuesNotNull(params);
			for (ApiParam p : params) {
				if (p instanceof ApiSingleParam) {
					this.requiredSingleParams.put(((ApiSingleParam) p).keyName, (ApiSingleParam) p);
				} else if (p instanceof ApiCollectionParam) {
					this.requiredCollectionParams.put(((ApiCollectionParam) p).keyName,
					                                  (ApiCollectionParam<Map<String, Object>, ?, ?>) p);
				} else if (p instanceof ApiMapParam) {
					this.requiredMapParams.put(((ApiMapParam) p).keyName, (ApiMapParam) p);
				} else if (p instanceof ApiCustomParam) {
					this.requiredCustomParams.add((ApiCustomParam) p);
				} else {
					throw new IllegalArgumentException("Unknown ApiParam implementation.");
				}
			}
			return this;
		}

		@SafeVarargs
		@SuppressWarnings("unchecked")
		public final Builder addOptionalParams(ApiParam<Map<String, Object>, ?>... params) {
			checkVarArgsNotNullAndValuesNotNull(params);
			for (ApiParam p : params) {
				if (p instanceof ApiSingleParam) {
					this.optionalSingleParams.put(((ApiSingleParam) p).keyName, (ApiSingleParam) p);
				} else if (p instanceof ApiCollectionParam) {
					this.optionalCollectionParams.put(((ApiCollectionParam) p).keyName,
					                                  (ApiCollectionParam<Map<String, Object>, ?, ?>) p);
				} else if (p instanceof ApiMapParam) {
					this.optionalMapParams.put(((ApiMapParam) p).keyName, (ApiMapParam) p);
				} else if (p instanceof ApiCustomParam) {
					this.optionalCustomParams.add((ApiCustomParam) p);
				} else {
					throw new IllegalArgumentException("Unknown ApiParam implementation.");
				}
			}
			return this;
		}

		/**
		 * Removes the parameters of the given key name from the builder (both
		 * optional and required) - this includes parameters that were added by
		 * copying another ApiMapParam.
		 *
		 * Note this does not include {@link ApiCustomParam} as they do not have
		 * a key name and must be removed with {@link #removeCustomParams(ApiCustomParam...)}.
		 *
		 * @param keyNames all key names to remove
		 * @return this builder
		 */
		public final Builder removeParams(String... keyNames) {
			checkVarArgsNotNullAndValuesNotNull(keyNames);
			for (String key : keyNames) {
				this.requiredSingleParams.remove(key);
				this.optionalSingleParams.remove(key);
				this.requiredMapParams.remove(key);
				this.optionalMapParams.remove(key);
				this.requiredCollectionParams.remove(key);
				this.optionalCollectionParams.remove(key);
			}
			return this;
		}

		/**
		 * Removes the parameters from any of the already added (required or
		 * optional) parameters - this includes parameters that were added by
		 * copying another ApiMapParam.
		 *
		 * Note this does not include {@link ApiCustomParam} as they do not have
		 * a key name and must be removed with {@link #removeCustomParams(ApiCustomParam...)}.
		 *
		 * @param params the parameters to remove (will retrieve key name and remove them)
		 * @return this builder
		 */
		public Builder removeParams(ApiParamBase<?,?>... params) {
		  checkVarArgsNotNullAndValuesNotNull(params);
			return removeParams(Arrays.stream(params).map(p -> p.keyName).toArray(String[]::new));
		}

		/**
		 * Removes the parameters from the required parameters of the builder
		 * - this includes parameters that were added by copying another ApiMapParam.
		 *
		 * Note this does not include {@link ApiCustomParam} as they do not have
		 * a key name and must be removed with {@link #removeRequiredCustomParams(ApiCustomParam...)}.
		 *
		 * @param keyNames all key names to remove
		 * @return this builder
		 */
		public final Builder removeRequiredParams(String... keyNames) {
			checkVarArgsNotNullAndValuesNotNull(keyNames);
			for (String key : keyNames) {
				this.requiredSingleParams.remove(key);
				this.requiredMapParams.remove(key);
				this.requiredCollectionParams.remove(key);
			}
			return this;
		}

		/**
		 * Removes the parameters from the required parameters of the builder
		 * - this includes parameters that were added by copying another ApiMapParam.
		 *
		 * Note this does not include {@link ApiCustomParam} as they do not have
		 * a key name and must be removed with {@link #removeRequiredCustomParams(ApiCustomParam...)}.
		 *
		 * @param params the parameters to remove (will retrieve key name and remove them)
		 * @return this builder
		 */
		public final Builder removeRequiredParams(ApiParamBase<?,?>... params) {
			checkVarArgsNotNullAndValuesNotNull(params);
			return removeRequiredParams(Arrays.stream(params).map(p -> p.keyName).toArray(String[]::new));
		}


		/**
		 * Removes the parameters from the optional parameters of the builder
		 * - this includes parameters that were added by copying another ApiMapParam.
		 *
		 * Note this does not include {@link ApiCustomParam} as they do not have
		 * a key name and must be removed with {@link #removeOptionalCustomParams(ApiCustomParam...)}.
		 *
		 * @param keyNames  list of keynames to remove
		 * @return this builder
		 */
		public final Builder removeOptionalParams(String... keyNames) {
			checkVarArgsNotNullAndValuesNotNull(keyNames);
			for (String key : keyNames) {
				this.optionalSingleParams.remove(key);
				this.optionalMapParams.remove(key);
				this.optionalCollectionParams.remove(key);
			}
			return this;
		}

		/**
		 * Removes the parameters from the optional parameters of the builder
		 * - this includes parameters that were added by copying another ApiMapParam.
		 *
		 * Note this does not include {@link ApiCustomParam} as they do not have
		 * a key name and must be removed with {@link #removeOptionalCustomParams(ApiCustomParam...)}.
		 *
		 * @param params the parameters to remove (will retrieve key name and remove them)
		 * @return this builder
		 */
		public final Builder removeOptionalParams(ApiParamBase<?,?>... params) {
			checkVarArgsNotNullAndValuesNotNull(params);
			return removeOptionalParams(Arrays.stream(params).map(p -> p.keyName).toArray(String[]::new));
		}


		/**
		 * Removes a parameter from the required or optional custom parameters
		 * of the builder - this includes parameters that were added by copying
		 * another ApiMapParam.
		 *
		 * @param params the parameters to remove
		 * @return this builder
		 */
		public final Builder removeCustomParams(ApiCustomParam... params) {
			checkVarArgsNotNullAndValuesNotNull(params);
			List<ApiCustomParam> customParamsToRemove = Arrays.asList(params);
			this.requiredCustomParams.removeAll(customParamsToRemove);
			this.optionalCustomParams.removeAll(customParamsToRemove);
			return this;
		}

		/**
		 * Removes a parameter from the required custom parameters. Does nothing if
		 * the parameter has been added as optional.
		 *
		 * @param params custom parameters to remove
		 * @return this builder
		 */
		public final Builder removeRequiredCustomParams(ApiCustomParam... params) {
			checkVarArgsNotNullAndValuesNotNull(params);
			this.requiredCustomParams.removeAll(Arrays.asList(params));
			return this;
		}

		/**
		 * Removes a parameter from the optional custom parameters. Does nothing if
		 * the parameter has been added as required.
		 *
		 * @param params custom parameters to remove
		 * @return this builder
		 */
		public final Builder removeOptionalCustomParams(ApiCustomParam... params) {
			checkVarArgsNotNullAndValuesNotNull(params);
			this.optionalCustomParams.removeAll(Arrays.asList(params));
			return this;
		}

		/**
		 * Adds the parameters to the required parameters group if the key names
		 * do not already exist in any other group. If a parameter by the same
		 * key name exists in the required parameters group it will be overridden.
		 * @param params
		 * @return
		 */
		public Builder addRequiredSingleParams(ApiSingleParam<?>... params) {
			checkVarArgsNotNullAndValuesNotNull(params);
			if (this.requiredSingleParams == null && params.length > 0) {
				this.requiredSingleParams = new HashMap<>();
			}
			for (ApiSingleParam p : params) {
				checkHasNotBeenAdded(p);
				this.requiredSingleParams.put(p.keyName, p);
			}
			return this;
		}

		public Builder addOptionalSingleParams(ApiSingleParam<?>... params) {
			checkVarArgsNotNullAndValuesNotNull(params);
			if (this.optionalSingleParams == null && params.length > 0) {
				this.optionalSingleParams = new HashMap<>();
			}
			for (ApiSingleParam p : params) {
				checkHasNotBeenAdded(p);
				this.optionalSingleParams.put(p.keyName, p);
			}
			return this;
		}

		public Builder addRequiredMapParams(ApiMapParam... params) {
			checkVarArgsNotNullAndValuesNotNull(params);
			if (this.requiredMapParams == null && params.length > 0) {
				this.requiredMapParams = new HashMap<>();
			}
			for (ApiMapParam p : params) {
				checkHasNotBeenAdded(p);
				this.requiredMapParams.put(p.keyName, p);
			}
			return this;
		}

		public Builder addOptionalMapParams(ApiMapParam... params) {
			checkVarArgsNotNullAndValuesNotNull(params);
			if (this.optionalMapParams == null && params.length > 0) {
				this.optionalMapParams = new HashMap<>();
			}
			for (ApiMapParam p : params) {
				checkHasNotBeenAdded(p);
				this.optionalMapParams.put(p.keyName, p);
			}
			return this;
		}

		@SafeVarargs
		public final Builder addRequiredCollectionParams(ApiCollectionParam<Map<String, Object>, ?, ?>... params) {
			checkVarArgsNotNullAndValuesNotNull(params);
			if (this.requiredCollectionParams == null && params.length > 0) {
				this.requiredCollectionParams = new HashMap<>();
			}
			for (ApiCollectionParam<Map<String, Object>, ?, ?> p : params) {
				checkHasNotBeenAdded(p);
				this.requiredCollectionParams.put(p.keyName, p);
			}
			return this;
		}

		@SafeVarargs
		public final Builder addOptionalCollectionParams(ApiCollectionParam<Map<String, Object>, ?, ?>... params) {
			checkVarArgsNotNullAndValuesNotNull(params);
			if (this.optionalCollectionParams == null && params.length > 0) {
				this.optionalCollectionParams = new HashMap<>();
			}
			for (ApiCollectionParam<Map<String, Object>, ?, ?> p : params) {
				checkHasNotBeenAdded(p);
				this.optionalCollectionParams.put(p.keyName, p);
			}
			return this;
		}

		public final Builder addRequiredCustomParams(ApiCustomParam... params) {
			checkVarArgsNotNullAndValuesNotNull(params);
			if (this.requiredCustomParams == null) {
				this.requiredCustomParams = new ArrayList<>();
			}
			/*
			* Need to check whether or not it has already been added since this is
			* a list and not a map where the other parameters are identified by key name.
			* */
			for (ApiCustomParam p : params) {
				if (!this.requiredCustomParams.contains(p)) {
					this.requiredCustomParams.add(p);
				}
			}
			return this;
		}

		public final Builder addOptionalCustomParams(ApiCustomParam... params) {
			checkVarArgsNotNullAndValuesNotNull(params);
			if (this.optionalCustomParams == null) {
				this.optionalCustomParams = new ArrayList<>();
			}
			/*
			 * Need to check whether or not it has already been added since this is
			 * a list and not a map where the other parameters are identified by key name.
			 * */
			for (ApiCustomParam p : params) {
				if (!this.optionalCustomParams.contains(p)) {
					this.optionalCustomParams.add(p);
				}
			}
			return this;
		}

		public Builder addConditionalChecks(ApiMapParamConditionalCheck... conditionalChecks) {
			checkVarArgsNotNullAndValuesNotNull(conditionalChecks);
			if (this.conditionalChecks == null) {
				this.conditionalChecks = new ArrayList<>();
			}
			/*
			 * Need to check whether or not it has already been added since this is
			 * a list and not a map where the other parameters are identified by key name.
			 * */
			for (ApiMapParamConditionalCheck check : conditionalChecks) {
				if (!this.conditionalChecks.contains(check)) {
					this.conditionalChecks.add(check);
				}
			}
			return this;
		}

		public Builder removeConditionalChecks(ApiMapParamConditionalCheck... conditionalChecks) {
			checkVarArgsNotNullAndValuesNotNull(conditionalChecks);
			this.conditionalChecks.removeAll(Arrays.asList(conditionalChecks));
		  return this;
		}

		private static <T extends ApiParamBase<?, ?>> Map<String, T> mergeCopyFromParams(T[] copyFromParams,
		                                                                                 Map<String, T> builderMap) {
			if (copyFromParams != null) {
				Map<String, T> copyMap = new HashMap<>();
				for (T p : copyFromParams) {
					copyMap.put(p.keyName, p);
				}
				copyMap.putAll(builderMap);
				return copyMap;
			}
			// do nothing, just return builder's map
			return builderMap;
		}

		private static <T> List<T> mergeCopyFromArray(T[] copyFromList, List<T> builderList) {
			if (copyFromList != null) {
				List<T> copyList = new ArrayList<>(Arrays.asList(copyFromList));
				copyList.addAll(builderList);
				return copyList;
			}
			// do nothing, just return builder's list
			return builderList;
		}


		@SuppressWarnings("unchecked")
		@Override
		public ApiMapParam build() {
			return new ApiMapParam(this);
		}
	}

	/*
	 *
	 * RESULT
	 *
	 * */

	public static class Result extends ApiParamBase.Result {

		/**
		 * Name (keyName) of all successfully checked parameters.
		 * Note: this contains all parameter's key names in the
		 * {@link #checkedCollectionResults} and {@link #checkedMapResults},
		 * but does not contain the keyName of a custom param.
		 */
		public final Set<String> checkedKeyNames;

		/**
		 * Name (keyName) of the successfully checked map parameters. These
		 * are nested maps within this checked map parameter. These key names
		 * also appear in {@link #checkedKeyNames}.
		 */
		public final Map<String, Result> checkedMapResults;

		/**
		 * Name (keyName) of the successfully checked arrays/lists of maps.
		 * If the innermost {@link ApiCollectionParam.Result} does not contain
		 * {@link ApiCollectionParam.Result#mapResults} then it will
		 * be ignored and only appear in {@link #checkedKeyNames}.
		 */
		public final Map<String, ApiCollectionParam.Result> checkedCollectionResults;

		/**
		 * If {@link ApiCustomParam.Result} returns a customValue, it will
		 * be available here. This requires the ApiCustomParam.Result return
		 * a key name (enforced by constructor).
		 */
		public final Map<String, Object> customValues;

		public Result(String keyName,
		              Set<String> checkedKeyNames,
		              Map<String, Result> checkedMapResults,
		              Map<String, ApiCollectionParam.Result> checkedCollectionResults,
		              Map<String, Object> customValues) {
			super(keyName);
			this.checkedKeyNames = checkedKeyNames;
			this.checkedMapResults = checkedMapResults;
			this.checkedCollectionResults = checkedCollectionResults;
			this.customValues = customValues;
		}

		public Result(ApiParamError error) {
			super(error);
			this.checkedKeyNames = null;
			this.checkedMapResults = null;
			this.checkedCollectionResults = null;
			this.customValues = null;
		}

		/**
		 * Whether or not any key names were provided.
		 * @return
		 */
		public boolean hasCheckedKeyNames() {
			return this.checkedKeyNames != null;
		}

		/**
		 * Converts the set containing parameter key names to a list.
		 * @return
		 */
		public List<String> getCheckedParamsAsList() {
			return this.checkedKeyNames != null ? new ArrayList<>(this.checkedKeyNames) : new ArrayList<>();
		}

		/**
		 * Can be used to check if any given parameter has been supplied. Every p
		 * @param parameterName
		 * @return
		 */
		public boolean containsParameter(String parameterName) {
			return this.checkedKeyNames != null && checkedKeyNames.contains(parameterName);
		}

		/* MAP */

		public boolean hasCheckedMapResults() {
			return this.checkedMapResults != null;
		}

		/**
		 * @param mapKeyName name of the object parameter that was checked
		 * @return the result or null
		 */
		public Result getMapResult(String mapKeyName) {
			return this.checkedMapResults != null ? checkedMapResults.get(mapKeyName) : null;
		}

		/* COLLECTION */

		public boolean hasCheckedCollectionResults() {
			return this.checkedCollectionResults != null;
		}

		public ApiCollectionParam.Result getCollectionResult(String collectionKeyName) {
			return this.checkedCollectionResults != null ? checkedCollectionResults.get(collectionKeyName) : null;
		}

		/* CUSTOM */

		public boolean hasCustomValues() {
			return this.customValues != null;
		}

		public Object getCustomValue(String customKeyName) {
			return this.customValues != null ? customValues.get(customKeyName) : null;
		}

		/* Static creation methods */

		/**
		 * When the Map is null or has no parameters itself.
		 * @param keyName
		 * @return
		 */
		public static Result success(String keyName) {
			return new Result(keyName, null, null, null, null);
		}


		public static Result success(String keyName, Set<String> checkedKeyNames) {
			return new Result(keyName, checkedKeyNames, null, null, null);
		}

		public static Result success(String keyName,
		                             Set<String> checkedKeyNames,
		                             Map<String, Result> checkedMapResults,
		                             Map<String, ApiCollectionParam.Result> checkedCollectionResults,
		                             Map<String, Object> customValues) {
			return new Result(keyName, checkedKeyNames, checkedMapResults, checkedCollectionResults, customValues);
		}

		public static Result failure(ApiParamError apiParamError) {
			return new Result(apiParamError);
		}

	}

	private static boolean mapIsNotNullOrEmpty(Map<?, ?> map) {
		return map != null && map.size() > 0;
	}

	/*
	 *
	 * IMPLEMENTATION
	 *
	 * */

	@SuppressWarnings("unchecked")
	private ApiMapParam(Builder builder) {
		super(builder);
		this.continueOnOptionalFailure = builder.continueOnOptionalFailure;
		this.requiredSingleParams = mapIsNotNullOrEmpty(builder.requiredSingleParams)
			? builder.requiredSingleParams.values().toArray(new ApiSingleParam[0])
			: null;
		this.optionalSingleParams = mapIsNotNullOrEmpty(builder.optionalSingleParams)
			? builder.optionalSingleParams.values().toArray(new ApiSingleParam[0])
			: null;
		this.requiredMapParams = mapIsNotNullOrEmpty(builder.requiredMapParams)
			? builder.requiredMapParams.values().toArray(new ApiMapParam[0])
			: null;
		this.optionalMapParams = mapIsNotNullOrEmpty(builder.optionalMapParams)
			? builder.optionalMapParams.values().toArray(new ApiMapParam[0])
			: null;
		this.requiredCollectionParams = mapIsNotNullOrEmpty(builder.requiredCollectionParams)
			? builder.requiredCollectionParams.values().toArray(new ApiCollectionParam[0])
			: null;
		this.optionalCollectionParams = mapIsNotNullOrEmpty(builder.optionalCollectionParams)
			? builder.optionalCollectionParams.values().toArray(new ApiCollectionParam[0])
			: null;
		this.requiredCustomParams = listIsNotNullOrEmpty(builder.requiredCustomParams)
			? builder.requiredCustomParams.toArray(new ApiCustomParam[0])
			: null;
		this.optionalCustomParams = listIsNotNullOrEmpty(builder.optionalCustomParams)
			? builder.optionalCustomParams.toArray(new ApiCustomParam[0])
			: null;
		this.conditionalChecks = listIsNotNullOrEmpty(builder.conditionalChecks)
			? builder.conditionalChecks.toArray(new ApiMapParamConditionalCheck[0])
			: null;
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
	 * ApiParamError for (keyName) "key1" (as {@link ApiParamError#childParamError}) so
	 * that it can be traced to the exact position of failure.
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
	 * @return an Result to be returned to the caller of {@link #check(Map)}
	 */
	private Result returnFailedCheckResult(ApiParamBase<?, ?> param, Exception exception) {
		ApiParamError apiParamError = null;
		if (exception instanceof ClassCastException) {
			apiParamError = ApiParamError.cast(param, exception);
		} else {
			apiParamError = ApiParamError.exceptional(param, exception);
		}
		return Result.failure(wrapCheckError(apiParamError));
	}

	/**
	 * Creates an {@link Result} to return an error. This will wrap the
	 * error if it this is a named map.
	 * @param failedCheckError
	 * @return
	 */
	private Result returnFailedCheckResult(ApiParamError failedCheckError) {
		return Result.failure(wrapCheckError(failedCheckError));
	}

	@SuppressWarnings({"unchecked", "ConstantConditions"})
	public Result check(Map<String, Object> params) {
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
					return Result.success(this.keyName);
				} else {
					return Result.failure(ApiParamError.missing(this));
				}
			}

			/*
			 * Currently there are no formatters/re-insertions for ApiMapParam. This likely is
			 * not a problem since all other parameters can re-insert their modified values
			 * back into the map and maps are just references, so any sub-map that has its
			 * parameters formatted and re-inserted into it will appear in the retrieve map
			 * */

			Set<String> checkedKeyNames = null;
			Map<String, Result> checkedMapResults = null;
			Map<String, ApiCollectionParam.Result> checkedCollectionResults = null;
			Map<String, Object> customValues = null;

			// check all required first. will fail faster if something is not provided.

			/* REQUIRED PARAMS */

			if (this.requiredSingleParams != null) {
				for (ApiSingleParam<?> param : this.requiredSingleParams) {
					// catch any fall-through exceptions (should never happen for this)
					try {
						ApiSingleParam.Result checkResult = param.check(mapParamToCheck);
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
						Result checkResult = param.check(mapParamToCheck);
						if (checkResult.failed()) {
							return returnFailedCheckResult(checkResult.error);
						}
						if (checkedKeyNames == null) {
							checkedKeyNames = new HashSet<>(this.requiredMapParams.length);
						}
						if (checkedMapResults == null) {
							checkedMapResults = new HashMap<>(this.requiredMapParams.length);
						}
						/*
						 * All maps within maps are required to have key-names (per constructor),
						 * so do not need to check for this here.
						 * */
						checkedKeyNames.add(checkResult.keyName);
						checkedMapResults.put(checkResult.keyName, checkResult);
					} catch (Exception e) {
						return returnFailedCheckResult(param, e);
					}
				}
			}

			if (this.requiredCollectionParams != null) {
				for (ApiCollectionParam<Map<String, Object>, ?, ?> param : this.requiredCollectionParams) {
					// catch any fall-through exceptions (should never happen for this)
					try {
						ApiCollectionParam.Result checkResult = param.check(mapParamToCheck);
						if (checkResult.failed()) {
							return returnFailedCheckResult(checkResult.error);
						}
						if (checkedKeyNames == null) {
							checkedKeyNames = new HashSet<>(this.requiredCollectionParams.length);
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
						if (checkResult.hasInnerCollectionResults() || checkResult.hasMapResults()) {
							if (checkedCollectionResults == null) {
								checkedCollectionResults = new HashMap<>(this.requiredCollectionParams.length);
							}
							checkedCollectionResults.put(checkResult.keyName, checkResult);
						}
					} catch (Exception e) {
						return returnFailedCheckResult(param, e);
					}
				}
			}

			if (this.requiredCustomParams != null) {
				for (ApiCustomParam param : this.requiredCustomParams) {
					try {
						ApiCustomParam.Result checkResult = param.check(mapParamToCheck);
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
						if (checkResult.hasCheckedCollectionParams()) {
							if (checkedCollectionResults == null) {
								checkedCollectionResults = new HashMap<>();
							}
							checkedCollectionResults.putAll(checkResult.checkedCollectionParams);
						}
						if (checkResult.hasCheckedMapParams()) {
							if (checkedMapResults == null) {
								checkedMapResults = new HashMap<>();
							}
							checkedMapResults.putAll(checkResult.checkedMapParams);
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
						// custom parameters do not have names, so error is considered to be with the map itself
						return returnFailedCheckResult(this, e);
					}
				}

			}

			/* OPTIONAL PARAMS */

			if (this.optionalSingleParams != null) {
				for (ApiSingleParam<?> param : this.optionalSingleParams) {
					// catch any fall-through exceptions (should never happen for this)
					try {
						ApiSingleParam.Result checkResult = param.check(mapParamToCheck);
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
						Result checkResult = param.check(mapParamToCheck);
						if (checkResult.failed()) {
							if (this.continueOnOptionalFailure || checkResult.error.errorType == ApiErrorType.MISSING_PARAMETER) {
								continue;
							}
							return returnFailedCheckResult(checkResult.error);
						}
						if (checkedKeyNames == null) {
							checkedKeyNames = new HashSet<>();
						}
						if (checkedMapResults == null) {
							checkedMapResults = new HashMap<>();
						}
						/*
						 * All maps within maps are required to have key-names (per constructor),
						 * so do not need to check for this here.
						 * */
						checkedKeyNames.add(checkResult.keyName);
						checkedMapResults.put(checkResult.keyName, checkResult);
					} catch (Exception e) {
						return returnFailedCheckResult(param, e);
					}
				}
			}


			if (this.optionalCollectionParams != null) {
				for (ApiCollectionParam<Map<String, Object>, ?, ?> param : this.optionalCollectionParams) {
					// catch any fall-through exceptions (should never happen for this)
					try {
						ApiCollectionParam.Result checkResult = param.check(mapParamToCheck);
						if (checkResult.failed()) {
							if (this.continueOnOptionalFailure || checkResult.error.errorType == ApiErrorType.MISSING_PARAMETER) {
								continue;
							}
							return returnFailedCheckResult(checkResult.error);
						}
						if (checkedKeyNames == null) {
							checkedKeyNames = new HashSet<>();
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
						if (checkResult.hasInnerCollectionResults() || checkResult.hasMapResults()) {
							if (checkedCollectionResults == null) {
								checkedCollectionResults = new HashMap<>();
							}
							checkedCollectionResults.put(checkResult.keyName, checkResult);
						}
					} catch (Exception e) {
						return returnFailedCheckResult(param, e);
					}
				}
			}

			if (this.optionalCustomParams != null) {
				for (ApiCustomParam param : this.optionalCustomParams) {
					try {
						ApiCustomParam.Result checkResult = param.check(mapParamToCheck);
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
						if (checkResult.hasCheckedCollectionParams()) {
							if (checkedCollectionResults == null) {
								checkedCollectionResults = new HashMap<>();
							}
							checkedCollectionResults.putAll(checkResult.checkedCollectionParams);
						}
						if (checkResult.hasCheckedMapParams()) {
							if (checkedMapResults == null) {
								checkedMapResults = new HashMap<>();
							}
							checkedMapResults.putAll(checkResult.checkedMapParams);
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
						// custom parameters do not have names, so error is considered to be with the map itself
						return returnFailedCheckResult(this, e);
					}
				}
			}

			// create here to pass to conditional checks if exist
			Result thisMapCheckResult = Result.success(this.keyName,
			                                           checkedKeyNames,
			                                           checkedMapResults,
			                                           checkedCollectionResults,
			                                           customValues);

			if (this.conditionalChecks != null) {
				for (ApiMapParamConditionalCheck cc : this.conditionalChecks) {
					ApiMapParamConditionalCheck.Result checkResult = cc.check(mapParamToCheck, thisMapCheckResult);
					if (checkResult.failed()) {
						return returnFailedCheckResult(checkResult.error);
					}
				}
			}

			return thisMapCheckResult;
		} catch (ClassCastException e) {
			return Result.failure(ApiParamError.cast(this, e));
		} catch (Exception e) {
			return Result.failure(ApiParamError.exceptional(this, e));
		}
	}

}
