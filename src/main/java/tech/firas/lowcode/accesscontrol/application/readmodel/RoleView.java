package tech.firas.lowcode.accesscontrol.application.readmodel;

import java.util.List;

public record RoleView(String code, String description, List<String> permissionCodes) {
}
