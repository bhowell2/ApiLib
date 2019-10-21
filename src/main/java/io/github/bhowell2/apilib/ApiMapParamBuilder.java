package io.github.bhowell2.apilib;

import io.github.bhowell2.apilib.checks.ConditionalCheck;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Blake Howell
 */
public class ApiMapParamBuilder extends ApiParamBuilderBase<ApiMapParamBuilder> {

	public static ApiMapParamBuilder rootBuilder() {
		return new ApiMapParamBuilder();
	}

	public static ApiMapParamBuilder builder(String keyName, String displayName) {
		return new ApiMapParamBuilder(keyName, displayName);
	}

	public static ApiMapParamBuilder builder(ApiMapParam copyFrom) {
		return new ApiMapParamBuilder(copyFrom.keyName, copyFrom.displayName, copyFrom);
	}

	public static ApiMapParamBuilder builder(String keyName, String displayName, ApiMapParam copyFrom) {
		return new ApiMapParamBuilder(keyName, displayName, copyFrom);
	}

	private ApiMapParam copyFrom;

	// default to false, avoids potential problems for user down the line
	private boolean canBeNull = false, continueOnOptionalFailure = false;
	// using maps to easily keep track of already added params (by key name)
	private Map<String, ApiSingleParam<?>> requiredSingleParams, optionalSingleParams;
	private Map<String, ApiMapParam> requiredMapParams, optionalMapParams;
	private Map<String, ApiArrayOrListParam<Map<String, Object>, ?>> requiredArrayParams, optionalArrayParams;

	// custom parameters and conditional checks do not have names
	private List<ApiCustomParam<Map<String, Object>, ?>> requiredCustomParams, optionalCustomParams;
	private List<ConditionalCheck> conditionalChecks;

	public ApiMapParamBuilder() {
		this(null, null);
	}

	public ApiMapParamBuilder(String keyName, String displayName) {
		super(keyName, displayName);
		this.requiredSingleParams = new HashMap<>();
		this.optionalSingleParams = new HashMap<>();
		this.requiredMapParams = new HashMap<>();
		this.optionalMapParams = new HashMap<>();
		this.requiredArrayParams = new HashMap<>();
		this.optionalArrayParams = new HashMap<>();
		this.requiredCustomParams = new ArrayList<>();
		this.optionalCustomParams = new ArrayList<>();
		this.conditionalChecks = new ArrayList<>();
	}

	public ApiMapParamBuilder(String keyName, String displayName, ApiMapParam copyFrom) {
		this(keyName, displayName);
		this.copyFrom = copyFrom;
		/*
		 * Do not want to copy this at the end like the other parameters, because it would not be possible to
		 * tell if it had already been overridden before calling build and it would be unclear which one to use
		 * (without introducing another variable to track if the user actually set it in the new builder..).
		 * Thus, defaulting to setting it here and allowing it to be overridden by the user during the build
		 * process.
		 *
		 * Will NOT add copyFrom's parameters until build() is called. This allows for adding parameters to this
		 * builder that override (by keyName) those in copyFrom and ensure that the same parameter (by keyName)
		 * is not added more than once to this builder. The parameters of this builder will override any of the
		 * same keyName from copyFrom.
		 * */
		this.canBeNull = copyFrom.canBeNull;
		this.continueOnOptionalFailure = copyFrom.continueOnOptionalFailure;
		/*
		 * Do not have a name for conditional checks, so any conditional checks are added as is and no overlap
		 * is checked.
		 * */
		this.conditionalChecks = new ArrayList<>(Arrays.asList(copyFrom.conditionalChecks));
	}

	public ApiMapParamBuilder setContinueOnOptionalFailure(boolean continueOnOptionalFailure) {
		this.continueOnOptionalFailure = continueOnOptionalFailure;
		return this;
	}

	// checks that the parameter by the given name has not been added to
	private void checkHasNotBeenAdded(ApiParamBase<?, ?> param) {
		// redundancy check since checkVarArgs... is used everywhere
		if (param == null) {
			throw new IllegalArgumentException("Cannot add null parameter.");
		}
		String keyName = param.keyName;
		if (keyName == null) {
			throw new IllegalArgumentException("Cannot add parameter with null key name to ApiMapParam");
		}
		if (this.requiredSingleParams.containsKey(keyName)) {
			throw new IllegalArgumentException(keyName + " has already been added to required single params.");
		} else if (this.optionalSingleParams.containsKey(keyName)) {
			throw new IllegalArgumentException(keyName + " has already been added to optional single params.");
		} else if (this.requiredArrayParams.containsKey(keyName)) {
			throw new IllegalArgumentException(keyName + " has already been added to required array params.");
		} else if (this.optionalArrayParams.containsKey(keyName)) {
			throw new IllegalArgumentException(keyName + " has already been added to optional array params.");
		} else if (this.requiredMapParams.containsKey(keyName)) {
			throw new IllegalArgumentException(keyName + " has already been added to required map params.");
		} else if (this.optionalMapParams.containsKey(keyName)) {
			throw new IllegalArgumentException(keyName + " has already been added to optional map params.");
		}
	}

	public ApiMapParamBuilder addRequiredSingleParams(ApiSingleParam<?>... params) {
		checkVarArgsNotNullAndValuesNotNull(params);
		for (ApiSingleParam p : params) {
			checkHasNotBeenAdded(p);
			this.requiredSingleParams.put(p.keyName, p);
		}
		return this;
	}

