package io.github.bhowell2.apilib;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Result of check for {@link ApiMapParam}. Contains all possible return types.
 * @author Blake Howell
 */
public class ApiMapCheckResult extends ApiCheckResultBase {

	/**
	 * Name (keyName) of all successfully checked parameters. Note: this
	 * contains all parameter's key names in the {@link #checkedArrayOfMapsParams}
	 * and {@link #checkedMapParams}.
	 */
	final Set<String> checkedKeyNames;

	/**
	 * Name (keyName) of the successfully checked arrays/lists of maps.
	 * If the innermost {@link ApiArrayOrListCheckResult} does not contain
	 * {@link ApiArrayOrListCheckResult#mapCheckResults} then it will
	 * be ignored and only appear in {@link #checkedKeyNames}.
	 */
	final Map<String, ApiArrayOrListCheckResult> checkedArrayOfMapsParams;

	/**
	 * Name (keyName) of the successfully checked map parameters. These are
	 * nested maps within this checked map parameter.
	 */
	final Map<String, ApiMapCheckResult> checkedMapParams;

	/**
	 * If {@link ApiCustomCheckResult} returns a customValue, it will
	 * be available here. This requires the ApiCustomCheckResult return
	 * a key name (enforced by constructor).
	 */
	final Map<String, Object> customValues;

	public ApiMapCheckResult(Set<String> checkedKeyNames,
	                         Map<String, ApiMapCheckResult> checkedMapParams,
	                         Map<String, ApiArrayOrListCheckResult> checkedArrayOfMapsParams,
	                         Map<String, Object> customValues) {
		this(null, checkedKeyNames, checkedMapParams, checkedArrayOfMapsParams, customValues);
	}

	public ApiMapCheckResult(String keyName,
	                         Set<String> checkedKeyNames,
	                         Map<String, ApiMapCheckResult> checkedMapParams,
	                         Map<String, ApiArrayOrListCheckResult> checkedArrayOfMapsParams,
	                         Map<String, Object> customValues) {
		super(keyName);
		this.checkedKeyNames = checkedKeyNames;
		this.checkedMapParams = checkedMapParams;
		this.checkedArrayOfMapsParams = checkedArrayOfMapsParams;
		this.customValues = customValues;
	}

	public ApiMapCheckResult(ApiParamError apiParamError) {
		super(apiParamError);
		this.checkedKeyNames = null;
		this.checkedMapParams = null;
		this.checkedArrayOfMapsParams = null;
		this.customValues = null;
	}

	public boolean failed() {
		return error != null;
	}

	public boolean successful() {
		return error == null;
	}

	public boolean hasCheckedKeyNames() {
		return this.checkedKeyNames != null;
	}

	public Set<String> getCheckedKeyNames() {
		return this.checkedKeyNames;
	}

	/**
	 * Converts the set containing parameter key names to a list.
	 * @return
	 */
	public List<String> getProvidedParamsAsList() {
		return this.checkedKeyNames != null ? new ArrayList<>(this.checkedKeyNames) : new ArrayList<>();
	}

	/**
	 * Can be used to check if any given parameter has been supplied. Every p
	 * @param parameterName
	 * @return
	 */
	public boolean containsParameter(String parameterName) {
		return this.checkedKeyNames != null && checkedKeyNames.contains(parameterName);
	}

	public boolean hasCheckedMapParams() {
		return this.checkedMapParams != null;
	}

	public Map<String, ApiMapCheckResult> getCheckedMapParams() {
		return this.checkedMapParams;
	}

	/**
	 * @param objectParamName name of the object parameter that was checked
	 * @return the ApiObjParamCheck or null
	 */
	public ApiMapCheckResult getMapParamCheck(String objectParamName) {
		return this.checkedMapParams != null ? checkedMapParams.get(objectParamName) : null;
	}


	public boolean hasCheckedArrayOfMapsParams() {
		return this.checkedArrayOfMapsParams != null;
	}

	public Map<String, ApiArrayOrListCheckResult> getCheckedArrayOfMapsParams() {
		return this.checkedArrayOfMapsParams;
	}

	/* Static creation methods */


	/**
	 * When the Map is null or has no parameters itself.
	 * @param keyName
	 * @return
	 */
	public static ApiMapCheckResult success(String keyName) {
		return new ApiMapCheckResult(keyName, null, null, null, null);
	}


	public static ApiMapCheckResult success(String keyName, Set<String> checkedKeyNames) {
		return new ApiMapCheckResult(keyName, checkedKeyNames, null, null, null);
	}

	public static ApiMapCheckResult success(String keyName,
	                                        Set<String> checkedKeyNames,
	                                        Map<String, ApiMapCheckResult> checkedMapParams,
	                                        Map<String, ApiArrayOrListCheckResult> checkedArrayParams,
	                                        Map<String, Object> customValues) {
		return new ApiMapCheckResult(keyName, checkedKeyNames, checkedMapParams, checkedArrayParams, customValues);
	}

	public static ApiMapCheckResult failure(ApiParamError apiParamError) {
		return new ApiMapCheckResult(apiParamError);
	}


}
