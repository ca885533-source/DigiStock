package com.digistock.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Entidad que representa un usuario del sistema DigiStock.
 *
 * Se mapea a la coleccion "usuarios" en MongoDB. Contiene la informacion
 * necesaria para el modulo de autenticacion: credenciales, rol asignado
 * y estado de la cuenta.
 *
 * Estandar de nombres: atributos en camelCase, clase en PascalCase,
 * siguiendo la convencion de codigo Java.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "usuarios")
public class Usuario {

    @Id
    private String id;

    /** Nombre de usuario unico usado para iniciar sesion. */
    @Indexed(unique = true)
    private String nombreUsuario;

    /** Correo electronico del usuario, tambien unico. */
    @Indexed(unique = true)
    private String correo;

    /** Nombre completo de la persona (para mostrar en la interfaz). */
    private String nombreCompleto;

    /** Contrasena encriptada con BCrypt (NUNCA se guarda en texto plano). */
    private String contrasena;

    /** Rol del usuario dentro del sistema (ver enum Rol). */
    private Rol rol;

    /** Indica si la cuenta esta activa; permite deshabilitar sin borrar. */
    private boolean activo = true;

    /** Fecha de creacion de la cuenta, se asigna automaticamente. */
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}
