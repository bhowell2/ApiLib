package io.github.bhowell2.apilib;

import io.github.bhowell2.apilib.errors.ApiParamError;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Allows the user to do anything the other ApiParam implementations do
 * as well as return multiple values for each type of result (i.e., checking
 * multiple parameters at the same time and returning their names/results).
 *
 * The user should try to handle all exceptions if possible. If they are not
 * the parameter in which the error occurred will be considered the calling
 * {@link ApiMapParam} (which could be the root ApiMapParam).
 *
 * @author Blake Howell
 */
@FunctionalInterface
public interface ApiCustomParam extends ApiParam<Map<String, Object>, ApiCustomParam.Result> {

	/**
	 * Returns everything that the other Results can return as well as allowing to return
	 * a custom value with the name. This custom value can them be retrieved downstream
	 * by the user.
	 */
	class Result extends ApiParamBase.Result {

		public final Set<String> checkedKeyNames;

		public final Map<String, ApiCollectionParam.Result> checkedCollectionParams;

		public final Map<String, ApiMapParam.Result> checkedMapParams;

		/**
		 * Allows the user to return any type of object. They will have to cast this
		 * when they retrieve it from a {@link ApiMapParam.Result}.
		 */
		public final Object customValue;

		public Result(String keyName,
		              Set<String> checkedKeyNames,
		              Map<String, ApiMapParam.Result> checkedMapParams,
		              Map<String, ApiCollectionParam.Result> checkedCollectionParams,
		              Object customValue) {
			super(keyName);
			this.checkedKeyNames = checkedKeyNames;
			this.checkedCollectionParams = checkedCollectionParams;
			this.checkedMapParams = checkedMapParams;
			this.customValue = customValue;
			if (customValue != null && keyName == null) {
				throw new IllegalArgumentException("Cannot have custom value without key name, otherwise would not have " +
					                                   "a way to retrieve it");
			}
		}

		public Result(ApiParamError apiParamError) {
			super(apiParamError);
			this.checkedKeyNames = null;
			this.checkedCollectionParams = null;
			this.checkedMapParams = null;
			this.customValue = null;
		}

		public boolean hasCheckedKeyNames() {
			return this.checkedKeyNames != null;
		}

		public boolean hasCheckedCollectionParams() {
			return this.checkedCollectionParams != null;
		}

		public boolean hasCheckedMapParams() {
			return this.checkedMapParams != null;
		}

		public boolean hasCustomValue() {
			return this.customValue != null;
		}

		/* Static Creation Methods */

		/**
		 * Used when performing some type of check on one or more parameters, but the
		 * key does not need to be returned. This would be similar to using
		 * {@link ApiMapParamConditionalCheck}.
		 * @return
		 */
		public static Result success() {
			return new Result(null, null, null, null, null);
		}

		/**
		 * Used to return the names of the successfully checked parameter key names.
		 * This should not be confused with {@link #keyName}, which IS NOT MERGED
		 * @param checkedKeyNames
		 * @return
		 */
		public static Result success(String... checkedKeyNames) {
			return success(new HashSet<>(Arrays.asList(checkedKeyNames)));
		}

		public static Result success(Set<String> checkedKeyNames) {
			return new Result(null, checkedKeyNames, null, null, null);
		}

		public static Result success(Set<String> checkedKeyNames,
		                             Map<String, ApiMapParam.Result> checkedMapParams,
		                             Map<String, ApiCollectionParam.Result> checkedCollectionParams) {
			return new Result(null, checkedKeyNames, checkedMapParams, checkedCollectionParams, null);
		}

		/**
		 * Any of these can be null. However, if a custom value is supplied, then a key name
		 * must also be supplied - otherwise the user would have no way of retrieving this
		 * value.
		 *
		 * @param keyName keyName is only required when custom value is set.
		 * @param checkedKeyNames set of all successfully checked key names. will be merged
		 *                        with parent {@link ApiMapParam.Result#checkedKeyNames}.
		 *                        can be null.
		 * @param checkedMapParams
		 * @param checkedCollectionParams collections' key names to their successfully checked result.
		 *                                will be merged with parent {@link ApiMapParam.Result#checkedCollectionResults}.
		 * @param customValue
		 * @return
		 */
		public static Result success(String keyName,
		                             Set<String> checkedKeyNames,
		                             Map<String, ApiMapParam.Result> checkedMapParams,
		                             Map<String, ApiCollectionParam.Result> checkedCollectionParams,
		                             Object customValue) {
			return new Result(keyName, checkedKeyNames, checkedMapParams, checkedCollectionParams, customValue);
		}

		/**
		 *
		 * @param keyName
		 * @param customResult
		 * @param <T>
		 * @return
		 */
		public static <T> Result successWithCustomResult(String keyName, T customResult) {
			return new Result(keyName, null, null, null, customResult);
		}

		public static Result failure(ApiParamError error) {
			return new Result(error);
		}

	}

}
