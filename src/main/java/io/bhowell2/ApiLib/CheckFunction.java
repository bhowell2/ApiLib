package io.bhowell2.ApiLib;

/**
 * @author Blake Howell
 */
@FunctionalInterface
public interface CheckFunction<T> {

    CheckFunctionTuple check(T param);

}
