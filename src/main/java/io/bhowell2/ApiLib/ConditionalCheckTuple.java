package io.bhowell2.ApiLib;

import java.util.List;

/**
 * @author Blake Howell
 */
public final class ConditionalCheckTuple {

  public final List<String> parameterNames;
  public final ErrorTuple errorTuple;

  public ConditionalCheckTuple(List<String> parameterNames) {
    this.parameterNames = parameterNames;
    this.errorTuple = null;
  }

  public ConditionalCheckTuple(ErrorTuple errorTuple) {
    this.parameterNames = null;
    this.errorTuple = errorTuple;
  }

  public boolean isSuccessful() {
    return errorTuple == null;
  }

  public boolean failed() {
    return errorTuple != null;
  }

  /* Static creation methods */

  public static ConditionalCheckTuple success(List<String> parameterNames) {
    return new ConditionalCheckTuple(parameterNames);
  }

  public static ConditionalCheckTuple failure(ErrorTuple errorTuple) {
    return new ConditionalCheckTuple(errorTuple);
  }

}
