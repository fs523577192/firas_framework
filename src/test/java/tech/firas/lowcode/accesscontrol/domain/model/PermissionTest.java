package tech.firas.lowcode.accesscontrol.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class PermissionTest {

    @Test
    void canBeDefinedWithACodeAndADescription() {
        Permission permission = Permission.define(PermissionCode.of("document:read"), "Read any document");

        assertThat(permission.code()).isEqualTo(PermissionCode.of("document:read"));
        assertThat(permission.description()).isEqualTo("Read any document");
    }

    @Test
    void canHaveItsDescriptionReworded() {
        Permission permission = Permission.define(PermissionCode.of("document:read"), "Read");

        permission.reword("Read any document in the workspace");

        assertThat(permission.description()).isEqualTo("Read any document in the workspace");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void rejectsARewordToABlankDescription(String description) {
        Permission permission = Permission.define(PermissionCode.of("document:read"), "Read");

        assertThatIllegalArgumentException().isThrownBy(() -> permission.reword(description));
        assertThat(permission.description()).isEqualTo("Read");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t\n"})
    void rejectsADefinitionWithoutADescription(String description) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Permission.define(PermissionCode.of("document:read"), description));
    }

    @Test
    void rejectsADefinitionWithoutACode() {
        assertThatIllegalArgumentException().isThrownBy(() -> Permission.define(null, "Read any document"));
    }
}
