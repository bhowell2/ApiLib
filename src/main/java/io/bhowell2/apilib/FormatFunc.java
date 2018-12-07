package io.bhowell2.apilib;

/**
 * @author Blake Howell
 */
@FunctionalInterface
public interface FormatFunc<T> {

    /**
     * Formats the value. This allows the developer to modify the provided parameter.
     * @param value
     * @return
     */
    T format(T value);

}
