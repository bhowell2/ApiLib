package io.github.bhowell2.apilib;


import io.github.bhowell2.apilib.checks.ArrayCheck;
import io.github.bhowell2.apilib.checks.Check;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Used to check an array or a list type parameter. Allows for multidimensional
 * arrays/lists by setting {@link ApiArrayOrListParam#innerArrayParam} (which can
 * be nested an arbitrary number of times).
 *
 * The outermost {@link ApiArrayOrListParam} should have an {@code In} type of
 * {@code Map<String, Object>} and any {@link Param} type desired. The {@link Param}
 * type should not be an array/list for the terminal array/list (i.e., the innermost
 * array/list), but just the type that the array/list is made of. This allows for
 * using the already created {@link Check}s if desired.
 *
 * @param <In>
 * @param <Param>
 */
public class ApiArrayOrListParam<In, Param> extends ApiParamBase<In, ApiArrayOrListCheckResult> {

	/**
	 * Checks work just like {@link io.github.bhowell2.apilib.checks.ListChecks}
	 * and {@link io.github.bhowell2.apilib.checks.ListChecks}, but they can be
	 * combined with indexMapCheck(s) or innerArrayParam - if the latter two are
	 * not needed the user should prefer
	 * {@link io.github.bhowell2.apilib.checks.ListChecks}.
	 */
	final ArrayCheck<Param>[] indexChecks;

	/**
	 * This is an array of arrays. The first array corresponds to the index to be
	 * checked and the inner array are the checks that should be run on the given
	 * index.
	 */
	final ArrayCheck<Param>[][] individualIndexChecks;

	/**
	 * Map check that applies to all indices. Only this or {@link #individualIndexMapChecks}
	 * can be set, but not both - because their return values would override each other.
	 */
	final ApiMapParam indexMapCheck;

	/**
	 * Used to check each index with the individual ApiMapParam.
	 */
	final ApiMapParam[] individualIndexMapChecks;

	/**
	 * If the parameter is a multidimensional array this should be set to handle the
	 * inner array. This can be of arbitrary depth. The innermost array will have this
	 * set to null and is required to have at least some type of {@link ArrayCheck}.
	 */
	final ApiArrayOrListParam<Param, ?> innerArrayParam;

	/**
	 * Without these set an array or a list is valid. However, it may be desired
	 * to only allow one or the other. Set these if so. They default to false.
	 * (Cannot set both to true.)
	 */
	final boolean requireArray, requireList;

	public ApiArrayOrListParam(String keyName,
	                           String displayName,
	                           String invalidErrorMessage,
	                           boolean canBeNull,
	                           ArrayCheck<Param>[] indexChecks,
	                           ArrayCheck<Param>[][] individualIndexChecks,
	                           ApiMapParam indexMapCheck,
	                           ApiMapParam[] individualIndexMapChecks,
	                           ApiArrayOrListParam<Param, ?> innerArrayParam) {
		this(keyName,
		     displayName,
		     invalidErrorMessage,
		     canBeNull,
		     indexChecks,
		     individualIndexChecks,
		     indexMapCheck,
		     individualIndexMapChecks,
		     innerArrayParam,
		     false,
		     false);
	}

