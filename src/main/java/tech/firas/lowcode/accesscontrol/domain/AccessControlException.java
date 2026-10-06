package tech.firas.lowcode.accesscontrol.domain;

public abstract class AccessControlException extends RuntimeException {

    private final AccessControlErrorType errorType;

    protected AccessControlException(String message, AccessControlErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }

    public AccessControlErrorType errorType() {
        return errorType;
    }
}
