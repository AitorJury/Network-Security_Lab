package com.server;

import com.auth.db.DatabaseManager;
import com.auth.util.HashUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.HashMap;

/**
 * Controlador principal de peticiones HTTP (Handler).
 * Implementa la interfaz HttpHandler para gestionar el ciclo de vida de las
 * solicitudes al servidor.
 */
public class AuthHandler implements HttpHandler {

    /**
     * Punto de entrada para todas las peticiones al contexto /auth.
     * Distribuye la carga según el método HTTP utilizado.
     * 
     * @param exchange Encapsula la petición recibida y la respuesta a enviar.
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        try {
            // Enrutamiento basado en el verbo HTTP (RESTful approach).
            switch (method.toUpperCase()) {
                // Login o Registro.
                case "POST" -> handlePost(exchange);
                // Consulta de datos.
                case "GET" -> handleGet(exchange);
                // Actualización de nombre.
                case "PUT" -> handlePut(exchange);
                // Borrado de cuenta.
                case "DELETE" -> handleDelete(exchange);
                default -> sendResponse(exchange, 405, "Método no permitido.");
            }
        } catch (Exception e) {
            // Captura de errores inesperados para evitar la caída del servidor.
            sendResponse(exchange, 500, "Error interno del servidor: " + e.getMessage());
        }
    }

    /**
     * Procesa el login o registro. Si el usuario no existe, se registra
     * automáticamente.
     * Implementa la lógica de verificación de Hashes y Salts.
     */
    private void handlePost(HttpExchange exchange) throws IOException {
        Map<String, String> params = parseRequestBody(exchange);
        String user = params.get("user");
        String pass = params.get("pass");

        if (user == null || pass == null) {
            sendResponse(exchange, 400, "Parámetros insuficientes.");
            return;
        }

        DatabaseManager.UserCredentials creds = DatabaseManager.getCredentials(user);

        if (creds == null) {
            // Proceso de Registro Seguro.
            String salt = HashUtil.generateSalt();
            String hash = HashUtil.hashPassword(pass, salt);
            DatabaseManager.registerUser(user, hash, salt);
            sendResponse(exchange, 201, "[INFO] NUEVA CUENTA: Usuario '" + user + "' registrado con éxito.");
        } else {
            // Proceso de Autenticación Segura.
            String challengeHash = HashUtil.hashPassword(pass, creds.salt());
            if (challengeHash.equals(creds.hash())) {
                sendResponse(exchange, 200, "[INFO] ACCESO CONCEDIDO: Bienvenido " + user + ".");
            } else {
                sendResponse(exchange, 401, "[ERROR] Credenciales incorrectas.");
            }
        }
    }

    /**
     * Recupera y muestra la información de seguridad del usuario actual.
     */
    private void handleGet(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String user = (query != null && query.contains("=")) ? query.split("=")[1] : "";

        DatabaseManager.UserCredentials creds = DatabaseManager.getCredentials(user);
        if (creds != null) {
            String info = "\n--- AUDITORÍA DE SEGURIDAD (BD) ---\n" +
                    " > Usuario: " + user + "\n" +
                    " > Hash SHA-256: " + creds.hash() + "\n" +
                    " > Salt: " + creds.salt() + "\n" +
                    "------------------------------------\n";
            sendResponse(exchange, 200, info);
        } else {
            sendResponse(exchange, 404, "[ERROR] Usuario no localizado.");
        }
    }

    /**
     * Actualiza el identificador de usuario en la persistencia.
     */
    private void handlePut(HttpExchange exchange) throws IOException {
        Map<String, String> params = parseRequestBody(exchange);
        String oldUser = params.get("oldUser");
        String newUser = params.get("newUser");

        if (DatabaseManager.updateUsername(oldUser, newUser)) {
            sendResponse(exchange, 200, "Nombre actualizado correctamente a '" + newUser + "'.");
        } else {
            sendResponse(exchange, 400, "Error: No se pudo actualizar el registro.");
        }
    }

    /**
     * Elimina permanentemente los datos del usuario del sistema.
     */
    private void handleDelete(HttpExchange exchange) throws IOException {
        Map<String, String> params = parseRequestBody(exchange);
        String user = params.get("user");

        if (DatabaseManager.deleteUser(user)) {
            sendResponse(exchange, 200, "Cuenta de usuario '" + user + "' eliminada del sistema.");
        } else {
            sendResponse(exchange, 400, "Error: El usuario no pudo ser eliminado.");
        }
    }

    /**
     * Utilidad para extraer y parsear los datos del cuerpo de la petición HTTP.
     */
    private Map<String, String> parseRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> map = new HashMap<>();
            if (!body.isEmpty()) {
                for (String param : body.split("&")) {
                    String[] pair = param.split("=");
                    if (pair.length > 1)
                        map.put(pair[0], pair[1]);
                }
            }
            return map;
        }
    }

    /**
     * Genera y envía la respuesta HTTP al cliente de forma segura y limpia.
     */
    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
            os.flush();
        }
    }
}
