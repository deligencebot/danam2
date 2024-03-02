package com.delbot.danam.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsUtils;

import com.delbot.danam.global.security.jwt.exception.CustomAuthenticationEntryPoint;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
  //
  private final CorsConfig corsConfig;
  private final AuthenticationManagerConfig authenticationManagerConfig;
  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .sessionManagement(sessionManagement ->
            sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .csrf(csrf -> csrf.disable())
        .cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()))
        .httpBasic(httpBasic -> httpBasic.disable())
        .formLogin(formLogin -> formLogin.disable())
        .authorizeHttpRequests(authroize -> authroize
            .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
            .requestMatchers("/admin/**").hasAnyRole("ADMIN")
            .anyRequest().permitAll()
        )
        .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(customAuthenticationEntryPoint))
        .with(authenticationManagerConfig, Customizer.withDefaults())
        .build();
  }
}
