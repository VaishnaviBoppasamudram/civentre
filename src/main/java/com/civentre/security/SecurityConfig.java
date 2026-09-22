package com.civentre.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    public SecurityConfig(
            CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {

        this.customAuthenticationSuccessHandler =
                customAuthenticationSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http

                /*
                 * CSRF PROTECTION
                 *
                 * The CSRF token is stored in the XSRF-TOKEN cookie.
                 * JavaScript reads this cookie and sends it using
                 * the X-XSRF-TOKEN header.
                 */
                .csrf(csrf -> csrf
                        .csrfTokenRepository(
                                CookieCsrfTokenRepository.withHttpOnlyFalse()
                        )
                        .csrfTokenRequestHandler(
                                new CsrfTokenRequestAttributeHandler()
                        )
                )
                .authorizeHttpRequests(auth -> auth

                        /*
                         * PUBLIC ROUTES
                         */
                        .requestMatchers(
                                "/api/users/register",
                                "/register",
                                "/login",
                                "/login.css",
                                "/register.css"
                        ).permitAll()

                        /*
                         * ADMIN ROUTES
                         */
                        .requestMatchers(
                                "/admin/**",
                                "/api/admin/**"
                        ).hasRole("ADMIN")

                        /*
                         * OFFICER ROUTES
                         */
                        .requestMatchers(
                                "/officer/**",
                                "/api/officer/**"
                        ).hasRole("OFFICER")


                        .requestMatchers(
                                "/",
                                "/dashboard",
                                "/report-issue",
                                "/my-issues",
                                "/notifications",
                                "/issue-details/**"
                        ).hasRole("CITIZEN")

                        /*
                         * EVERYTHING ELSE
                         * Requires login.
                         */
                        .anyRequest().authenticated()
                )

                /*
                 * LOGIN
                 */
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(customAuthenticationSuccessHandler)
                        .permitAll()
                );

        return http.build();
    }

    /*
     * PASSWORD ENCODER
     */
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    /*
     * AUTHENTICATION PROVIDER
     */
    @Bean
    public AuthenticationProvider authenticationProvider(
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }
}