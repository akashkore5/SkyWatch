package com.dice.skywatch.utility;

import com.dice.skywatch.model.User;
import com.dice.skywatch.service.CustomUserDetailsService;
import com.dice.skywatch.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService userService; // Assuming you have a UserService implementation


    public JwtTokenFilter(String jwtSecret, long expirationMs, CustomUserDetailsService userService) {
        this.jwtTokenUtil = new JwtTokenUtil(jwtSecret,expirationMs);
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwtToken = extractJwtToken(request);

        if (jwtToken != null && jwtTokenUtil.validateToken(jwtToken)) {
            String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            if (username != null) {
                User user = (User) userService.loadUserByUsername(username); // Assuming UserService has a method to find user by email
                if (user != null && user.isVerified()) { // Check if user exists and is verified
                    List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                            .map(role -> new SimpleGrantedAuthority(role.getName()))
                            .collect(Collectors.toList());
                    Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractJwtToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }
}