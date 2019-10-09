package io.github.bhowell2.apilib;

import io.github.bhowell2.apilib.formatters.Formatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This is only used for checking an array/list that has Maps/JsonObjects as values.
 * This is not to be used for checking an array/list of single values (e.g., String,
 * Double, Integer, etc.) - those should be checked with {@link ApiSingleParam}
 * utilizing {@link io.github.bhowell2.apilib.checks.ArrayChecks}.
 *
 * This operates in a very similar manner as {@link ApiSingleParam}, but mainly
 * differs in the return type which returns {@code List<ApiMapParamCheckResult>}.
 *
 * This fails if any of the {@link ApiMapParam} checks fail for any index.
 *
 * @author Blake Howell
 */
public class ApiArrayOfMapsParam<Param> extends ApiNamedParamBase<ApiArrayOfMapsParamCheckResult> {

	final String invalidErrMsg;
	final boolean canBeNull, indicesCanBeNull;
	final Formatter<? super Object, ? super Object>[] formatters;
	/*
	 * The user may want to supply a check for each position in the array or use one check
	 * for all positions in the array. One of these should be set and the other should be null.
	 * */
	final ApiMapParam check;
	final ApiMapParam[] checks;

	public ApiArrayOfMapsParam(String keyName,
	                           String displayName,
	                           boolean canBeNull,
	                           boolean indicesCanBeNull,
	                           Formatter<? super Object, ? super Object>[] formatters,
	                           ApiMapParam check,
	                           String invalidErrMsg) {
		this(keyName, displayName, canBeNull, indicesCanBeNull, formatters, check, null, invalidErrMsg);
	}

	public ApiArrayOfMapsParam(String keyName,
	                           String displayName,
	                           boolean canBeNull,
	                           boolean indicesCanBeNull,
	                           Formatter<? super Object, ? super Object>[] formatters,
	                           ApiMapParam[] checks,
	                           String invalidErrMsg) {
		this(keyName, displayName, canBeNull, indicesCanBeNull, formatters, null, checks, invalidErrMsg);
	}

	private ApiArrayOfMapsParam(String keyName,
	                            String displayName,
	                            boolean canBeNull,
	                            boolean indicesCanBeNull,
	                            Formatter<? super Object, ? super Object>[] formatters,
	                            ApiMapParam check,
	                            ApiMapParam[] checks,
	                            String invalidErrMsg) {
		super(keyName, displayName);
		this.canBeNull = canBeNull;
		this.indicesCanBeNull = indicesCanBeNull;
		this.formatters = formatters;
		this.check = check;
		this.checks = checks;
		if (this.check == null && this.checks == null) {
			throw new IllegalArgumentException("Must supply a single check (via check field) for all array indices or a " +
				                                   "check for each position in the array (via checks field).");
		} else if (this.check != null && this.checks != null) {
			throw new IllegalArgumentException("Only one of check or checks field can be non-null. Otherwise it is ambiguous " +
				                                   "which check to use when checking the array.");
		}
		if (this.checks != null) {
			if (this.checks.length == 0) {
				throw new IllegalArgumentException("No checks were provided in checks array.");
			}
			for (ApiMapParam c : this.checks) {
				if (c == null) {
					throw new IllegalArgumentException("Cannot have null values in ApiMapParam checks array.");
				}
			}
		}
		this.invalidErrMsg = invalidErrMsg;
	}

