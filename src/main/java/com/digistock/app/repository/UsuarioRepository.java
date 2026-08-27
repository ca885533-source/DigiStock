package com.digistock.app.repository;

import com.digistock.app.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Repositorio de acceso a datos para la coleccion "usuarios" en MongoDB.
 *
 * Al extender MongoRepository, Spring Data genera automaticamente las
 * operaciones basicas (guardar, buscar por id, listar, eliminar) y permite
 * declarar consultas derivadas del nombre del metodo, como las de abajo.
 */
public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    /** Busca un usuario por su nombre de usuario (usado en el login). */
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    /** Busca un usuario por correo (usado para validar duplicados en el registro). */
    Optional<Usuario> findByCorreo(String correo);

    /** Valida si ya existe un usuario con ese nombre de usuario. */
    boolean existsByNombreUsuario(String nombreUsuario);

    /** Valida si ya existe un usuario con ese correo. */
    boolean existsByCorreo(String correo);
}
