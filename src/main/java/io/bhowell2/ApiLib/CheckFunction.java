package io.bhowell2.ApiLib;

/**
 * @author Blake Howell
 */
@FunctionalInterface
public interface CheckFunction<T> {

    FunctionCheckTuple check(T param);

}
