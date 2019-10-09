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
 * @author Blake Howell
 */
@FunctionalInterface
public interface ApiCustomParam extends ApiParam<ApiCustomParamCheckResult> {

}
