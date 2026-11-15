package com.digistock.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Cadena de seguridad exclusiva de la API REST (/api/**).
 *
 * Es independiente de la cadena web de SecurityConfig (formulario + sesion):
 *  - Autenticacion HTTP Basic con las mismas cuentas y BCrypt de la app web.
 *  - Sin sesion (STATELESS): cada peticion lleva sus credenciales.
 *  - CSRF desactivado: no aplica a clientes que no usan cookies de sesion.
 *  - Si no hay credenciales responde 401 en vez de redirigir a /login.
 *
 * Permisos por rol:
 *  - /api/auth/login ................ publico
 *  - /api/auth/registro, /api/usuarios/** ... ADMINISTRADOR
 *  - GET /api/productos/** .......... cualquier usuario autenticado
 *  - resto de /api/productos/** ..... ADMINISTRADOR, ALMACENISTA
 *  - PATCH /api/facturas/{id}/anular  ADMINISTRADOR
 *  - resto de /api/facturas/** ...... ADMINISTRADOR, VENDEDOR
 */
@Configuration
public class ApiSecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/**")
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers("/api/auth/registro", "/api/usuarios/**").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.GET, "/api/productos/**").authenticated()
                .requestMatchers("/api/productos/**").hasAnyRole("ADMINISTRADOR", "ALMACENISTA")
                .requestMatchers(HttpMethod.PATCH, "/api/facturas/*/anular").hasRole("ADMINISTRADOR")
                .requestMatchers("/api/facturas/**").hasAnyRole("ADMINISTRADOR", "VENDEDOR")
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> { });

        return http.build();
    }
}
