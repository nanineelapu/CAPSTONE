package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByMobileNumber(String mobileNumber);
    boolean existsByEmail(String email);
    boolean existsByMobileNumber(String mobileNumber);
}