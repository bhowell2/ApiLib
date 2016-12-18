package io.bhowell2.ApiLib;

import io.bhowell2.ApiLib.exceptions.InvalidApiParameterException;
import io.bhowell2.ApiLib.exceptions.SafeMissingApiParametersException;
import io.bhowell2.ApiLib.exceptions.ParameterCastException;
import io.bhowell2.ApiLib.exceptions.ParameterCheckException;

/**
 * @author Blake Howell
 */
public final class ConditionalParameterCheckReturnTuple {

  public final String[] parameterNames;
  public final ParameterCheckException error;


  public ConditionalParameterCheckReturnTuple(String[] parameterNames) {
    this.parameterNames = parameterNames;
    this.error = null;
  }

  public ConditionalParameterCheckReturnTuple(ParameterCheckException error) {
    this.error = error;
    this.parameterNames = null;
  }

  public boolean isSuccessful() {
    return error == null;
  }

  public Exception getError() {
    return error;
  }

  /* Static creation methods */

  public static ConditionalParameterCheckReturnTuple success(String[] parameterNames) {
    return new ConditionalParameterCheckReturnTuple(parameterNames);
  }

  public static ConditionalParameterCheckReturnTuple missingParameterFailure(String[] missingParamNames) {
    StringBuilder missingParams = new StringBuilder();
    missingParams.append("The following parameters were missing from the request: ");
    for (String s : missingParamNames) {
      missingParams.append(s).append(", ");
    }
    missingParams.replace(missingParams.length() - 2, missingParams.length(), "."); // replace the trailing ", " with a period.
    return new ConditionalParameterCheckReturnTuple(new SafeMissingApiParametersException(missingParams.toString()));
  }

  public static ConditionalParameterCheckReturnTuple invalidParameterFailure(String parameterNames) {
    return new ConditionalParameterCheckReturnTuple(new InvalidApiParameterException(parameterNames));
  }

  public static ConditionalParameterCheckReturnTuple invalidParameterFailure(String parameterName, String reasonForFailure) {
    return new ConditionalParameterCheckReturnTuple(new InvalidApiParameterException(parameterName, reasonForFailure));
  }

  public static ParameterCheckReturnTuple parameterCastException(String parameterName, Class<?> clazz) {
    return new ParameterCheckReturnTuple(new ParameterCastException(parameterName, clazz));
  }

}
