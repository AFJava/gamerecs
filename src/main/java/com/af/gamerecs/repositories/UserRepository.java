package com.af.gamerecs.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.af.gamerecs.entities.User;



public interface UserRepository
        extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

}
