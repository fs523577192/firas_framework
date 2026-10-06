package tech.firas.lowcode.accesscontrol.domain;

public class RoleStillAssignedException extends AccessControlException {

    public RoleStillAssignedException(String roleCode) {
        super("Role " + roleCode + " cannot be deleted while users still hold it", AccessControlErrorType.CONFLICT);
    }
}
