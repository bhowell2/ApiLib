package io.bhowell2.ApiLib;

/**
 * @author Blake Howell
 */
@FunctionalInterface
public interface CheckFunc<T> {

    CheckFuncResult check(T param);

}
