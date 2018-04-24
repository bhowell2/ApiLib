package io.bhowell2.ApiLib;

import java.util.ArrayList;
import java.util.List;

/**
 * Facilitates the creation of a parameter for the library user.
 * @author Blake Howell
 */
public class ApiParamBuilder<ParamType, ParamsObj> {

    public static <ParamType, ParamsObj> ApiParamBuilder<ParamType, ParamsObj> builder(String paramName,
                                                                                       ParamRetrievalFunc<ParamType, ParamsObj> retrievalFunc) {
        return new ApiParamBuilder<>(paramName, retrievalFunc);
    }

    /**
     * Allows the copying of a previously created ApiParameter. There will likely be cases where the user wants to have all of the same
     * information, except for possibly whether the parameter is required or not.
     * @param apiParam
     * @param <ParamType>
     * @param <ParamsObj>
     * @return
     */
    public static <ParamType, ParamsObj> ApiParamBuilder<ParamType, ParamsObj> copyFrom(ApiParam<ParamType, ParamsObj> apiParam) {
        return copyFrom(apiParam.parameterName, apiParam);
    }

    /**
     * Allows the copying of a previously created ApiParameter. There will likely be cases where the user wants to have all of the same
     * information/checks, but possibly wants to change the name and add more checks.
     * @param paramName name of new parameter
     * @param param the parameter whose values to copy
     * @param <ParamType> type of parameter (e.g., String)
     * @param <ParamsObj> type of request parameters object (e.g., Map<String, Object>)
     * @return
     */
    public static <ParamType, ParamsObj> ApiParamBuilder<ParamType, ParamsObj> copyFrom(String paramName,
                                                                                        ApiParam<ParamType, ParamsObj> param) {
        return new ApiParamBuilder<ParamType, ParamsObj>(paramName, param);
    }

    protected String paramName;
    protected List<CheckFunc<ParamType>> checkFuncs;
    protected ParamRetrievalFunc<ParamType, ParamsObj> retrievalFunc;
    protected List<FormatFunc<ParamType>> formatFuncs;
    protected FormatInsertFunc<ParamType, ParamsObj> formatInsertFunc;

    /**
     *
     * @param paramName the name of the parameter
     * @param retrievalFunc a function that will accept the
     */
    public ApiParamBuilder(String paramName, ParamRetrievalFunc<ParamType, ParamsObj> retrievalFunc) {
        this.paramName = paramName;
        this.retrievalFunc = retrievalFunc;
        this.checkFuncs = new ArrayList<>(1);       // must have at least 1
        this.formatFuncs = new ArrayList<>(0);
    }

    public ApiParamBuilder(String paramName, ApiParam<ParamType, ParamsObj> apiParam) {
        this(paramName, apiParam.paramRetrievalFunc);
        addCheckFunctions(apiParam.checkFuncs.clone());
        addFormatFunctions(apiParam.formatFuncs.clone());
        this.formatInsertFunc = apiParam.formatInsertFunc;  // may be null
    }

    @SuppressWarnings("unchecked")
    public ApiParam<ParamType, ParamsObj> build() {
        return new ApiParam(paramName,
                            retrievalFunc,
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

    public ApiParamBuilder<ParamType, ParamsObj> addFormatInsertionFunction(FormatInsertFunc<ParamType, ParamsObj> formatInsertFunc) {
        if (this.formatInsertFunc != null) {
            // added just to remove accidental overwrites or unintended additions
            throw new RuntimeException("Format insert function has already been added.");
        }
        this.formatInsertFunc = formatInsertFunc;
        return this;
    }

}
