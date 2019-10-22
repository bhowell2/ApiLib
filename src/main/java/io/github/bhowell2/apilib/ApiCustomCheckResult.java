package io.github.bhowell2.apilib;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Available so that the user can return anything they want from
 * {@link ApiParam}. This allows for returning everything that the
 * other check results can return and an additional "custom" field.
 *
 * If {@link #checkedKeyNames}, {@link #checkedArrayOfMapsParams}, or
 * {@link #checkedMapParams} is returned to an {@link ApiMapParam},
 * then they will be merged with the {@link ApiMapCheckResult}'s
 * {@link ApiMapCheckResult#checkedKeyNames},
 * {@link ApiMapCheckResult#checkedMapParams},
 * and {@link ApiMapCheckResult#checkedArrayOfMapsParams}.
 *
 * If only key name is returned to an {@link ApiMapParam} then it
 * will be added to {@link ApiMapCheckResult}'s
 * {@link ApiMapCheckResult#checkedKeyNames}.
 *
 * @author Blake Howell
 */
public class ApiCustomCheckResult extends ApiCheckResultBase {

	public final Set<String> checkedKeyNames;

	public final Map<String, ApiArrayOrListCheckResult> checkedArrayOfMapsParams;

	public final Map<String, ApiMapCheckResult> checkedMapParams;

	/**
	 * Allows the user to return any type of object. They will have to cast this
	 * when they retrieve it from an {@link ApiMapCheckResult}.
	 */
	public final Object customValue;

	public ApiCustomCheckResult(String keyName,
	                            Set<String> checkedKeyNames,
	                            Map<String, ApiArrayOrListCheckResult> checkedArrayOfMapsParams,
	                            Map<String, ApiMapCheckResult> checkedMapParams,
	                            Object customValue) {
		super(keyName);
		this.checkedKeyNames = checkedKeyNames;
		this.checkedArrayOfMapsParams = checkedArrayOfMapsParams;
		this.checkedMapParams = checkedMapParams;
		this.customValue = customValue;
		if (customValue != null && keyName == null) {
			throw new IllegalArgumentException("Cannot have custom value without key name, otherwise would not have " +
				                                   "a way to retrieve it");
		}
	}

	public ApiCustomCheckResult(ApiParamError apiParamError) {
		super(apiParamError);
		this.checkedKeyNames = null;
		this.checkedArrayOfMapsParams = null;
		this.checkedMapParams = null;
		this.customValue = null;
	}

	public boolean hasCheckedKeyNames() {
		return this.checkedKeyNames != null;
	}

	public boolean hasCheckedArrayOfMapsParams() {
		return this.checkedArrayOfMapsParams != null;
	}

	public boolean hasCheckedMapParams() {
		return this.checkedMapParams != null;
	}

	public boolean hasCustomValue() {
		return this.customValue != null;
	}

	/* Static Creation Methods */

	public static ApiCustomCheckResult success(String... checkedKeyNames) {
		return success(new HashSet<>(Arrays.asList(checkedKeyNames)));
	}

	public static ApiCustomCheckResult success(Set<String> checkedKeyNames) {
		return new ApiCustomCheckResult(null, checkedKeyNames, null, null, null);
	}

	public static ApiCustomCheckResult success(Set<String> checkedKeyNames,
	                                           Map<String, ApiArrayOrListCheckResult> checkedArrayOfMapsParams,
	                                           Map<String, ApiMapCheckResult> checkedMapParams) {
		return new ApiCustomCheckResult(null, checkedKeyNames, checkedArrayOfMapsParams, checkedMapParams, null);
	}

	/**
	 * @param keyName keyName is only required when custom value is set.
	 * @param checkedKeyNames
	 * @param checkedArrayOfMapsParams
	 * @param checkedMapParams
	 * @param customValue
	 * @return
	 */
	public static ApiCustomCheckResult success(String keyName,
	                                           Set<String> checkedKeyNames,
	                                           Map<String, ApiArrayOrListCheckResult> checkedArrayOfMapsParams,
	                                           Map<String, ApiMapCheckResult> checkedMapParams,
	                                           Object customValue) {
		return new ApiCustomCheckResult(keyName, checkedKeyNames, checkedArrayOfMapsParams, checkedMapParams, customValue);
	}


	public static <T> ApiCustomCheckResult successWithCustomResult(String keyName, T customResult) {
		Objects.requireNonNull(keyName, "Cannot have custom value without key name, otherwise would not have " +
			"a way to retrieve it");
		return new ApiCustomCheckResult(keyName, null, null, null, customResult);
	}

	public static ApiCustomCheckResult failure(ApiParamError error) {
		return new ApiCustomCheckResult(error);
	}

}
