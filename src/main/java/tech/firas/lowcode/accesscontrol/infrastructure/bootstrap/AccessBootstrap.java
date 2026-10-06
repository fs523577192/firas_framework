package tech.firas.lowcode.accesscontrol.infrastructure.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import tech.firas.lowcode.accesscontrol.application.AccessAdministrationService;
import tech.firas.lowcode.accesscontrol.domain.model.PermissionCode;
import tech.firas.lowcode.accesscontrol.domain.repository.RoleRepository;
import tech.firas.lowcode.accesscontrol.domain.repository.UserRepository;

@Component
public class AccessBootstrap {

    private static final Logger log = LoggerFactory.getLogger(AccessBootstrap.class);

    private final AccessBootstrapProperties properties;
    private final AccessAdministrationService administration;
    private final UserRepository users;
    private final RoleRepository roles;

    public AccessBootstrap(
            AccessBootstrapProperties properties,
            AccessAdministrationService administration,
            UserRepository users,
            RoleRepository roles) {
        this.properties = properties;
        this.administration = administration;
        this.users = users;
        this.roles = roles;
    }

    @Transactional
    public void ensureAdministratorExists() {
        if (!properties.enabled()) {
            return;
        }
        if (!properties.hasAPassword()) {
            log.warn("No bootstrap password is configured for access administrator '{}'; "
                    + "set lowcode.access-control.bootstrap.password to create one.", properties.username());
            return;
        }
        if (users.findByUsername(properties.username()).isPresent()) {
            return;
        }

        ensureRoleCarryingEveryConfiguredPermission();
        administration.registerUser(properties.username(), properties.password());
        administration.assignRole(properties.username(), properties.roleCode());
        log.info("Bootstrapped access administrator '{}' holding role '{}'.",
                properties.username(), properties.roleCode());
    }

    private void ensureRoleCarryingEveryConfiguredPermission() {
        if (roles.findByCode(properties.roleCode()).isEmpty()) {
            administration.createRole(properties.roleCode(), properties.roleDescription());
        }
        for (String rawCode : properties.permissionCodes()) {
            PermissionCode code = PermissionCode.of(rawCode);
            administration.definePermissionIfAbsent(code, "Administer access control: " + rawCode);
            administration.grantPermission(properties.roleCode(), code);
        }
    }
}
