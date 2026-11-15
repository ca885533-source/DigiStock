package com.digistock.app.exception;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Cuerpo JSON estandar que devuelve la API cuando ocurre un error.
 *
 * @param fecha    momento en que se produjo el error
 * @param estado   codigo HTTP (400, 401, 404, 500...)
 * @param mensaje  descripcion legible del problema
 * @param errores  detalle por campo cuando falla una validacion (puede ser null)
 */
public record ApiError(LocalDateTime fecha, int estado, String mensaje, Map<String, String> errores) {

    public static ApiError de(int estado, String mensaje) {
        return new ApiError(LocalDateTime.now(), estado, mensaje, null);
    }

    public static ApiError de(int estado, String mensaje, Map<String, String> errores) {
        return new ApiError(LocalDateTime.now(), estado, mensaje, errores);
    }
}
