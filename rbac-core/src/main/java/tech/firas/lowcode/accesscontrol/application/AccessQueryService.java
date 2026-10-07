package tech.firas.lowcode.accesscontrol.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tech.firas.lowcode.accesscontrol.application.readmodel.PermissionView;
import tech.firas.lowcode.accesscontrol.application.readmodel.RoleView;
import tech.firas.lowcode.accesscontrol.application.readmodel.UserView;
import tech.firas.lowcode.accesscontrol.domain.NoSuchPermissionException;
import tech.firas.lowcode.accesscontrol.domain.NoSuchRoleException;
import tech.firas.lowcode.accesscontrol.domain.NoSuchUserException;
import tech.firas.lowcode.accesscontrol.domain.model.Permission;
import tech.firas.lowcode.accesscontrol.domain.model.PermissionCode;
import tech.firas.lowcode.accesscontrol.domain.model.Role;
import tech.firas.lowcode.accesscontrol.domain.model.User;
import tech.firas.lowcode.accesscontrol.domain.repository.PermissionRepository;
import tech.firas.lowcode.accesscontrol.domain.repository.RoleRepository;
import tech.firas.lowcode.accesscontrol.domain.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class AccessQueryService {

    private final UserRepository users;
    private final RoleRepository roles;
    private final PermissionRepository permissions;

    public AccessQueryService(UserRepository users, RoleRepository roles, PermissionRepository permissions) {
        this.users = users;
        this.roles = roles;
        this.permissions = permissions;
    }

    public List<UserView> listUsers() {
        return users.findAll().stream().map(AccessQueryService::toView).toList();
    }

    public List<RoleView> listRoles() {
        return roles.findAll().stream().map(AccessQueryService::toView).toList();
    }

    public List<PermissionView> listPermissions() {
        return permissions.findAll().stream().map(AccessQueryService::toView).toList();
    }

    public UserView requireUser(String username) {
        return users.findByUsername(username)
                .map(AccessQueryService::toView)
                .orElseThrow(() -> new NoSuchUserException(username));
    }

    public RoleView requireRole(String code) {
        return roles.findByCode(code)
                .map(AccessQueryService::toView)
                .orElseThrow(() -> new NoSuchRoleException(code));
    }

    public PermissionView requirePermission(String code) {
        PermissionCode permissionCode = PermissionCode.of(code);
        return permissions.findByCode(permissionCode)
                .map(AccessQueryService::toView)
                .orElseThrow(() -> new NoSuchPermissionException(permissionCode));
    }

    public static UserView toView(User user) {
        return new UserView(
                user.username(),
                user.isEnabled(),
                user.roleCodes().stream().sorted().toList(),
                user.effectivePermissions().stream().map(PermissionCode::value).sorted().toList());
    }

    public static RoleView toView(Role role) {
        return new RoleView(
                role.code(),
                role.description(),
                role.permissionCodes().stream().map(PermissionCode::value).sorted().toList());
    }

    public static PermissionView toView(Permission permission) {
        return new PermissionView(permission.code().value(), permission.description());
    }
}
