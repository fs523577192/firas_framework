package tech.firas.lowcode.accesscontrol.application;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class AccessAdministrationService {

    private final UserRepository users;
    private final RoleRepository roles;
    private final PermissionRepository permissions;
    private final PasswordEncoder passwordEncoder;

    public AccessAdministrationService(
            UserRepository users,
            RoleRepository roles,
            PermissionRepository permissions,
            PasswordEncoder passwordEncoder) {
        this.users = users;
        this.roles = roles;
        this.permissions = permissions;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(String username, String rawPassword) {
        return users.save(User.register(username, passwordEncoder.encode(rawPassword)));
    }

    @Transactional
    public User changeUserPassword(String username, String rawPassword) {
        User user = requireUser(username);
        user.changePassword(passwordEncoder.encode(rawPassword));
        return users.save(user);
    }

    @Transactional
    public void deleteUser(String username) {
        users.delete(requireUser(username));
    }

    @Transactional
    public User disableUser(String username) {
        User user = requireUser(username);
        user.disable();
        return users.save(user);
    }

    @Transactional
    public User enableUser(String username) {
        User user = requireUser(username);
        user.enable();
        return users.save(user);
    }

    @Transactional
    public User assignRole(String username, String roleCode) {
        User user = requireUser(username);
        user.assignRole(requireRole(roleCode));
        return users.save(user);
    }

    @Transactional
    public User revokeRole(String username, String roleCode) {
        User user = requireUser(username);
        requireRole(roleCode);
        user.revokeRole(roleCode);
        return users.save(user);
    }

    @Transactional
    public Role createRole(String code, String description) {
        return roles.save(Role.create(code, description));
    }

    @Transactional
    public Role updateRoleDescription(String code, String description) {
        Role role = requireRole(code);
        role.reword(description);
        return roles.save(role);
    }

    @Transactional
    public void deleteRole(String code) {
        roles.delete(requireRole(code));
    }

    @Transactional
    public Role grantPermission(String roleCode, PermissionCode permissionCode) {
        Role role = requireRole(roleCode);
        role.grant(requirePermission(permissionCode));
        return roles.save(role);
    }

    @Transactional
    public Role revokePermission(String roleCode, PermissionCode permissionCode) {
        Role role = requireRole(roleCode);
        requirePermission(permissionCode);
        role.revoke(permissionCode);
        return roles.save(role);
    }

    @Transactional
    public Permission definePermission(PermissionCode code, String description) {
        return permissions.save(Permission.define(code, description));
    }

    @Transactional
    public Permission definePermissionIfAbsent(PermissionCode code, String description) {
        return permissions.findByCode(code).orElseGet(() -> definePermission(code, description));
    }

    @Transactional
    public Permission updatePermissionDescription(PermissionCode code, String description) {
        Permission permission = requirePermission(code);
        permission.reword(description);
        return permissions.save(permission);
    }

    @Transactional
    public void deletePermission(PermissionCode code) {
        permissions.delete(requirePermission(code));
    }

    private User requireUser(String username) {
        return users.findByUsername(username).orElseThrow(() -> new NoSuchUserException(username));
    }

    private Role requireRole(String code) {
        return roles.findByCode(code).orElseThrow(() -> new NoSuchRoleException(code));
    }

    private Permission requirePermission(PermissionCode code) {
        return permissions.findByCode(code).orElseThrow(() -> new NoSuchPermissionException(code));
    }
}
