package io.bhowell2.ApiLib;

import io.bhowell2.ApiLib.exceptions.ParameterCheckException;

import java.util.List;

/**
 * @author Blake Howell
 */
public final class PathParamsTuple {

  private final List<String> providedParameterNames;
  private final ParameterCheckException checkFailure;

  PathParamsTuple(List<String> providedParameterNames) {
    this.providedParameterNames = providedParameterNames;
    this.checkFailure = null;
  }

  PathParamsTuple(ParameterCheckException checkFailure) {
    this.checkFailure = checkFailure;
    this.providedParameterNames = null;
  }

  public boolean isSuccessful() {
    return checkFailure == null;
  }

  public ParameterCheckException getCheckFailure() {
    return checkFailure;
  }

  public List<String> getProvidedParameterNames() {
    return providedParameterNames;
  }

  /* Static Creation Methods */
  public static PathParamsTuple successful(List<String> providedParameterNames) {
    return new PathParamsTuple(providedParameterNames);
  }

  public static PathParamsTuple failed(ParameterCheckException checkFailure) {
    return new PathParamsTuple(checkFailure);
  }

}
