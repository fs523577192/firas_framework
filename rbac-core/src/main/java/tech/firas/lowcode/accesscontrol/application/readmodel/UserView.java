package tech.firas.lowcode.accesscontrol.application.readmodel;

import java.util.List;

public record UserView(String username, boolean enabled, List<String> roleCodes, List<String> permissions) {
}
