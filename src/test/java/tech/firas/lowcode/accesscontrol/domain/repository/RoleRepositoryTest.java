package tech.firas.lowcode.accesscontrol.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import tech.firas.lowcode.PostgreSqlIntegrationTest;
import tech.firas.lowcode.accesscontrol.domain.RoleStillAssignedException;
import tech.firas.lowcode.accesscontrol.domain.model.Permission;
import tech.firas.lowcode.accesscontrol.domain.model.PermissionCode;
import tech.firas.lowcode.accesscontrol.domain.model.Role;
import tech.firas.lowcode.accesscontrol.domain.model.User;

class RoleRepositoryTest extends PostgreSqlIntegrationTest {

    @Autowired
    private RoleRepository repository;

    @Autowired
    private PermissionRepository permissions;

    @Autowired
    private UserRepository users;

    @Autowired
    private TransactionTemplate transactions;

    @Test
    void makesASavedRoleFindableByItsCodeTogetherWithItsGrants() {
        Permission read = permissions.save(Permission.define(PermissionCode.of("document:read"), "Read any document"));
        Permission write = permissions.save(Permission.define(PermissionCode.of("document:write"), "Write any document"));
        Role editor = Role.create("editor", "Can author and revise documents");
        editor.grant(read);
        editor.grant(write);

        repository.save(editor);

        Role found = repository.findByCode("editor").orElseThrow();
        assertThat(found.description()).isEqualTo("Can author and revise documents");
        assertThat(found.permissionCodes()).containsExactlyInAnyOrder(
                PermissionCode.of("document:read"), PermissionCode.of("document:write"));
    }

    @Test
    void regardsTheSameRoleLoadedInTwoSeparateTransactionsAsEqual() {
        transactions.executeWithoutResult(status ->
                repository.save(Role.create("editor", "Can author and revise documents")));

        Role first = transactions.execute(status -> repository.findByCode("editor").orElseThrow());
        Role second = transactions.execute(status -> repository.findByCode("editor").orElseThrow());

        assertThat(first).isEqualTo(second).hasSameHashCodeAs(second);
    }

    @Test
    void dropsARolesGrantsWhenTheRoleIsDeleted() {
        Permission read = permissions.save(Permission.define(PermissionCode.of("document:read"), "Read any document"));
        Role editor = Role.create("editor", "Can author and revise documents");
        editor.grant(read);
        repository.save(editor);

        repository.delete(editor);

        assertThat(permissions.findByCode(PermissionCode.of("document:read"))).isPresent();
        assertThat(repository.findByCode("editor")).isEmpty();
    }

    @Test
    void refusesToDeleteARoleThatUsersStillHold() {
        Role editor = repository.save(Role.create("editor", "Can author and revise documents"));
        User alice = User.register("alice", "{noop}s3cret");
        alice.assignRole(editor);
        users.save(alice);

        assertThatThrownBy(() -> repository.delete(editor))
                .isInstanceOf(RoleStillAssignedException.class)
                .hasMessageContaining("editor");

        assertThat(repository.findByCode("editor")).isPresent();
    }
}
