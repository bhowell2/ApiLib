package io.bhowell2.apilib.extensions.map.utils;

import io.bhowell2.apilib.FormatInsertFunc;

import java.util.Map;

/**
 * @author Blake Howell
 */
public class ApiMapFormatInsertFuncs {

	public static final FormatInsertFunc<?, Map<String, Object>> MAP_FORMAT_INSERT_FUNC = (name, map, o) -> map.put(name, o);

	/**
	 * Used to keep type-safety of injection function.
	 *
	 * @param classType
	 * @param <T>       type of parameter to inject back into map
	 * @return a type-safe FormatInjectionFunction
	 */
	public static final <T> FormatInsertFunc<T, Map<String, Object>> getFormatInsertFuncForType(Class<T> classType) {
		return (name, map, o) -> map.put(name, o);
	}

}
