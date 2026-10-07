package tech.firas.lowcode.accesscontrol.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import tech.firas.lowcode.IntegrationTest;
import tech.firas.lowcode.accesscontrol.domain.NoSuchPermissionException;
import tech.firas.lowcode.accesscontrol.domain.NoSuchRoleException;
import tech.firas.lowcode.accesscontrol.domain.NoSuchUserException;
import tech.firas.lowcode.accesscontrol.domain.RoleStillAssignedException;
import tech.firas.lowcode.accesscontrol.domain.model.PermissionCode;
import tech.firas.lowcode.accesscontrol.domain.model.User;
import tech.firas.lowcode.accesscontrol.domain.repository.PermissionRepository;
import tech.firas.lowcode.accesscontrol.domain.repository.RoleRepository;
import tech.firas.lowcode.accesscontrol.domain.repository.UserRepository;

class AccessAdministrationServiceTest extends IntegrationTest {

    @Autowired
    private UserRepository users;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthorizationService authorization;

    @Autowired
    private RoleRepository roles;

    @Autowired
    private PermissionRepository permissions;

    @Test
    void registersAUserWhosePasswordIsStoredEncoded() {
        User alice = administration.registerUser("alice", "s3cret");

        assertThat(users.findByUsername("alice")).isPresent();
        assertThat(alice.passwordHash()).isNotEqualTo("s3cret");
        assertThat(passwordEncoder.matches("s3cret", alice.passwordHash())).isTrue();
    }

    @Test
    void assigningAnExistingRoleToAnExistingUserMakesThatUserHoldIt() {
        administration.registerUser("alice", "s3cret");
        administration.createRole("editor", "Can author and revise documents");

        administration.assignRole("alice", "editor");

        assertThat(users.findByUsername("alice").orElseThrow().roleCodes()).containsExactly("editor");
    }

    @Test
    void grantingAPermissionToARoleAUserHoldsMakesThatUserPermitted() {
        User alice = administration.registerUser("alice", "s3cret");
        administration.createRole("editor", "Can author and revise documents");
        administration.definePermission(PermissionCode.of("document:read"), "Read any document");
        administration.assignRole("alice", "editor");
        PermissionCode required = PermissionCode.of("document:read");

        assertThat(authorization.authorize(alice.id(), required).allowed()).isFalse();

        administration.grantPermission("editor", required);

        assertThat(authorization.authorize(alice.id(), required).allowed()).isTrue();
    }

    @Test
    void reportsAssigningARoleThatWasNeverCreated() {
        administration.registerUser("alice", "s3cret");

        assertThatThrownBy(() -> administration.assignRole("alice", "ghost"))
                .isInstanceOf(NoSuchRoleException.class)
                .hasMessageContaining("ghost");
    }

    @Test
    void reportsAssigningARoleToAUserThatWasNeverRegistered() {
        administration.createRole("editor", "Can author and revise documents");

        assertThatThrownBy(() -> administration.assignRole("ghost", "editor"))
                .isInstanceOf(NoSuchUserException.class)
                .hasMessageContaining("ghost");
    }

    @Test
    void reportsGrantingAPermissionThatWasNeverDefined() {
        administration.createRole("editor", "Can author and revise documents");

        assertThatThrownBy(() -> administration.grantPermission("editor", PermissionCode.of("document:read")))
                .isInstanceOf(NoSuchPermissionException.class)
                .hasMessageContaining("document:read");
    }

    @Test
    void revokingARoleFromAUserTakesItsPermissionsAwayFromThem() {
        User alice = givenAlicePermittedToReadDocuments();
        PermissionCode required = PermissionCode.of("document:read");
        assertThat(authorization.authorize(alice.id(), required).allowed()).isTrue();

        administration.revokeRole("alice", "editor");

        assertThat(authorization.authorize(alice.id(), required).allowed()).isFalse();
    }

    @Test
    void revokingAPermissionFromARoleTakesItAwayFromEveryoneHoldingThatRole() {
        User alice = givenAlicePermittedToReadDocuments();
        PermissionCode required = PermissionCode.of("document:read");
        assertThat(authorization.authorize(alice.id(), required).allowed()).isTrue();

        administration.revokePermission("editor", required);

        assertThat(authorization.authorize(alice.id(), required).allowed()).isFalse();
    }

