package com.auth.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Clase de utilidad para la protección criptográfica de credenciales.
 * Implementa el algoritmo SHA-256 junto con técnicas de Salting aleatorio.
 */
public final class HashUtil {

    // Constructor privado para evitar instanciación, ya que es una clase de
    // utilidad.
    private HashUtil() {
        throw new UnsupportedOperationException("Clase de utilidad");
    }

    /**
     * Genera un "Salt" (sal) criptográficamente fuerte.
     * Se utiliza SecureRandom en lugar de Random para garantizar que el valor sea
     * impredecible, evitando ataques basados en patrones de generación.
     * 
     * @return String La sal aleatoria codificada en Base64.
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        // Codificamos en Base64 para facilitar su almacenamiento en columnas VARCHAR de
        // la BD.
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Genera un resumen (hash) de la contraseña utilizando SHA-256 y una sal.
     * La combinación de Password + Salt previene ataques de tablas arcoíris
     * (Rainbow Tables) y garantiza que contraseñas idénticas generen hashes
     * distintos para diferentes usuarios.
     * 
     * @param password Contraseña proporcionada por el usuario en texto plano.
     * @param salt     Sal única asociada al usuario recuperada de la base de datos.
     * @return String Resumen criptográfico codificado en Base64.
     */
    public static String hashPassword(String password, String salt) {
        try {
            // Instanciamos el motor de digestión para SHA-256 (estándar seguro actual).
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Incorporamos la sal al proceso de digestión.
            md.update(salt.getBytes(StandardCharsets.UTF_8));

            // Procesamos la contraseña.
            byte[] hashedBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));

            // Retornamos el resultado en Base64 para una representación textual segura.
            return Base64.getEncoder().encodeToString(hashedBytes);

        } catch (NoSuchAlgorithmException e) {
            // Excepción de sistema: SHA-256 es parte del estándar Java, pero se captura por
            // seguridad.
            throw new RuntimeException("Error Crítico: El algoritmo de cifrado no está disponible.", e);
        }
    }
}
