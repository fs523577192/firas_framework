package tech.firas.lowcode.accesscontrol.domain.repository;

import java.util.List;
import java.util.Optional;

import tech.firas.lowcode.accesscontrol.domain.RoleStillAssignedException;
import tech.firas.lowcode.accesscontrol.domain.model.Role;

public interface RoleRepository {

    Role save(Role role);

    Optional<Role> findByCode(String code);

    List<Role> findAll();

    void delete(Role role) throws RoleStillAssignedException;
}
