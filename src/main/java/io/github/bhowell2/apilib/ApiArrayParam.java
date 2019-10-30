package io.github.bhowell2.apilib;


import java.util.Map;

/**
 * Requires that the parameter be of type Array.
 * @author Blake Howell
 */
public class ApiArrayParam<In, Param> extends ApiCollectionParam<In, Param, Param[]> {

	public static <In, Param> Builder<In, Param> builder(String keyName) {
		return new Builder<>(keyName);
	}

	public static <In, Param> Builder<In, Param> copyFrom(String keyName, ApiArrayParam<In, Param> copyFrom) {
		return new Builder<>(keyName, copyFrom);
	}

	public static <In, Param> Builder<In, Param> unnamedBuilder() {
		return builder(null);
	}

	public static <Param> Builder<Map<String, Object>, Param>
	mapInputBuilder(String keyName) {
		return builder(keyName);
	}

	public static <Param> Builder<Map<String, Object>, Param>
	mapInputBuilder(String keyName, Class<Param> paramType) {
		return builder(keyName);
	}

	public static <Param> Builder<Param[], Param> innerArrayBuilder() {
		return unnamedBuilder();
	}

	public static <Param> Builder<Param[], Param> innerArrayBuilder(Class<Param> paramType) {
		return unnamedBuilder();
	}


	public static class Builder<In, Param> extends ApiCollectionParam.Builder<
		In,
		Param,
		Param[],
		Builder<In, Param>,
		ApiArrayParam<In, Param>> {

		public Builder(String keyName) {
			super(keyName);
		}

		public Builder(String keyName, ApiArrayParam<In, Param> copyFrom) {
			super(keyName, copyFrom);
		}

		@Override
		protected ApiArrayParam<In, Param> buildImplementation() {
			return new ApiArrayParam<>(this);
		}

	}

	private ApiArrayParam(Builder<In, Param> builder) {
		super(builder);
	}

	@Override
	protected int getCollectionSize(Param[] paramCollection) {
		return paramCollection.length;
	}

	@Override
	protected Param getParamAtIndex(int i, Param[] paramCollection) {
		return paramCollection[i];
	}

}
