package io.bhowell2.apilib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Facilitates creation of an ApiObjParam.
 *
 * @author Blake Howell
 */
public class ApiObjParamBuilder<ObjectType, ParentParamType> {

	/**
	 * Use when the object parameter is not embedded within another object or is an array's parameter and thus has no name.
	 *
	 * @param retrievalFunction a function that will return the parameter to check from the request parameters provided (ParentParamType)
	 * @param <ObjectType>      the object type that will be checked
	 * @param <ParentParamType> type of parameter supplied to check method
	 * @return
	 */
	public static <ObjectType, ParentParamType> ApiObjParamBuilder<ObjectType, ParentParamType> rootObjBuilder(
		ParamRetrievalFunc<ObjectType, ParentParamType> retrievalFunction) {
		return new ApiObjParamBuilder<>(null, retrievalFunction);
	}

	/**
	 * Use when the object parameter is embedded within another object or the name of this parameter should be supplied to the retrievalFunction
	 *
	 * @param objectParameterName name of object parameter supplied to retrieval function
	 * @param retrievalFunction   a function that will return the parameter to check from the request parameters provided (ParentParamType)
	 * @param <ObjectType>        the object type that will be checked
	 * @param <ParentParamType>   type of parameter supplied to check method
	 * @return
	 */
	public static <ObjectType, ParentParamType> ApiObjParamBuilder<ObjectType, ParentParamType> builder(
		String objectParameterName, ParamRetrievalFunc<ObjectType, ParentParamType> retrievalFunction) {
		return new ApiObjParamBuilder<>(objectParameterName, retrievalFunction);
	}

	/**
	 * Provides functionality to copy all of another ApiObjParam, including the paramName. Use when it is desired to keep all previous parameters
	 * from another object and to add on additional parameters.
	 *
	 * @param objParamToCopy    an apiParamObj whose required and optional parameters should be copied into the new parameter
	 * @param <ObjectType>      the object type that will be checked
	 * @param <ParentParamType> type of parameter supplied to check method
	 * @return
	 */
	public static <ObjectType, ParentParamType> ApiObjParamBuilder<ObjectType, ParentParamType> copyFrom(
		ApiObjParam<ObjectType, ParentParamType> objParamToCopy) {
		return copyFrom(objParamToCopy.paramName, objParamToCopy);
	}

	/**
	 * @param paramName         name of parameter. not required for root parameter
	 * @param objParamToCopy
	 * @param <ObjectType>
	 * @param <ParentParamType>
	 * @return
	 */
	public static <ObjectType, ParentParamType> ApiObjParamBuilder<ObjectType, ParentParamType> copyFrom(
		String paramName, ApiObjParam<ObjectType, ParentParamType> objParamToCopy) {
		return new ApiObjParamBuilder<>(paramName, objParamToCopy);
	}

	// copy mode informs the builder that it is copying from an existing object and it is possible to
	// override and remove already parameters
	protected boolean copyMode = false;
	protected String paramName;
	protected ParamRetrievalFunc<ObjectType, ParentParamType> retrievalFunction;
	protected List<ApiParam<?, ObjectType>> requiredParams;
	protected List<ApiParam<?, ObjectType>> optionalParams;
	protected List<ApiObjParam<?, ObjectType>> requiredObjParams;
	protected List<ApiObjParam<?, ObjectType>> optionalObjParams;
	protected List<ApiCustomParam<ObjectType>> requiredCustomParams;
	protected List<ApiCustomParam<ObjectType>> optionalCustomParams;
	protected boolean continueOnOptionalFailure = false;

	protected Set<String> paramsSet = new HashSet<>();
	protected Set<String> objParamsSet = new HashSet<>();

	/**
	 * @param paramName         name may be null if it is not used in the retrieval function
	 * @param retrievalFunction a function that will return the parameter to check from the request parameters provided (ParentParamType)
	 */
	public ApiObjParamBuilder(String paramName,
	                          ParamRetrievalFunc<ObjectType, ParentParamType> retrievalFunction) {
		this.paramName = paramName;
		this.retrievalFunction = retrievalFunction;
		this.requiredParams = new ArrayList<>();
		this.optionalParams = new ArrayList<>();
		this.requiredObjParams = new ArrayList<>();
		this.optionalObjParams = new ArrayList<>();
		this.requiredCustomParams = new ArrayList<>();
		this.optionalCustomParams = new ArrayList<>();
	}

	/**
	 * This constructor was created to make it easier to reuse already created object parameters and add on or override them
	 * for specific functionality.
	 *
	 * @param objParamName
	 * @param objParamToCopy
	 */
	public ApiObjParamBuilder(String objParamName, ApiObjParam<ObjectType, ParentParamType> objParamToCopy) {
		this(objParamName, objParamToCopy.retrievalFunction);
		addRequiredParams(objParamToCopy.requiredParams.clone());
		addOptionalParams(objParamToCopy.optionalParams.clone());
		addRequiredObjParams(objParamToCopy.requiredObjParams.clone());
		addOptionalObjParams(objParamToCopy.optionalObjParams.clone());
		addRequiredCustomParams(objParamToCopy.requiredCustomParams.clone());
		addOptionalCustomParams(objParamToCopy.optionalCustomParams.clone());
		setContinueOnOptionalFailure(objParamToCopy.continueOnOptionalFailure);
		this.copyMode = true;
	}