	public ApiArrayOrListParam(String keyName,
	                           String displayName,
	                           String invalidErrorMessage,
	                           boolean canBeNull,
	                           ArrayCheck<Param>[] indexChecks,
	                           ArrayCheck<Param>[][] individualIndexChecks,
	                           ApiMapParam indexMapCheck,
	                           ApiMapParam[] individualIndexMapChecks,
	                           ApiArrayOrListParam<Param, ?> innerArrayParam,
	                           boolean requireArray,
	                           boolean requireList) {
		super(keyName, displayName, invalidErrorMessage, canBeNull);
		this.indexChecks = indexChecks;
		this.individualIndexChecks = individualIndexChecks;
		this.indexMapCheck = indexMapCheck;
		this.individualIndexMapChecks = individualIndexMapChecks;
		this.innerArrayParam = innerArrayParam;
		this.requireArray = requireArray;
		this.requireList = requireList;
		if (innerArrayParam != null && innerArrayParam.keyName != null) {
			throw new IllegalArgumentException("Cannot add inner array param that is named. Use builder to copy existing " +
				                                   "ApiArrayOrListParam if necessary.");
		}
		/*
		 * These cannot both be supplied since they both return values that would override each other.
		 * */
		if (this.indexMapCheck != null && (this.individualIndexMapChecks != null && this.individualIndexMapChecks.length > 0)) {
			throw new IllegalArgumentException("Cannot set both indexMapChecks and individualIndexMapChecks.");
		}
		/*
		 * Cannot have inner array param set AND map checks. This is because the parameter is
		 * either a list or a map, but not both..
		 * */
		if ((this.indexMapCheck != null ||
			(this.individualIndexMapChecks != null && this.individualIndexMapChecks.length > 0)) &&
			this.innerArrayParam != null) {
			throw new IllegalArgumentException("Cannot set both index map check and inner array param");
		}
		/*
		* Cannot create without checks for innermost array/list.
		* */
		if (this.innerArrayParam == null &&
			(indexChecks == null || indexChecks.length == 0) &&
			(individualIndexChecks == null || individualIndexChecks.length == 0) &&
			indexMapCheck == null &&
			(individualIndexMapChecks == null || individualIndexMapChecks.length == 0)) {
			throw new IllegalArgumentException("Cannot create ApiArrayOrListParam with no checks and no inner array. Use " +
				                                   "an always pass check if this is desired.");
		}
		if (requireArray && requireList) {
			throw new IllegalArgumentException("Cannot require the parameter to be of both Array and List type.");
		}
	}

	/**
	 * Wraps the error and provides the index where the error occurred.
	 * @param index
	 * @param apiParamError
	 * @return
	 */
	private ApiArrayOrListCheckResult returnFailedCheckResult(int index, ApiParamError apiParamError) {
		return ApiArrayOrListCheckResult.failure(new ApiParamError(this.keyName,
		                                                           this.displayName,
		                                                           apiParamError.errorType,
		                                                           apiParamError.errorMessage,
		                                                           apiParamError.exception,
		                                                           index,
		                                                           apiParamError));
	}

	/**
	 * Retrieves the parameter at the specified index. This reduces the redundancy of
	 * creating a check for a parameter of List type or Array type.
	 * @param index the position to retrieve
	 * @param arrayOrList the array/list to retrieve the index from
	 * @return the parameter
	 */
	@SuppressWarnings("unchecked")
	private Param getParamAtIndex(int index, Object arrayOrList) {
		if (arrayOrList instanceof Object[]) {
			return ((Param[]) arrayOrList)[index];
		} else if (arrayOrList instanceof List) {
			return (Param) ((List) arrayOrList).get(index);
		} else {
			throw new IllegalArgumentException("Only arrays or lists are supported.");
		}
	}

	/**
	 *
	 * @param params most likely a list/array or a map
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public ApiArrayOrListCheckResult check(In params) {
		try {
			/*
			 * params argument can be either a Map or a Array/List (for nested arrays).
			 * The topmost level will be a Map, but if this is a nested ApiArrayParam it
			 * will be a list.
			 * */
			Object arrayOrListParam;
			if (this.keyName != null) {
				// should only have key name if
				if (!(params instanceof Map)) {
					return ApiArrayOrListCheckResult.failure(new ApiParamError(this.keyName,
					                                                           this.displayName,
					                                                           ApiErrorType.CASTING_ERROR,
					                                                           "err"));
				}
				arrayOrListParam = ((Map) params).get(this.keyName);
			} else {
				// If there is no key name then this must be a list (and this is likely an innerArrayParam)
				arrayOrListParam = params;
			}

			if (arrayOrListParam == null) {
				if (this.canBeNull) {
					return ApiArrayOrListCheckResult.success(this.keyName);
				} else {
					return ApiArrayOrListCheckResult.failure(ApiParamError.missing(this.keyName, this.displayName));
				}
			}

