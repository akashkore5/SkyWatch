package com.dice.skywatch.config;

import com.dice.skywatch.service.CustomUserDetailsService;
import com.dice.skywatch.utility.JwtTokenFilter;
import com.dice.skywatch.utility.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SpringSecurity {

    @Autowired
    private UserDetailsService userDetailsService;

    @Value("${rapidApi.host}")
    private String host;

    @Value("${rapidApi.key}")
    private String key;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @Bean
    public RestTemplate restTemplate(){
        return  new RestTemplate();
    }

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
                                .and()
                                .addFilterBefore(new JwtTokenFilter(jwtSecret,jwtExpirationMs, (CustomUserDetailsService) userDetailsService), UsernamePasswordAuthenticationFilter.class)
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
                new AntPathRequestMatcher("/verify"),
                new AntPathRequestMatcher("/forgot_password"),
                new AntPathRequestMatcher("/reset_password"),
                new AntPathRequestMatcher("/generateToken"),
                new AntPathRequestMatcher("/login")
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
    @Bean
    public HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-rapidApi-host", host);
        headers.set("x-rapidApi-key", key);
        return headers;
    }


    @Bean
    public JwtTokenFilter jwtTokenFilter() {
        return new JwtTokenFilter(jwtSecret, jwtExpirationMs, (CustomUserDetailsService) userDetailsService);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
