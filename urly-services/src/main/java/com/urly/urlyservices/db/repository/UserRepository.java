package com.urly.urlyservices.db.repository;

import com.urly.urlyservices.db.entity.User;
import com.urly.urlyservices.enums.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Optional<User> findById(Long id);

    Optional<User> findByEmailAndProvider(String email, AuthProvider provider);
}
