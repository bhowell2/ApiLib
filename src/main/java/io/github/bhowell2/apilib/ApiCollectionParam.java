package io.github.bhowell2.apilib;

import io.github.bhowell2.apilib.checks.CollectionCheck;
import io.github.bhowell2.apilib.checks.Check;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Blake Howell
 */
public abstract class ApiCollectionParam<In, Param, Collection>
	extends ApiParamBase<In, ApiCollectionParam.Result>
{

	/**
	 * Result from checking an {@link ApiCollectionParam}.
	 */
	public static class Result extends ApiParamBase.Result {
		/**
		 * Used to return the results of array checks within this
		 * array check. This will be used in the (very) rare case
		 * that an array of arrays (of arrays...) of maps is a
		 * parameter.
		 */
		public final List<Result> innerCollectionResults;

		/**
		 * Used to return ApiMapParam.Result for each position in an
		 * array. If an array of arrays of maps is a parameter this
		 * will be null for the outermost array while
		 * {@link #innerCollectionResults} will be set in the outermost
		 * array and the innermost array will have this set while
		 * {@link #innerCollectionResults} will be null.
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
		 * Will return the following Result (JSON):
		 * <pre>
		 *   {@code
		 *    {
		 *      keyName: "some key name"
		 *      mapCheckResults: null,
		 *      innerCollectionCheckResults: [
		 *        // 1st inner array check result
		 *        {
		 *          keyName: null,
		 *          innerCollectionCheckResults: null,
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
		 *          innerCollectionCheckResults: null,
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
		public final List<ApiMapParam.Result> mapResults;

		public Result(String keyName,
		              List<Result> innerCollectionResults,
		              List<ApiMapParam.Result> mapResults) {
			super(keyName);
			this.innerCollectionResults = innerCollectionResults;
			this.mapResults = mapResults;
		}

		public Result(ApiParamError error) {
			super(error);
			this.innerCollectionResults = null;
			this.mapResults = null;
		}

		public boolean hasInnerCollectionResults() {
			return this.innerCollectionResults != null;
		}

		/**
		 * @return
		 */
		public boolean hasMapResults() {
			return this.mapResults != null;
		}

		/* Static Creation Methods */

		/**
		 * Used when only the values of the list are checked and there are
		 * no {@link ApiMapParam.Result}s to return at any level of the the
		 * array/list.
		 *
		 * @param keyName name of successfully checked list/array parameter
		 * @return
		 */
		public static Result success(String keyName) {
			return new Result(keyName, null, null);
		}

		/**
		 * Used when an array/list is nested within another and the innermost
		 * array/list needs to return {@link ApiMapParam}s.
		 * @param innerCollectionResults
		 * @return
		 */
		public static Result successWithNestedList(String keyName,
		                                           List<Result> innerCollectionResults) {
			return new Result(null, innerCollectionResults, null);
		}

		/**
		 * @param keyName
		 * @param mapResults
		 * @return
		 */
		public static Result successWithMapCheckResults(String keyName,
		                                                List<ApiMapParam.Result> mapResults) {
			return new Result(keyName, null, mapResults);
		}

		public static Result failure(ApiParamError error) {
			return new Result(error);
		}

	}

	/**
	 * Allows for requiring the collection's length/size meets some conditions.
	 */
	final Check<Integer> sizeCheck;

	/**
	 * Checks used on ALL indices of the array parameter. This, or
	 * {@link #individualIndexChecks} or {@link #indexMapCheck} or
	 * {@link #individualIndexMapChecks} is required for the innermost
	 * collection parameter (i.e., the ApiCollectionParam that does not
	 * have {@link #innerCollectionParam} set).
	 */
	final CollectionCheck<Param>[] indexChecks;

	/**
	 * This is an array of arrays. The first array corresponds to the index to be
	 * checked and the inner array are the checks that should be run on the given
	 * index.
	 */
	final CollectionCheck<Param>[][] individualIndexChecks;

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
	 * set to null and is required to have at least some type of {@link CollectionCheck}.
	 */
	final ApiCollectionParam<Param, ?, ?> innerCollectionParam;

	/**
	 * ArrayOrListParam builder base.
	 * @param <In>
	 * @param <Param>
	 * @param <Collection>
	 * @param <B>
	 * @param <C>
	 */
	protected static abstract class Builder<
		In,
		Param,
		Collection,
		B extends Builder<In, Param, Collection, B, C>,
		C extends ApiCollectionParam<In, Param, Collection>
		>
		extends ApiParamBase.Builder<
		ApiCollectionParam<In, Param, Collection>,
		B
		> {

		boolean returnMissingOnEmptyCollection;
		Check<Integer> sizeCheck;
		List<CollectionCheck<Param>> indexChecks;
		List<List<CollectionCheck<Param>>> individualIndexChecks;
		ApiMapParam indexMapCheck;
		List<ApiMapParam> individualIndexMapCheck;
		ApiCollectionParam<Param, ?, ?> innerCollectionParam;

		public Builder(String keyName) {
			super(keyName);
		}

		public Builder(String keyName, ApiCollectionParam<In, Param, ?> copyFrom) {
			super(keyName);
			this.indexChecks = arrayIsNotNullOrEmpty(copyFrom.indexChecks)
				? new ArrayList<>(Arrays.asList(copyFrom.indexChecks))
				: null;
			this.individualIndexChecks = arrayIsNotNullOrEmpty(copyFrom.individualIndexChecks)
				? Arrays.stream(copyFrom.individualIndexChecks)
				        .map(Arrays::asList)
				        .collect(ArrayList::new,
				                 ArrayList::add,
				                 List::addAll)
				: null;
			this.indexMapCheck = copyFrom.indexMapCheck;
			this.individualIndexMapCheck = arrayIsNotNullOrEmpty(copyFrom.individualIndexMapChecks)
				? Arrays.asList(copyFrom.individualIndexMapChecks)
				: new ArrayList<>();
		}

//		/**
//		 * Set whether or not the {@link ApiCollectionParam.Result} should return
//		 * {@link ApiErrorType#MISSING_PARAMETER} if the collection is empty.
//		 *
//		 * This is useful when {@link ApiMapParam#continueOnOptionalFailure} is set to
//		 * false, but the user does not want an emtpy (optional)collection to be added to
//		 * the list of {@link ApiMapParam.Result#checkedKeyNames}. In the case that the
//		 * parameter is required, this would be like setting {@link #setLengthCheck(Check)}
//		 * to {@code IntegerChecks.valueGreaterThan(0)}.
//		 *
//		 * @param returnMissingOnEmptyCollection
//		 * @return
//		 */
//		@SuppressWarnings("unchecked")
//		public B setReturnMissingOnEmptyCollection(boolean returnMissingOnEmptyCollection) {
//			this.returnMissingOnEmptyCollection = returnMissingOnEmptyCollection;
//			return (B) this;
//		}

		/**
		 * Sets a check for the length of the collection.
		 * @param sizeCheck
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public B setSizeCheck(Check<Integer> sizeCheck) {
			this.sizeCheck = sizeCheck;
			return (B) this;
		}

		/**
		 * Adds checks that are applied to each index of the collection. This takes
		 * normal {@link Check}s and wraps them with {@link CollectionCheck}s. This
		 * allows for using regular checks when the actual index value is not needed
		 * for the check.
		 * @param indexChecks checks to be applied to each index
		 * @return this builder
		 */
		@SafeVarargs
		@SuppressWarnings("unchecked")
		public final B addIndexChecks(Check<Param>... indexChecks) {
			checkVarArgsNotNullAndValuesNotNull(indexChecks);
			return (B) this.addIndexChecks(Arrays.stream(indexChecks)
			                                     .map(CollectionCheck::wrapCheck)
				                               .<CollectionCheck<Param>>toArray(CollectionCheck[]::new));
		}

		/**
		 * Adds checks that are applied to each index of the collection.
		 * @param indexChecks checks to be applied to each index
		 * @return this builder
		 */
		@SafeVarargs
		@SuppressWarnings("unchecked")
		public final B addIndexChecks(CollectionCheck<Param>... indexChecks) {
			checkVarArgsNotNullAndValuesNotNull(indexChecks);
			if (this.indexChecks == null) {
				this.indexChecks = new ArrayList<>(indexChecks.length);
			}
			this.indexChecks.addAll(Arrays.asList(indexChecks));
			return (B) this;
		}

		/**
		 * Sets the list of checks that are to be applied to each index of the
		 * collection. Each index of the outer/container list contains an inner
		 * list of checks to be applied to that index of the collection.
		 *
		 * If these are set the collection parameter must be of the same length.
		 * If this is not desired the user could achieve the same effect with
		 * their own {@link CollectionCheck}.
		 *
		 * @param individualIndexChecks list of list of checks (where the inner list's checks are
		 *                              applied to each index of the collection parameter)
		 * @return this builder
		 */
		@SuppressWarnings("unchecked")
		public B setIndividualIndexChecksWrapCheck(List<List<Check<Param>>> individualIndexChecks) {
			this.individualIndexChecks =
				individualIndexChecks.stream()
				                     .map(checks -> checks.stream()
				                                          .map(CollectionCheck::wrapCheck)
				                                          .collect(Collectors.toList()))
				                     .collect(Collectors.toList());
			return (B) this;
		}

		/**
		 * Sets the list of checks that are to be applied to each index of the
		 * collection. Each index of the outer/container list contains an inner
		 * list of checks to be applied to that index of the collection.
		 *
		 * If these are set the collection parameter must be of the same length.
		 * If this is not desired the user could achieve the same effect with
		 * their own {@link CollectionCheck}.
		 *
		 * @param individualIndexChecks list of list of checks (where the inner list's checks are
		 *                              applied to each index of the collection parameter)
		 * @return this builder
		 */
		@SuppressWarnings("unchecked")
		public B setIndividualIndexChecks(List<List<CollectionCheck<Param>>> individualIndexChecks) {
			if (this.individualIndexChecks != null) {
				throw new IllegalArgumentException("Individual index checks has already been set.");
			}
			// could set to null if copying from another and wanting to remove it
			this.individualIndexChecks = individualIndexChecks;
			return (B) this;
		}

		/**
		 * Sets the {@link ApiMapParam} to be applied to each index of the collection.
		 * collection. This is used for collections of Maps.
		 *
		 * Either this or {@link #setIndividualIndexMapChecks(List)} can be set, but
		 * not both, because they would each return an {@link ApiMapParam.Result}
		 * where one would override the other.
		 *
		 * Either this or {@link #setInnerCollectionParam(ApiCollectionParam)} can be
		 * set, but not both.
		 *
		 * @param indexMapCheck the {@link ApiMapParam} to be used for each index
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public B setIndexMapCheck(ApiMapParam indexMapCheck) {
			this.indexMapCheck = indexMapCheck;
			return (B) this;
		}

		/**
		 * Sets the {@link ApiMapParam} to be applied to a specific index of the
		 * collection. This is used for collections of Maps.
		 *
		 * Either this or {@link #setIndexMapCheck(ApiMapParam)} can be set, but
		 * not both, because they would each return an {@link ApiMapParam.Result}
		 * where one would override the other.
		 *
		 * Either this or {@link #setInnerCollectionParam(ApiCollectionParam)} can be
		 * set, but not both.
		 *
		 * @param individualIndexMapChecks the {@link ApiMapParam} to be used for a specific index
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public B setIndividualIndexMapChecks(List<ApiMapParam> individualIndexMapChecks) {
			this.individualIndexMapCheck = individualIndexMapChecks;
			return (B) this;
		}

		/**
		 * Sets the {@link ApiCollectionParam} used for a collection of collections (ad infinitum).
		 *
		 * Either this or {@link #setIndexMapCheck(ApiMapParam)} or {@link #setIndividualIndexMapChecks(List)}
		 * can be set, but not both.
		 *
		 * @param innerListParam
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public B setInnerCollectionParam(ApiCollectionParam<Param, ?, ?> innerListParam) {
			if (innerListParam.keyName != null) {
				throw new IllegalArgumentException("Cannot have inner array with a key name. If using an ApiCollectionParam " +
					                                   "that was created elsewhere, make a copy of it with the the builder.");
			}
			this.innerCollectionParam = innerListParam;
			return (B) this;
		}

		/**
		 *
		 * @return
		 */
		protected abstract C buildImplementation();

		@Override
		public C build() {
			if (this.innerCollectionParam != null && this.innerCollectionParam.keyName != null) {
				throw new IllegalArgumentException("Cannot add inner array param that is named. Use builder to copy existing " +
					                                   "ApiCollectionParam if necessary.");
			}
			/*
			 * These cannot both be supplied since they both return values that would override each other.
			 * */
			if (this.indexMapCheck != null && listIsNotNullOrEmpty(this.individualIndexMapCheck)) {
				throw new IllegalArgumentException("Cannot set both indexMapChecks and individualIndexMapChecks.");
			}
			/*
			 * Cannot have inner array param set AND map checks. This is because the parameter is
			 * either a list or a map, but not both..
			 * */
			if ((this.indexMapCheck != null || listIsNotNullOrEmpty(this.individualIndexMapCheck)) &&
				this.innerCollectionParam != null) {
				throw new IllegalArgumentException("Cannot set both index map check and inner array param.");
			}
			/*
			 * Cannot create without checks for innermost array/list.
			 * */
			if (this.innerCollectionParam == null &&
				!listIsNotNullOrEmpty(this.indexChecks) &&
				!listIsNotNullOrEmpty(this.individualIndexChecks) &&
				indexMapCheck == null &&
				!listIsNotNullOrEmpty(this.individualIndexMapCheck)) {
				throw new IllegalArgumentException("Cannot create ApiCollectionParam with no checks and no inner array. Use " +
					                                   "an always pass check if this is desired.");
			}
			return buildImplementation();
		}

	}

	@SuppressWarnings("unchecked")
	protected ApiCollectionParam(Builder<In, Param, Collection, ?, ?> builder) {
		super(builder);
		this.sizeCheck = builder.sizeCheck;
		this.indexChecks = listIsNotNullOrEmpty(builder.indexChecks)
			? builder.indexChecks.toArray(new CollectionCheck[0])
			: null;
		this.individualIndexChecks = listIsNotNullOrEmpty(builder.individualIndexChecks)
			? builder.individualIndexChecks.stream()
			.<List<CollectionCheck<Param>[]>>collect(ArrayList::new,
			                                         (collectionList, listOfArrayChecks) ->
				                                         collectionList.add(listOfArrayChecks.toArray(new CollectionCheck[0])),
			                                         List::addAll)
			.toArray(new CollectionCheck[0][])
			: null;
		this.indexMapCheck = builder.indexMapCheck;
		this.individualIndexMapChecks = listIsNotNullOrEmpty(builder.individualIndexMapCheck)
			? builder.individualIndexMapCheck.toArray(new ApiMapParam[0])
			: null;
		this.innerCollectionParam = builder.innerCollectionParam;
	}


	/**
	 * Wraps the error and provides the index where the error occurred.
	 * @param index
	 * @param apiParamError
	 * @return
	 */
	private Result returnFailedCheckResult(int index, ApiParamError apiParamError) {
		return Result.failure(new ApiParamError(this.keyName,
		                                        this.displayName,
		                                        apiParamError.errorType,
		                                        apiParamError.errorMessage,
		                                        apiParamError.exception,
		                                        index,
		                                        apiParamError));
	}

	protected abstract int getCollectionSize(Collection paramCollection);

	protected abstract Param getParamAtIndex(int i, Collection paramCollection);

	@SuppressWarnings("unchecked")
	@Override
	public Result check(In params) {
		try {
			/*
			 * params argument can be either a Map or a Array/List (for nested arrays).
			 * The topmost level will be a Map, but if this is a nested ApiArrayParam it
			 * will be a list.
			 * */
			Collection collectionParam;
			if (this.keyName != null) {
				// should only have key name if
				if (!(params instanceof Map)) {
					return Result.failure(new ApiParamError(this.keyName,
					                                        this.displayName,
					                                        ApiErrorType.CASTING_ERROR,
					                                        "err"));
				}
				collectionParam = (Collection) ((Map) params).get(this.keyName);
			} else {
				// If there is no key name then this must be a list (and this is likely an innerArrayParam)
				collectionParam = (Collection) params;
			}

			if (collectionParam == null) {
				if (this.canBeNull) {
					return Result.success(this.keyName);
				} else {
					return Result.failure(ApiParamError.missing(this.keyName, this.displayName));
				}
			}

			/*
			 * In the interest of reducing redundancy need to get the length here so that
			 * it can be used throughout the checks below. Otherwise would need to have a
			 * branch of type Array and a branch for type List. The method getParamAtIndex()
			 * is also supplied to complement this. Now, only one branch of code is necessary
			 * to handle an array or list.
			 * */
			int collectionLength = getCollectionSize(collectionParam);

			if (this.sizeCheck != null) {
				Check.Result lengthCheckResult = this.sizeCheck.check(collectionLength);
				if (lengthCheckResult.failed()) {
					return Result.failure(ApiParamError.invalid(this, lengthCheckResult.failureMessage));
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
			 * */
			if (this.indexChecks != null && this.indexChecks.length > 0) {
				for (int i = 0; i < collectionLength; i++) {
					Param paramToCheck = getParamAtIndex(i, collectionParam);
					for (int j = 0; j < this.indexChecks.length; j++) {
						Check.Result checkResult = this.indexChecks[j].check(i, paramToCheck);
						if (checkResult.failed()) {
							return Result.failure(new ApiParamError(this.keyName,
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
				if (this.individualIndexChecks.length != collectionLength) {
					return Result.failure(ApiParamError.invalid(this, "List of incorrect size. Should be " +
						this.individualIndexChecks.length));
				}
				for (int i = 0; i < this.individualIndexChecks.length; i++) {
					CollectionCheck<Param>[] indexChecks = this.individualIndexChecks[i];
					if (indexChecks != null && indexChecks.length > 0) {
						Param paramToCheck = getParamAtIndex(i, collectionParam);
						for (int j = 0; j < indexChecks.length; j++) {
							Check.Result checkResult = indexChecks[j].check(i, paramToCheck);
							if (checkResult.failed()) {
								return Result.failure(new ApiParamError(this.keyName,
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
			if (this.innerCollectionParam != null) {
				/*
				 * Need to keep all returned check results at first, because some may return null
				 * while others may return something of interest. If only kept the ones that were
				 * of interest then the positions would be off.
				 * */
				List<Result> innerArrayCheckResults = new ArrayList<>(collectionLength);
				// only want to add to these if the returned
				for (int i = 0; i < collectionLength; i++) {
					Param param = getParamAtIndex(i, collectionParam);
					if (!(param instanceof List) && !(param instanceof Object[])) {
						return Result.failure(new ApiParamError(this.keyName,
						                                        this.displayName,
						                                        ApiErrorType.INVALID_PARAMETER,
						                                        "Not a list or array.",
						                                        null,
						                                        i,
						                                        null));
					}
					Result checkResult = this.innerCollectionParam.check(param);
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
				for (Result res : innerArrayCheckResults) {
					if (res.hasInnerCollectionResults() || res.hasMapResults()) {
						return Result.successWithNestedList(this.keyName, innerArrayCheckResults);
					}
				}
				return Result.success(this.keyName);
			} else if (this.indexMapCheck != null) {
				List<ApiMapParam.Result> indexMapCheckResults = new ArrayList<>(collectionLength);
				for (int i = 0; i < collectionLength; i++) {
					ApiMapParam.Result mapCheckResult =
						this.indexMapCheck.check((Map<String, Object>) getParamAtIndex(i, collectionParam));
					if (mapCheckResult.failed()) {
						return returnFailedCheckResult(i, mapCheckResult.error);
					}
					// successful, add it
					indexMapCheckResults.add(mapCheckResult);
				}
				return Result.successWithMapCheckResults(this.keyName, indexMapCheckResults);
			} else if (this.individualIndexMapChecks != null && this.individualIndexMapChecks.length > 0) {
				if (this.individualIndexMapChecks.length != collectionLength) {
					return Result.failure(ApiParamError.invalid(this, "List of incorrect size. Should be size " +
						this.individualIndexMapChecks.length + "."));
				}
				List<ApiMapParam.Result> indexMapCheckResults = new ArrayList<>(collectionLength);
				for (int i = 0; i < this.individualIndexMapChecks.length; i++) {
					// must be of map type
					ApiMapParam.Result mapCheckResult =
						this.individualIndexMapChecks[i].check((Map<String, Object>) getParamAtIndex(i, collectionParam));
					if (mapCheckResult.failed()) {
						return returnFailedCheckResult(i, mapCheckResult.error);
					}
					indexMapCheckResults.add(mapCheckResult);
				}
				return Result.successWithMapCheckResults(this.keyName, indexMapCheckResults);
			}
			/*
			 * There are no inner array checks or index map checks, therefore this must
			 * be simply checking an array (potentially nested). This is usually not
			 * recommended as {@link io.github.bhowell2.apilib.checks.ArrayChecks} with
			 * a {@link ApiSingleParam} will work just as well, but this allows for more
			 * nesting than the standard structure.
			 * */
			return Result.success(this.keyName);
		} catch (ClassCastException e) {
			return Result.failure(ApiParamError.cast(this, e));
		} catch (Exception e) {
			return Result.failure(ApiParamError.exceptional(this, e));
		}
	}

}
