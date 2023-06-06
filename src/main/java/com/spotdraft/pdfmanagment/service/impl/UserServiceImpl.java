package com.spotdraft.pdfmanagment.service.impl;

import com.spotdraft.pdfmanagment.dto.UserDto;
import com.spotdraft.pdfmanagment.exception.UserNotFoundException;
import com.spotdraft.pdfmanagment.model.Role;
import com.spotdraft.pdfmanagment.model.User;
import com.spotdraft.pdfmanagment.repository.RoleRepository;
import com.spotdraft.pdfmanagment.repository.UserRepository;
import com.spotdraft.pdfmanagment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private EntityManager entityManager;

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Saves a new user based on the provided UserDto.
     *
     * @param userDto The UserDto containing user information.
     */
    @Override
    public void saveUser(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getFirstName() + " " + userDto.getLastName());
        user.setEmail(userDto.getEmail());
        // Encrypt the password using Spring Security's password encoder
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        // Check if the roles table exists in the database
        String tableName = "roles";
        Query query = entityManager.createNativeQuery("SELECT 1 FROM " + tableName + " LIMIT 1");
        boolean tableExists;
        try {
            query.getSingleResult();
            tableExists = true;
        } catch (Exception ex) {
            tableExists = false;
        }

        // Assign a role to the user
        Role role = new Role();
        if (tableExists) {
            role = roleRepository.findByName("ROLE_ADMIN");
        }
        role.setName("ROLE_ADMIN");
        if (role == null) {
            role = checkRoleExist();
        }
        user.setRoles(Arrays.asList(role));
        userRepository.save(user);
    }

    /**
     * Finds a user by their email address.
     *
     * @param email The email address of the user.
     * @return The User object if found, otherwise null.
     */
    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Retrieves a list of all users and maps them to UserDto objects.
     *
     * @return The list of UserDto objects.
     */
    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map((user) -> mapToUserDto(user))
                .collect(Collectors.toList());
    }

    /**
     * Maps a User object to a UserDto object.
     *
     * @param user The User object to be mapped.
     * @return The mapped UserDto object.
     */
    private UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        String[] str = user.getName().split(" ");
        userDto.setFirstName(str[0]);
        userDto.setLastName(str[1]);
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    /**
     * Checks if the role "ROLE_ADMIN" exists in the role repository.
     * If not, creates and saves a new role with the name "ROLE_ADMIN".
     *
     * @return The Role object.
     */
    private Role checkRoleExist() {
        Role role = new Role();
        role.setName("ROLE_ADMIN");
        return roleRepository.save(role);
    }

    /**
     * Updates the reset password token for a user with the specified email.
     *
     * @param token The new reset password token.
     * @param email The email address of the user.
     * @throws UserNotFoundException if the user with the specified email is not found.
     */
    public void updateResetPasswordToken(String token, String email) throws UserNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setResetPasswordToken(token);
            userRepository.save(user);
        } else {
            throw new UserNotFoundException("Could not find any customer with the email " + email);
        }
    }

    /**
     * Retrieves a user by their reset password token.
     *
     * @param token The reset password token.
     * @return The User object if found, otherwise null.
     */
    public User getByResetPasswordToken(String token) {
        return userRepository.findByResetPasswordToken(token);
    }

    /**
     * Updates the password for a user.
     *
     * @param user         The user to update.
     * @param newPassword The new password.
     */
    public void updatePassword(User user, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        user.setResetPasswordToken(null);
        userRepository.save(user);
    }
}
