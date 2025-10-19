package com.yindi.card.user;

import org.springframework.data.jpa.repository.JpaRepository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Query("select u from User u " +
            "where u.expireDate is not null and u.expireDate between :from and :to")
    List<User> findExpiringBetween(@Param("from") LocalDate from,
                                   @Param("to") LocalDate to);
}
