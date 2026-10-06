package tech.firas.lowcode.accesscontrol.infrastructure.bootstrap;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import tech.firas.lowcode.PostgreSqlIntegrationTest;
import tech.firas.lowcode.accesscontrol.application.AuthorizationService;
import tech.firas.lowcode.accesscontrol.domain.model.PermissionCode;
import tech.firas.lowcode.accesscontrol.domain.model.User;
import tech.firas.lowcode.accesscontrol.domain.repository.UserRepository;

class AccessBootstrapTest extends PostgreSqlIntegrationTest {

    @Autowired
    private AccessBootstrap bootstrap;

    @Autowired
    private UserRepository users;

    @Autowired
    private AuthorizationService authorization;

    @Test
    void registersTheConfiguredAdministratorAlreadyPermittedToAdministerAccess() {
        bootstrap.ensureAdministratorExists();

        User admin = users.findByUsername("admin").orElseThrow();
        assertThat(admin.isEnabled()).isTrue();
        assertThat(authorization.authorize(admin.id(), PermissionCode.of(ADMINISTER_ACCESS)).allowed()).isTrue();
    }

    @Test
    void leavesAnAlreadyBootstrappedAdministratorAlone() {
        bootstrap.ensureAdministratorExists();
        UUID firstId = users.findByUsername("admin").orElseThrow().id();

        bootstrap.ensureAdministratorExists();

        assertThat(users.findByUsername("admin").orElseThrow().id()).isEqualTo(firstId);
    }
}
