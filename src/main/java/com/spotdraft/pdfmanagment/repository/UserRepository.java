package com.spotdraft.pdfmanagment.repository;

import com.spotdraft.pdfmanagment.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
    public User findByResetPasswordToken(String token);

    User findByVerificationToken(String verificationToken);
}