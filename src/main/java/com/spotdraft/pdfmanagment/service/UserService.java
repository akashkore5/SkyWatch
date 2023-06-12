package com.spotdraft.pdfmanagment.service;

import com.spotdraft.pdfmanagment.dto.UserDto;
import com.spotdraft.pdfmanagment.exception.UserNotFoundException;
import com.spotdraft.pdfmanagment.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void saveUser(UserDto userDto,String token);

    User findUserByEmail(String email);

    List<UserDto> findAllUsers();

    void updateResetPasswordToken(String token, String email);

    User getByResetPasswordToken(String token);

    void updatePassword(User customer, String password);
    void verifyEmail(String email, String verificationCode) throws UserNotFoundException;


    User verifyUser(String verificationToken);
    boolean isVerified(User user);

    void updateVerification(User customer);
}