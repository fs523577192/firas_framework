package tech.firas.lowcode.accesscontrol.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import tech.firas.lowcode.PostgreSqlIntegrationTest;
import tech.firas.lowcode.accesscontrol.domain.model.Role;
import tech.firas.lowcode.accesscontrol.domain.model.User;

class UserRepositoryTest extends PostgreSqlIntegrationTest {

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository roles;

    @Test
    void makesARegisteredUserFindableByUsernameTogetherWithTheirAssignments() {
        Role editor = roles.save(Role.create("editor", "Can author and revise documents"));
        User alice = User.register("alice", "{noop}s3cret");
        alice.assignRole(editor);

        repository.save(alice);

        User found = repository.findByUsername("alice").orElseThrow();
        assertThat(found.passwordHash()).isEqualTo("{noop}s3cret");
        assertThat(found.isEnabled()).isTrue();
        assertThat(found.roleCodes()).containsExactly("editor");
    }
}
