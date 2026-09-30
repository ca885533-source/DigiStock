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
                .requestMatchers("/login", "/registro", "/css/**", "/js/**").permitAll()
                // API REST (evidencia AA5-EV03): publica para poder probarla
                // desde Postman sin necesitar una sesion de navegador. En un
                // entorno de produccion real se protegeria con un token
                // (JWT/OAuth2), pero eso queda fuera del alcance de esta
                // evidencia formativa.
                .requestMatchers("/api/**").permitAll()
                // Solo el administrador puede gestionar usuarios
                .requestMatchers("/usuarios/**").hasRole("ADMINISTRADOR")
                // Facturacion: la usan tanto el administrador como el vendedor
                .requestMatchers("/facturas/**").hasAnyRole("ADMINISTRADOR", "VENDEDOR")
                // Inventario: cualquier usuario autenticado puede CONSULTAR el
                // listado (el vendedor necesita ver el inventario), pero solo
                // ADMINISTRADOR y ALMACENISTA pueden crear, editar o eliminar
                // productos.
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
            )
            // CSRF sigue activo para los formularios web (login, registro,
            // creacion de usuarios), pero se desactiva unicamente para
            // "/api/**": una API JSON consumida por Postman u otro cliente
            // externo no tiene el token CSRF que genera Thymeleaf en sus
            // formularios, asi que exigirlo bloquearia toda peticion
            // POST/PUT/DELETE de la API sin aportar proteccion real aqui.
            .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"));

        return http.build();
    }
}
