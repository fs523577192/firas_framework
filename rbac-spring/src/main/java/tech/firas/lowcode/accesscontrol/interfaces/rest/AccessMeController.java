package tech.firas.lowcode.accesscontrol.interfaces.rest;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tech.firas.lowcode.accesscontrol.application.AccessQueryService;
import tech.firas.lowcode.accesscontrol.application.AuthorizationService;
import tech.firas.lowcode.accesscontrol.application.readmodel.UserView;
import tech.firas.lowcode.accesscontrol.domain.model.AuthorizationDecision;
import tech.firas.lowcode.accesscontrol.domain.model.PermissionCode;

@RestController
@RequestMapping("/api/access")
public class AccessMeController {

    private final AccessQueryService queries;
    private final AuthorizationService authorization;

    public AccessMeController(AccessQueryService queries, AuthorizationService authorization) {
        this.queries = queries;
        this.authorization = authorization;
    }

    @GetMapping("/me")
    UserView me(Authentication authentication) {
        return queries.requireUser(authentication.getName());
    }

    @GetMapping("/decisions/{permissionCode}")
    AuthorizationDecision decide(Authentication authentication, @PathVariable String permissionCode) {
        return authorization.authorize(authentication.getName(), PermissionCode.of(permissionCode));
    }
}
