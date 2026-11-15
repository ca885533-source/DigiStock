package com.digistock.app.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

/** Utilidad que convierte un texto (una URL) en la imagen PNG de un codigo QR. */
public final class QrGenerador {

    private QrGenerador() {
    }

    /**
     * @param contenido texto que llevara el QR (por ejemplo, la URL de verificacion)
     * @param lado      ancho y alto de la imagen en pixeles
     * @return bytes de la imagen PNG
     */
    public static byte[] png(String contenido, int lado) {
        try {
            BitMatrix matriz = new QRCodeWriter().encode(
                    contenido, BarcodeFormat.QR_CODE, lado, lado,
                    Map.of(EncodeHintType.MARGIN, 1));
            ByteArrayOutputStream salida = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matriz, "PNG", salida);
            return salida.toByteArray();
        } catch (WriterException | IOException e) {
            throw new IllegalStateException("No se pudo generar el codigo QR", e);
        }
    }
}
