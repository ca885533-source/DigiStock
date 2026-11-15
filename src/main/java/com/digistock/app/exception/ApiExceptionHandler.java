package com.digistock.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manejo centralizado de errores de la API REST.
 *
 * Se limita al paquete controller.api para no alterar el comportamiento
 * de los controladores MVC con vistas Thymeleaf del sistema.
 *
 * Traduce las excepciones de la capa de servicio a codigos HTTP:
 *  - Validacion de campos fallida  -> 400
 *  - Cuerpo JSON ilegible          -> 400
 *  - Regla de negocio incumplida   -> 400
 *  - Recurso inexistente           -> 404
 *  - Credenciales invalidas        -> 401
 *  - Error inesperado              -> 500 (sin exponer detalles internos)
 */
@RestControllerAdvice(basePackages = "com.digistock.app.controller.api")
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errores.putIfAbsent(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest()
                .body(ApiError.de(400, "Los datos enviados no son validos", errores));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> cuerpoIlegible(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest()
                .body(ApiError.de(400, "El cuerpo de la peticion no es un JSON valido o tiene tipos incorrectos"));
    }

    /**
     * Los servicios existentes lanzan IllegalArgumentException tanto para
     * "no encontrado" como para reglas de negocio (stock insuficiente,
     * duplicados). Se distinguen por el mensaje para devolver 404 o 400
     * sin tener que modificar la logica ya probada de los servicios.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> negocio(IllegalArgumentException ex) {
        String mensaje = ex.getMessage() == null ? "Solicitud invalida" : ex.getMessage();
        HttpStatus estado = mensaje.toLowerCase().contains("no encontrad")
                ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(estado).body(ApiError.de(estado.value(), mensaje));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> credenciales(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiError.de(401, "Credenciales invalidas"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> inesperado(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.de(500, "Error interno del servidor"));
    }
}
