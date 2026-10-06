package tech.firas.lowcode.accesscontrol.infrastructure.bootstrap;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import tech.firas.lowcode.accesscontrol.domain.model.AccessPermissions;

@ConfigurationProperties(prefix = "lowcode.access-control.bootstrap")
public record AccessBootstrapProperties(
        @DefaultValue("true") boolean enabled,
        @DefaultValue("admin") String username,
        @DefaultValue("") String password,
        @DefaultValue("super-admin") String roleCode,
        @DefaultValue("Full control over access administration") String roleDescription,
        @DefaultValue(AccessPermissions.ADMINISTER) List<String> permissionCodes) {

    public boolean hasAPassword() {
        return password != null && !password.isBlank();
    }
}
