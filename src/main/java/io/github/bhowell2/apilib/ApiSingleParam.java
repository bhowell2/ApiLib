package io.github.bhowell2.apilib;

import io.github.bhowell2.apilib.checks.Check;
import io.github.bhowell2.apilib.formatters.Formatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
public class ApiSingleParam<Param> extends ApiParamBase<Map<String, Object>, ApiSingleCheckResult> {

	public static <Param> ApiSingleParam.Builder<Param> builder(String keyName) {
		return new Builder<>(keyName);
	}

	public static <Param> ApiSingleParam.Builder<Param> builder(String keyName, Class<Param> paramType) {
		return new Builder<>(keyName);
	}

	final Formatter<? super Object, ? super Object>[] formatters;
	final Check<Param>[] checks;

	public static class Builder<Param> extends ApiParamBase.Builder<Builder<Param>> {

		private List<Check<Param>> checks;
		private List<Formatter<?,?>> formatters;

		public Builder(String keyName) {
			super(keyName);
			this.checks = new ArrayList<>(1);
			this.formatters = new ArrayList<>();
		}

		public Builder(String keyName, ApiSingleParam<Param> copyFrom) {
			super(keyName);
			this.checks = new ArrayList<>(Arrays.asList(copyFrom.checks));
			this.formatters = (copyFrom.formatters != null && copyFrom.formatters.length > 0)
				? new ArrayList<>(Arrays.asList(copyFrom.formatters))
				: new ArrayList<>();
		}

		@SafeVarargs
		@SuppressWarnings("varargs")
		public final Builder<Param> addChecks(Check<Param>... checks) {
			checkVarArgsNotNullAndValuesNotNull(checks);
			this.checks.addAll(Arrays.asList(checks));
			return this;
		}

		/**
		 * Formatters are applied in the order they are added, so if a later formatter depends on the parameter being of a
		 * different type then it must come after the formatter that cast the parameter to a different type.
		 * @param formatters
		 * @return
		 */
		@SuppressWarnings("varargs")
		public Builder<Param> addFormatters(Formatter<?, ?>... formatters) {
			checkVarArgsNotNullAndValuesNotNull(formatters);
			this.formatters.addAll(Arrays.asList(formatters));
			return this;
		}

		public ApiSingleParam<Param> build() {
			return new ApiSingleParam<>(this);
		}

	}

	public static class Result extends ApiNamedCheckResultBase {
		public Result(String keyName) {
			super(keyName);
		}
	}

	@SuppressWarnings("unchecked")
	private ApiSingleParam(Builder<Param> builder) {
		super(builder);
		this.checks = builder.checks.toArray(new Check[0]);
		this.formatters = builder.formatters.toArray(new Formatter[0]);
	}

	/**
	 *
	 * @param keyName     key to retrieve the parameter from Map of string keys
	 * @param displayName the name that the client/user would understand (e.g., primary_email may be the name of a
	 *                    parameter in a JsonObject, but the user sees 'primary email' as the field they are inputting
	 *                    their information into.)
	 * @param invalidErrorMessage overrides all error messages from invalid parameter checks (i.e., when a check fails) and
	 *                      returns this message instead
	 * @param canBeNull   whether or not the parameter value is allowed to be SET to null
	 * @param formatters  requiring formatters to return object of Param type. so if a cast needs to happen to Param,
	 *                    it should be done in the first formatter (they are run in order)
	 * @param checks      ensure the value of the parameter is as expected
	 */
	public ApiSingleParam(String keyName,
	                      String displayName,
	                      String invalidErrorMessage,
	                      boolean canBeNull,
	                      Formatter<? super Object, ? super Object>[] formatters,
	                      Check<Param>[] checks) {
		super(keyName, displayName, invalidErrorMessage, canBeNull);
		if (keyName == null) {
			throw new IllegalArgumentException("Cannot create ApiSingleParam with null keyName.");
		} else if (displayName == null) {
			throw new IllegalArgumentException("Cannot create ApiSingleParam with null displayName.");
		}
		this.formatters = formatters;
		if (checks == null || checks.length == 0) {
			throw new RuntimeException("No checks were provided for parameter (key name) '" + keyName + "'." +
				                           "If this is intentional, provide a check that always passes or always fails." +
				                           "If the check should always pass Check.alwaysPass() has been provided for this " +
				                           "case and Check.alwaysFail() has been provided if the parameter check should " +
				                           "always fail.");
		}
		for (Check check : checks) {
			if (check == null) {
				throw new RuntimeException("Cannot have null values in Check array.");
			}
		}
		this.checks = checks;
	}

	private ApiSingleCheckResult returnInvalidErrorMessage(String errMsg) {
		// if invalid error message is set it overrides all other error messages
		if (this.invalidErrorMessage != null) {
			return ApiSingleCheckResult.failure(ApiParamError.invalid(this, this.invalidErrorMessage));
		} else {
			return errMsg == null
				?
				ApiSingleCheckResult.failure(ApiParamError.invalid(this))
				:
				ApiSingleCheckResult.failure(ApiParamError.invalid(this, errMsg));
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public ApiSingleCheckResult check(Map<String, Object> params) {
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
						return ApiSingleCheckResult.success(this.keyName);
					} else {
						return returnInvalidErrorMessage(ApiLibSettings.DEFAULT_CANNOT_BE_NULL_MESSAGE);
					}
				} else {
					// param is null, but was not SET to null. therefore it is missing.
					return ApiSingleCheckResult.failure(ApiParamError.missing(this));
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
							ApiSingleCheckResult.failure(ApiParamError.format(this, formatResult.getFailureMessage()))
							:
							ApiSingleCheckResult.failure(ApiParamError.format(this));
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
					return ApiSingleCheckResult.success(this.keyName);
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
			return ApiSingleCheckResult.success(this.keyName);
		} catch (ClassCastException e) {
			return ApiSingleCheckResult.failure(ApiParamError.cast(this, e));
		} catch (Exception e) {
			return ApiSingleCheckResult.failure(ApiParamError.exceptional(this, e));
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
