package tech.firas.lowcode.accesscontrol.domain.model;

import static tech.firas.lowcode.accesscontrol.domain.model.Invariants.requireText;

import java.util.regex.Pattern;

public record PermissionCode(String value) {

    private static final Pattern SEGMENTED_CODE = Pattern.compile("[a-z][a-z0-9-]*(:[a-z][a-z0-9-]*)+");

    public PermissionCode {
        requireText(value, "A permission code must not be blank");
        if (!SEGMENTED_CODE.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    "A permission code must be two or more lowercase ':'-separated segments "
                            + "such as 'document:read', but was: " + value);
        }
    }

    public static PermissionCode of(String value) {
        return new PermissionCode(value);
    }
}
