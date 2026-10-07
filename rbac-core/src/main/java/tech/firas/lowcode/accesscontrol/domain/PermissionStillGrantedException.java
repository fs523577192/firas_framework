package tech.firas.lowcode.accesscontrol.domain;

import tech.firas.lowcode.accesscontrol.domain.model.PermissionCode;

public class PermissionStillGrantedException extends AccessControlException {

    public PermissionStillGrantedException(PermissionCode code) {
        super("Permission " + code.value() + " cannot be deleted while roles still carry it",
                AccessControlErrorType.CONFLICT);
    }
}
