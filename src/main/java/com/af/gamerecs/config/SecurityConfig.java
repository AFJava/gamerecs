package com.af.gamerecs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import com.af.gamerecs.entities.User;
import com.af.gamerecs.repositories.UserRepository;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**", "/profile").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/", "/login", "/css/**", "/js/**", "/auth/**", "/assets/**, /error").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler((request, response, authentication) -> {
                    Object principal = authentication.getPrincipal();
                    
                    User user;

                    if(principal instanceof User localUser) {
                        user = localUser;
                    }
                    else if(principal instanceof OAuth2User oauthUser) {
                        String email = oauthUser.getAttribute("email");

                        user = userRepository.findByEmail(email).orElseGet( () -> userRepository.save( new User(email) ) );
                    } else {
                        throw new IllegalStateException("Unknown principal type");
                    }

                    Long id = user.getId();
                    response.sendRedirect("/users/" + id + "/profile");
                })
                .usernameParameter("emailLogin")
                .passwordParameter("passwordLogin")
                .loginProcessingUrl("/login")
                .failureUrl("/login")
                .permitAll()
            )
            .oauth2Login(oauth -> oauth
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
            )
            .logout(logout -> logout.logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .permitAll()
            );

        return http.build();
    }
}