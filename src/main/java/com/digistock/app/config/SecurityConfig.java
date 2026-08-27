package com.digistock.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuracion central de seguridad del sistema DigiStock.
 *
 * Define:
 *  - Que rutas son publicas (login, registro, recursos estaticos).
 *  - Que rutas requieren un rol especifico (control de acceso por rol).
 *  - El formulario de login personalizado.
 *  - El algoritmo de encriptado de contrasenas (BCrypt).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt agrega un "salt" automatico y es el estandar recomendado
        // para almacenar contrasenas de forma segura.
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Rutas publicas: login, registro y archivos estaticos (css/js)
                .requestMatchers("/login", "/registro", "/css/**", "/js/**").permitAll()
                // Solo el administrador puede gestionar usuarios
                .requestMatchers("/usuarios/**").hasRole("ADMINISTRADOR")
                // El resto de rutas del sistema requieren estar autenticado
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            );
            // Nota: CSRF queda activo (comportamiento por defecto de Spring Security)
            // para proteger los formularios de login, registro y creacion de usuarios.

        return http.build();
    }
}
