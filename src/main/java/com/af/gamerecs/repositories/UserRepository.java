package com.af.gamerecs.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.af.gamerecs.entities.User;

public interface UserRepository
        extends JpaRepository<User, Long> {
}