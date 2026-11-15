package com.digistock.app.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion de la documentacion interactiva (Swagger UI).
 *
 *  - Interfaz: http://localhost:8080/swagger-ui.html
 *  - Especificacion JSON: http://localhost:8080/v3/api-docs
 *
 * El esquema "basicAuth" habilita el boton Authorize para probar los
 * servicios protegidos con usuario y contrasena.
 */
@Configuration
@OpenAPIDefinition(info = @Info(
        title = "API REST de DigiStock",
        version = "1.0.0",
        description = "Servicios web del sistema de facturacion e inventario DigiStock"))
@SecurityScheme(name = "basicAuth", type = SecuritySchemeType.HTTP, scheme = "basic")
public class OpenApiConfig {
}
