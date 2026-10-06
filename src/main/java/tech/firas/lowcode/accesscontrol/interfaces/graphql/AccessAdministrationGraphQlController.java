package tech.firas.lowcode.accesscontrol.interfaces.graphql;

import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import tech.firas.lowcode.accesscontrol.application.AccessAdministrationService;
import tech.firas.lowcode.accesscontrol.application.AccessQueryService;
import tech.firas.lowcode.accesscontrol.application.readmodel.PermissionView;
import tech.firas.lowcode.accesscontrol.application.readmodel.RoleView;
import tech.firas.lowcode.accesscontrol.application.readmodel.UserView;
import tech.firas.lowcode.accesscontrol.domain.model.AccessPermissions;
import tech.firas.lowcode.accesscontrol.domain.model.PermissionCode;

@Controller
@PreAuthorize("hasAuthority('" + AccessPermissions.ADMINISTER + "')")
public class AccessAdministrationGraphQlController {

    private final AccessAdministrationService administration;
    private final AccessQueryService queries;

    public AccessAdministrationGraphQlController(
            AccessAdministrationService administration, AccessQueryService queries) {
        this.administration = administration;
        this.queries = queries;
    }

    @QueryMapping
    List<UserView> users() {
        return queries.listUsers();
    }

    @QueryMapping
    UserView user(@Argument String username) {
        return queries.requireUser(username);
    }

    @QueryMapping
    List<RoleView> roles() {
        return queries.listRoles();
    }

    @QueryMapping
    RoleView role(@Argument String code) {
        return queries.requireRole(code);
    }

    @QueryMapping
    List<PermissionView> permissions() {
        return queries.listPermissions();
    }

    @QueryMapping
    PermissionView permission(@Argument String code) {
        return queries.requirePermission(code);
    }

    @MutationMapping
    UserView registerUser(@Argument String username, @Argument String password) {
        return AccessQueryService.toView(administration.registerUser(username, password));
    }

    @MutationMapping
    UserView changeUserPassword(@Argument String username, @Argument String password) {
        return AccessQueryService.toView(administration.changeUserPassword(username, password));
    }

    @MutationMapping
    boolean deleteUser(@Argument String username) {
        administration.deleteUser(username);
        return true;
    }

    @MutationMapping
    UserView disableUser(@Argument String username) {
        return AccessQueryService.toView(administration.disableUser(username));
    }

    @MutationMapping
    UserView enableUser(@Argument String username) {
        return AccessQueryService.toView(administration.enableUser(username));
    }

    @MutationMapping
    UserView assignRole(@Argument String username, @Argument String roleCode) {
        return AccessQueryService.toView(administration.assignRole(username, roleCode));
    }

    @MutationMapping
    UserView revokeRole(@Argument String username, @Argument String roleCode) {
        return AccessQueryService.toView(administration.revokeRole(username, roleCode));
    }

    @MutationMapping
    RoleView createRole(@Argument String code, @Argument String description) {
        return AccessQueryService.toView(administration.createRole(code, description));
    }

    @MutationMapping
    RoleView updateRoleDescription(@Argument String code, @Argument String description) {
        return AccessQueryService.toView(administration.updateRoleDescription(code, description));
    }

    @MutationMapping
    boolean deleteRole(@Argument String code) {
        administration.deleteRole(code);
        return true;
    }

    @MutationMapping
    RoleView grantPermission(@Argument String roleCode, @Argument String code) {
        return AccessQueryService.toView(administration.grantPermission(roleCode, PermissionCode.of(code)));
    }

    @MutationMapping
    RoleView revokePermission(@Argument String roleCode, @Argument String code) {
        return AccessQueryService.toView(administration.revokePermission(roleCode, PermissionCode.of(code)));
    }

    @MutationMapping
    PermissionView definePermission(@Argument String code, @Argument String description) {
        return AccessQueryService.toView(administration.definePermission(PermissionCode.of(code), description));
    }

    @MutationMapping
    PermissionView updatePermissionDescription(@Argument String code, @Argument String description) {
        return AccessQueryService.toView(
                administration.updatePermissionDescription(PermissionCode.of(code), description));
    }

    @MutationMapping
    boolean deletePermission(@Argument String code) {
        administration.deletePermission(PermissionCode.of(code));
        return true;
    }
}
