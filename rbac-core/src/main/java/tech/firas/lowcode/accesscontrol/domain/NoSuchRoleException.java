package tech.firas.lowcode.accesscontrol.domain;

public class NoSuchRoleException extends AccessControlException {

    public NoSuchRoleException(String code) {
        super("No role exists with code " + code, AccessControlErrorType.NOT_FOUND);
    }
}
