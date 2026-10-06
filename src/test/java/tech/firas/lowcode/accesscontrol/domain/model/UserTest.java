package tech.firas.lowcode.accesscontrol.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class UserTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t\n"})
    void rejectsRegistrationWithoutAUsername(String username) {
        assertThatIllegalArgumentException().isThrownBy(() -> User.register(username, "{noop}s3cret"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t\n"})
    void rejectsRegistrationWithoutAPasswordHash(String passwordHash) {
        assertThatIllegalArgumentException().isThrownBy(() -> User.register("alice", passwordHash));
    }

    @Test
    void canBeRegisteredWithAUsernameAndAPasswordHash() {
        User user = User.register("alice", "{noop}s3cret");

        assertThat(user.username()).isEqualTo("alice");
        assertThat(user.passwordHash()).isEqualTo("{noop}s3cret");
    }

    @Test
    void holdsARoleThatHasBeenAssigned() {
        User user = User.register("alice", "{noop}s3cret");

        user.assignRole(Role.create("editor", "Can author and revise documents"));

        assertThat(user.roleCodes()).containsExactly("editor");
    }

    @Test
    void reportsWhetherARevokeActuallyRemovedARoleAssignment() {
        User user = User.register("alice", "{noop}s3cret");
        user.assignRole(Role.create("editor", "Can author and revise documents"));

        assertThat(user.revokeRole("editor")).isTrue();
        assertThat(user.roleCodes()).isEmpty();
        assertThat(user.revokeRole("editor")).isFalse();
    }

    @Test
    void isEnabledUponRegistration() {
        assertThat(User.register("alice", "{noop}s3cret").isEnabled()).isTrue();
    }

    @Test
    void canBeDisabledAndReEnabled() {
        User user = User.register("alice", "{noop}s3cret");

        user.disable();
        assertThat(user.isEnabled()).isFalse();

        user.enable();
        assertThat(user.isEnabled()).isTrue();
    }

    @Test
    void canHaveItsPasswordChanged() {
        User alice = User.register("alice", "{noop}old");

        alice.changePassword("{noop}new");

        assertThat(alice.passwordHash()).isEqualTo("{noop}new");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void rejectsAChangeToABlankPasswordHash(String passwordHash) {
        User alice = User.register("alice", "{noop}old");

        assertThatIllegalArgumentException().isThrownBy(() -> alice.changePassword(passwordHash));
        assertThat(alice.passwordHash()).isEqualTo("{noop}old");
    }

    @Test
    void holdsTheUnionOfThePermissionsCarriedByEveryAssignedRole() {
        Role editor = Role.create("editor", "Can author and revise documents");
        editor.grant(Permission.define(PermissionCode.of("document:read"), "Read any document"));
        editor.grant(Permission.define(PermissionCode.of("document:write"), "Write any document"));
        Role publisher = Role.create("publisher", "Can release documents");
        publisher.grant(Permission.define(PermissionCode.of("document:write"), "Write any document"));
        publisher.grant(Permission.define(PermissionCode.of("document:publish"), "Release a document"));

        User alice = User.register("alice", "{noop}s3cret");
        alice.assignRole(editor);
        alice.assignRole(publisher);

        assertThat(alice.effectivePermissions()).containsExactlyInAnyOrder(
                PermissionCode.of("document:read"),
                PermissionCode.of("document:write"),
                PermissionCode.of("document:publish"));
    }
}
