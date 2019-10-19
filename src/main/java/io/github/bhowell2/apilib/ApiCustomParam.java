package io.github.bhowell2.apilib;

/**
 * Used when the user must check multiple parameters at the same time.
 * This allows for returning multiple parameters' check results of any
 * of the types available (except other {@link ApiCustomParamCheckResult})
 *
 * The user should try to handle all exceptions if possible. If they are not
 * the parameter in which the error occurred will be considered the calling
 * ApiMapParam.
 *
 * @param <In> the object input type to {@link #check(Object)}
 * @param <T> the optional custom value to return in the check result
 * @author Blake Howell
 */
@FunctionalInterface
public interface ApiCustomParam<In, T> extends ApiParam<In, ApiCustomCheckResult<T>> {

}
