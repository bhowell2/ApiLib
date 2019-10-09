package io.github.bhowell2.apilib;

import io.github.bhowell2.apilib.checks.Check;
import io.github.bhowell2.apilib.formatters.Formatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder to help create an {@link ApiSingleParam}.
 * @author Blake Howell
 */
public class ApiSingleParamBuilder<Param> {

	/**
	 * Creates a builder with the keyName and displayName set to the keyName.
	 *
	 * Will need to parameterize the builder like this:
	 * {@code ApiSingleParam<String> param = ApiSingleParamBuilder.<String>builder("aparam").build();}
	 *
	 * It may be more desirable to use {@link #builder(String, Class)}, which looks like:
	 * {@code ApiSingleParam<String> param = ApiSingleParamBuilder.builder("aparam", String.class).build();}
	 *
	 * @param keyName
	 * @param <Param>
	 * @return
	 */
	public static <Param> ApiSingleParamBuilder<Param> builder(String keyName) {
		return builder(keyName, keyName);
	}

	/**
	 * Same as {@link #builder(String)}, but provided with class type so that the user can parameterize it in a
	 * different manner.
	 *
	 * Using this will result in:
	 * {@code ApiSingleParam<String> param = ApiSingleParamBuilder.builder("aparam", String.class).build();}
	 *
	 * Otherwise, will need to look like this:
	 * {@code ApiSingleParam<String> param = ApiSingleParamBuilder.<String>builder("aparam").build();}
	 *
	 * @param keyName
	 * @param paramClass
	 * @param <Param>
	 * @return
	 */
	public static <Param> ApiSingleParamBuilder<Param> builder(String keyName, Class<Param> paramClass) {
		return builder(keyName);
	}

	/**
	 * @param keyName     the name used to retrieve the parameter from the ParamsObj (e.g., map key)
	 * @param displayName the name that will appear when returning error messages (so they can be sent back to user)
	 * @param <Param> type of the parameter that will be checked
	 * @return a builder to add on to
	 */
	public static <Param> ApiSingleParamBuilder<Param> builder(String keyName, String displayName) {
		return new ApiSingleParamBuilder<>(keyName, displayName);
	}

	public static <Param> ApiSingleParamBuilder<Param> builder(String keyName,
	                                                           String displayName,
	                                                           Class<Param> paramClass) {
		return builder(keyName, displayName);
	}

	/**
	 * Allows the copying of a previously created ApiParam. There will be cases where a user wants almost the
	 * exact same parameter, but possibly with additional checks added.
	 * @return the builder
	 */
	public static <Param> ApiSingleParamBuilder<Param> builder(ApiSingleParam<Param> apiParam) {
		return builder(apiParam.keyName, apiParam.displayName, apiParam);
	}

	/**
	 * Allows the copying of a previously created ApiParameter. There will likely be cases where the user wants to have
	 * all of the same information/checks, but possibly wants to change the name and add more checks.
	 *
	 * @param keyName the name used to retrieve the parameter from the ParamsObj (e.g., map key)
	 * @param displayName   the name that will appear when returning error messages (so they can be sent back to user)
	 * @param copyParam         the parameter to copy
	 * @param <Param>       type of the parameter that will be checked
	 * @return
	 */
	public static <Param> ApiSingleParamBuilder<Param> builder(String keyName,
	                                                           String displayName,
	                                                           ApiSingleParam<Param> copyParam) {
		return new ApiSingleParamBuilder<Param>(keyName, displayName)
			.setCanBeNull(copyParam.canBeNull)
			.setInvalidErrorMessage(copyParam.invalidErrMsg)
			.addChecks(copyParam.checks)
			.addFormatters(copyParam.formatters);
	}

	protected boolean canBeNull = false;
	protected String keyName, displayName, invalidErrorMsg;
	protected List<Check<Param>> paramChecks;
	protected List<Formatter<?, ?>> paramFormatters;

	/**
	 * @param keyName the name of the parameter
	 * @param displayName   the name that will be returned in error messages
	 */
	public ApiSingleParamBuilder(String keyName, String displayName) {
		this.keyName = keyName;
		this.displayName = displayName;
		this.paramChecks = new ArrayList<>(1);       // must have at least 1
		this.paramFormatters = new ArrayList<>(0);
	}

	/**
	 * Set whether or not the parameter is allowed to be null. This is used when the user needs to differentiate
	 * whether or no the user provided the parameter and set it to null or if the parameter was just not provided.
	 * @param canBeNull
	 * @return
	 */
	public ApiSingleParamBuilder<Param> setCanBeNull(boolean canBeNull) {
		this.canBeNull = canBeNull;
		return this;
	}

	public ApiSingleParamBuilder<Param> setInvalidErrorMessage(String errMsg) {
		this.invalidErrorMsg = errMsg;
		return this;
	}

	public ApiSingleParamBuilder<Param> addCheck(Check<Param> paramCheck) {
		if (paramCheck == null) {
			throw new RuntimeException("Cannot add null ParamCheck");
		}
		this.paramChecks.add(paramCheck);
		return this;
	}

	@SafeVarargs
	@SuppressWarnings("varargs")
	public final ApiSingleParamBuilder<Param> addChecks(Check<Param>... paramChecks) {
		for (Check<Param> check : paramChecks) {
			this.addCheck(check);
		}
		return this;
	}

	/**
	 * Formatters are applied in the order they are added, so if a later formatter depends on the parameter being of a
	 * different type then it must come after the formatter that cast the parameter to a different type.
	 * @param formatter
	 * @return
	 */
	public ApiSingleParamBuilder<Param> addFormatter(Formatter<?, ?> formatter) {
		if (formatter == null) {
			throw new RuntimeException("Cannot add null Formatter");
		}
		this.paramFormatters.add(formatter);
		return this;
	}

	@SuppressWarnings("varargs")
	public ApiSingleParamBuilder<Param> addFormatters(Formatter<?, ?>... formatters) {
		for (Formatter<?,?> f : formatters) {
			this.addFormatter(f);
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	public ApiSingleParam<Param> build() {
		return new ApiSingleParam<>(keyName,
		                            displayName,
		                            canBeNull,
		                            paramFormatters.toArray(new Formatter[0]),
		                            paramChecks.toArray(new Check[0]),
		                            invalidErrorMsg);
	}

}
