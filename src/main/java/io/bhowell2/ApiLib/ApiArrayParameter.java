package io.bhowell2.ApiLib;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to check parameters in an array.
 * @author Blake Howell
 */
public class ApiArrayParameter<ArrayType, ParamsObj> {

    public final String parameterName;      // used when array parameter is not nested (e.g. {paramName: []}, but not {paramName: [ [ ] ]} for the inner arrays)
    private final boolean continueOnFailedOptional;
    private final ParameterRetrievalFunction<ArrayType, ParamsObj> retrievalFunction;
    private final List<ApiCustomParameters<ArrayType>> requiredCustomParams;
    private final List<ApiCustomParameters<ArrayType>> optionalCustomParams;

    public ApiArrayParameter(boolean contineOnFailedOptional,
                             ParameterRetrievalFunction<ArrayType, ParamsObj> retrievalFunction,
                             List<ApiCustomParameters<ArrayType>> requiredCustomParams,
                             List<ApiCustomParameters<ArrayType>> optionalCustomParams) {
        this(null, contineOnFailedOptional, retrievalFunction, requiredCustomParams, optionalCustomParams);
    }

    public ApiArrayParameter(String parameterName,
                             boolean contineOnFailedOptional,
                             ParameterRetrievalFunction<ArrayType, ParamsObj> retrievalFunction,
                             List<ApiCustomParameters<ArrayType>> requiredCustomParams,
                             List<ApiCustomParameters<ArrayType>> optionalCustomParams) {
        this.parameterName = parameterName;
        this.continueOnFailedOptional = contineOnFailedOptional;
        this.retrievalFunction = retrievalFunction;
        this.requiredCustomParams = requiredCustomParams;
        this.optionalCustomParams = optionalCustomParams;
    }

    public ApiArrayParamTuple check(ParamsObj requestParameters) {
        try {
            ArrayType array = retrievalFunction.retrieveParameter(parameterName, requestParameters);
            List<ApiCustomParamsTuple> providedCustomParams = new ArrayList<>(this.requiredCustomParams.size());

            for (ApiCustomParameters<ArrayType> customParameter : this.requiredCustomParams) {
                ApiCustomParamsTuple checkTuple = customParameter.check(array);
                if (checkTuple.failed()) {
                    return ApiArrayParamTuple.failed(checkTuple.errorTuple);
                }
                providedCustomParams.add(checkTuple);
            }

            for (ApiCustomParameters<ArrayType> customParameters : this.optionalCustomParams) {
                ApiCustomParamsTuple checkTuple = customParameters.check(array);
                if (checkTuple.failed()) {
                    if (checkTuple.errorTuple.errorType == ErrorType.MISSING_PARAMETER || continueOnFailedOptional) {
                        continue;
                    } else {
                        return ApiArrayParamTuple.failed(checkTuple.errorTuple);
                    }
                }
                providedCustomParams.add(checkTuple);
            }
            return ApiArrayParamTuple.success(parameterName, providedCustomParams);
        } catch (ClassCastException e) {
            return ApiArrayParamTuple.failed(new ErrorTuple(ErrorType.PARAMETER_CAST, "Failed to cast array to correct type. " + e.getMessage()));
        }
    }

}