    @Test
    void deletingARoleNobodyHoldsRemovesIt() {
        administration.createRole("editor", "Can author and revise documents");

        administration.deleteRole("editor");

        assertThat(roles.findByCode("editor")).isEmpty();
    }

    @Test
    void deletingARoleThatUsersStillHoldIsRefused() {
        givenAlicePermittedToReadDocuments();

        assertThatThrownBy(() -> administration.deleteRole("editor"))
                .isInstanceOf(RoleStillAssignedException.class);

        assertThat(roles.findByCode("editor")).isPresent();
    }

    @Test
    void disablingAUserTakesTheirAuthorizationAway() {
        User alice = givenAlicePermittedToReadDocuments();
        PermissionCode required = PermissionCode.of("document:read");

        administration.disableUser("alice");

        assertThat(authorization.authorize(alice.id(), required).allowed()).isFalse();
    }

    @Test
    void reEnablingAUserRestoresTheirAuthorization() {
        User alice = givenAlicePermittedToReadDocuments();
        administration.disableUser("alice");
        PermissionCode required = PermissionCode.of("document:read");

        administration.enableUser("alice");

        assertThat(authorization.authorize(alice.id(), required).allowed()).isTrue();
    }

    @Test
    void deletingAUserRemovesThemButKeepsTheRolesTheyHeld() {
        givenAlicePermittedToReadDocuments();

        administration.deleteUser("alice");

        assertThat(users.findByUsername("alice")).isEmpty();
        assertThat(roles.findByCode("editor")).isPresent();
    }

    @Test
    void reportsDeletingAUserThatWasNeverRegistered() {
        assertThatThrownBy(() -> administration.deleteUser("ghost"))
                .isInstanceOf(NoSuchUserException.class);
    }

    @Test
    void changingAUsersPasswordReplacesTheStoredHash() {
        administration.registerUser("alice", "old-password");

        administration.changeUserPassword("alice", "new-password");

        String stored = users.findByUsername("alice").orElseThrow().passwordHash();
        assertThat(passwordEncoder.matches("new-password", stored)).isTrue();
        assertThat(passwordEncoder.matches("old-password", stored)).isFalse();
    }

    @Test
    void updatingARolesDescriptionRewordsIt() {
        administration.createRole("editor", "Can author documents");

        administration.updateRoleDescription("editor", "Can author and revise documents");

        assertThat(roles.findByCode("editor").orElseThrow().description())
                .isEqualTo("Can author and revise documents");
    }

    @Test
    void updatingAPermissionsDescriptionRewordsIt() {
        administration.definePermission(PermissionCode.of("document:read"), "Read");

        administration.updatePermissionDescription(PermissionCode.of("document:read"), "Read any document");

        assertThat(permissions.findByCode(PermissionCode.of("document:read")).orElseThrow().description())
                .isEqualTo("Read any document");
    }

    @Test
    void reportsUpdatingARoleThatDoesNotExist() {
        assertThatThrownBy(() -> administration.updateRoleDescription("ghost", "Anything"))
                .isInstanceOf(NoSuchRoleException.class);
    }

    @Test
    void definingAnAlreadyDefinedPermissionAgainLeavesTheOriginalAlone() {
        administration.definePermission(PermissionCode.of("document:read"), "Read any document");

        administration.definePermissionIfAbsent(PermissionCode.of("document:read"), "A conflicting description");

        assertThat(permissions.findByCode(PermissionCode.of("document:read")).orElseThrow().description())
                .isEqualTo("Read any document");
    }

    private User givenAlicePermittedToReadDocuments() {
        User alice = administration.registerUser("alice", "s3cret");
        administration.createRole("editor", "Can author and revise documents");
        administration.definePermission(PermissionCode.of("document:read"), "Read any document");
        administration.assignRole("alice", "editor");
        administration.grantPermission("editor", PermissionCode.of("document:read"));
        return alice;
    }
}
