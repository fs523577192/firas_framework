package tech.firas.lowcode.accesscontrol.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import tech.firas.lowcode.accesscontrol.domain.DuplicatePermissionCodeException;
import tech.firas.lowcode.accesscontrol.domain.PermissionStillGrantedException;
import tech.firas.lowcode.accesscontrol.domain.model.Permission;
import tech.firas.lowcode.accesscontrol.domain.model.PermissionCode;
import tech.firas.lowcode.accesscontrol.domain.repository.PermissionRepository;

@Repository
public class PermissionRepositoryAdapter implements PermissionRepository {

    private final SpringDataPermissionRepository delegate;

    public PermissionRepositoryAdapter(SpringDataPermissionRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public Permission save(Permission permission) {
        if (delegate.existsByCodeAndIdNot(permission.code(), permission.id())) {
            throw new DuplicatePermissionCodeException(permission.code());
        }
        return delegate.saveAndFlush(permission);
    }

    @Override
    public Optional<Permission> findByCode(PermissionCode code) {
        return delegate.findByCode(code);
    }

    @Override
    public void delete(Permission permission) {
        if (delegate.isCarriedByAnyRole(permission.id())) {
            throw new PermissionStillGrantedException(permission.code());
        }
        delegate.delete(permission);
        delegate.flush();
    }

    @Override
    public List<Permission> findAll() {
        return delegate.findAll();
    }
}
