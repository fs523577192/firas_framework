package tech.firas.lowcode.accesscontrol.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import tech.firas.lowcode.PostgreSqlIntegrationTest;
import tech.firas.lowcode.accesscontrol.domain.model.PermissionCode;

class RbacUserDetailsServiceTest extends PostgreSqlIntegrationTest {

    @Autowired
    private UserDetailsService userDetailsService;

    @Test
    void loadsAUsersRolesAndEffectivePermissionsAsAuthorities() {
        administration.registerUser("alice", "s3cret");
        administration.createRole("editor", "Can author and revise documents");
        administration.definePermission(PermissionCode.of("document:read"), "Read any document");
        administration.assignRole("alice", "editor");
        administration.grantPermission("editor", PermissionCode.of("document:read"));

        UserDetails alice = userDetailsService.loadUserByUsername("alice");

        assertThat(alice.getUsername()).isEqualTo("alice");
        assertThat(alice.isEnabled()).isTrue();
        assertThat(alice.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .contains("document:read", "ROLE_editor");
    }

    @Test
    void reportsADisabledUserAsDisabledSoThatAuthenticationRejectsThem() {
        administration.registerUser("alice", "s3cret");
        administration.disableUser("alice");

        assertThat(userDetailsService.loadUserByUsername("alice").isEnabled()).isFalse();
    }

    @Test
    void reportsAnUnknownUsernameAsNotFound() {
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("ghost"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
