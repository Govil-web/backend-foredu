package com.foroescolar.config.security;

import com.foroescolar.config.security.filters.BlacklistTokenFilter;
import com.foroescolar.config.security.filters.JwtAuthenticationFilter;
import com.foroescolar.config.security.filters.RequestLoggingFilter;
import com.foroescolar.config.security.handlers.SecurityExceptionHandler;
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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final RequestLoggingFilter requestLoggingFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final BlacklistTokenFilter blacklistTokenFilter;
    private final UserDetailsService userDetailsService;
    private final SecurityExceptionHandler securityExceptionHandler;

    private static final String ADMINISTRADOR = "ADMINISTRADOR";


    public SecurityConfiguration(
            RequestLoggingFilter requestLoggingFilter,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            BlacklistTokenFilter blacklistTokenFilter,
            UserDetailsService userDetailsService,
            SecurityExceptionHandler securityExceptionHandler) {
        this.requestLoggingFilter = requestLoggingFilter;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.blacklistTokenFilter = blacklistTokenFilter;
        this.userDetailsService = userDetailsService;
        this.securityExceptionHandler = securityExceptionHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(securityExceptionHandler)
                        .accessDeniedHandler(securityExceptionHandler)
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(getPublicEndpoints()).permitAll()
                        .requestMatchers(getSelfAccessEndpoints()).authenticated()
                        .requestMatchers(getAdministratorEndpoints()).hasRole(ADMINISTRADOR)
                        .requestMatchers(getTeacherEndpoints()).hasAnyRole(ADMINISTRADOR, "PROFESOR")
                        .requestMatchers(getTutorEndpoints()).hasAnyRole(ADMINISTRADOR, "PROFESOR", "TUTOR")
                        .anyRequest().authenticated()
                )

                .addFilterBefore(requestLoggingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtAuthenticationFilter, RequestLoggingFilter.class)
                .addFilterAfter(blacklistTokenFilter, JwtAuthenticationFilter.class)
                .userDetailsService(userDetailsService)
                .build();
    }

    private RequestMatcher[] getPublicEndpoints() {
        return new RequestMatcher[] {
                new AntPathRequestMatcher("/api/auth/login", HttpMethod.POST.name()),
                new AntPathRequestMatcher("/swagger-ui.html"),
                new AntPathRequestMatcher("/v3/api-docs/**"),
                new AntPathRequestMatcher("/swagger-ui/**"),
                new AntPathRequestMatcher("/api/test/redis", HttpMethod.GET.name())
        };
    }
    private RequestMatcher[] getSelfAccessEndpoints() {
        return new RequestMatcher[] {
                new AntPathRequestMatcher("/api/user/{id}", HttpMethod.GET.name()),
                new AntPathRequestMatcher("/api/profesor/{id}", HttpMethod.GET.name()),
                new AntPathRequestMatcher("/api/tutorlegal/{id}", HttpMethod.GET.name())
        };
    }

    private RequestMatcher[] getAdministratorEndpoints() {
        return new RequestMatcher[] {
                new AntPathRequestMatcher("/api/user/getAll", HttpMethod.GET.name()),
                new AntPathRequestMatcher("/api/estudiante/getAll", HttpMethod.GET.name()),
                new AntPathRequestMatcher("/api/profesor/add", HttpMethod.POST.name()),
                new AntPathRequestMatcher("/api/estudiante/add", HttpMethod.POST.name()),
                new AntPathRequestMatcher("/api/tutorlegal/add", HttpMethod.POST.name()),
                new AntPathRequestMatcher("/api/user/add", HttpMethod.POST.name()),
                new AntPathRequestMatcher("/api/**", HttpMethod.DELETE.name()),
                new AntPathRequestMatcher("/api/estudiante/update", HttpMethod.PUT.name()),
                new AntPathRequestMatcher("/api/profesor/update", HttpMethod.PUT.name()),
                new AntPathRequestMatcher("/api/tutorlegal/update", HttpMethod.PUT.name())
        };
    }

    private RequestMatcher[] getTeacherEndpoints() {
        return new RequestMatcher[] {
                new AntPathRequestMatcher("/api/asistencia/add", HttpMethod.POST.name()),
                new AntPathRequestMatcher("/api/asistencia/update/**", HttpMethod.PUT.name()),
                new AntPathRequestMatcher("/api/asistencia/**", HttpMethod.GET.name()),
                new AntPathRequestMatcher("/api/profesor/**", HttpMethod.GET.name()),
                new AntPathRequestMatcher("/api/tutorlegal/**", HttpMethod.GET.name())
        };
    }

    private RequestMatcher[] getTutorEndpoints() {
        return new RequestMatcher[] {
                new AntPathRequestMatcher("/api/estudiante/{id}", HttpMethod.GET.name()),
                new AntPathRequestMatcher("/api/estudiante/{id}/asistencias", HttpMethod.GET.name()),
                new AntPathRequestMatcher("/api/estudiante/filterGrado", HttpMethod.GET.name()),
                new AntPathRequestMatcher("/api/tutorlegal/asistenciaHijo/{id}", HttpMethod.GET.name()),
                new AntPathRequestMatcher("/api/auth/logout", HttpMethod.POST.name())
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}