	private ApiArrayOfMapsParamCheckResult returnInvalidErrorMessage(String errMsg) {
		// if invalid error message is set it overrides all other error messages
		if (this.invalidErrMsg != null) {
			return ApiArrayOfMapsParamCheckResult.failure(ApiParamError.invalid(this, this.invalidErrMsg));
		} else {
			return errMsg == null
				?
				ApiArrayOfMapsParamCheckResult.failure(ApiParamError.invalid(this))
				:
				ApiArrayOfMapsParamCheckResult.failure(ApiParamError.invalid(this, errMsg));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public ApiArrayOfMapsParamCheckResult check(Map<String, Object> params) {
		// this is very much like ApiSingleParam, but provides functionality for an array of maps
		try {
			/*
			 * Due to type erasure this is really just (Object) here. Will not see any casting issues until param is
			 * passed to formatters or checks that cast to a specific class.
			 * */
			Param param = (Param) params.get(this.keyName);
			if (param == null) {
				if (params.containsKey(this.keyName)) {
					// was SET to null, not just null because it was not set at all
					if (canBeNull) {
						return ApiArrayOfMapsParamCheckResult.success(this.keyName, null);
					} else {
						return returnInvalidErrorMessage(ApiLibSettings.DEFAULT_CANNOT_BE_NULL_MESSAGE);
					}
				} else {
					// param is null, but was not SET to null. therefore it is missing.
					return ApiArrayOfMapsParamCheckResult.failure(ApiParamError.missing(this));
				}
			}

			/*
			 * It has been decided to avoid re-inserting the formatted value until all checks pass.
			 * This allows the developer to more easily trace where possible errors occurred.
			 * Formatters could make irreversible changes to an object and therefore the developer
			 * would not be able to deduce the original value from the formatted value.
			 * */
			boolean formatted = false;
			if (this.formatters != null && this.formatters.length > 0) {
				formatted = true;
				Object paramToFormat = param;
				for (Formatter<? super Object, ? super Object> formatter : this.formatters) {
					Formatter.Result formatResult = formatter.format(paramToFormat);
					if (formatResult.failed()) {
						return formatResult.hasFailureMessage()
							?
							ApiArrayOfMapsParamCheckResult.failure(ApiParamError.format(this, formatResult.getFailureMessage()))
							:
							ApiArrayOfMapsParamCheckResult.failure(ApiParamError.format(this));
					}
					paramToFormat = formatResult.getFormattedValue();
				}
				param = (Param) paramToFormat;
			}

			/*
			 * Possible that the formatter set the param to null or that the formatter did
			 * not do anything with it and it already was null. In either case there are no
			 * possible checks to run on a null value, so return here.
			 * */
			if (param == null) {
				if (canBeNull) {
					return ApiArrayOfMapsParamCheckResult.success(this.keyName, null);
				} else {
					return returnInvalidErrorMessage(ApiLibSettings.DEFAULT_CANNOT_BE_NULL_MESSAGE);
				}
			}

			/* The parameter could be an array or it could be a List. Code is pretty much the same.. */
			List<ApiMapParamCheckResult> results;
			if (param instanceof List) {
				List<Map<String, Object>> listParam = (List) param;
				results = new ArrayList<>(listParam.size());
				if (this.check != null) {
					for (int i = 0; i < listParam.size(); i++) {
						Map<String, Object> indexVal = listParam.get(i);
						if (indexVal == null) {
							if (this.indicesCanBeNull) {
								results.add(null);
							} else {
								return returnInvalidErrorMessage("Cannot have null indices.");
							}
						}
						ApiMapParamCheckResult checkResult = this.check.check(indexVal);
						if (checkResult.successful()) {
							results.add(checkResult);
						} else {
							return ApiArrayOfMapsParamCheckResult.failure(checkResult.apiParamError);
						}
					}
				} else {
					/*
					 * this.checks must not be null as required by the constructor. Each position in
					 * this.checks corresponds to the position to check in the supplied parameter
					 * array - if they are not the same size this results in an error.
					 * */
					if (this.checks.length != listParam.size()) {
						return returnInvalidErrorMessage("Array not of proper size.");
					}
					for (int i = 0; i < this.checks.length; i++) {
						Map<String, Object> indexVal = listParam.get(i);
						if (indexVal == null) {
							if (this.indicesCanBeNull) {
								results.add(null);
							} else {
								return returnInvalidErrorMessage("Cannot have null indices.");
							}
						}
						ApiMapParamCheckResult checkResult = this.checks[i].check(indexVal);
						if (checkResult.successful()) {
							results.add(checkResult);
						} else {
							return ApiArrayOfMapsParamCheckResult.failure(checkResult.apiParamError);
						}
					}
				}
			} else if (param instanceof Map[]) {
				Map<String, Object>[] arrayParam = (Map<String, Object>[]) param;
				results = new ArrayList<>(arrayParam.length);
				if (this.check != null) {
					for (int i = 0; i < arrayParam.length; i++) {
						Map<String, Object> indexVal = arrayParam[i];
						if (indexVal == null) {
							if (this.indicesCanBeNull) {
								results.add(null);
							} else {
								return returnInvalidErrorMessage("Cannot have null indices.");
							}
						}
						ApiMapParamCheckResult checkResult = this.check.check(indexVal);
						if (checkResult.successful()) {
							results.add(checkResult);
						} else {
							return ApiArrayOfMapsParamCheckResult.failure(checkResult.apiParamError);
						}
					}
				} else {
					/*
					 * this.checks must not be null as required by the constructor. Each position in
					 * this.checks corresponds to the position to check in the supplied parameter
					 * array - if they are not the same size this results in an error.
					 * */
					if (this.checks.length != arrayParam.length) {
						return returnInvalidErrorMessage("Array not of proper size.");
					}
					for (int i = 0; i < this.checks.length; i++) {
						Map<String, Object> indexVal = arrayParam[i];
						if (indexVal == null) {
							if (this.indicesCanBeNull) {
								results.add(null);
							} else {
								return returnInvalidErrorMessage("Cannot have null indices.");
							}
						}
						ApiMapParamCheckResult checkResult = this.checks[i].check(indexVal);
						if (checkResult.successful()) {
							results.add(checkResult);
						} else {
							return ApiArrayOfMapsParamCheckResult.failure(checkResult.apiParamError);
						}
					}
				}

			} else {
				// unknown array type
				return returnInvalidErrorMessage("Unknown array type.");
			}

			// parameter was formatted in some way and successfully passed all checks. put back in map for user to access later
			if (formatted) {
				params.put(this.keyName, param);
			}
			return ApiArrayOfMapsParamCheckResult.success(this.keyName, results);
		} catch (ClassCastException e) {
			return ApiArrayOfMapsParamCheckResult.failure(ApiParamError.cast(this, e));
		} catch (Exception e) {
			return ApiArrayOfMapsParamCheckResult.failure(ApiParamError.exceptional(this, e));
		}
	}

}
