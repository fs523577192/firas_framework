package tech.firas.lowcode.accesscontrol.interfaces.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import tech.firas.lowcode.accesscontrol.application.AccessAdministrationService;
import tech.firas.lowcode.accesscontrol.application.AccessQueryService;
import tech.firas.lowcode.accesscontrol.application.readmodel.PermissionView;
import tech.firas.lowcode.accesscontrol.application.readmodel.RoleView;
import tech.firas.lowcode.accesscontrol.application.readmodel.UserView;
import tech.firas.lowcode.accesscontrol.domain.model.AccessPermissions;
import tech.firas.lowcode.accesscontrol.domain.model.PermissionCode;

@RestController
@RequestMapping("/api/access/admin")
@PreAuthorize("hasAuthority('" + AccessPermissions.ADMINISTER + "')")
public class AccessAdministrationController {

    public record RegisterUserRequest(String username, String password) {
    }

    public record ChangePasswordRequest(String password) {
    }

    public record CreateRoleRequest(String code, String description) {
    }

    public record UpdateDescriptionRequest(String description) {
    }

    public record DefinePermissionRequest(String code, String description) {
    }

    public record AssignRoleRequest(String roleCode) {
    }

    public record GrantPermissionRequest(String code) {
    }

    private final AccessAdministrationService administration;
    private final AccessQueryService queries;

    public AccessAdministrationController(AccessAdministrationService administration, AccessQueryService queries) {
        this.administration = administration;
        this.queries = queries;
    }

    @GetMapping("/users")
    List<UserView> listUsers() {
        return queries.listUsers();
    }

    @GetMapping("/users/{username}")
    UserView getUser(@PathVariable String username) {
        return queries.requireUser(username);
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    UserView registerUser(@RequestBody RegisterUserRequest request) {
        return AccessQueryService.toView(administration.registerUser(request.username(), request.password()));
    }

    @PostMapping("/users/{username}/password")
    UserView changeUserPassword(@PathVariable String username, @RequestBody ChangePasswordRequest request) {
        return AccessQueryService.toView(administration.changeUserPassword(username, request.password()));
    }

    @DeleteMapping("/users/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteUser(@PathVariable String username) {
        administration.deleteUser(username);
    }

    @PostMapping("/users/{username}/disable")
    UserView disableUser(@PathVariable String username) {
        return AccessQueryService.toView(administration.disableUser(username));
    }

    @PostMapping("/users/{username}/enable")
    UserView enableUser(@PathVariable String username) {
        return AccessQueryService.toView(administration.enableUser(username));
    }

    @PostMapping("/users/{username}/roles")
    UserView assignRole(@PathVariable String username, @RequestBody AssignRoleRequest request) {
        return AccessQueryService.toView(administration.assignRole(username, request.roleCode()));
    }

    @DeleteMapping("/users/{username}/roles/{roleCode}")
    UserView revokeRole(@PathVariable String username, @PathVariable String roleCode) {
        return AccessQueryService.toView(administration.revokeRole(username, roleCode));
    }

    @GetMapping("/roles")
    List<RoleView> listRoles() {
        return queries.listRoles();
    }

    @GetMapping("/roles/{roleCode}")
    RoleView getRole(@PathVariable String roleCode) {
        return queries.requireRole(roleCode);
    }

    @PostMapping("/roles")
    @ResponseStatus(HttpStatus.CREATED)
    RoleView createRole(@RequestBody CreateRoleRequest request) {
        return AccessQueryService.toView(administration.createRole(request.code(), request.description()));
    }

    @PatchMapping("/roles/{roleCode}")
    RoleView updateRoleDescription(@PathVariable String roleCode, @RequestBody UpdateDescriptionRequest request) {
        return AccessQueryService.toView(administration.updateRoleDescription(roleCode, request.description()));
    }

    @DeleteMapping("/roles/{roleCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteRole(@PathVariable String roleCode) {
        administration.deleteRole(roleCode);
    }

    @PostMapping("/roles/{roleCode}/permissions")
    RoleView grantPermission(@PathVariable String roleCode, @RequestBody GrantPermissionRequest request) {
        return AccessQueryService.toView(administration.grantPermission(roleCode, PermissionCode.of(request.code())));
    }

    @DeleteMapping("/roles/{roleCode}/permissions/{permissionCode}")
    RoleView revokePermission(@PathVariable String roleCode, @PathVariable String permissionCode) {
        return AccessQueryService.toView(
                administration.revokePermission(roleCode, PermissionCode.of(permissionCode)));
    }

    @GetMapping("/permissions")
    List<PermissionView> listPermissions() {
        return queries.listPermissions();
    }

    @GetMapping("/permissions/{permissionCode}")
    PermissionView getPermission(@PathVariable String permissionCode) {
        return queries.requirePermission(permissionCode);
    }

    @PostMapping("/permissions")
    @ResponseStatus(HttpStatus.CREATED)
    PermissionView definePermission(@RequestBody DefinePermissionRequest request) {
        return AccessQueryService.toView(
                administration.definePermission(PermissionCode.of(request.code()), request.description()));
    }

    @PatchMapping("/permissions/{permissionCode}")
    PermissionView updatePermissionDescription(
            @PathVariable String permissionCode, @RequestBody UpdateDescriptionRequest request) {
        return AccessQueryService.toView(administration
                .updatePermissionDescription(PermissionCode.of(permissionCode), request.description()));
    }

    @DeleteMapping("/permissions/{permissionCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deletePermission(@PathVariable String permissionCode) {
        administration.deletePermission(PermissionCode.of(permissionCode));
    }
}
