package tech.firas.lowcode.accesscontrol.interfaces.graphql;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import tech.firas.lowcode.accesscontrol.application.AccessQueryService;
import tech.firas.lowcode.accesscontrol.application.AuthorizationService;
import tech.firas.lowcode.accesscontrol.application.readmodel.UserView;
import tech.firas.lowcode.accesscontrol.domain.model.AuthorizationDecision;
import tech.firas.lowcode.accesscontrol.domain.model.PermissionCode;

@Controller
public class AccessIdentityGraphQlController {

    private final AccessQueryService queries;
    private final AuthorizationService authorization;

    public AccessIdentityGraphQlController(AccessQueryService queries, AuthorizationService authorization) {
        this.queries = queries;
        this.authorization = authorization;
    }

    @QueryMapping
    UserView me(Authentication authentication) {
        return queries.requireUser(authentication.getName());
    }

    @QueryMapping
    AuthorizationDecision amIPermitted(Authentication authentication, @Argument String permission) {
        return authorization.authorize(authentication.getName(), PermissionCode.of(permission));
    }
}
