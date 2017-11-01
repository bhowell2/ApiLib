package io.bhowell2.ApiLib;

/**
 * Allows the user to use any type of object specific to obtain the parameters (rather than forcing them to use Map<String, Object>, for instance.) The user will generally want to make a few static instances of these for their use cases. (e.g., {@code ParameterRetrievalFunction<JsonObject, String, String> = (jsonObject, paramName) -> jsonObject.getString(paramName);}
 *
 * The user, really should extend this interface to something like:
 * {@code  public interface ExtendParamRetrievalFunction<ParamType> extends ParameterRetrievalFunction<ParamType, Map<String, Object>>}
 *  which will cut down on their boilerplate a bit.
 *
 * @author Blake Howell
 */
@FunctionalInterface
public interface ParameterRetrievalFunction<ParamType, Params> {

    /**
     * Retrieves a parameter from the params object.
     * @param parameters object containing list of parameters
     * @return the parameter retrieved
     */
    ParamType retrieveParameter(String parameterName, Params parameters);

}
