package tech.firas.lowcode.accesscontrol.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import tech.firas.lowcode.PostgreSqlIntegrationTest;
import tech.firas.lowcode.accesscontrol.domain.DuplicatePermissionCodeException;
import tech.firas.lowcode.accesscontrol.domain.PermissionStillGrantedException;
import tech.firas.lowcode.accesscontrol.domain.model.Permission;
import tech.firas.lowcode.accesscontrol.domain.model.PermissionCode;
import tech.firas.lowcode.accesscontrol.domain.model.Role;

class PermissionRepositoryTest extends PostgreSqlIntegrationTest {

    @Autowired
    private PermissionRepository repository;

    @Autowired
    private RoleRepository roles;

    @Autowired
    private TransactionTemplate transactions;

    @Test
    void makesASavedPermissionFindableByItsCode() {
        repository.save(Permission.define(PermissionCode.of("document:read"), "Read any document"));

        Optional<Permission> found = repository.findByCode(PermissionCode.of("document:read"));

        assertThat(found).isPresent();
        assertThat(found.get().description()).isEqualTo("Read any document");
    }

    @Test
    void reportsASecondPermissionDefinedWithAnAlreadyTakenCode() {
        repository.save(Permission.define(PermissionCode.of("document:read"), "Read any document"));

        assertThatThrownBy(() -> repository
                .save(Permission.define(PermissionCode.of("document:read"), "A second, conflicting definition")))
                .isInstanceOf(DuplicatePermissionCodeException.class)
                .hasMessageContaining("document:read");

        assertThat(repository.findByCode(PermissionCode.of("document:read")).orElseThrow().description())
                .isEqualTo("Read any document");
    }

    @Test
    void refusesToDeleteAPermissionThatRolesStillCarry() {
        Permission read = repository.save(Permission.define(PermissionCode.of("document:read"), "Read any document"));
        Role editor = Role.create("editor", "Can author and revise documents");
        editor.grant(read);
        roles.save(editor);

        assertThatThrownBy(() -> repository.delete(read))
                .isInstanceOf(PermissionStillGrantedException.class)
                .hasMessageContaining("document:read");

        assertThat(repository.findByCode(PermissionCode.of("document:read"))).isPresent();
    }

    @Test
    void regardsTheSamePermissionLoadedInTwoSeparateTransactionsAsEqual() {
        PermissionCode code = PermissionCode.of("document:read");
        transactions.executeWithoutResult(status ->
                repository.save(Permission.define(code, "Read any document")));

        Permission first = transactions.execute(status -> repository.findByCode(code).orElseThrow());
        Permission second = transactions.execute(status -> repository.findByCode(code).orElseThrow());

        assertThat(first).isEqualTo(second).hasSameHashCodeAs(second);
    }

    @Test
    void doesNotReportADuplicateWhenTheSamePermissionIsSavedAgain() {
        Permission read = repository.save(Permission.define(PermissionCode.of("document:read"), "Read any document"));

        repository.save(read);

        assertThat(repository.findByCode(PermissionCode.of("document:read"))).isPresent();
    }

    @Test
    void deletesAPermissionThatNoRoleCarries() {
        Permission read = repository.save(Permission.define(PermissionCode.of("document:read"), "Read any document"));

        repository.delete(read);

        assertThat(repository.findByCode(PermissionCode.of("document:read"))).isEmpty();
    }
}
