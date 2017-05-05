package io.bhowell2.ApiLib;

/**
 * @author Blake Howell
 */
public class FunctionCheckTuple {

  String failureMessage;
  boolean successful;

  /**
   * The parameter's check may fail or pass. If it passes, there is no need for a failure message. If it fails, sometimes there is also no need for a failure message.
   * @param successful whether or not the check failed
   */
  public FunctionCheckTuple(boolean successful) {
    this.successful = successful;
  }

  /**
   * Optionally provide a reason for failure.
   * @param failureMessage
   */
  public FunctionCheckTuple(String failureMessage) {
    this.successful = false;  // if a failure message is provided, it wasn't successful..
    this.failureMessage = failureMessage;
  }

  boolean isSuccessful() {
    return successful;
  }

  boolean failed() {
    return !successful;
  }

  String getFailureMessage() {
    return failureMessage;
  }

  boolean hasFailureMessage() {
    return failureMessage != null;
  }

  /* Static Creation Methods */

  public static FunctionCheckTuple success() {
    return new FunctionCheckTuple(true);
  }

  public static FunctionCheckTuple failure() {
    return new FunctionCheckTuple(false);
  }

  public static FunctionCheckTuple failure(String failureMessage) {
    return new FunctionCheckTuple(failureMessage);
  }

}
