//package io.github.bhowell2.apilib;
//
//import io.github.bhowell2.apilib.checks.Check;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * @author Blake Howell
// */
//public class ApiArrayParam<Param, In> extends ApiArrayOrListParamBase<Param, In> {
//
//
//	public ApiArrayParam(String keyName,
//	                     String displayName,
//	                     String invalidErrorMessage,
//	                     boolean canBeNull,
//	                     Check<Param>[] indexChecks,
//	                     Check<Param>[][] individualIndexChecks,
//	                     ApiMapParam indexMapCheck,
//	                     ApiMapParam[] individualIndexMapChecks,
//	                     ApiListParam<?, Param> innerArrayParam) {
//		super(keyName, displayName, invalidErrorMessage, canBeNull, indexChecks,
//		      individualIndexChecks, indexMapCheck, individualIndexMapChecks, innerArrayParam);
//	}
//
//	/**
//	 *
//	 * @param params most likely a list/array or a map
//	 * @return
//	 */
//	@Override
//	@SuppressWarnings("unchecked")
//	public ApiArrayOrListCheckResult check(In params) {
//		try {
//			/*
//			 * params argument can be either a Map or a List/Array (for nested arrays).
//			 * The topmost level will be a Map, but if this is a nested ApiArrayParam it
//			 * will be a list.
//			 * */
//			Param[] paramArray;
//			if (this.keyName != null) {
//				// should only have key name if
//				if (!(params instanceof Map)) {
//					return ApiCheckResult.failure(new ApiParamError(this.keyName, this.displayName,
//					                                                ApiErrorType.CASTING_ERROR, "err"));
//				}
//				paramArray = (Param[]) ((Map) params).get(this.keyName);
//			} else {
//				// If there is no key name then this must be a list (and this is likely an innerArrayParam)
//				paramArray = (Param[]) params;
//			}
//
//			if (paramArray == null) {
//				if (this.canBeNull) {
//					return ApiCheckResult.success(this.keyName);
//				} else {
//					return ApiCheckResult.failure(ApiParamError.missing(this.keyName, this.displayName));
//				}
//			}
//
//			/*
//			 * Checks can be run at any level, though it is likely they will be null for
//			 * all levels except the most inner array.
//			 *
//			 * Checks only return successful or a failure message. If they are successful
//			 * just continue on, otherwise return the (possibly null) failure message with
//			 * an invalid parameter check.
//			 *
//			 * TODO: return index?
//			 * */
//			if (this.indexChecks != null && this.indexChecks.length > 0) {
//				for (int i = 0; i < paramArray.length; i++) {
//					Param paramToCheck = paramArray[i];
//					for (int j = 0; j < this.indexChecks.length; j++) {
//						Check.Result checkResult = this.indexChecks[i].check(paramToCheck);
//						if (checkResult.failed()) {
//							return ApiCheckResult.failure(ApiParamError.invalid(this, checkResult.failureMessage));
//						}
//					}
//				}
//			}
//
//			// check at each position
//			if (this.individualIndexChecks != null && this.individualIndexChecks.length > 0) {
//				if (this.individualIndexChecks.length != paramArray.length) {
//					return ApiCheckResult.failure(ApiParamError.invalid(this, "List of incorrect size. Should be " +
//						this.individualIndexChecks.length));
//				}
//				for (int i = 0; i < this.individualIndexChecks.length; i++) {
//					Check<Param>[] indexChecks = this.individualIndexChecks[i];
//					if (indexChecks != null && indexChecks.length > 0) {
//						Param paramToCheck = paramArray[i];
//						for (int j = 0; j < indexChecks.length; j++) {
//							Check.Result checkResult = indexChecks[j].check(paramToCheck);
//							if (checkResult.failed()) {
//								return ApiCheckResult.failure(ApiParamError.invalid(this, checkResult.failureMessage));
//							}
//						}
//					}
//				}
//			}
//
//			ApiListParam.ListCheckResultBuilder checkResultBuilder = ApiListParam.ListCheckResultBuilder.builder();
//
//			// TODO: Need to wrap error!
//
//			/*
//			 * Map checks can only be run when there is no innerArrayParam supplied. This
//			 * is guaranteed by the constructor.
//			 * */
//			if (this.innerArrayParam != null) {
//				// only want to add to these if the returned
//				for (int i = 0; i < paramArray.length; i++) {
//					Param param = paramArray[i];
//					if (!(param instanceof List) && !(param instanceof Object[])) {
//						// throw / return failure here
//					}
//					ApiCheckResult checkResult = this.innerArrayParam.check(param);
//					if (checkResult.failed()) {
//						return checkResult;
//					}
//					// only want to keep check result if it has fields set... should not have any named fields
//					checkResultBuilder.mergeCheckResult(checkResult);
//				}
//			} else {
//				/*
//				 * If indexMapCheck has been set and an innerArrayParam is not supplied then this
//				 * must be the terminal case and thus the indices of the array must contain Maps.
//				 * */
//				if (this.indexMapCheck != null) {
//					for (int i = 0; i < paramArray.length; i++) {
//						ApiCheckResult checkResult = this.indexMapCheck.check((Map<String, Object>) paramArray[i]);
//						if (checkResult.failed()) {
//							// TODO: Add index or message?
//							return checkResult;
//						}
//						// successful
//						checkResultBuilder.mergeCheckResult(checkResult);
//					}
//				} else if (this.individualIndexMapChecks != null && this.individualIndexMapChecks.length > 0) {
//					if (this.individualIndexMapChecks.length != paramArray.length) {
//						return ApiCheckResult.failure(ApiParamError.invalid(this, "List of incorrect size. Should be size " +
//							this.individualIndexMapChecks.length + "."));
//					}
//					for (int i = 0; i < this.individualIndexMapChecks.length; i++) {
//						// must be of map type
//						ApiCheckResult checkResult = this.individualIndexMapChecks[i].check((Map<String, Object>) paramArray[i]);
//						if (checkResult.failed()) {
//							return checkResult;
//						}
//						checkResultBuilder.mergeCheckResult(checkResult);
//					}
//				}
//			}
//			return checkResultBuilder.setKeyName(this.keyName).build();
//		} catch (ClassCastException e) {
//			return ApiCheckResult.failure(ApiParamError.cast(this, e));
//		} catch (Exception e) {
//			return ApiCheckResult.failure(ApiParamError.exceptional(this, e));
//		}
//	}
//
//}
