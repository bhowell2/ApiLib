package io.github.bhowell2.apilib;

import io.github.bhowell2.apilib.checks.CollectionIndexCheck;
import io.github.bhowell2.apilib.checks.Check;
import io.github.bhowell2.apilib.errors.ApiErrorType;
import io.github.bhowell2.apilib.errors.ApiParamError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This is a base class for ApiCollectionParam implementations such as
 * {@link ApiListParam} and {@link ApiArrayParam}.
 *
 * @param <In> the type of parameter from which the collection will be retrieved
 * @param <Param> the parameter type of each index
 * @param <Collection> the collection type (e.g., for an array of Param type String, Collection type = String[])
 * @author Blake Howell
 */
public abstract class ApiCollectionParam<In, Param, Collection>
	extends ApiParamBase<In, ApiCollectionParam.Result>
{


	/**
	 * Checks used on the entire collection. This is most useful when needing
	 * to check the entire collection for some condition (e.g., unique numbers,
	 * or size). If only this was to be used, without the other index checks, then
	 * a simple {@link ApiSingleParam} could be used for the entire collection, but
	 * it's not, so here we are.
	 */
	final Check<Collection>[] collectionChecks;

	/**
	 * Checks used on ALL indices of the array parameter. This, or
	 * {@link #individualIndexChecks} or {@link #indexMapCheck} or
	 * {@link #individualIndexMapChecks} is required for the innermost
	 * collection parameter (i.e., the ApiCollectionParam that does not
	 * have {@link #innerCollectionParam} set).
	 */
	final CollectionIndexCheck<Param>[] indexChecks;

	/**
	 * This is an array of arrays. The first array corresponds to the index to be
	 * checked and the inner array are the checks that should be run on the given
	 * index.
	 */
	final CollectionIndexCheck<Param>[][] individualIndexChecks;

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
	 * set to null and is required to have at least some type of {@link CollectionIndexCheck}.
	 */
	final ApiCollectionParam<Param, ?, ?> innerCollectionParam;

	/**
	 * ArrayOrListParam builder base.
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

		/*
		 * Currently this is not handled/used, but it may need to be in the future.
		 * This is ALMOST handled via a list.size check, but that would return
		 * ApiErrorType.INVALID_PARAMETER, rather than possibly a desired
		 * ApiErrorType.MISSING_PARAMETER for an empty array. For now it seems
		 * fine to allow the user to only return INVALID_PARAMETER rather than
		 * MISSING_PARAMETER on an empty collection as it is technically provided
		 * and invalid...
		 * */
		boolean returnMissingOnEmptyCollection;

		List<Check<Collection>> collectionChecks;
		List<CollectionIndexCheck<Param>> indexChecks;
		List<List<CollectionIndexCheck<Param>>> individualIndexChecks;
		ApiMapParam indexMapCheck;
		List<ApiMapParam> individualIndexMapCheck;
		ApiCollectionParam<Param, ?, ?> innerCollectionParam;

		public Builder(String keyName) {
			super(keyName);
		}

		public Builder(String keyName, ApiCollectionParam<In, Param, Collection> copyFrom) {
			super(keyName);
			this.collectionChecks = arrayIsNotNullOrEmpty(copyFrom.collectionChecks)
				? new ArrayList<>(Arrays.asList(copyFrom.collectionChecks))
				: null;
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
				: null;
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
		 * (Alias of {@link #addCollectionChecks).}
		 * @param sizeCheck check for size of index. can be greater, less, or equal to check
		 * @return this builder
		 */
		@SuppressWarnings("unchecked")
		public B setSizeCheck(Check<Integer> sizeCheck) {
			return this.addCollectionChecks(collection -> {
				if (collection instanceof List) {
					return sizeCheck.check(((List<?>) collection).size());
				}
				return sizeCheck.check(((Object[]) collection).length);
			});
		}

		/**
		 * Adds checks that are applied to the entire collection. E.g., a check
		 * that ensures the collection's size meets some criteria or that the
		 * collection has unique elements.
		 * @param checks to be applied to the entire collection
		 * @return this builder
		 */
		@SafeVarargs
		@SuppressWarnings("unchecked")
		public final B addCollectionChecks(Check<Collection>... checks) {
			checkVarArgsNotNullAndValuesNotNull(checks);
			if (this.collectionChecks == null) {
				this.collectionChecks = new ArrayList<>(checks.length);
			}
			this.collectionChecks.addAll(Arrays.asList(checks));
			return (B) this;
		}

		/**
		 * Adds checks that are applied to each index of the collection. This takes
		 * normal {@link Check}s and wraps them with {@link CollectionIndexCheck}s. This
		 * allows for using regular checks when the actual index value is not needed
		 * for the check.
		 * @param indexChecks checks to be applied to each index
		 * @return this builder
		 */
		@SafeVarargs
		@SuppressWarnings("unchecked")
		public final B addIndexChecks(Check<Param>... indexChecks) {
			checkVarArgsNotNullAndValuesNotNull(indexChecks);
			return this.addIndexChecks(Arrays.stream(indexChecks)
			                                 .map(CollectionIndexCheck::wrapCheck)
			                                 .<CollectionIndexCheck<Param>>toArray(CollectionIndexCheck[]::new));
		}

		/**
		 * Adds checks that are applied to each index of the collection.
		 * @param indexChecks checks to be applied to each index
		 * @return this builder
		 */
		@SafeVarargs
		@SuppressWarnings("unchecked")
		public final B addIndexChecks(CollectionIndexCheck<Param>... indexChecks) {
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
		 * their own {@link CollectionIndexCheck}.
		 *
		 * @param individualIndexChecks list of list of checks (where the inner list's checks are
		 *                              applied to each index of the collection parameter)
		 * @return this builder
		 */
		@SuppressWarnings("unchecked")
		public B setIndividualIndexChecksWrapCheck(List<List<Check<Param>>> individualIndexChecks) {
			this.individualIndexChecks =
				individualIndexChecks
					.stream()
					.map(checks -> checks.stream()
					                     .map(CollectionIndexCheck::wrapCheck)
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
		 * their own {@link CollectionIndexCheck}.
		 *
		 * @param individualIndexChecks list of list of checks (where the inner list's checks are
		 *                              applied to each index of the collection parameter)
		 * @return this builder
		 */
		@SuppressWarnings("unchecked")
		public B setIndividualIndexChecks(List<List<CollectionIndexCheck<Param>>> individualIndexChecks) {
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
		 * @param innerCollectionParam
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public B setInnerCollectionParam(ApiCollectionParam<Param, ?, ?> innerCollectionParam) {
			if (innerCollectionParam.keyName != null) {
				throw new IllegalArgumentException("Cannot have inner array with a key name. If using an ApiCollectionParam " +
					                                   "that was created elsewhere, make a copy of it with the the builder.");
			}
			this.innerCollectionParam = innerCollectionParam;
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

	/*
	*
	* IMPLEMENTATION
	*
	* */

	@SuppressWarnings("unchecked")
	protected ApiCollectionParam(Builder<In, Param, Collection, ?, ?> builder) {
		super(builder);
		this.collectionChecks = getArrayIfNotNullOrEmpty(builder.collectionChecks, new Check[0]);
		this.indexChecks = getArrayIfNotNullOrEmpty(builder.indexChecks, new CollectionIndexCheck[0]);
		this.individualIndexChecks = listIsNotNullOrEmpty(builder.individualIndexChecks)
			?
			builder
				.individualIndexChecks
				.stream()
				.<List<CollectionIndexCheck<Param>[]>>collect(
					ArrayList::new,
					(collectionList, listOfArrayChecks) ->
						collectionList.add(listOfArrayChecks.toArray(new CollectionIndexCheck[0])),
					List::addAll
				)
				.toArray(new CollectionIndexCheck[0][])
			:
			null;
		this.indexMapCheck = builder.indexMapCheck;
		this.individualIndexMapChecks = listIsNotNullOrEmpty(builder.individualIndexMapCheck)
			?
			builder.individualIndexMapCheck.toArray(new ApiMapParam[0])
			:
			null;
		this.innerCollectionParam = builder.innerCollectionParam;
	}


	/**
	 * Wraps the error and provides the index where the error occurred.
	 * @param index
	 * @param apiParamError
	 * @return a failed parameter result
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
			 * params argument can be either a Map or an Array/List (for nested arrays).
			 * The topmost level will be a Map, but if this is a nested ApiArrayParam it
			 * will be an Array/List.
			 * */
			Collection collectionParam;
			if (this.keyName != null) {
				if (!(params instanceof Map)) {
					return Result.failure(new ApiParamError(this.keyName,
					                                        this.displayName,
					                                        ApiErrorType.CASTING_ERROR,
					                                        "Casting error."));
				}
				collectionParam = (Collection) ((Map<?,?>) params).get(this.keyName);
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

			if (this.collectionChecks != null) {
				for (Check<Collection> check : this.collectionChecks) {
					Check.Result checkResult = check.check(collectionParam);
					if (checkResult.failed()) {
						return Result.failure(new ApiParamError(this.keyName,
						                                        this.displayName,
						                                        ApiErrorType.INVALID_PARAMETER,
						                                        checkResult.failureMessage));
					}
				}
			}

			/*
			 * Checks can be run at any level, though it is likely they will be null for
			 * all levels except the most inner array.
			 *
			 * Checks only return successful or a failure message. If they are successful
			 * just continue on, otherwise return the (possibly null) failure message with
			 * an invalid parameter check.
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
					CollectionIndexCheck<Param>[] indexChecks = this.individualIndexChecks[i];
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
					return Result.failure(
						ApiParamError.invalid(this,
						                      "List of incorrect size. Should be size "
							                      + this.individualIndexMapChecks.length + ".")
					);
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

	/*
	 *
	 * RESULT
	 *
	 * */

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
		 * Whether or not there are resulting {@link ApiParamBase.Result}s for each index
		 * of the collection. The user would have set {@link #indexMapCheck} or
		 * {@link #individualIndexChecks}.
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
		 * @return successful param result
		 */
		public static Result success(String keyName) {
			return new Result(keyName, null, null);
		}

		/**
		 * Used when an array/list is nested within another and the innermost
		 * array/list needs to return {@link ApiMapParam}s.
		 @param keyName currently allowing keyName, but it doesn't strictly make sense as there are
			*                no keyNames for the index positions. the position in the list is the index
			*                of each result as it is
			* @param innerCollectionResults result for check of (inner) collection at each index of collection
		 * @return successful param result
		 */
		public static Result successWithNestedList(String keyName,
		                                           List<Result> innerCollectionResults) {
			return new Result(keyName, innerCollectionResults, null);
		}

		/**
		 * @param keyName currently allowing keyName, but it doesn't strictly make sense as there are
		 *                no keyNames for the index positions. the position in the list is the index
		 *                of each result as it is
		 * @param mapResults result for check of map at each index of collection
		 * @return successful param result
		 */
		public static Result successWithMapCheckResults(String keyName,
		                                                List<ApiMapParam.Result> mapResults) {
			return new Result(keyName, null, mapResults);
		}

		public static Result failure(ApiParamError error) {
			return new Result(error);
		}

	}

}
