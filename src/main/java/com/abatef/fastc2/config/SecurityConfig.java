package com.abatef.fastc2.config;

import com.abatef.fastc2.security.FirebaseAuthProvider;
import com.abatef.fastc2.security.UsernamePasswordAuthProvider;
import com.abatef.fastc2.security.filters.FirebaseAuthenticationFilter;
import com.abatef.fastc2.security.filters.JwtAuthenticationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final FirebaseAuthProvider firebaseAuthProvider;
    private final UsernamePasswordAuthProvider usernamePasswordAuthProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            FirebaseAuthProvider firebaseAuthProvider,
            UsernamePasswordAuthProvider usernamePasswordAuthProvider,
            JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.firebaseAuthProvider = firebaseAuthProvider;
        this.usernamePasswordAuthProvider = usernamePasswordAuthProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(
                Arrays.asList(firebaseAuthProvider, usernamePasswordAuthProvider));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        FirebaseAuthenticationFilter fbFilter =
                new FirebaseAuthenticationFilter(authenticationManager());

        http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(
                        session -> {
                            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                        })
                .addFilterBefore(
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                //                .addFilterAfter(fbFilter, JwtAuthenticationFilter.class)
                .authenticationManager(authenticationManager())
                .authorizeHttpRequests(
                        request -> {
                            request.requestMatchers("/api/v1/auth/me", "/api/v1/auth/password")
                                    .authenticated();
                            request.requestMatchers(
                                            "/api/v1/pharmacies/search", "/api/v1/pharmacies/all")
                                    .permitAll()
                                    .requestMatchers("/api/v1/pharmacies/**")
                                    .authenticated();
                            request.requestMatchers("/api/v1/orders/**").authenticated();
                            request.requestMatchers(
                                            "/api/v1/drugs/search",
                                            "/api/v1/drugs/fill",
                                            "/api/v1/drugs/all")
                                    .permitAll()
                                    .requestMatchers("/api/v1/drugs/**")
                                    .authenticated();
                            request.requestMatchers(
                                            "/api/v1/auth/login",
                                            "/api/v1/auth/refresh",
                                            "/api/v1/auth/signup")
                                    .permitAll();
                            request.requestMatchers("/api/v1/employees/**").authenticated();
                            request.requestMatchers("/swagger-ui/**")
                                    .permitAll()
                                    .requestMatchers("/v3/api-docs*/**")
                                    .permitAll();

                            request.requestMatchers("/api/v1/receipts/**").authenticated();
                            request.requestMatchers("/api/v1/users/**").authenticated();
                        })
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
