package tech.firas.lowcode.accesscontrol.domain;

public class DuplicateRoleCodeException extends AccessControlException {

    public DuplicateRoleCodeException(String code) {
        super("A role with code " + code + " already exists", AccessControlErrorType.CONFLICT);
    }
}
