package io.bhowell2.apilib.utils.formatfuncs;

import io.bhowell2.apilib.FormatFunc;

/**
 * @author Blake Howell
 */
public class DoubleFormatFuncs {

	public static final FormatFunc<?, Double> STRING_OR_DOUBLE_TO_DOUBLE = d -> {
		if (d instanceof Double) {
			return (Double) d;
		} else if (d instanceof String) {
			return Double.valueOf((String) d);
		}
		throw new IllegalArgumentException();
	};

}