	public ApiMapParamBuilder addOptionalSingleParams(ApiSingleParam<?>... params) {
		checkVarArgsNotNullAndValuesNotNull(params);
		for (ApiSingleParam p : params) {
			checkHasNotBeenAdded(p);
			this.optionalSingleParams.put(p.keyName, p);
		}
		return this;
	}

	public ApiMapParamBuilder addRequiredMapParams(ApiMapParam... params) {
		checkVarArgsNotNullAndValuesNotNull(params);
		for (ApiMapParam p : params) {
			checkHasNotBeenAdded(p);
			this.requiredMapParams.put(p.keyName, p);
		}
		return this;
	}

	public ApiMapParamBuilder addOptionalMapParams(ApiMapParam... params) {
		checkVarArgsNotNullAndValuesNotNull(params);
		for (ApiMapParam p : params) {
			checkHasNotBeenAdded(p);
			this.optionalMapParams.put(p.keyName, p);
		}
		return this;
	}

	@SafeVarargs
	public final ApiMapParamBuilder addRequiredArrayOfMapsParam(ApiArrayOrListParam<Map<String, Object>, ?>... params) {
		checkVarArgsNotNullAndValuesNotNull(params);
		for (ApiArrayOrListParam<Map<String, Object>, ?> p : params) {
			checkHasNotBeenAdded(p);
			this.requiredArrayParams.put(p.keyName, p);
		}
		return this;
	}

	@SafeVarargs
	public final ApiMapParamBuilder addOptionalArrayOfMapsParam(ApiArrayOrListParam<Map<String, Object>, ?>... params) {
		checkVarArgsNotNullAndValuesNotNull(params);
		for (ApiArrayOrListParam<Map<String, Object>, ?> p : params) {
			checkHasNotBeenAdded(p);
			this.optionalArrayParams.put(p.keyName, p);
		}
		return this;
	}

	@SafeVarargs
	public final ApiMapParamBuilder addRequiredCustomParams(ApiCustomParam<Map<String, Object>, ?>... params) {
		checkVarArgsNotNullAndValuesNotNull(params);
		this.requiredCustomParams.addAll(Arrays.asList(params));
		return this;
	}

	@SafeVarargs
	public final ApiMapParamBuilder addOptionalCustomParam(ApiCustomParam<Map<String, Object>, ?>... params) {
		checkVarArgsNotNullAndValuesNotNull(params);
		this.optionalCustomParams.addAll(Arrays.asList(params));
		return this;
	}

	public ApiMapParamBuilder addConditionalChecks(ConditionalCheck... conditionalChecks) {
		checkVarArgsNotNullAndValuesNotNull(conditionalChecks);
		this.conditionalChecks.addAll(Arrays.asList(conditionalChecks));
		return this;
	}

	private static <T extends ApiParamBase<?, ?>> Map<String, T> mergeCopyFromParams(T[] copyFromParams,
	                                                                              Map<String, T> builderMap) {
		Map<String, T> copyMap = new HashMap<>();
		for (T p : copyFromParams) {
			copyMap.put(p.keyName, p);
		}
		copyMap.putAll(builderMap);
		return copyMap;
	}

	private static <T> List<T> mergeCopyFromList(T[] copyFromList, List<T> builderList) {
		List<T> copyList = new ArrayList<>(Arrays.asList(copyFromList));
		copyList.addAll(builderList);
		return copyList;
	}

	@SuppressWarnings("unchecked")
	public ApiMapParam build() {
		if (this.copyFrom != null) {
			this.requiredSingleParams = mergeCopyFromParams(this.copyFrom.requiredSingleParams, this.requiredSingleParams);
			this.optionalSingleParams = mergeCopyFromParams(this.copyFrom.optionalSingleParams, this.optionalSingleParams);
			this.requiredMapParams = mergeCopyFromParams(this.copyFrom.requiredMapParams, this.requiredMapParams);
			this.optionalMapParams = mergeCopyFromParams(this.copyFrom.optionalMapParams, this.optionalMapParams);
			this.requiredArrayParams = mergeCopyFromParams(this.copyFrom.requiredArrayParams, this.requiredArrayParams);
			this.optionalArrayParams = mergeCopyFromParams(this.copyFrom.optionalArrayParams, this.optionalArrayParams);
			this.requiredCustomParams = mergeCopyFromList(this.copyFrom.requiredCustomParams, this.requiredCustomParams);
			this.optionalCustomParams = mergeCopyFromList(this.copyFrom.optionalCustomParams, this.optionalCustomParams);
			this.conditionalChecks = mergeCopyFromList(this.copyFrom.conditionalChecks, this.conditionalChecks);
		}
		return new ApiMapParam(this.keyName,
		                       this.displayName,
		                       this.invalidErrorMessage,
		                       this.canBeNull,
		                       this.continueOnOptionalFailure,
		                       this.requiredSingleParams.values().toArray(new ApiSingleParam[0]),
		                       this.optionalSingleParams.values().toArray(new ApiSingleParam[0]),
		                       this.requiredMapParams.values().toArray(new ApiMapParam[0]),
		                       this.optionalMapParams.values().toArray(new ApiMapParam[0]),
		                       this.requiredArrayParams.values().toArray(new ApiArrayOrListParam[0]),
		                       this.optionalArrayParams.values().toArray(new ApiArrayOrListParam[0]),
		                       this.requiredCustomParams.toArray(new ApiCustomParam[0]),
		                       this.optionalCustomParams.toArray(new ApiCustomParam[0]),
		                       this.conditionalChecks.toArray(new ConditionalCheck[0]));
	}

}

