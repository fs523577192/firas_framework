package tech.firas.lowcode.accesscontrol.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tech.firas.lowcode.accesscontrol.domain.model.Role;

interface SpringDataRoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByCode(String code);

    boolean existsByCodeAndIdNot(String code, UUID id);

    @Query("select case when count(u) > 0 then true else false end from User u join u.roles r where r.id = :roleId")
    boolean isHeldByAnyUser(@Param("roleId") UUID roleId);
}
