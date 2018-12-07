package io.bhowell2.apilib;

/**
 * @author Blake Howell
 */
@FunctionalInterface
public interface CheckFunc<T> {

    CheckFuncResult check(T param);

}
