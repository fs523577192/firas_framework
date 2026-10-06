package tech.firas.lowcode.accesscontrol.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import tech.firas.lowcode.accesscontrol.domain.model.User;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByUsername(String username);

    List<User> findAll();

    void delete(User user);
}
