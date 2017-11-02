package io.bhowell2.ApiLib;

/**
 * @author Blake Howell
 */
public final class CheckFunctionTuple {

    public final String failureMessage;
    public final boolean successful;

    /**
     * The parameter's check may fail or pass. If it passes, there is no need for a failure message. If it fails, sometimes there is also no need for a failure message.
     *
     * @param successful whether or not the check failed
     */
    public CheckFunctionTuple(boolean successful) {
        this.successful = successful;
        this.failureMessage = null;
    }

    /**
     * Optionally, provide a reason for failure.
     *
     * @param failureMessage
     */
    public CheckFunctionTuple(String failureMessage) {
        this.successful = false;  // if a failure message is provided, it wasn't successful..
        this.failureMessage = failureMessage;
    }

    boolean failed() {
        return !successful;
    }

    boolean hasFailureMessage() {
        return failureMessage != null;
    }

        /* Static Creation Methods */

    public static CheckFunctionTuple success() {
        return new CheckFunctionTuple(true);
    }

    public static CheckFunctionTuple failure() {
        return new CheckFunctionTuple(false);
    }

    public static CheckFunctionTuple failure(String failureMessage) {
        return new CheckFunctionTuple(failureMessage);
    }
}
