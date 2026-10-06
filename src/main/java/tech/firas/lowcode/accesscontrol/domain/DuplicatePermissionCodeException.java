package tech.firas.lowcode.accesscontrol.domain;

import tech.firas.lowcode.accesscontrol.domain.model.PermissionCode;

public class DuplicatePermissionCodeException extends AccessControlException {

    public DuplicatePermissionCodeException(PermissionCode code) {
        super("A permission with code " + code.value() + " is already defined", AccessControlErrorType.CONFLICT);
    }
}
