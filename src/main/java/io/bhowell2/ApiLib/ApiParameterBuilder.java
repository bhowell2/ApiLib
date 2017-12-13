package io.bhowell2.ApiLib;

import java.util.ArrayList;
import java.util.List;

/**
 * Facilitates the creation of a parameter for the library user.
 * @author Blake Howell
 */
public class ApiParameterBuilder<ParamType, ParamsObj> {

    public static <ParamType, ParamsObj> ApiParameterBuilder<ParamType, ParamsObj> builder(String parameterName,
                                                                                           ParameterRetrievalFunction<ParamType, ParamsObj> retrievalFunction) {
        return new ApiParameterBuilder<>(parameterName, retrievalFunction);
    }

    protected String parameterName;
    protected List<CheckFunction<ParamType>> checkFunctions;
    protected ParameterRetrievalFunction<ParamType, ParamsObj> retrievalFunction;

    public ApiParameterBuilder(String parameterName, ParameterRetrievalFunction<ParamType, ParamsObj> retrievalFunction) {
        this.parameterName = parameterName;
        this.retrievalFunction = retrievalFunction;
        this.checkFunctions = new ArrayList<>(1);
    }

    @SuppressWarnings("unchecked")
    public ApiParameter<ParamType, ParamsObj> build() {
        return new ApiParameter<>(parameterName,
                                  retrievalFunction,
                                  checkFunctions.toArray((CheckFunction<ParamType>[]) new CheckFunction[checkFunctions.size()]));
    }


    public ApiParameterBuilder<ParamType, ParamsObj> addCheckFunction(CheckFunction<ParamType> checkFunction) {
        this.checkFunctions.add(checkFunction);
        return this;
    }



}
