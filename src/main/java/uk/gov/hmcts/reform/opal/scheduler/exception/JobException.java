package uk.gov.hmcts.reform.opal.scheduler.exception;

public class JobException extends RuntimeException {

    public JobException(String message, Throwable cause) {
        super(message, cause);
    }
}
