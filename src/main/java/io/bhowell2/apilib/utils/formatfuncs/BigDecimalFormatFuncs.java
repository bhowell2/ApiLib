package io.bhowell2.apilib.utils.formatfuncs;

import io.bhowell2.apilib.FormatFunc;

import java.math.BigDecimal;

/**
 * There are cases when using a rest API that the
 *
 * @author Blake Howell
 */
public class BigDecimalFormatFuncs {

	public static final FormatFunc<String, BigDecimal> STRING_TO_BIG_DECIMAL = BigDecimal::new;

	public static final FormatFunc<Double, BigDecimal> DOUBLE_TO_BIG_DECIMAL = BigDecimal::valueOf;

}
