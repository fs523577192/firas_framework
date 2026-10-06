package tech.firas.lowcode.accesscontrol.infrastructure.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import tech.firas.lowcode.accesscontrol.domain.model.PermissionCode;
import tech.firas.lowcode.accesscontrol.domain.model.User;
import tech.firas.lowcode.accesscontrol.domain.repository.UserRepository;

@Service
public class RbacUserDetailsService implements UserDetailsService {

    private final UserRepository users;

    public RbacUserDetailsService(UserRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return users.findByUsername(username)
                .map(this::toUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("No user is registered as " + username));
    }

    private UserDetails toUserDetails(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        user.roleCodes().forEach(roleCode -> authorities.add(new SimpleGrantedAuthority("ROLE_" + roleCode)));
        user.effectivePermissions()
                .forEach(code -> authorities.add(new SimpleGrantedAuthority(code.value())));
        return org.springframework.security.core.userdetails.User.withUsername(user.username())
                .password(user.passwordHash())
                .disabled(!user.isEnabled())
                .authorities(authorities)
                .build();
    }
}
