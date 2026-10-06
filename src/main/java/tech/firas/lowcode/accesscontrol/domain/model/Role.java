package tech.firas.lowcode.accesscontrol.domain.model;

import static tech.firas.lowcode.accesscontrol.domain.model.Invariants.requireText;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "ac_role")
public class Role {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "code", nullable = false, unique = true, length = 64)
    private String code;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "ac_permission_grant",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions = new LinkedHashSet<>();

    protected Role() {
    }

    private Role(UUID id, String code, String description) {
        this.id = id;
        this.code = code;
        this.description = description;
    }

    public static Role create(String code, String description) {
        requireText(code, "A role must be created with a non-blank code");
        requireText(description, "Role " + code + " must be created with a non-blank description");
        return new Role(UUID.randomUUID(), code, description);
    }

    public void reword(String description) {
        requireText(description, "Role " + code + " must be given a non-blank description");
        this.description = description;
    }

    public void grant(Permission permission) {
        permissions.add(permission);
    }

    public boolean revoke(PermissionCode code) {
        return permissions.removeIf(permission -> permission.code().equals(code));
    }

    public UUID id() {
        return id;
    }

    public String code() {
        return code;
    }

    public String description() {
        return description;
    }

    public Set<PermissionCode> permissionCodes() {
        Set<PermissionCode> codes = new LinkedHashSet<>();
        for (Permission permission : permissions) {
            codes.add(permission.code());
        }
        return Collections.unmodifiableSet(codes);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Role that && id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }
}
