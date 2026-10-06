package tech.firas.lowcode.accesscontrol.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class PermissionCodeTest {

    @Test
    void acceptsAResourceAndActionCode() {
        assertThat(PermissionCode.of("document:read").value()).isEqualTo("document:read");
    }

    @Test
    void acceptsAMultiSegmentCode() {
        assertThat(PermissionCode.of("report:finance:export").value()).isEqualTo("report:finance:export");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            "   ",
            "document",
            "Document:Read",
            "document:",
            ":read",
            "document::read",
            "1document:read",
            "-document:read",
            "document:1read",
            "document:-read",
            "document read:all",
            "document_read:all",
            "文档:读取",
    })
    void rejectsAMalformedCode(String raw) {
        assertThatIllegalArgumentException().isThrownBy(() -> PermissionCode.of(raw));
    }

    @Test
    void isComparedByValue() {
        assertThat(PermissionCode.of("document:read")).isEqualTo(PermissionCode.of("document:read"));
        assertThat(PermissionCode.of("document:read")).isNotEqualTo(PermissionCode.of("document:write"));
    }
}
