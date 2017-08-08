package io.bhowell2.ApiLib;

import io.bhowell2.ApiLib.exceptions.ParameterCheckException;

import java.util.List;

/**
 * Tuple to return after checking all parameters for a given path. Either provides the list of provided parameters, or a tuple representing an
 * error that occurred.
 * @author Blake Howell
 */
public final class PathParamsTuple {

  private final List<String> providedParameterNames;
  private final ErrorTuple errorTuple;


  PathParamsTuple(List<String> providedParameterNames) {
    this.providedParameterNames = providedParameterNames;
    this.errorTuple = null;
  }

  PathParamsTuple(ErrorTuple errorTuple) {
    this.errorTuple = errorTuple;
    this.providedParameterNames = null;
  }

  public boolean failed() {
    return errorTuple != null;
  }

  public boolean isSuccessful() {
    return errorTuple == null;
  }

  public ErrorTuple getErrorTuple() {
    return errorTuple;
  }

  public List<String> getProvidedParameterNames() {
    return providedParameterNames;
  }

  /* Static Creation Methods */
  public static PathParamsTuple successful(List<String> providedParameterNames) {
    return new PathParamsTuple(providedParameterNames);
  }

  public static PathParamsTuple failed(ErrorTuple errorTuple) {
    return new PathParamsTuple(errorTuple);
  }

}
