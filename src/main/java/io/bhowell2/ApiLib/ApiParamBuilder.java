package io.bhowell2.ApiLib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Facilitates the creation of a parameter for the library user.
 * @author Blake Howell
 */
public class ApiParamBuilder<ParamType, ParamsObj> {

    public static <ParamType, ParamsObj> ApiParamBuilder<ParamType, ParamsObj> builder(String parameterName,
                                                                                       boolean isRequired,
                                                                                       ParamRetrievalFunc<ParamType, ParamsObj> retrievalFunction) {
        return new ApiParamBuilder<>(parameterName, isRequired, retrievalFunction);
    }

    /**
     * Allows the copying of a previously created ApiParameter. There will likely be cases where the user wants to have all of the same
     * information, except for possibly whether the parameter is required or not.
     * @param parameter
     * @param isRequired
     * @param <ParamType>
     * @param <ParamsObj>
     * @return
     */
    public static <ParamType, ParamsObj> ApiParamBuilder<ParamType, ParamsObj> copyFrom(boolean isRequired,
                                                                                        ApiParam<ParamType, ParamsObj> parameter) {
        return copyFrom(parameter.parameterName, isRequired, parameter);
    }

    /**
     * Allows the copying of a previously created ApiParameter. There will likely be cases where the user wants to have all of the same
     * information, except for possibly whether the parameter is required or not.
     * @param parameterName name of new parameter
     * @param isRequired w
     * @param parameter the parameter whose values to copy
     * @param <ParamType> type of parameter (e.g., String)
     * @param <ParamsObj> type of request parameters object (e.g., Map<String, Object>)
     * @return
     */
    public static <ParamType, ParamsObj> ApiParamBuilder<ParamType, ParamsObj> copyFrom(String parameterName,
                                                                                        boolean isRequired,
                                                                                        ApiParam<ParamType, ParamsObj> parameter) {
        ApiParamBuilder<ParamType, ParamsObj> builder = new ApiParamBuilder<>(parameterName,
                                                                              isRequired,
                                                                              parameter.paramRetrievalFunc);
        builder.formatFuncs = new ArrayList<>(Arrays.asList(parameter.formatFuncs));
        builder.formatInsertFunc = parameter.formatInsertFunc;
        builder.checkFuncs = new ArrayList<>(Arrays.asList(parameter.paramCheckFuncs));
        return builder;
    }

    protected String parameterName;
    protected boolean isRequired;
    protected List<CheckFunc<ParamType>> checkFuncs;
    protected ParamRetrievalFunc<ParamType, ParamsObj> retrievalFunction;
    protected List<FormatFunc<ParamType>> formatFuncs;
    protected FormatInsertFunc<ParamType, ParamsObj> formatInsertFunc;

    /**
     *
     * @param parameterName the name of the parameter
     * @param isRequired whether or not parameter is required
     * @param retrievalFunction a function that will accept the
     */
    public ApiParamBuilder(String parameterName, boolean isRequired, ParamRetrievalFunc<ParamType, ParamsObj> retrievalFunction) {
        this.parameterName = parameterName;
        this.isRequired = isRequired;
        this.retrievalFunction = retrievalFunction;
        this.checkFuncs = new ArrayList<>(1);       // must have at last 1
        this.formatFuncs = new ArrayList<>(0);
    }

    @SuppressWarnings("unchecked")
    public ApiParam<ParamType, ParamsObj> build() {
        return new ApiParam(parameterName,
                            isRequired,
                            retrievalFunction,
                            formatFuncs.toArray(new FormatFunc[0]),
                            formatInsertFunc,
                            checkFuncs.toArray(new CheckFunc[0]));
    }

    public ApiParamBuilder<ParamType, ParamsObj> addCheckFunction(CheckFunc<ParamType> checkFunc) {
        if (checkFunc == null) {
            throw new RuntimeException("Cannot add null check function");
        }
        this.checkFuncs.add(checkFunc);
        return this;
    }

    @SuppressWarnings("varargs")
    public ApiParamBuilder<ParamType, ParamsObj> addCheckFunctions(CheckFunc<ParamType>... checkFuncs) {
        // call addCheckFunction to avoid duplicate constraint code (e.g., checking for null)
        for (CheckFunc<ParamType> cf : checkFuncs) {
            this.addCheckFunction(cf);
        }
        return this;
    }

    public ApiParamBuilder<ParamType, ParamsObj> addFormatFunction(FormatFunc<ParamType> formatFunc) {
        if (formatFunc == null) {
            throw new RuntimeException("Cannot add null format func");
        }
        this.formatFuncs.add(formatFunc);
        return this;
    }

    @SuppressWarnings("varargs")
    public ApiParamBuilder<ParamType, ParamsObj> addFormatFunctions(FormatFunc<ParamType>... formatFuncs) {
        // call addFormatFunction to avoid duplicate constraint code (e.g., checking for null)
        for (FormatFunc<ParamType> ff : formatFuncs) {
            this.addFormatFunction(ff);
        }
        return this;
    }

    public ApiParamBuilder<ParamType, ParamsObj> addFormatInjectionFunction(FormatInsertFunc<ParamType, ParamsObj> formatInsertFunc) {
        if (this.formatInsertFunc != null) {
            // added just to remove accidental overwrites or unintended additions
            throw new RuntimeException("Format insert function has already been added.");
        }
        this.formatInsertFunc = formatInsertFunc;
        return this;
    }

}
