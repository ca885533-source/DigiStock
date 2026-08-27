package com.digistock.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal del sistema DigiStock.
 *
 * DigiStock es un sistema web de facturacion e inventario desarrollado
 * como proyecto formativo del programa ADSI (SENA).
 *
 * Este modulo corresponde a la evidencia GA7-220501096-AA3-EV01:
 * codificacion del modulo de autenticacion y gestion de usuarios,
 * usando el framework Spring Boot (Java) y MongoDB como base de datos.
 *
 * @author Equipo DigiStock
 */
@SpringBootApplication
public class DigistockApplication {

    public static void main(String[] args) {
        SpringApplication.run(DigistockApplication.class, args);
    }

}
