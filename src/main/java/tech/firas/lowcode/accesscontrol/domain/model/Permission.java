package tech.firas.lowcode.accesscontrol.domain.model;

import static tech.firas.lowcode.accesscontrol.domain.model.Invariants.requirePresent;
import static tech.firas.lowcode.accesscontrol.domain.model.Invariants.requireText;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ac_permission")
public class Permission {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "code", nullable = false, unique = true, length = 128)
    private PermissionCode code;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    protected Permission() {
    }

    private Permission(UUID id, PermissionCode code, String description) {
        this.id = id;
        this.code = code;
        this.description = description;
    }

    public static Permission define(PermissionCode code, String description) {
        requirePresent(code, "A permission must be defined with a code");
        requireText(description, "Permission " + code.value() + " must be defined with a non-blank description");
        return new Permission(UUID.randomUUID(), code, description);
    }

    public void reword(String description) {
        requireText(description, "Permission " + code.value() + " must be given a non-blank description");
        this.description = description;
    }

    public UUID id() {
        return id;
    }

    public PermissionCode code() {
        return code;
    }

    public String description() {
        return description;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Permission that && id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }
}
