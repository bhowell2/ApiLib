package io.bhowell2.ApiLib;

import io.bhowell2.ApiLib.exceptions.InvalidApiParameterException;
import io.bhowell2.ApiLib.exceptions.SafeInvalidApiParameterException;
import io.bhowell2.ApiLib.exceptions.SafeMissingApiParameterException;
import io.bhowell2.ApiLib.exceptions.ParameterCastException;
import io.bhowell2.ApiLib.exceptions.ParameterCheckException;

/**
 * The return from a parameter check. If the parameter checks out, the parameter name is returned; otherwise, an error is returned. If the returned
 * error is not of type {@link io.bhowell2.ApiLib.exceptions.SafeParameterCheckException} then its error message should probably not be returned to
 * the end-user.
 *
 * @author Blake Howell
 */
public final class ParameterCheckTuple {

  final String parameterName;
  final ParameterCheckException checkFailure;

  public ParameterCheckTuple(String parameterName) {
    this.parameterName = parameterName;
    this.checkFailure = null;
  }

  public ParameterCheckTuple(ParameterCheckException checkFailure) {
    this.checkFailure = checkFailure;
    this.parameterName = null;
  }

  public boolean isSuccessful() {
    return checkFailure == null;
  }

  public Exception getCheckFailure() {
    return checkFailure;
  }

  public String getParameterName() {
    return parameterName;
  }

  /* Static creation methods */

  public static ParameterCheckTuple success(String parameterName) {
    return new ParameterCheckTuple(parameterName);
  }

  public static ParameterCheckTuple missingParameterFailure(String missingParamName) {
    return new ParameterCheckTuple(new SafeMissingApiParameterException(missingParamName));
  }

  public static ParameterCheckTuple invalidParameterFailure(String parameterName) {
    return new ParameterCheckTuple(new InvalidApiParameterException(parameterName));
  }

  public static ParameterCheckTuple invalidParameterFailure(String parameterName, String reasonForFailure) {
    return new ParameterCheckTuple(new InvalidApiParameterException(parameterName, reasonForFailure));
  }

  public static ParameterCheckTuple safeInvalidParameterFailure(String parameterName, String reasonForFailure) {
    return new ParameterCheckTuple(new SafeInvalidApiParameterException(parameterName, reasonForFailure));
  }

  public static ParameterCheckTuple parameterCastException(String parameterName, Class<?> clazz) {
    return new ParameterCheckTuple(new ParameterCastException(parameterName, clazz));
  }

}