			/*
			* In the interest of reducing redundancy need to get the length here so that
			* it can be used throughout the checks below. Otherwise would need to have a
			* branch of type Array and a branch for type List. The method getParamAtIndex()
			* is also supplied to complement this. Now, only one branch of code is necessary
			* to handle an array or list.
			* */
			int arrayOrListLength;
			if (arrayOrListParam instanceof Object[]) {
				if (requireList) {
					ClassCastException e = new ClassCastException("Must be of type List, but was an Array.");
					return ApiArrayOrListCheckResult.failure(new ApiParamError(this.keyName,
					                                                           this.displayName,
					                                                           ApiErrorType.CASTING_ERROR,
					                                                           e.getMessage(),
					                                                           e));
				}
				arrayOrListLength = ((Object[]) arrayOrListParam).length;
			} else if (arrayOrListParam instanceof List) {
				if (requireArray) {
					ClassCastException e = new ClassCastException("Must be of type Array, but was a List.");
					return ApiArrayOrListCheckResult.failure(new ApiParamError(this.keyName,
					                                                           this.displayName,
					                                                           ApiErrorType.CASTING_ERROR,
					                                                           e.getMessage(),
					                                                           e));
				}
				arrayOrListLength = ((List) arrayOrListParam).size();
			} else {
				ClassCastException e = new ClassCastException("Must be an Array or a List.");
				return ApiArrayOrListCheckResult.failure(new ApiParamError(this.keyName,
				                                                           this.displayName,
				                                                           ApiErrorType.CASTING_ERROR,
				                                                           e.getMessage(),
				                                                           e));
			}

			/*
			 * Checks can be run at any level, though it is likely they will be null for
			 * all levels except the most inner array.
			 *
			 * Checks only return successful or a failure message. If they are successful
			 * just continue on, otherwise return the (possibly null) failure message with
			 * an invalid parameter check.
			 *
			 * */
			if (this.indexChecks != null && this.indexChecks.length > 0) {
				for (int i = 0; i < arrayOrListLength; i++) {
					Param paramToCheck = getParamAtIndex(i, arrayOrListParam);
					for (int j = 0; j < this.indexChecks.length; j++) {
						Check.Result checkResult = this.indexChecks[j].check(i, paramToCheck);
						if (checkResult.failed()) {
							return ApiArrayOrListCheckResult.failure(new ApiParamError(this.keyName,
							                                                           this.displayName,
							                                                           ApiErrorType.INVALID_PARAMETER,
							                                                           checkResult.failureMessage,
							                                                           null,
							                                                           i,
							                                                           null));
						}
					}
				}
			}

			// check at each position
			if (this.individualIndexChecks != null && this.individualIndexChecks.length > 0) {
				if (this.individualIndexChecks.length != arrayOrListLength) {
					return ApiArrayOrListCheckResult.failure(ApiParamError.invalid(this, "List of incorrect size. Should be " +
						this.individualIndexChecks.length));
				}
				for (int i = 0; i < this.individualIndexChecks.length; i++) {
					ArrayCheck<Param>[] indexChecks = this.individualIndexChecks[i];
					if (indexChecks != null && indexChecks.length > 0) {
						Param paramToCheck = getParamAtIndex(i, arrayOrListParam);
						for (int j = 0; j < indexChecks.length; j++) {
							Check.Result checkResult = indexChecks[j].check(i, paramToCheck);
							if (checkResult.failed()) {
								return ApiArrayOrListCheckResult.failure(new ApiParamError(this.keyName,
								                                                           this.displayName,
								                                                           ApiErrorType.INVALID_PARAMETER,
								                                                           checkResult.failureMessage,
								                                                           null,
								                                                           i,
								                                                           null));
							}
						}
					}
				}
			}

