package io.bhowell2.apilib;

/**
 * Allows the user to use any type of object specific to obtain the parameters (rather than forcing the use of {@code Map<String, Object>}, for instance).
 * The user will generally want to make a few static instances of these for their use cases.
 * (e.g., {@code ParameterRetrievalFunction<Integer, Map<String, Object>> = (paramName, requestParams) -> (Integer)requestParams.get(paramName);}
 *
 * The user, really should extend this interface to something like:
 * {@code public interface ExtendParamRetrievalFunction<ParamType> extends ParameterRetrievalFunction<ParamType, Map<String, Object>>}
 *  which will cut down on their boilerplate a bit.
 *
 * @author Blake Howell
 */
@FunctionalInterface
public interface ParamRetrievalFunc<ParamType, Params> {

    /**
     * Retrieves a parameter from the params object.
     * @param parameters object containing list of parameters
     * @return the parameter retrieved
     */
    ParamType retrieveParameter(String parameterName, Params parameters);

}
