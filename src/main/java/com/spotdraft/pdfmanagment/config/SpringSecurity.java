package com.spotdraft.pdfmanagment.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SpringSecurity {

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Configures the password encoder to use BCrypt algorithm.
     *
     * @return The BCrypt password encoder.
     */
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the security filter chain for HTTP requests.
     *
     * @param http The HttpSecurity object.
     * @return The configured SecurityFilterChain.
     * @throws Exception If an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests((authorize) ->
                        authorize
                                // Public URLs accessible to all
                                .requestMatchers(getPublicRequestMatchers()).permitAll()
                                // URL accessible only by users with ADMIN role
                                .requestMatchers(new AntPathRequestMatcher("/")).hasRole("ADMIN")
                                // All other URLs require authentication
                                .anyRequest().authenticated()
                )
                .formLogin(
                        form -> form
                                .loginPage("/login")
                                .loginProcessingUrl("/login")
                                .defaultSuccessUrl("/dashboard", true)
                                .successHandler((request, response, authentication) -> {
                                    // Redirect to the requested URL after successful login
                                    String targetUrl = request.getParameter("targetUrl");
                                    if (targetUrl != null && !targetUrl.isEmpty()) {
                                        response.sendRedirect(targetUrl);
                                    } else {
                                        response.sendRedirect("/dashboard");
                                    }
                                })
                                .permitAll()
                )
                .logout(
                        logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .permitAll()
                );
        return http.build();
    }

    /**
     * Retrieves an array of public request matchers for URLs that don't require authentication.
     *
     * @return An array of RequestMatchers for public URLs.
     */
    private RequestMatcher[] getPublicRequestMatchers() {
        List<RequestMatcher> requestMatchers = Arrays.asList(
                new AntPathRequestMatcher("/register/**"),
                new AntPathRequestMatcher("/index"),
                new AntPathRequestMatcher("/users"),
                new AntPathRequestMatcher("/dashboard"),
                new AntPathRequestMatcher("/dashboard/**"),
                new AntPathRequestMatcher("/shared/**"),
                new AntPathRequestMatcher("/forgot_password"),
                new AntPathRequestMatcher("/reset_password"),
                new AntPathRequestMatcher("/comments/add"),
                new AntPathRequestMatcher("/login"),
                new AntPathRequestMatcher("/upload"),
                new AntPathRequestMatcher("/download/**")
        );
        return requestMatchers.toArray(new RequestMatcher[0]);
    }

    /**
     * Configures the global authentication manager.
     *
     * @param auth The AuthenticationManagerBuilder object.
     * @throws Exception If an error occurs during configuration.
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }
}
