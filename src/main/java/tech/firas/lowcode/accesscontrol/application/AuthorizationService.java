package tech.firas.lowcode.accesscontrol.application;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tech.firas.lowcode.accesscontrol.domain.model.AuthorizationDecision;
import tech.firas.lowcode.accesscontrol.domain.model.PermissionCode;
import tech.firas.lowcode.accesscontrol.domain.model.User;
import tech.firas.lowcode.accesscontrol.domain.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class AuthorizationService {

    private final UserRepository users;

    public AuthorizationService(UserRepository users) {
        this.users = users;
    }

    public AuthorizationDecision authorize(UUID userId, PermissionCode required) {
        return judge(users.findById(userId), "id " + userId, required);
    }

    public AuthorizationDecision authorize(String username, PermissionCode required) {
        return judge(users.findByUsername(username), username, required);
    }

    private AuthorizationDecision judge(Optional<User> found, String reference, PermissionCode required) {
        return found.map(user -> judge(user, required))
                .orElseGet(() -> AuthorizationDecision.deny("No user is registered with " + reference));
    }

    private AuthorizationDecision judge(User user, PermissionCode required) {
        if (!user.isEnabled()) {
            return AuthorizationDecision.deny("User " + user.username() + " is disabled");
        }
        if (!user.effectivePermissions().contains(required)) {
            return AuthorizationDecision.deny(
                    "User " + user.username() + " holds no role carrying " + required.value());
        }
        return AuthorizationDecision.allow("User " + user.username() + " holds " + required.value());
    }
}
