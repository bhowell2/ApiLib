package io.bhowell2.ApiLib;

import java.util.List;

/**
 * Tuple to return after checking all parameters for a given path (if there is not an error -- which will cause a short-circuit return). Either
 * provides the list of provided parameters, or a tuple representing an error that occurred.
 * @author Blake Howell
 */
public final class PathParamsCheckTuple {

    public final List<String> providedParameterNames;
    public final ErrorTuple errorTuple;

    PathParamsCheckTuple(List<String> providedParameterNames) {
        this.providedParameterNames = providedParameterNames;
        this.errorTuple = null;
    }

    PathParamsCheckTuple(ErrorTuple errorTuple) {
        this.errorTuple = errorTuple;
        this.providedParameterNames = null;
    }

    public boolean failed() {
        return errorTuple != null;
    }

    public boolean successful() {
        return errorTuple == null;
    }

    public ErrorTuple getErrorTuple() {
        return errorTuple;
    }

    public List<String> getProvidedParameterNames() {
        return providedParameterNames;
    }

    /* Static Creation Methods */
    public static PathParamsCheckTuple successful(List<String> providedParameterNames) {
        return new PathParamsCheckTuple(providedParameterNames);
    }

    public static PathParamsCheckTuple failed(ErrorTuple errorTuple) {
        return new PathParamsCheckTuple(errorTuple);
    }

}
