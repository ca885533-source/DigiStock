package com.digistock.app.service;

import com.digistock.app.model.Factura;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.Locale;

/**
 * Genera el CUFE (Codigo Unico de Factura Electronica) de una factura.
 *
 * NOTA DE ALCANCE: en la facturacion electronica real de Colombia el CUFE
 * lo calcula el sistema autorizado por la DIAN con sus propios datos
 * (NIT del emisor, clave tecnica, etc.). Aqui se replica la idea con fines
 * formativos: un hash SHA-384 (el mismo algoritmo que usa la DIAN) de los
 * datos principales de la factura, de modo que cualquier cambio en esos
 * datos produzca un codigo distinto.
 */
public final class CufeGenerador {

    private CufeGenerador() {
    }

    /** Calcula el CUFE a partir de los datos de la factura (96 caracteres hexadecimales). */
    public static String generar(Factura factura) {
        String base = String.join("|",
                String.valueOf(factura.getNumeroFactura()),
                factura.getFecha().truncatedTo(ChronoUnit.SECONDS).toString(),
                String.format(Locale.ROOT, "%.2f", factura.getSubtotal()),
                String.format(Locale.ROOT, "%.2f", factura.getIva()),
                String.format(Locale.ROOT, "%.2f", factura.getTotal()),
                String.valueOf(factura.getDocumentoCliente()),
                String.valueOf(factura.getVendedor()));
        try {
            MessageDigest sha384 = MessageDigest.getInstance("SHA-384");
            return HexFormat.of().formatHex(sha384.digest(base.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            // SHA-384 viene incluido en toda JVM; este caso no deberia ocurrir
            throw new IllegalStateException("Algoritmo SHA-384 no disponible", e);
        }
    }
}
