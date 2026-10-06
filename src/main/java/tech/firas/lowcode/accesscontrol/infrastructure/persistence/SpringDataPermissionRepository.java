package tech.firas.lowcode.accesscontrol.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tech.firas.lowcode.accesscontrol.domain.model.Permission;
import tech.firas.lowcode.accesscontrol.domain.model.PermissionCode;

interface SpringDataPermissionRepository extends JpaRepository<Permission, UUID> {

    Optional<Permission> findByCode(PermissionCode code);

    boolean existsByCodeAndIdNot(PermissionCode code, UUID id);

    @Query("select case when count(r) > 0 then true else false end from Role r join r.permissions p where p.id = :permissionId")
    boolean isCarriedByAnyRole(@Param("permissionId") UUID permissionId);
}
