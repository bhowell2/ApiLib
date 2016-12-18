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
public final class ParameterCheckReturnTuple {

  final String parameterName;
  final ParameterCheckException checkFailure;

  public ParameterCheckReturnTuple(String parameterName) {
    this.parameterName = parameterName;
    this.checkFailure = null;
  }

  public ParameterCheckReturnTuple(ParameterCheckException checkFailure) {
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

  public static ParameterCheckReturnTuple success(String parameterName) {
    return new ParameterCheckReturnTuple(parameterName);
  }

  public static ParameterCheckReturnTuple missingParameterFailure(String missingParamName) {
    return new ParameterCheckReturnTuple(new SafeMissingApiParameterException(missingParamName));
  }

  public static ParameterCheckReturnTuple invalidParameterFailure(String parameterName) {
    return new ParameterCheckReturnTuple(new InvalidApiParameterException(parameterName));
  }

  public static ParameterCheckReturnTuple invalidParameterFailure(String parameterName, String reasonForFailure) {
    return new ParameterCheckReturnTuple(new InvalidApiParameterException(parameterName, reasonForFailure));
  }

  public static ParameterCheckReturnTuple safeInvalidParameterFailure(String parameterName, String reasonForFailure) {
    return new ParameterCheckReturnTuple(new SafeInvalidApiParameterException(parameterName, reasonForFailure));
  }

  public static ParameterCheckReturnTuple parameterCastException(String parameterName, Class<?> clazz) {
    return new ParameterCheckReturnTuple(new ParameterCastException(parameterName, clazz));
  }

}
