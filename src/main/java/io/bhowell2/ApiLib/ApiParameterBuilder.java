package io.bhowell2.ApiLib;

import java.util.ArrayList;
import java.util.List;

/**
 * Facilitates the creation of a parameter for the library user.
 * @author Blake Howell
 */
public final class ApiParameterBuilder<ParamType, ParamsObj> {

    public static <ParamType, ParamsObj> ApiParameterBuilder<ParamType, ParamsObj> builder(String parameterName, ParameterRetrievalFunction<ParamType, ParamsObj> retrievalFunction) {
        return new ApiParameterBuilder<>(parameterName, retrievalFunction);
    }

    private String parameterName;
    private List<CheckFunction<ParamType>> checkFunctions;
    private ParameterRetrievalFunction<ParamType, ParamsObj> retrievalFunction;

    public ApiParameterBuilder(String parameterName, ParameterRetrievalFunction<ParamType, ParamsObj> retrievalFunction) {
        this.parameterName = parameterName;
        this.retrievalFunction = retrievalFunction;
        this.checkFunctions = new ArrayList<>(1);
    }

    @SuppressWarnings("unchecked")
    public ApiParameter<ParamType, ParamsObj> build() {
        // do not want the user to accidentally create a parameter that always passes. therefore, a runtime exception will be thrown
        if (this.checkFunctions.size() == 0) {
            throw new NoFunctionChecksProvidedException(this.parameterName);
        }
        return new ApiParameter<>(parameterName,
                                  checkFunctions.toArray((CheckFunction<ParamType>[]) new CheckFunction[checkFunctions.size()]),
                                  retrievalFunction);
    }


    public ApiParameterBuilder<ParamType, ParamsObj> addCheckFunction(CheckFunction<ParamType> checkFunction) {
        this.checkFunctions.add(checkFunction);
        return this;
    }

    private static class NoFunctionChecksProvidedException extends RuntimeException {
        public NoFunctionChecksProvidedException(String parameterName) {
            super("No function checks were provided for the parameter " + "'" + parameterName + "'. If this is intentional, just provide a function" +
                      "check that returns success (e.g., FunctionCheckReturnTuple.success()) if you want it to always pass, or failure if it should " +
                      "always fail or is a placeholder that has not yet been implemented.");
        }
    }


}
