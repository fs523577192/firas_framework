package tech.firas.lowcode.accesscontrol.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import tech.firas.lowcode.accesscontrol.domain.DuplicateRoleCodeException;
import tech.firas.lowcode.accesscontrol.domain.RoleStillAssignedException;
import tech.firas.lowcode.accesscontrol.domain.model.Role;
import tech.firas.lowcode.accesscontrol.domain.repository.RoleRepository;

@Repository
public class RoleRepositoryAdapter implements RoleRepository {

    private final SpringDataRoleRepository delegate;

    public RoleRepositoryAdapter(SpringDataRoleRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public Role save(Role role) {
        if (delegate.existsByCodeAndIdNot(role.code(), role.id())) {
            throw new DuplicateRoleCodeException(role.code());
        }
        return delegate.saveAndFlush(role);
    }

    @Override
    public Optional<Role> findByCode(String code) {
        return delegate.findByCode(code);
    }

    @Override
    public void delete(Role role) {
        if (delegate.isHeldByAnyUser(role.id())) {
            throw new RoleStillAssignedException(role.code());
        }
        delegate.delete(role);
        delegate.flush();
    }

    @Override
    public List<Role> findAll() {
        return delegate.findAll();
    }
}
