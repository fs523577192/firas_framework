package tech.firas.lowcode.accesscontrol.domain.model;

public record AuthorizationDecision(boolean allowed, String reason) {

    public static AuthorizationDecision allow(String reason) {
        return new AuthorizationDecision(true, reason);
    }

    public static AuthorizationDecision deny(String reason) {
        return new AuthorizationDecision(false, reason);
    }
}
