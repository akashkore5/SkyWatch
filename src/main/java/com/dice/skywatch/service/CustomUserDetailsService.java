package com.dice.skywatch.service;

import com.dice.skywatch.exception.UserNotVerifiedException;
import com.dice.skywatch.model.Role;
import com.dice.skywatch.model.User;
import com.dice.skywatch.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Custom implementation of UserDetailsService for loading user details during authentication.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads user details based on the provided email.
     *
     * @param email the email of the user to load
     * @return the UserDetails object representing the authenticated user
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException,UserNotVerifiedException {
        User user = userRepository.findByEmail(email);

        if (user != null) {
            if (user.isVerified()) {
                return new org.springframework.security.core.userdetails.User(
                        user.getEmail(),
                        user.getPassword(),
                        mapRolesToAuthorities(user.getRoles())
                );
            } else {
                throw new UserNotVerifiedException("User is not verified.");
            }
        } else {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
    }

    /**
     * Maps user roles to Spring Security GrantedAuthority objects.
     *
     * @param roles the collection of roles associated with the user
     * @return the collection of GrantedAuthority objects representing the roles
     */
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}
