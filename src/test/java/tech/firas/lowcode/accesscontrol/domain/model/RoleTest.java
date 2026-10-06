package tech.firas.lowcode.accesscontrol.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class RoleTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t\n"})
    void rejectsCreationWithoutACode(String code) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Role.create(code, "Can author and revise documents"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t\n"})
    void rejectsCreationWithoutADescription(String description) {
        assertThatIllegalArgumentException().isThrownBy(() -> Role.create("editor", description));
    }

    @Test
    void canBeCreatedWithACodeAndADescription() {
        Role role = Role.create("editor", "Can author and revise documents");

        assertThat(role.code()).isEqualTo("editor");
        assertThat(role.description()).isEqualTo("Can author and revise documents");
        assertThat(role.permissionCodes()).isEmpty();
    }

    @Test
    void canHaveItsDescriptionReworded() {
        Role role = Role.create("editor", "Can author documents");

        role.reword("Can author and revise documents");

        assertThat(role.description()).isEqualTo("Can author and revise documents");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void rejectsARewordToABlankDescription(String description) {
        Role role = Role.create("editor", "Can author documents");

        assertThatIllegalArgumentException().isThrownBy(() -> role.reword(description));
        assertThat(role.description()).isEqualTo("Can author documents");
    }

    @Test
    void carriesAPermissionItHasBeenGranted() {
        Role role = Role.create("editor", "Can author and revise documents");

        role.grant(Permission.define(PermissionCode.of("document:read"), "Read any document"));

        assertThat(role.permissionCodes()).containsExactly(PermissionCode.of("document:read"));
    }

    @Test
    void noLongerCarriesARevokedPermission() {
        Role role = Role.create("editor", "Can author and revise documents");
        role.grant(Permission.define(PermissionCode.of("document:read"), "Read any document"));
        role.grant(Permission.define(PermissionCode.of("document:write"), "Write any document"));

        role.revoke(PermissionCode.of("document:read"));

        assertThat(role.permissionCodes()).containsExactly(PermissionCode.of("document:write"));
    }

    @Test
    void reportsWhetherARevokeActuallyRemovedAGrant() {
        Role role = Role.create("editor", "Can author and revise documents");
        role.grant(Permission.define(PermissionCode.of("document:read"), "Read any document"));

        assertThat(role.revoke(PermissionCode.of("document:read"))).isTrue();
        assertThat(role.revoke(PermissionCode.of("document:read"))).isFalse();
    }
}
