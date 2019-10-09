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
public class ApiMapParamBuilder {

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

	private String keyName, displayName, invalidErrorMessage;
	// default to false, avoids potential problems for user down the line
	private boolean canBeNull = false, continueOnOptionalFailure = false;
	// using maps to easily keep track of already added params (by key name)
	private Map<String, ApiSingleParam<?>> requiredSingleParams, optionalSingleParams;
	private Map<String, ApiMapParam> requiredMapParams, optionalMapParams;
	private Map<String, ApiArrayOfMapsParam> requiredArrayOfMapsParams, optionalArrayOfMapsParams;

	// custom parameters and conditional checks do not have names
	private List<ApiCustomParam> requiredCustomParams, optionalCustomParams;
	private List<ConditionalCheck> conditionalChecks;

	public ApiMapParamBuilder() {
		this(null, null);
	}

	public ApiMapParamBuilder(String keyName, String displayName) {
		this.keyName = keyName;
		this.displayName = displayName;
		this.requiredSingleParams = new HashMap<>();
		this.optionalSingleParams = new HashMap<>();
		this.requiredMapParams = new HashMap<>();
		this.optionalMapParams = new HashMap<>();
		this.requiredArrayOfMapsParams = new HashMap<>();
		this.optionalArrayOfMapsParams = new HashMap<>();
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

	public ApiMapParamBuilder setCanBeNull(boolean canBeNull) {
		this.canBeNull = canBeNull;
	  return this;
	}

	public ApiMapParamBuilder setContinueOnOptionalFailure(boolean continueOnOptionalFailure) {
		this.continueOnOptionalFailure = continueOnOptionalFailure;
		return this;
	}

	/**
	 * Allows for providing an error message that will override any message returned by
	 * child parameters. This is not usually recommended, but is available if the user
	 * deems it necessary.
	 * @param errMsg
	 * @return
	 */
	public ApiMapParamBuilder setInvalidErrorMessage(String errMsg) {
		this.invalidErrorMessage = errMsg;
		return this;
	}

	private void checkIsNotNullAndHasNotBeenAdded(ApiParamBase<?> param) {
		if (param == null) {
			throw new IllegalArgumentException("Cannot add null parameter.");
		}
		if (param instanceof ApiSingleParam) {
			if (this.requiredSingleParams.containsKey(param.keyName)) {
				throw new IllegalArgumentException("ApiSingleParam (" + param.keyName +
					                                   ") has already been added to required single params.");
			} else if (this.optionalSingleParams.containsKey(param.keyName)) {
				throw new IllegalArgumentException("ApiSingleParam (" + param.keyName +
					                                   ") has already been added to optional single params.");
			}
		} else if (param instanceof ApiArrayOfMapsParam) {
			if (this.requiredArrayOfMapsParams.containsKey(param.keyName)) {
				throw new IllegalArgumentException("ApiCustomParam (" + param.keyName +
					                                   ") has already been added to required custom params.");
			} else if (this.optionalArrayOfMapsParams.containsKey(param.keyName)) {
				throw new IllegalArgumentException("ApiCustomParam (" + param.keyName +
					                                   ") has already been added to optional custom params.");
			}
		} else if (param instanceof ApiMapParam) {
			if (this.requiredMapParams.containsKey(param.keyName)) {
				throw new IllegalArgumentException("ApiMapParam (" + param.keyName +
					                                   ") has already been added to required map params");
			} else if (this.optionalMapParams.containsKey(param.keyName)) {
				throw new IllegalArgumentException("ApiMapParam (" + param.keyName +
					                                   ") has already been added to optional map params");
			}
		} else {
			throw new RuntimeException("Unknown parameter type.");
		}
	}

	public ApiMapParamBuilder addRequiredSingleParam(ApiSingleParam<?> param) {
		checkIsNotNullAndHasNotBeenAdded(param);
		this.requiredSingleParams.put(param.keyName, param);
		return this;
	}

	public ApiMapParamBuilder addRequiredSingleParams(ApiSingleParam<?>... params) {
		for (ApiSingleParam p : params) {
			this.addRequiredSingleParam(p);
		}
		return this;
	}

	public ApiMapParamBuilder addOptionalSingleParam(ApiSingleParam<?> param) {
		checkIsNotNullAndHasNotBeenAdded(param);
		this.optionalSingleParams.put(param.keyName, param);
		return this;
	}

	public ApiMapParamBuilder addOptionalSingleParams(ApiSingleParam<?>... params) {
		for (ApiSingleParam p : params) {
			this.addOptionalSingleParam(p);
		}
		return this;
	}

	public ApiMapParamBuilder addRequiredMapParam(ApiMapParam param) {
		checkIsNotNullAndHasNotBeenAdded(param);
		this.requiredMapParams.put(param.keyName, param);
		return this;
	}

	public ApiMapParamBuilder addRequiredMapParams(ApiMapParam... params) {
		for (ApiMapParam p : params) {
			this.addRequiredMapParam(p);
		}
		return this;
	}

	public ApiMapParamBuilder addOptionalMapParam(ApiMapParam param) {
		checkIsNotNullAndHasNotBeenAdded(param);
		this.optionalMapParams.put(param.keyName, param);
		return this;
	}

	public ApiMapParamBuilder addOptionalMapParams(ApiMapParam... params) {
		for (ApiMapParam p : params) {
			this.addOptionalMapParam(p);
		}
		return this;
	}

	public ApiMapParamBuilder addRequiredArrayOfMapsParam(ApiArrayOfMapsParam param) {
	  checkIsNotNullAndHasNotBeenAdded(param);
		this.requiredArrayOfMapsParams.put(param.keyName, param);
	  return this;
	}

	public ApiMapParamBuilder addRequiredArrayOfMapsParams(ApiArrayOfMapsParam... params) {
		for (ApiArrayOfMapsParam param : params) {
			this.addRequiredArrayOfMapsParam(param);
		}
		return this;
	}

	public ApiMapParamBuilder addOptionalArrayOfMapsParam(ApiArrayOfMapsParam param) {
	  checkIsNotNullAndHasNotBeenAdded(param);
		this.optionalArrayOfMapsParams.put(param.keyName, param);
	  return this;
	}

	public ApiMapParamBuilder addOptionalArrayOfMapsParams(ApiArrayOfMapsParam... params) {
		for (ApiArrayOfMapsParam param : params) {
			this.addOptionalArrayOfMapsParam(param);
		}
		return this;
	}


	public ApiMapParamBuilder addRequiredCustomParam(ApiCustomParam param) {
		if (param == null) {
			throw new IllegalArgumentException("Cannot add null custom parameter");
		}
		this.requiredCustomParams.add(param);
		return this;
	}

	public ApiMapParamBuilder addRequiredCustomParams(ApiCustomParam... params) {
		for (ApiCustomParam p : params) {
			this.addRequiredCustomParam(p);
		}
		return this;
	}

	public ApiMapParamBuilder addOptionalCustomParam(ApiCustomParam param) {
		if (param == null) {
			throw new IllegalArgumentException("Cannot add null custom parameter");
		}
		this.optionalCustomParams.add(param);
		return this;
	}

	public ApiMapParamBuilder addOptionalCustomParams(ApiCustomParam... params) {
		for (ApiCustomParam p : params) {
			this.addOptionalCustomParam(p);
		}
		return this;
	}
	public ApiMapParamBuilder addConditonalCheck(ConditionalCheck conditionalCheck) {
		if (conditionalCheck == null) {
			throw new IllegalArgumentException("Cannot add null check");
		}
		// because these are not named, just make sure the same check has not been added before
		if (!this.conditionalChecks.contains(conditionalCheck)) {
			this.conditionalChecks.add(conditionalCheck);
		}
		return this;
	}

	public ApiMapParamBuilder addConditonalChecks(ConditionalCheck... conditionalChecks) {
		for (ConditionalCheck cc : conditionalChecks) {
			this.addConditonalCheck(cc);
		}
		return this;
	}

	private static <T extends ApiParamBase<?>> Map<String, T> mergeCopyFromParams(T[] copyFromParams,
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

	public ApiMapParam build() {
		if (this.copyFrom != null) {
			this.requiredSingleParams = mergeCopyFromParams(this.copyFrom.requiredParams, this.requiredSingleParams);
			this.optionalSingleParams = mergeCopyFromParams(this.copyFrom.optionalParams, this.optionalSingleParams);
			this.requiredMapParams = mergeCopyFromParams(this.copyFrom.requiredMapParams, this.requiredMapParams);
			this.optionalMapParams = mergeCopyFromParams(this.copyFrom.optionalMapParams, this.optionalMapParams);
			this.requiredArrayOfMapsParams = mergeCopyFromParams(this.copyFrom.requiredArrayOfMapsParam,
			                                                     this.requiredArrayOfMapsParams);
			this.optionalArrayOfMapsParams = mergeCopyFromParams(this.copyFrom.optionalArrayOfMapsParam,
			                                                     this.optionalArrayOfMapsParams);

			this.requiredCustomParams = mergeCopyFromList(this.copyFrom.requiredCustomParams, this.requiredCustomParams);
			this.optionalCustomParams = mergeCopyFromList(this.copyFrom.optionalCustomParams, this.optionalCustomParams);
			this.conditionalChecks = mergeCopyFromList(this.copyFrom.conditionalChecks,
			                                           this.conditionalChecks);
		}
		return new ApiMapParam(this.keyName,
		                       this.displayName,
		                       this.canBeNull,
		                       this.continueOnOptionalFailure,
		                       this.requiredSingleParams.values().toArray(new ApiSingleParam[0]),
		                       this.optionalSingleParams.values().toArray(new ApiSingleParam[0]),
		                       this.requiredMapParams.values().toArray(new ApiMapParam[0]),
		                       this.optionalMapParams.values().toArray(new ApiMapParam[0]),
		                       this.requiredArrayOfMapsParams.values().toArray(new ApiArrayOfMapsParam[0]),
		                       this.optionalArrayOfMapsParams.values().toArray(new ApiArrayOfMapsParam[0]),
		                       this.requiredCustomParams.toArray(new ApiCustomParam[0]),
		                       this.optionalCustomParams.toArray(new ApiCustomParam[0]),
		                       this.conditionalChecks.toArray(new ConditionalCheck[0]));
	}

}
