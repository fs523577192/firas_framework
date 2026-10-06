package tech.firas.lowcode.accesscontrol.domain;

import tech.firas.lowcode.accesscontrol.domain.model.PermissionCode;

public class NoSuchPermissionException extends AccessControlException {

    public NoSuchPermissionException(PermissionCode code) {
        super("No permission is defined with code " + code.value(), AccessControlErrorType.NOT_FOUND);
    }
}
