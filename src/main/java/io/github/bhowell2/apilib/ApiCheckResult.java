//package io.github.bhowell2.apilib;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
///**
// * The same return value is used for all {@link ApiParam}s, which allows for
// * any implementation to return anything. This library returns specific fields
// * for each of its implementations, but the user can write their own
// * implementation and return a value in every field if they desire and deem
// * it necessary.
// *
// * Any field can be null, so the user needs to be careful when accessing them.
// *
// * @author Blake Howell
// */
//public class ApiCheckResult {
//
//	/**
//	 * The key name of this parameter. May be null if top level Map/JsonObject
//	 * or is the index of an array of Maps/JsonObjects.
//	 */
//	public final String keyName;
//
//	/**
//	 * For returning multiple keyNames. Used by a container parameter such
//	 * as a Map or JsonObject to return its successfully checked parameters.
//	 */
//	public final Set<String> checkedKeyNames;
//
//	/**
//	 * For returning ApiCheckResults for a given key name. Used for nested
//	 * container parameters such as a Map, JsonObject, or Array.
//	 */
//	public final Map<String, ApiCheckResult> innerCheckResults;
//
//	/**
//	 * For returning the ApiCheckResult of an array of Maps/JsonObjects. Allows
//	 * for arbitrarily nested array depth.
//	 *
//	 * E.g.,
//	 * An array of arrays of JSON.
//	 * <pre>
//	 * {@code
//	 * {
//	 *   "array": [
//	 *      // 1st array within array
//	 *      [
//	 *        {
//	 *          param1: "pass",
//	 *          param2: "pass",
//	 *        },
//	 *        {
//	 *          param1: "pass",
//	 *          param2: "pass"
//	 *        }
//	 *      ],
//	 *      // 2nd array within array
//	 *      [
//	 *        {
//	 *          param2: "pass"
//	 *        }
//	 *      ]
//	 *   ]
//	 * }
//	 *
//	 * // Will result in an ApiCheckResult that looks like this (JSON format):
//	 *
//	 * {  // ApiCheckResult (base)
//	 *  keyName: null,            // top level JSON, so has no name
//	 *  keyNames: ["array"],      // successfully checked the array key name
//	 *  checkResults:
//	 *  {
//	 *    array: {  // ApiCheckResult
//	 *      keyName: "array",
//	 *      keyNames: null,       // has no keys within it, is just indices of an array
//	 *      checkResults: null,   // has no keys within it that are container parameters
//	 *      arrayCheckResults:
//	 *      [
//	 *        { // ArrayCheckResult
//	 *          keyName: null,        // has no name as this is the index of an array (within an array)
//	 *          keyNames: null,       // has no keys since it is an array
//	 *          checkResults: null,   // has no keys within
//	 *          arrayCheckResults:
//	 *          [
//	 *            { // ArrayCheckResult
//	 *              keyName: null,     // array index with value JsonObject, so has no name
//	 *              keyNames: ["param1", "param2"]      // param1 and param2 were successfully checked for the JSON of [0, 0]
//	 *              checkResults: null,       // there are no nested container parameters within
//	 *              arrayCheckResults: null   // no arrays within arrays
//	 *            },
//	 *            { // ArrayCheckResult
//	 *              keyName: null,     // array index with value JsonObject, so has no name
//	 *              keyNames: ["param1", "param2"]      // param1 and param2 were successfully checked for the JSON of [0, 1]
//	 *              checkResults: null,    // there are no nested container parameters within
//	 *              arrayCheckResults: null
//	 *            },
//	 *          ],
//	 *          [
//	 *            { // ArrayCheckResult
//	 *              keyName: null,     // array index with value JsonObject, so has no name
//	 *              keyNames: ["param2"]      // param2 was successfully checked for the JSON of [1, 0]
//	 *              checkResults: null,    // there are no nested container parameters within
//	 *              arrayCheckResults: null
//	 *            },
//	 *          ]
//	 *        }
//	 *      ]
//	 *    }
//	 *  }
//	 *  arrayCheckResults: null   // is top-level JSON, not an array
//	 * }
//	 * }
//	 * </pre>
//	 */
//	public final List<ApiCheckResult> arrayCheckResults;
//
//	/**
//	 * ApiLib does not use this, but if the user implements their own {@link ApiParam}
//	 * they may want to return their own value.
//	 */
//	public final Object customReturnValue;
//
//	public final ApiParamError error;
//
//	public ApiCheckResult(String keyName,
//	                      Set<String> checkedKeyNames,
//	                      Map<String, ApiCheckResult> innerCheckResults,
//	                      List<ApiCheckResult> arrayCheckResults) {
//		this(keyName, checkedKeyNames, innerCheckResults, arrayCheckResults, null);
//	}
//
//	public ApiCheckResult(String keyName,
//	                      Set<String> checkedKeyNames,
//	                      Map<String, ApiCheckResult> innerCheckResults,
//	                      List<ApiCheckResult> arrayCheckResults,
//	                      Object customReturnValue) {
//		this.keyName = keyName;
//		this.checkedKeyNames = checkedKeyNames;
//		this.innerCheckResults = innerCheckResults;
//		this.arrayCheckResults = arrayCheckResults;
//		this.customReturnValue = customReturnValue;
//		this.error = null;
//	}
//
//	public ApiCheckResult(ApiParamError error) {
//		this.error = error;
//		this.keyName = null;
//		this.checkedKeyNames = null;
//		this.innerCheckResults = null;
//		this.arrayCheckResults = null;
//		this.customReturnValue = null;
//	}
//
//	public boolean successful() {
//		return this.error == null;
//	}
//
//	public boolean failed() {
//		return this.error != null;
//	}
//
//	/**
//	 * @return whether or not {@link ApiCheckResult#keyName} has been set (is not null) for this check result.
//	 */
//	public boolean hasKeyName() {
//		return this.keyName != null;
//	}
//
//	/**
//	 * Returns the key name for this check result. The top level ApiCheckResult
//	 * (for {@link ApiMapParam}) will not have a key name (is null) as the top level
//	 * {@link ApiMapParam} does not have a name. Can use {@link #hasKeyName()} to
//	 * ensure this will not be null if desired.
//	 *
//	 * @return the key name for this ApiCheckResult - may be null
//	 */
//	public String getKeyName() {
//		return this.keyName;
//	}
//
//	/**
//	 * @return whether or not {@link ApiCheckResult#checkedKeyNames} has been set (is not null) for this check result.
//	 */
//	public boolean hasCheckedKeyNames() {
//		return this.checkedKeyNames != null;
//	}
//
//	/**
//	 * Returns the set of key names that were checked by the {@link ApiParam} that
//	 * returned this check result. When using only the library's {@link ApiParam}
//	 * implementations, this will also contain key names that are in
//	 * {@link #innerCheckResults}; however, innerCheckResults will also be set if
//	 * the child {@link ApiParam} returns an {@link ApiCheckResult} with fields other
//	 * than {@link ApiCheckResult#keyName} set. Can use {@link #hasCheckedKeyNames()}
//	 * to ensure this will not be null if desired.
//	 *
//	 * @return a set of key names - may be null
//	 */
//	public Set<String> getCheckedKeyNames() {
//		return this.checkedKeyNames;
//	}
//
//	/**
//	 * Checks whether or not the keyName is in the {@link #checkedKeyNames} set.
//	 * @param keyName
//	 * @return true if the key name exists in the set. false if set is null or the key does not exists.
//	 */
//	public boolean containsKeyName(String keyName) {
//		return this.checkedKeyNames != null && this.checkedKeyNames.contains(keyName);
//	}
//
//	/**
//	 * There may be cases where the user desires a list of the key names rather
//	 * than a set, this will return that. An empty set is returned if the key
//	 * names are null.
//	 * @return list of strings of the {@link #checkedKeyNames} set.
//	 */
//	public List<String> getCheckedKeyNamesAsList() {
//		return this.checkedKeyNames == null ? new ArrayList<>() : new ArrayList<>(this.checkedKeyNames);
//	}
//
//	/**
//	 * @return whether or not {@link ApiCheckResult#innerCheckResults} has been set (is not null) for this check result
//	 */
//	public boolean hasInnerCheckResults() {
//		return this.innerCheckResults != null;
//	}
//
//	/**
//	 * Returns a map of key names to {@link ApiCheckResult}. When using only the
//	 * library's {@link ApiParam} implementations this will contain any key names
//	 * that are also in {@link #checkedKeyNames} AND also returned more set fields
//	 * than just {@link #keyName} (e.g., {@link #checkedKeyNames}). This allows
//	 * for embedded Maps/JsonObjects. Can use {@link #hasInnerCheckResults()} to
//	 * ensure this will not be null if desired.
//	 *
//	 * @return a map of key names to check results - may be null.
//	 */
//	public Map<String, ApiCheckResult> getInnerCheckResults() {
//		return this.innerCheckResults;
//	}
//
//	/**
//	 * @return whether or not {@link ApiCheckResult#arrayCheckResults} has been set (is not null) for this check result
//	 */
//	public boolean hasArrayCheckResults() {
//		return this.arrayCheckResults != null;
//	}
//
//	/**
//	 * When using the library's {@link ApiParam} implementations this is only used for
//	 * arrays that have maps as their indices's values. This can be used for arbitrary
//	 * depth arrays. This returns a list of {@link ApiCheckResult}s for each index in
//	 * the array, so the user will know what keys were provided (and successfully checked)
//	 * in the Map/JsonObject within the array. Can use {@link #hasArrayCheckResults()}
//	 * to ensure this will not be null if desired.
//	 *
//	 * @return a list of check results for each index of an array (that contains some
//	 * type of complex parameter) - may be null.
//	 */
//	public List<ApiCheckResult> getArrayCheckResults() {
//		return arrayCheckResults;
//	}
//
//	/**
//	 * @return whether or not {@link ApiCheckResult#customReturnValue} has been set (is not null) for this check result
//	 */
//	public boolean hasCustomReturnValue() {
//		return this.customReturnValue != null;
//	}
//
//	/**
//	 * Used to return some type of object (user will need to cast to the correct type) for
//	 * the check result. The library does not use this for its implementations of
//	 * {@link ApiParam}, but the user may desire to use this when writing their own (likely
//	 * in very rare cases). Can use {@link #hasCustomReturnValue()} to ensure this will not
//	 * be null if desired.
//	 *
//	 * @return some object - may be null
//	 */
//	public Object getCustomReturnValue() {
//		return customReturnValue;
//	}
//
//	public ApiCheckResult getInnerCheckResult(String keyName) {
//		return this.innerCheckResults == null ? null : this.innerCheckResults.get(keyName);
//	}
//
//	/* Static Creation Methods */
//
//	public static ApiCheckResult success(String keyName) {
//		return new ApiCheckResult(keyName, null, null, null, null);
//	}
//
//	public static ApiCheckResult success(String keyName,
//	                                     Set<String> checkedKeyNames,
//	                                     Map<String, ApiCheckResult> innerCheckResults,
//	                                     List<ApiCheckResult> arrayCheckResults) {
//		return new ApiCheckResult(keyName, checkedKeyNames, innerCheckResults, arrayCheckResults);
//	}
//
//	public static ApiCheckResult success(String keyName,
//	                                     Set<String> checkedKeyNames,
//	                                     Map<String, ApiCheckResult> innerCheckResults,
//	                                     List<ApiCheckResult> arrayCheckResults,
//	                                     Object customReturnValue) {
//		return new ApiCheckResult(keyName, checkedKeyNames, innerCheckResults, arrayCheckResults, customReturnValue);
//	}
//
//
//	public static ApiCheckResult failure(ApiParamError paramError) {
//		return new ApiCheckResult(paramError);
//	}
//
//	public boolean containsParameter(String keyName) {
//		return this.checkedKeyNames.contains(keyName);
//	}
//
//	@Override
//	public String toString() {
//		return
//			(this.keyName != null ? "Key name: " + this.keyName : "") +
//				((this.checkedKeyNames != null && this.checkedKeyNames.size() > 0) ? ". Checked keys: " + String.join(", ", this.checkedKeyNames) : "");
//	}
//
//}
