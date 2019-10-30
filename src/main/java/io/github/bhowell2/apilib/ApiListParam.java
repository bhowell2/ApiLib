package io.github.bhowell2.apilib;


import java.util.List;
import java.util.Map;

/**
 * @author Blake Howell
 */
public class ApiListParam<In, Param> extends ApiCollectionParam<In, Param, List<Param>> {

	public static <In, Param> Builder<In, Param> builder(String keyName) {
		return new Builder<>(keyName);
	}

	public static <In, Param> Builder<In, Param> builder(String keyName, ApiListParam<In, Param> copyFrom) {
		return new Builder<>(keyName, copyFrom);
	}

	public static <In, Param> Builder<In, Param> unnamedBuilder() {
		return builder(null);
	}

	/**
	 * Used for a named list within a Map.
	 * @param keyName
	 * @param <Param>
	 * @return
	 */
	public static <Param> Builder<Map<String, Object>, Param>
	mapInputBuilder(String keyName) {
		return builder(keyName);
	}

	public static <Param> Builder<Map<String, Object>, Param>
	mapInputBuilder(String keyName, Class<Param> paramType) {
		return builder(keyName);
	}

	public static <Param> Builder<List<Param>, Param> innerListBuilder() {
		return unnamedBuilder();
	}

	public static <Param> Builder<List<Param>, Param> innerListBuilder(Class<Param> paramType) {
		return unnamedBuilder();
	}

	public static class Builder<In, Param> extends ApiCollectionParam.Builder<
		In,
		Param,
		List<Param>,
		Builder<In, Param>,
		ApiListParam<In, Param>> {

		public Builder(String keyName) {
			super(keyName);
		}

		public Builder(String keyName, ApiListParam<In, Param> copyFrom) {
			super(keyName, copyFrom);
		}

		@Override
		protected ApiListParam<In, Param> buildImplementation() {
			return new ApiListParam<>(this);
		}

	}

	private ApiListParam(Builder<In, Param> builder) {
		super(builder);
	}

	@Override
	protected int getCollectionSize(List<Param> paramCollection) {
		return paramCollection.size();
	}

	@Override
	protected Param getParamAtIndex(int i, List<Param> paramCollection) {
		return paramCollection.get(i);
	}
}
