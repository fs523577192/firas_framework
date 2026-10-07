package tech.firas.lowcode.accesscontrol.domain.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tech.firas.lowcode.accesscontrol.domain.model.Role;

@Repository
public interface RoleRespositoryTest extends RoleRepository, JpaRepository<Role, UUID> {

}
