package tech.firas.lowcode.accesscontrol.domain.repository;

import java.util.List;
import java.util.Optional;

import tech.firas.lowcode.accesscontrol.domain.PermissionStillGrantedException;
import tech.firas.lowcode.accesscontrol.domain.model.Permission;
import tech.firas.lowcode.accesscontrol.domain.model.PermissionCode;

public interface PermissionRepository {

    Permission save(Permission permission);

    Optional<Permission> findByCode(PermissionCode code);

    List<Permission> findAll();

    void delete(Permission permission) throws PermissionStillGrantedException;
}
