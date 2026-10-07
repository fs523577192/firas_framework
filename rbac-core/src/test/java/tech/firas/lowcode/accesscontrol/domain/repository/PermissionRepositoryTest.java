package tech.firas.lowcode.accesscontrol.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tech.firas.lowcode.accesscontrol.domain.model.Permission;
import tech.firas.lowcode.accesscontrol.domain.model.PermissionCode;

@Repository
public interface PermissionRepositoryTest extends PermissionRepository, JpaRepository<Permission, PermissionCode> {
    
}
