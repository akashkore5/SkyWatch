package com.dice.skywatch.repository;

import com.dice.skywatch.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
    User findByResetPasswordToken(String token);

    User findByVerificationToken(String verificationToken);
}