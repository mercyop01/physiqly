package com.physiqly.userservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // <-- Make sure to import this

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA will automatically create the implementation for this method
    // based on the method name. It will search for a user by their email address.
    Optional<User> findByEmail(String email);
}

