package io.github.bhowell2.apilib;

import java.util.List;

/**
 * Check result for {@link ApiArrayParam} and {@link ApiListParam}.
 * Only allows for returning the name of a successfully checked
 * array-type parameter and a list of {@link ApiMapCheckResult} in
 * case the array has maps as values.
 *
 * @author Blake Howell
 */
public class ApiArrayOrListCheckResult extends ApiCheckResultBase {

	/**
	 * Used to return the results of array checks within this
	 * array check. This will be used in the (very) rare case
	 * that an array of arrays (of arrays...) of maps is a
	 * parameter.
	 */
	final List<ApiArrayOrListCheckResult> innerArrayCheckResults;

	/**
	 * Used to return ApiMapCheckResult for each position in an
	 * array. If an array of arrays of maps is a parameter this
	 * will be null for the outermost array while
	 * {@link #innerArrayCheckResults} will be set in the outermost
	 * array and the innermost array will have this set while
	 * {@link #innerArrayCheckResults} will be null.
	 *
	 * Take the following example (JSON):
	 * <pre>
	 *  {@code
	 * 	 {
	 * 	  array: [
	 * 	     // 1st array within array
	 * 	     [
	 * 	       {
	 * 	         param1: "passing",
	 * 	         param2: "passing"
	 * 	       },
	 * 	       {
	 * 	         param1: "passing"
	 * 	       }
	 * 	     ],
	 * 	     // 2nd array within array
	 * 	     [
	 * 	       {
	 * 	         param2: "passing"
	 * 	       }
	 * 	     ]
	 * 	  ]
	 * 	 }
	 * 	}
	 * </pre>
	 * Will return the following {@link ApiArrayOrListCheckResult} (JSON):
	 * <pre>
	 *   {@code
	 *    {
	 *      keyName: "some key name"
	 *      mapCheckResults: null,
	 *      innerArrayCheckResults: [
	 *        // 1st inner array check result
	 *        {
	 *          keyName: null,
	 *          innerArrayCheckResults: null,
	 *          mapCheckResults: [
	 *            {
	 *              checkedKeyNames: ["param1", "param2"]
	 *            },
	 *            {
	 *              checkedKeyNames: ["param1"]
	 *            }
	 *          ]
	 *        },
	 *        // 2nd inner array check result
	 *        {
	 *          keyName: null,
	 *          innerArrayCheckResults: null,
	 *          mapCheckResults: [
	 *            {
	 *              checkedKeyNames: ["param2"]
	 *            }
	 *          ]
	 *        }
	 *      ]
	 *    }
	 *   }
	 * </pre>
	 *
	 */
	final List<ApiMapCheckResult> mapCheckResults;

	public ApiArrayOrListCheckResult(String keyName, List<ApiArrayOrListCheckResult> innerArrayCheckResults,
	                                 List<ApiMapCheckResult> mapCheckResults) {
		super(keyName);
		this.innerArrayCheckResults = innerArrayCheckResults;
		this.mapCheckResults = mapCheckResults;
	}

	public ApiArrayOrListCheckResult(ApiParamError apiParamError) {
		super(apiParamError);
		this.innerArrayCheckResults = null;
		this.mapCheckResults = null;
	}

	public boolean hasInnerArrayCheckResults() {
		return this.innerArrayCheckResults != null;
	}

	public List<ApiArrayOrListCheckResult> getInnerArrayCheckResults() {
		return this.innerArrayCheckResults;
	}

	/**
	 * @return
	 */
	public boolean hasMapCheckResults() {
		return this.mapCheckResults != null;
	}

	public List<ApiMapCheckResult> getMapCheckResults() {
		return this.mapCheckResults;
	}

	/* Static creation methods */

	/**
	 * Used for inner arrays that do not need to actually return any information,
	 * just that the check was successful. This means that in the final case
	 * there are no maps within the array, just that it was a multi-dimensional
	 * array and the values were okay. If the user has a multi-dimensional array
	 * and needs to return custom information, they should use an
	 * {@link ApiCustomParam} and use {@link ApiCustomCheckResult#customValue}
	 * to return whatever they need.
	 *
	 * @return
	 */
	public static ApiArrayOrListCheckResult success() {
		return new ApiArrayOrListCheckResult(null, null, null);
	}


	/**
	 * Used when only the values of the list are checked and there are
	 * no {@link ApiMapCheckResult}s to return at any level of the the
	 * array/list.
	 *
	 * @param keyName name of successfully checked list/array parameter
	 * @return
	 */
	public static ApiArrayOrListCheckResult success(String keyName) {
		return new ApiArrayOrListCheckResult(keyName, null, null);
	}

	/**
	 * Used when an array/list is nested within another and the innermost
	 * array/list needs to return {@link ApiMapParam}s.
	 * @param innerListCheckResults
	 * @return
	 */
	public static ApiArrayOrListCheckResult successWithNestedList(String keyName,
	                                                              List<ApiArrayOrListCheckResult> innerListCheckResults) {
		return new ApiArrayOrListCheckResult(null, innerListCheckResults, null);
	}

	/**
	 *
	 * @param keyName
	 * @param mapCheckResults
	 * @return
	 */
	public static ApiArrayOrListCheckResult successWithMapCheckResults(String keyName,
	                                                                   List<ApiMapCheckResult> mapCheckResults) {
		return new ApiArrayOrListCheckResult(keyName, null, mapCheckResults);
	}

	public static ApiArrayOrListCheckResult failure(ApiParamError error) {
		return new ApiArrayOrListCheckResult(error);
	}

}
