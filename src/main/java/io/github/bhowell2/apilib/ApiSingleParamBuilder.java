package io.github.bhowell2.apilib;

import io.github.bhowell2.apilib.checks.Check;
import io.github.bhowell2.apilib.formatters.Formatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Builder to help create an {@link ApiSingleParam}.
 * @author Blake Howell
 */
public class ApiSingleParamBuilder<Param> extends ApiParamBuilderBase<ApiSingleParamBuilder<Param>> {

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

	protected List<Check<Param>> checks;
	protected List<Formatter<?, ?>> formatters;

	/**
	 * @param keyName the name of the parameter
	 * @param displayName   the name that will be returned in error messages
	 */
	public ApiSingleParamBuilder(String keyName, String displayName) {
		super(keyName, displayName);
		this.checks = new ArrayList<>(1);       // must have at least 1
		this.formatters = new ArrayList<>(0);
	}

	@SafeVarargs
	@SuppressWarnings("varargs")
	public final ApiSingleParamBuilder<Param> addChecks(Check<Param>... checks) {
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
	public ApiSingleParamBuilder<Param> addFormatters(Formatter<?, ?>... formatters) {
		checkVarArgsNotNullAndValuesNotNull(formatters);
		this.formatters.addAll(Arrays.asList(formatters));
		return this;
	}

	@SuppressWarnings("unchecked")
	public ApiSingleParam<Param> build() {
		return new ApiSingleParam<>(keyName,
		                            displayName,
		                            invalidErrorMessage,
		                            canBeNull,
		                            formatters.toArray(new Formatter[0]),
		                            checks.toArray(new Check[0]));
	}

}
