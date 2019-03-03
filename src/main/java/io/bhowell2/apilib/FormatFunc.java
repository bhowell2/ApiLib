package io.bhowell2.apilib;

/**
 * @author Blake Howell
 */
@FunctionalInterface
public interface FormatFunc<In, Out> {

	/**
	 * Formats the value. This allows the developer to modify the provided parameter.
	 *
	 * @param value
	 * @return
	 */
	Out format(In value) throws Exception;

}
