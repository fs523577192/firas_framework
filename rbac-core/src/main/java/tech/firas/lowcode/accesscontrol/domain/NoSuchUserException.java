package tech.firas.lowcode.accesscontrol.domain;

public class NoSuchUserException extends AccessControlException {

    public NoSuchUserException(String username) {
        super("No user is registered as " + username, AccessControlErrorType.NOT_FOUND);
    }
}
