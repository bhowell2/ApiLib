package io.github.bhowell2.apilib;


import io.github.bhowell2.apilib.checks.ArrayCheck;
import io.github.bhowell2.apilib.checks.Check;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This can be used to check a List parameter of any type. Generally, though,
 * this is intended to be used when checking a List of Maps, so that this can
 * return an {@link ApiCheckResult} with {@link ApiCheckResult#arrayCheckResults}
 * set. With the parameterization an arbitrary depth of checking with type safety
 * can be ensured.
 *
 * @param <Param> the type of the index of the list/array
 * @param <In> the input type to check (should be a List/Array or Map)
 * @author Blake Howell
 */
public class ApiListParam<Param, In> extends ApiArrayOrListParamBase<Param, In> {

	public ApiListParam(String keyName,
	                    String displayName,
	                    String invalidErrorMessage,
	                    boolean canBeNull,
	                    ArrayCheck<Param>[] indexChecks,
	                    ArrayCheck<Param>[][] individualIndexChecks,
	                    ApiMapParam indexMapCheck,
	                    ApiMapParam[] individualIndexMapChecks,
	                    ApiArrayOrListParamBase<?, Param> innerArrayParam) {
		super(keyName,
		      displayName,
		      invalidErrorMessage,
		      canBeNull,
		      indexChecks,
		      individualIndexChecks,
		      indexMapCheck,
		      individualIndexMapChecks,
		      innerArrayParam);
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
	 *
	 * @param params most likely a list/array or a map
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public ApiArrayOrListCheckResult check(In params) {
		try {
			/*
			 * params argument can be either a Map or a List/Array (for nested arrays).
			 * The topmost level will be a Map, but if this is a nested ApiArrayParam it
			 * will be a list.
			 * */
			List<Param> paramList;
			if (this.keyName != null) {
				// should only have key name if
				if (!(params instanceof Map)) {
					return ApiArrayOrListCheckResult.failure(new ApiParamError(this.keyName,
					                                                           this.displayName,
					                                                           ApiErrorType.CASTING_ERROR,
					                                                           "err"));
				}
				paramList = (List<Param>) ((Map) params).get(this.keyName);
			} else {
				// If there is no key name then this must be a list (and this is likely an innerArrayParam)
				paramList = (List<Param>) params;
			}

			if (paramList == null) {
				if (this.canBeNull) {
					return ApiArrayOrListCheckResult.success(this.keyName);
				} else {
					return ApiArrayOrListCheckResult.failure(ApiParamError.missing(this.keyName, this.displayName));
				}
			}

			/*
			 * Checks can be run at any level, though it is likely they will be null for
			 * all levels except the most inner array.
			 *
			 * Checks only return successful or a failure message. If they are successful
			 * just continue on, otherwise return the (possibly null) failure message with
			 * an invalid parameter check.
			 *
			 * TODO: return index?
			 * */
			if (this.indexChecks != null && this.indexChecks.length > 0) {
				for (int i = 0; i < paramList.size(); i++) {
					Param paramToCheck = paramList.get(i);
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
				if (this.individualIndexChecks.length != paramList.size()) {
					return ApiArrayOrListCheckResult.failure(ApiParamError.invalid(this, "List of incorrect size. Should be " +
						this.individualIndexChecks.length));
				}
				for (int i = 0; i < this.individualIndexChecks.length; i++) {
					ArrayCheck<Param>[] indexChecks = this.individualIndexChecks[i];
					if (indexChecks != null && indexChecks.length > 0) {
						Param paramToCheck = paramList.get(i);
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
				List<ApiArrayOrListCheckResult> innerArrayCheckResults = new ArrayList<>(paramList.size());
				// only want to add to these if the returned
				for (int i = 0; i < paramList.size(); i++) {
					Param param = paramList.get(i);
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
				return ApiArrayOrListCheckResult.success();
			} else if (this.indexMapCheck != null) {
				List<ApiMapCheckResult> indexMapCheckResults = new ArrayList<>(paramList.size());
				for (int i = 0; i < paramList.size(); i++) {
					ApiMapCheckResult mapCheckResult = this.indexMapCheck.check((Map<String, Object>) paramList.get(i));
					if (mapCheckResult.failed()) {
						return returnFailedCheckResult(i, mapCheckResult.error);
					}
					// successful, add it
					indexMapCheckResults.add(mapCheckResult);
				}
				return ApiArrayOrListCheckResult.successWithMapCheckResults(this.keyName, indexMapCheckResults);
			} else if (this.individualIndexMapChecks != null && this.individualIndexMapChecks.length > 0) {
				if (this.individualIndexMapChecks.length != paramList.size()) {
					return ApiArrayOrListCheckResult.failure(ApiParamError.invalid(this, "List of incorrect size. Should be size " +
						this.individualIndexMapChecks.length + "."));
				}
				List<ApiMapCheckResult> indexMapCheckResults = new ArrayList<>(paramList.size());
				for (int i = 0; i < this.individualIndexMapChecks.length; i++) {
					// must be of map type
					ApiMapCheckResult mapCheckResult =
						this.individualIndexMapChecks[i].check((Map<String, Object>) paramList.get(i));
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
