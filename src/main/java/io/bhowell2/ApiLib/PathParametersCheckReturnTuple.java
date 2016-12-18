package io.bhowell2.ApiLib;

import io.bhowell2.ApiLib.exceptions.ParameterCheckException;

import java.util.List;

/**
 * @author Blake Howell
 */
public final class PathParametersCheckReturnTuple {

  private final List<String> providedParameterNames;
  private final ParameterCheckException checkFailure;

  PathParametersCheckReturnTuple(List<String> providedParameterNames) {
    this.providedParameterNames = providedParameterNames;
    this.checkFailure = null;
  }

  PathParametersCheckReturnTuple(ParameterCheckException checkFailure) {
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
  public static PathParametersCheckReturnTuple successful(List<String> providedParameterNames) {
    return new PathParametersCheckReturnTuple(providedParameterNames);
  }

  public static PathParametersCheckReturnTuple failed(ParameterCheckException checkFailure) {
    return new PathParametersCheckReturnTuple(checkFailure);
  }

}
