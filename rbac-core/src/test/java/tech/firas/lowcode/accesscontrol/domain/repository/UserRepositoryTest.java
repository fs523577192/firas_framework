package tech.firas.lowcode.accesscontrol.domain.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tech.firas.lowcode.accesscontrol.domain.model.User;

@Repository
public interface UserRepositoryTest extends UserRepository, JpaRepository<User, UUID> {
    
}
