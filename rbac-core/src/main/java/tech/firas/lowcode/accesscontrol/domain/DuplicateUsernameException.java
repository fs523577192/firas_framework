package tech.firas.lowcode.accesscontrol.domain;

public class DuplicateUsernameException extends AccessControlException {

    public DuplicateUsernameException(String username) {
        super("A user named " + username + " is already registered", AccessControlErrorType.CONFLICT);
    }
}
