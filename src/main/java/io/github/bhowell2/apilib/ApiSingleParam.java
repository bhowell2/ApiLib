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
public class ApiSingleParam<Param> extends ApiParamBase<Map<String, Object>, ApiSingleParam.Result> {

	/**
	 * Creates a builder for {@link ApiSingleParam}. A key name is required for a
	 * single parameter so that the parameter can be retrieved from the Map input
	 * into {@link #check(Map)}.
	 *
	 * canBeNull defaults to false
	 * displayName defaults to null
	 * invalidErrorMessage defaults to null
	 *
	 * @param keyName name used to retrieve the parameter from a Map. cannot be null.
	 * @param <Param> the parameter's type
	 * @return a builder for the parameter
	 */
	public static <Param> Builder<Param> builder(String keyName) {
		return new Builder<>(keyName);
	}

	/**
	 * Creates a builder for {@link ApiSingleParam}. A key name is required for a
	 * single parameter so that the parameter can be retrieved from the Map input
	 * into {@link #check(Map)}.
	 *
	 * canBeNull defaults to false
	 * displayName defaults to null
	 * invalidErrorMessage defaults to null
	 *
	 * @param keyName name used to retrieve the parameter from a Map. cannot be null.
	 * @param paramType allows for providing the param type as a parameter rather than
	 *                  parameterizing the method call or casting.
	 * @param <Param> the parameter's type (will be the Class provided as parameter)
	 * @return a builder for the parameter
	 */
	public static <Param> Builder<Param> builder(String keyName, Class<Param> paramType) {
		return builder(keyName);
	}

	/**
	 * Creates a builder for {@link ApiSingleParam} that uses all of the fields
	 * from the supplied ApiSingleParam to copy. A key name is required for a
	 * single parameter so that the parameter can be retrieved from the Map input
	 * into {@link #check(Map)}.
	 *
	 * The copied fields include: keyName, displayName, canBeNull,
	 * invalidErrorMessage, formatters, and checks.
	 *
	 * @param copyFrom the single parameter to copy
	 * @param <Param> the parameter's type
	 * @return a builder for the parameter
	 */
	public static <Param> Builder<Param> builder(ApiSingleParam<Param> copyFrom) {
		return new Builder<>(copyFrom.keyName, copyFrom);
	}

	/**
	 * Creates a builder for {@link ApiSingleParam} that uses all of the fields
	 * from the supplied ApiSingleParam to copy, but uses a different key name.
	 * A key name is required for a single parameter so that the parameter can
	 * be retrieved from the Map input into {@link #check(Map)}.
	 *
	 * The copied fields include: displayName, canBeNull, invalidErrorMessage,
	 * formatters, and checks.
	 *
	 * @param keyName name used to retrieve the parameter from a Map. cannot be null.
	 * @param copyFrom the single parameter to copy
	 * @param <Param> the parameter's type
	 * @return a builder for the parameter
	 */
	public static <Param> Builder<Param> builder(String keyName, ApiSingleParam<Param> copyFrom) {
		return new Builder<>(keyName, copyFrom);
	}

	final Formatter<? super Object, ? super Object>[] formatters;
	final Check<Param>[] checks;

	public static class Builder<Param> extends ApiParamBase.Builder<
		ApiSingleParam<Param>,
		Builder<Param>
		> {

		private List<Check<Param>> checks;
		private List<Formatter<?,?>> formatters;

		public Builder(String keyName) {
			super(keyName);
			this.checks = new ArrayList<>(1);
			this.formatters = new ArrayList<>();
		}

		public Builder(String keyName, ApiSingleParam<Param> copyFrom) {
			this(keyName);
			// checks can never be null or empty
			this.checks = new ArrayList<>(Arrays.asList(copyFrom.checks));
			this.formatters = arrayIsNotNullOrEmpty(copyFrom.formatters)
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
			if (this.formatters == null) {
				this.formatters = new ArrayList<>(formatters.length);
			}
			this.formatters.addAll(Arrays.asList(formatters));
			return this;
		}

		public ApiSingleParam<Param> build() {
			// check key name
			if (keyName == null) {
				throw new IllegalArgumentException("Cannot create ApiSingleParam with null keyName.");
			}
			// ensure some checks are provided
			if (checks == null || checks.size() == 0) {
				throw new RuntimeException("No checks were provided for parameter (key name) '" + keyName + "'." +
					                           "If this is intentional, provide a check that always passes or always fails." +
					                           "If the check should always pass Check.alwaysPass() has been provided for this " +
					                           "case and Check.alwaysFail() has been provided if the parameter check should " +
					                           "always fail.");
			}
			return new ApiSingleParam<>(this);
		}

	}

	/**
	 * Result for {@link ApiSingleParam#check(Map)}.
	 */
	public static class Result extends ApiParamBase.Result {

		public Result(String keyName) {
			super(keyName);
		}

		public Result(ApiParamError error) {
			super(error);
		}

		public static Result success(String keyName) {
			return new Result(keyName);
		}

		public static Result failure(ApiParamError error) {
			return new Result(error);
		}

	}

	@SuppressWarnings("unchecked")
	private ApiSingleParam(Builder<Param> builder) {
		super(builder);
		this.checks = builder.checks.toArray(new Check[0]);
		this.formatters = listIsNotNullOrEmpty(builder.formatters)
			? builder.formatters.toArray(new Formatter[0])
			: null;
	}

	private Result returnInvalidErrorMessage(String errMsg) {
		// if invalid error message is set it overrides all other error messages
		if (this.invalidErrorMessage != null) {
			return Result.failure(ApiParamError.invalid(this, this.invalidErrorMessage));
		} else {
			return errMsg == null
				?
				Result.failure(ApiParamError.invalid(this))
				:
				Result.failure(ApiParamError.invalid(this, errMsg));
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public Result check(Map<String, Object> params) {
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
						return Result.success(this.keyName);
					} else {
						return returnInvalidErrorMessage(ApiLibSettings.DEFAULT_CANNOT_BE_NULL_MESSAGE);
					}
				} else {
					// param is null, but was not SET to null. therefore it is missing.
					return Result.failure(ApiParamError.missing(this));
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
							Result.failure(ApiParamError.format(this, formatResult.getFailureMessage()))
							:
							Result.failure(ApiParamError.format(this));
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
					return Result.success(this.keyName);
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
			return Result.success(this.keyName);
		} catch (ClassCastException e) {
			return Result.failure(ApiParamError.cast(this, e));
		} catch (Exception e) {
			return Result.failure(ApiParamError.exceptional(this, e));
		}
	}

	/**
	 * Copies all settings (checks, formatters, and canBeNull) from this ApiSingleParam
	 * and overwrites the keyName and displayName.
	 * @param keyName
	 * @param displayName
	 * @return
	 */
	public Builder<Param> toBuilder(String keyName, String displayName) {
		return ApiSingleParam.builder(keyName, this)
		                     .setDisplayName(displayName);
	}

}