			/*
			 * Map checks can only be run when there is no innerArrayParam supplied. This
			 * is guaranteed by the constructor. So if/else works fine here. If an inner
			 * array is supplied AND it returns
			 * */
			if (this.innerArrayParam != null) {
				/*
				 * Need to keep all returned check results at first, because some may return null
				 * while others may return something of interest. If only kept the ones that were
				 * of interest then the positions would be off.
				 * */
				List<ApiArrayOrListCheckResult> innerArrayCheckResults = new ArrayList<>(arrayOrListLength);
				// only want to add to these if the returned
				for (int i = 0; i < arrayOrListLength; i++) {
					Param param = getParamAtIndex(i, arrayOrListParam);
					if (!(param instanceof List) && !(param instanceof Object[])) {
						return ApiArrayOrListCheckResult.failure(new ApiParamError(this.keyName,
						                                                           this.displayName,
						                                                           ApiErrorType.INVALID_PARAMETER,
						                                                           "Not a list or array.",
						                                                           null,
						                                                           i,
						                                                           null));
					}
					ApiArrayOrListCheckResult checkResult = this.innerArrayParam.check(param);
					if (checkResult.failed()) {
						return returnFailedCheckResult(i, checkResult.error);
					}
					// only want to keep check result if it has fields set... should not have any named fields
					innerArrayCheckResults.add(checkResult);
				}
				/*
				 * Go through inner array check results. If any one has inner arrays or maps
				 * then need to return them all, otherwise just return success.
				 * */
				for (ApiArrayOrListCheckResult res : innerArrayCheckResults) {
					if (res.hasInnerArrayCheckResults() || res.hasMapCheckResults()) {
						return ApiArrayOrListCheckResult.successWithNestedList(this.keyName, innerArrayCheckResults);
					}
				}
				return ApiArrayOrListCheckResult.success(this.keyName);
			} else if (this.indexMapCheck != null) {
				List<ApiMapCheckResult> indexMapCheckResults = new ArrayList<>(arrayOrListLength);
				for (int i = 0; i < arrayOrListLength; i++) {
					ApiMapCheckResult mapCheckResult =
						this.indexMapCheck.check((Map<String, Object>) getParamAtIndex(i, arrayOrListParam));
					if (mapCheckResult.failed()) {
						return returnFailedCheckResult(i, mapCheckResult.error);
					}
					// successful, add it
					indexMapCheckResults.add(mapCheckResult);
				}
				return ApiArrayOrListCheckResult.successWithMapCheckResults(this.keyName, indexMapCheckResults);
			} else if (this.individualIndexMapChecks != null && this.individualIndexMapChecks.length > 0) {
				if (this.individualIndexMapChecks.length != arrayOrListLength) {
					return ApiArrayOrListCheckResult.failure(ApiParamError.invalid(this, "List of incorrect size. Should be size " +
						this.individualIndexMapChecks.length + "."));
				}
				List<ApiMapCheckResult> indexMapCheckResults = new ArrayList<>(arrayOrListLength);
				for (int i = 0; i < this.individualIndexMapChecks.length; i++) {
					// must be of map type
					ApiMapCheckResult mapCheckResult =
						this.individualIndexMapChecks[i].check((Map<String, Object>) getParamAtIndex(i, arrayOrListParam));
					if (mapCheckResult.failed()) {
						return returnFailedCheckResult(i, mapCheckResult.error);
					}
					indexMapCheckResults.add(mapCheckResult);
				}
				return ApiArrayOrListCheckResult.successWithMapCheckResults(this.keyName, indexMapCheckResults);
			}
			/*
			 * There are no inner array checks or index map checks, therefore this must
			 * be simply checking an array (potentially nested). This is usually not
			 * recommended as {@link io.github.bhowell2.apilib.checks.ArrayChecks} with
			 * a {@link ApiSingleParam} will work just as well, but this allows for more
			 * nesting than the standard structure.
			 * */
			return ApiArrayOrListCheckResult.success(this.keyName);
		} catch (ClassCastException e) {
			return ApiArrayOrListCheckResult.failure(ApiParamError.cast(this, e));
		} catch (Exception e) {
			return ApiArrayOrListCheckResult.failure(ApiParamError.exceptional(this, e));
		}
	}

}
