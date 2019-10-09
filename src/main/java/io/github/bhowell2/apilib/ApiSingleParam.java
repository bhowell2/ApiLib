package io.github.bhowell2.apilib;

import io.github.bhowell2.apilib.checks.Check;
import io.github.bhowell2.apilib.formatters.Formatter;

import java.util.Map;

/**
 * Used to check a single parameter. The value of the parameter is retrieved from the map
 * with {@link #keyName} and then passed to any provided {@link Formatter}s and {@link Check}s.
 * If any formatter or check fails an error message may be returned describing the reason for
 * failure - if no failure message is returned, a default message will be returned (these can
 * be overridden, see {@link ApiLibSettings}). If the parameter can be set to null, this should
 * be set with {@link #canBeNull} - in this case, the null value will be passed to the formatters
 * (if they exist) and
 *
 * @author Blake Howell
 */
public class ApiSingleParam<Param> extends ApiNamedParamBase<ApiSingleParamCheckResult> {

	public final String invalidErrMsg;
	public final boolean canBeNull;
	final Formatter<? super Object, ? super Object>[] formatters;
	final Check<Param>[] checks;

	/**
	 *
	 * @param keyName     key to retrieve the parameter from Map of string keys
	 * @param displayName the name that the client/user would understand (e.g., primary_email may be the name of a
	 *                    parameter in a JsonObject, but the user sees 'primary email' as the field they are inputting
	 *                    their information into.)
	 * @param canBeNull   whether or not the parameter value is allowed to be SET to null
	 * @param formatters  requiring formatters to return object of Param type. so if a cast needs to happen to Param,
	 *                    it should be done in the first formatter (they are run in order)
	 * @param checks      ensure the value of the parameter is as expected
	 * @param invalidErrMsg overrides all error messages from invalid parameter checks (i.e., when a check fails) and
	 *                      returns this message instead
	 */
	public ApiSingleParam(String keyName,
	                      String displayName,
	                      boolean canBeNull,
	                      Formatter<? super Object, ? super Object>[] formatters,
	                      Check<Param>[] checks,
	                      String invalidErrMsg) {
		super(keyName, displayName);
		this.canBeNull = canBeNull;
		this.formatters = formatters;
		if (checks == null || checks.length == 0) {
			throw new RuntimeException("No checks were provided for parameter (key name) '" + keyName + "'." +
				                           "If this is intentional, provide a function check that returns " +
				                           "a Check.Result. If the check should always pass Check.alwaysPass() has been " +
				                           "provided for this case and Check.alwaysFail() has been provided if the parameter " +
				                           "check should always fail.");
		}
		for (Check check : checks) {
			if (check == null) {
				throw new RuntimeException("Cannot have null values in Check array.");
			}
		}
		this.checks = checks;
		this.invalidErrMsg = invalidErrMsg;
	}

	private ApiSingleParamCheckResult returnInvalidErrorMessage(String errMsg) {
		// if invalid error message is set it overrides all other error messages
		if (this.invalidErrMsg != null) {
			return ApiSingleParamCheckResult.failure(ApiParamError.invalid(this, this.invalidErrMsg));
		} else {
			return errMsg == null
				?
				ApiSingleParamCheckResult.failure(ApiParamError.invalid(this))
				:
				ApiSingleParamCheckResult.failure(ApiParamError.invalid(this, errMsg));
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public ApiSingleParamCheckResult check(Map<String, Object> params) {
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
						return ApiSingleParamCheckResult.success(this.keyName);
					} else {
						return returnInvalidErrorMessage(ApiLibSettings.DEFAULT_CANNOT_BE_NULL_MESSAGE);
					}
				} else {
					// param is null, but was not SET to null. therefore it is missing.
					return ApiSingleParamCheckResult.failure(ApiParamError.missing(this));
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
							ApiSingleParamCheckResult.failure(ApiParamError.format(this, formatResult.getFailureMessage()))
							:
							ApiSingleParamCheckResult.failure(ApiParamError.format(this));
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
					return ApiSingleParamCheckResult.success(this.keyName);
				} else {
					return returnInvalidErrorMessage(ApiLibSettings.DEFAULT_CANNOT_BE_NULL_MESSAGE);
				}
			}

			for (Check<Param> check : checks) {
				Check.Result checkResult = check.check(param);
				if (checkResult.failed()) {
					return returnInvalidErrorMessage(checkResult.failureMessage);
				}
			}

			// parameter was formatted in some way and successfully passed all checks. put back in map for user to access later
			if (formatted) {
				params.put(this.keyName, param);
			}
			return ApiSingleParamCheckResult.success(this.keyName);
		} catch (ClassCastException e) {
			return ApiSingleParamCheckResult.failure(ApiParamError.cast(this, e));
		} catch (Exception e) {
			return ApiSingleParamCheckResult.failure(ApiParamError.exceptional(this, e));
		}
	}

	/**
	 * This is the same as calling {@link ApiSingleParamBuilder#builder(ApiSingleParam)}, but a bit more succinct
	 * and clear for which parameter is used.
	 * @return
	 */
	public ApiSingleParamBuilder<Param> toBuilder() {
		return ApiSingleParamBuilder.builder(this);
	}

	/**
	 * Copies all settings (checks, formatters, and canBeNull) from this ApiSingleParam
	 * and overwrites the keyName and displayName.
	 * @param keyName
	 * @param displayName
	 * @return
	 */
	public ApiSingleParamBuilder<Param> toBuilder(String keyName, String displayName) {
		return ApiSingleParamBuilder.builder(keyName, displayName, this);
	}

}