	public ApiObjParamBuilder<ObjectType, ParentParamType> setContinueOnOptionalFailure(boolean continueOnOptionalFailure) {
		this.continueOnOptionalFailure = continueOnOptionalFailure;
		return this;
	}

	/**
	 * Ensures that a parameter has not already been added.
	 * Should be used for BOTH required and optional parameters - which will ensure that a parameter is not added as both.
	 *
	 * @param param
	 */
	protected void addParamToSet(ApiParam<?, ObjectType> param) {
		if (!this.paramsSet.add(param.parameterName)) {
			throw new RuntimeException("Parameter (" + param.parameterName + ") has already been added as a required or optional parameter.");
		}
	}

	public ApiObjParamBuilder<ObjectType, ParentParamType> addRequiredParam(ApiParam<?, ObjectType> param) {
		if (param == null) {
			throw new RuntimeException("Cannot add null parameters.");
		}
		addParamToSet(param);
		this.requiredParams.add(param);
		return this;
	}

	public ApiObjParamBuilder<ObjectType, ParentParamType> addRequiredParams(ApiParam<?, ObjectType>... params) {
		for (ApiParam<?, ObjectType> parameter : params) {
			this.addRequiredParam(parameter);
		}
		return this;
	}

	public ApiObjParamBuilder<ObjectType, ParentParamType> addOptionalParam(ApiParam<?, ObjectType> param) {
		if (param == null) {
			throw new RuntimeException("Cannot add null parameters.");
		}
		addParamToSet(param);
		this.optionalParams.add(param);
		return this;
	}

	public ApiObjParamBuilder<ObjectType, ParentParamType> addOptionalParams(ApiParam<?, ObjectType>... params) {
		for (ApiParam<?, ObjectType> parameter : params) {
			this.addOptionalParam(parameter);
		}
		return this;
	}

	/**
	 * Ensures that an object parameter has not already been added.
	 * Should be used for BOTH required and optional parameters -- which will ensure that a parameter is not added as both.
	 *
	 * @param objParam
	 */
	protected void addObjParamToSet(ApiObjParam<?, ObjectType> objParam) {
		if (!this.objParamsSet.add(objParam.paramName)) {
			throw new RuntimeException("Object parameter (" + objParam.paramName + ") has already been added as a required or optional parameter.");
		}
	}

	public ApiObjParamBuilder<ObjectType, ParentParamType> addRequiredObjParam(ApiObjParam<?, ObjectType> objParam) {
		if (objParam == null) {
			throw new RuntimeException("Cannot add null object parameters.");
		}
		addObjParamToSet(objParam);
		this.requiredObjParams.add(objParam);
		return this;
	}

	public ApiObjParamBuilder<ObjectType, ParentParamType> addRequiredObjParams(ApiObjParam<?, ObjectType>... objParams) {
		for (ApiObjParam<?, ObjectType> objParam : objParams) {
			this.addRequiredObjParam(objParam);
		}
		return this;
	}

	public ApiObjParamBuilder<ObjectType, ParentParamType> addOptionalObjParam(ApiObjParam<?, ObjectType> objParam) {
		if (objParam == null) {
			throw new RuntimeException("Cannot add null object parameters.");
		}
		addObjParamToSet(objParam);
		this.optionalObjParams.add(objParam);
		return this;
	}

	public ApiObjParamBuilder<ObjectType, ParentParamType> addOptionalObjParams(ApiObjParam<?, ObjectType>... objParams) {
		for (ApiObjParam<?, ObjectType> objParam : objParams) {
			this.addOptionalObjParam(objParam);
		}
		return this;
	}

	public ApiObjParamBuilder<ObjectType, ParentParamType> addRequiredCustomParam(ApiCustomParam<ObjectType> customParam) {
		if (customParam == null) {
			throw new RuntimeException("Cannot add null custom parameter.");
		}
		this.requiredCustomParams.add(customParam);
		return this;
	}

	public ApiObjParamBuilder<ObjectType, ParentParamType> addRequiredCustomParams(ApiCustomParam<ObjectType>... customParams) {
		for (ApiCustomParam<ObjectType> customParameter : customParams) {
			this.addRequiredCustomParam(customParameter);
		}
		return this;
	}

	public ApiObjParamBuilder<ObjectType, ParentParamType> addOptionalCustomParam(ApiCustomParam<ObjectType> customParam) {
		if (customParam == null) {
			throw new RuntimeException("Cannot add null custom parameter.");
		}
		this.optionalCustomParams.add(customParam);
		return this;
	}

	public ApiObjParamBuilder<ObjectType, ParentParamType> addOptionalCustomParams(ApiCustomParam<ObjectType>... customParams) {
		for (ApiCustomParam<ObjectType> customParam : customParams) {
			this.addOptionalCustomParam(customParam);
		}
		return this;
	}


	@SuppressWarnings("unchecked")
	public ApiObjParam<ObjectType, ParentParamType> build() {
		return new ApiObjParam<>(this.paramName,
		                         this.continueOnOptionalFailure,
		                         this.retrievalFunction,
		                         this.requiredParams.toArray(new ApiParam[0]),
		                         this.optionalParams.toArray(new ApiParam[0]),
		                         this.requiredObjParams.toArray(new ApiObjParam[0]),
		                         this.optionalObjParams.toArray(new ApiObjParam[0]),
		                         this.requiredCustomParams.toArray(new ApiCustomParam[0]),
		                         this.optionalCustomParams.toArray(new ApiCustomParam[0]));
	}

}
