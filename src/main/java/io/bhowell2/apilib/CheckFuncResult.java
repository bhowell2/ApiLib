package io.bhowell2.apilib;

/**
 * @author Blake Howell
 */
public final class CheckFuncResult {

    public final String failureMessage;
    public final boolean successful;

    /**
     * The parameter's check may fail or pass. If it passes, there is no need for a failure message.
     * If it fails, sometimes there is also no need for a failure message (hence the need for successful param and not just failure message).
     * @param successful whether or not the check failed
     */
    public CheckFuncResult(boolean successful) {
        this.successful = successful;
        this.failureMessage = null;
    }

    /**
     * Optionally, provide a reason for failure.
     *
     * @param failureMessage
     */
    public CheckFuncResult(String failureMessage) {
        this.successful = false;  // if a failure message is provided, it wasn't successful..
        this.failureMessage = failureMessage;
    }

    public boolean successful() {
        return successful;
    }

    public boolean failed() {
        return !successful;
    }

    public boolean hasFailureMessage() {
        return failureMessage != null;
    }

        /* Static Creation Methods */

    public static CheckFuncResult success() {
        return new CheckFuncResult(true);
    }

    public static CheckFuncResult failure() {
        return new CheckFuncResult(false);
    }

    public static CheckFuncResult failure(String failureMessage) {
        return new CheckFuncResult(failureMessage);
    }
}
