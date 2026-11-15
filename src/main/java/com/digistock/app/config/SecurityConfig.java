package com.digistock.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                .requestMatchers("/login", "/registro", "/css/**", "/js/**", "/img/**").permitAll()
                // Documentacion Swagger de la API (la API en si exige credenciales)
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // Verificacion publica de facturas (la abre quien escanea el QR)
                .requestMatchers("/verificar/**").permitAll()
                // Solo el administrador puede gestionar usuarios
                .requestMatchers("/usuarios/**").hasRole("ADMINISTRADOR")
                // Facturacion: la usan tanto el administrador como el vendedor
                .requestMatchers("/facturas/**").hasAnyRole("ADMINISTRADOR", "VENDEDOR")
                // Inventario: cualquier usuario autenticado puede CONSULTAR el
                // listado (el vendedor necesita ver el inventario), pero solo
                // ADMINISTRADOR y ALMACENISTA pueden crear, editar o eliminar
                // productos.
                // Los formularios de alta y edicion tambien son solo para ellos.
                .requestMatchers(HttpMethod.GET, "/productos/nuevo", "/productos/*/editar")
                    .hasAnyRole("ADMINISTRADOR", "ALMACENISTA")
                .requestMatchers(HttpMethod.GET, "/productos/**")
                    .hasAnyRole("ADMINISTRADOR", "VENDEDOR", "ALMACENISTA")
                .requestMatchers(HttpMethod.POST, "/productos/**")
                    .hasAnyRole("ADMINISTRADOR", "ALMACENISTA")
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
