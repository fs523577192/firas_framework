package tech.firas.lowcode.accesscontrol.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import tech.firas.lowcode.IntegrationTest;
import tech.firas.lowcode.accesscontrol.domain.model.AuthorizationDecision;
import tech.firas.lowcode.accesscontrol.domain.model.Permission;
import tech.firas.lowcode.accesscontrol.domain.model.PermissionCode;
import tech.firas.lowcode.accesscontrol.domain.model.Role;
import tech.firas.lowcode.accesscontrol.domain.model.User;
import tech.firas.lowcode.accesscontrol.domain.repository.PermissionRepository;
import tech.firas.lowcode.accesscontrol.domain.repository.RoleRepository;
import tech.firas.lowcode.accesscontrol.domain.repository.UserRepository;

class AuthorizationServiceTest extends IntegrationTest {

    @Autowired
    private AuthorizationService authorization;

    @Autowired
    private UserRepository users;

    @Autowired
    private RoleRepository roles;

    @Autowired
    private PermissionRepository permissions;

    private User registerAliceHolding(String roleCode, String... permissionCodes) {
        Role role = Role.create(roleCode, "Role " + roleCode);
        for (String permissionCode : permissionCodes) {
            PermissionCode code = PermissionCode.of(permissionCode);
            role.grant(permissions.save(Permission.define(code, "Permission " + permissionCode)));
        }
        roles.save(role);

        User alice = User.register("alice", "{noop}s3cret");
        alice.assignRole(role);
        return users.save(alice);
    }

    @Test
    void allowsAUserWhoseRoleCarriesTheRequiredPermission() {
        User alice = registerAliceHolding("editor", "document:read", "document:write");

        AuthorizationDecision decision = authorization.authorize(alice.id(), PermissionCode.of("document:write"));

        assertThat(decision.allowed()).isTrue();
    }

    @Test
    void deniesADisabledUserEvenThoughTheirRoleCarriesThePermission() {
        User alice = registerAliceHolding("editor", "document:read");
        alice.disable();
        users.save(alice);

        AuthorizationDecision decision = authorization.authorize(alice.id(), PermissionCode.of("document:read"));

        assertThat(decision.allowed()).isFalse();
        assertThat(decision.reason()).contains("disabled");
    }

    @Test
    void deniesAPermissionThatNoneOfTheUsersRolesCarry() {
        User alice = registerAliceHolding("editor", "document:read");

        AuthorizationDecision decision = authorization.authorize(alice.id(), PermissionCode.of("document:publish"));

        assertThat(decision.allowed()).isFalse();
    }

    @Test
    void deniesAPermissionCarriedOnlyByARoleTheUserDoesNotHold() {
        Role admin = Role.create("admin", "Can do anything");
        admin.grant(permissions.save(Permission.define(PermissionCode.of("user:disable"), "Disable a user")));
        roles.save(admin);
        User alice = registerAliceHolding("editor", "document:read");

        AuthorizationDecision decision = authorization.authorize(alice.id(), PermissionCode.of("user:disable"));

        assertThat(decision.allowed()).isFalse();
    }

    @Test
    void allowsAPermissionThatArrivesThroughAnyOneOfSeveralRoles() {
        User alice = registerAliceHolding("editor", "document:read", "document:write");
        Role publisher = Role.create("publisher", "Can release documents");
        publisher.grant(permissions.save(Permission.define(PermissionCode.of("document:publish"), "Release a document")));
        roles.save(publisher);
        alice.assignRole(publisher);
        users.save(alice);

        assertThat(authorization.authorize(alice.id(), PermissionCode.of("document:read")).allowed()).isTrue();
        assertThat(authorization.authorize(alice.id(), PermissionCode.of("document:publish")).allowed()).isTrue();
    }

    @Test
    void deniesAUserThatDoesNotExist() {
        AuthorizationDecision decision = authorization.authorize(
                UUID.randomUUID(), PermissionCode.of("document:read"));

        assertThat(decision.allowed()).isFalse();
        assertThat(decision.reason()).contains("No user is registered");
    }

    @Test
    void deniesAUserWhoHoldsNoRoleAtAll() {
        User alice = users.save(User.register("alice", "{noop}s3cret"));

        AuthorizationDecision decision = authorization.authorize(alice.id(), PermissionCode.of("document:read"));

        assertThat(decision.allowed()).isFalse();
    }
}
