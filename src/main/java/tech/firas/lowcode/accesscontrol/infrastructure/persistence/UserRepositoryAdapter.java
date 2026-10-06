package tech.firas.lowcode.accesscontrol.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import tech.firas.lowcode.accesscontrol.domain.DuplicateUsernameException;
import tech.firas.lowcode.accesscontrol.domain.model.User;
import tech.firas.lowcode.accesscontrol.domain.repository.UserRepository;

@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository delegate;

    public UserRepositoryAdapter(SpringDataUserRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public User save(User user) {
        if (delegate.existsByUsernameAndIdNot(user.username(), user.id())) {
            throw new DuplicateUsernameException(user.username());
        }
        return delegate.saveAndFlush(user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return delegate.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return delegate.findByUsername(username);
    }

    @Override
    public List<User> findAll() {
        return delegate.findAll();
    }

    @Override
    public void delete(User user) {
        delegate.delete(user);
        delegate.flush();
    }
}
