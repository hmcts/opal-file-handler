package uk.gov.hmcts.reform.opal.scheduler.exception;

public class JobNotFoundException extends RuntimeException {

    public JobNotFoundException(String message) {
        super(message);
    }
}
