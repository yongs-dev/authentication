package com.mark.authentication.app.user.repository;

import com.mark.authentication.app.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmailAndPassword(final String email, final String password);

    Optional<User> findByEmail(final String email);

    boolean existsByEmail(final String email);
}
