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
@Table(name = "ac_user")
public class User {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "username", nullable = false, unique = true, length = 64)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "ac_role_assignment",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new LinkedHashSet<>();

    protected User() {
    }

    private User(UUID id, String username, String passwordHash, boolean enabled) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.enabled = enabled;
    }

    public static User register(String username, String passwordHash) {
        requireText(username, "A user must be registered with a non-blank username");
        requireText(passwordHash, "User " + username + " must be registered with a non-blank password hash");
        return new User(UUID.randomUUID(), username, passwordHash, true);
    }

    public void changePassword(String passwordHash) {
        requireText(passwordHash, "User " + username + " must be given a non-blank password hash");
        this.passwordHash = passwordHash;
    }

    public void assignRole(Role role) {
        roles.add(role);
    }

    public boolean revokeRole(String code) {
        return roles.removeIf(role -> role.code().equals(code));
    }

    public void disable() {
        enabled = false;
    }

    public void enable() {
        enabled = true;
    }

    public UUID id() {
        return id;
    }

    public String username() {
        return username;
    }

    public String passwordHash() {
        return passwordHash;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Set<String> roleCodes() {
        Set<String> codes = new LinkedHashSet<>();
        for (Role role : roles) {
            codes.add(role.code());
        }
        return Collections.unmodifiableSet(codes);
    }

    public Set<PermissionCode> effectivePermissions() {
        Set<PermissionCode> codes = new LinkedHashSet<>();
        for (Role role : roles) {
            codes.addAll(role.permissionCodes());
        }
        return Collections.unmodifiableSet(codes);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof User that && id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }
}
