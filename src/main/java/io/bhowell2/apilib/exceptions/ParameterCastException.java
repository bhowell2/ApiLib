package io.bhowell2.apilib.exceptions;

/**
 * @author Blake Howell
 */
public class ParameterCastException extends ParameterCheckException {
	public ParameterCastException(String parameterName, Class<?> clazz) {
		super("Failed to cast the parameter '" + parameterName + "' to the class '" + clazz.getTypeName() + "'.", parameterName, false);
	}
}
