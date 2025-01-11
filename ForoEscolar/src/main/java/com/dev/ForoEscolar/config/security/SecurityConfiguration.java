package com.dev.ForoEscolar.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final SecurityFilter securityFilter;
    private final UserDetailsService userDetailsService;

    @Autowired
    public SecurityConfiguration(SecurityFilter securityFilter, UserDetailsService userDetailsService) {
        this.securityFilter = securityFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas
                        .requestMatchers(HttpMethod.POST, "/api/login").permitAll()
                        .requestMatchers("/swagger-ui.html", "/v3/api-docs/**", "/swagger-ui/**").permitAll()

                        // Rutas para ADMINISTRADOR
                        .requestMatchers(HttpMethod.GET, "/api/user/getAll").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST, "/api/profesor/add",
                                "/api/estudiante/add",
                                "/api/tutorlegal/add", "/api/user/add").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMINISTRADOR")

                        // Rutas para PROFESOR
                        .requestMatchers(HttpMethod.POST, "/api/asistencia/add").hasAnyRole("ADMINISTRADOR", "PROFESOR")
                        .requestMatchers(HttpMethod.PUT, "/api/asistencia/update/**").hasAnyRole("ADMINISTRADOR", "PROFESOR")

                        // Rutas para consulta de estudiantes (requieren validación adicional en el controlador)
                        .requestMatchers(HttpMethod.GET,
                                "/api/estudiante/{id}",
                                "/api/estudiante/{id}/asistencias",
                                "/api/estudiante/filterGrado").hasAnyRole("ADMINISTRADOR", "PROFESOR", "TUTOR")

                        // Rutas para actualización de información
                        .requestMatchers(HttpMethod.PUT,
                                "/api/estudiante/update",
                                "/api/profesor/update",
                                "/api/tutorlegal/update").hasRole("ADMINISTRADOR")

                        // Consultas generales (requieren validación adicional en el controlador)
                        .requestMatchers(HttpMethod.GET,
                                "/api/asistencia/**",
                                "/api/profesor/**",
                                "/api/tutorlegal/**").hasAnyRole("ADMINISTRADOR", "PROFESOR")

                        // Cualquier otra ruta requiere autenticación
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .userDetailsService(userDetailsService)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}