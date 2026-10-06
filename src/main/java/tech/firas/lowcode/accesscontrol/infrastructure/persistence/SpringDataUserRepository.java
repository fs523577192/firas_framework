package tech.firas.lowcode.accesscontrol.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import tech.firas.lowcode.accesscontrol.domain.model.User;

interface SpringDataUserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    boolean existsByUsernameAndIdNot(String username, UUID id);
}
