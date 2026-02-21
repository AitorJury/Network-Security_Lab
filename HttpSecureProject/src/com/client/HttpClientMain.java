package com.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

/**
 * Clase principal del cliente que gestiona la interfaz de usuario y las
 * peticiones en red.
 * Implementa la lógica de consumo de un servicio RESTful mediante protocolos
 * estándar.
 */
public class HttpClientMain {
    private static final String BASE_URL = "http://localhost:8080/auth";
    private static final Scanner sc = new Scanner(System.in);
    private static String currentUser = null;

    public static void main(String[] args) {
        System.out.println("=== SISTEMA DE GESTIÓN SEGURA ===");
        System.out.println("(Escribe 'SALIR' en cualquier momento para finalizar)");

        // Bucle principal de ejecución del cliente.
        while (true) {
            if (currentUser == null) {
                // Flujo de 'Login / Registro'.
                handleInitialAccess();
            } else {
                // Flujo de Gestión de cuenta.
                showMainMenu();
            }
        }
    }

    /**
     * Gestiona el acceso inicial del usuario al sistema.
     * Valida que los campos no estén vacíos antes de realizar la petición POST.
     */
    private static void handleInitialAccess() {
        String user = "";
        String pass = "";

        while (user.trim().isEmpty()) {
            user = readInput("\nUsuario: ");
            if (user.trim().isEmpty())
                System.out.println("[!] El nombre de usuario es obligatorio.");
        }

        while (pass.trim().isEmpty()) {
            pass = readInput("Contraseña: ");
            if (pass.trim().isEmpty())
                System.out.println("[!] La contraseña no puede estar vacía.");
        }

        // Si el servidor responde con 200 o 201, establecemos la sesión local
        if (sendRequest("POST", "user=" + user + "&pass=" + pass)) {
            currentUser = user;
        }
    }

    /**
     * Despliega el menú de opciones para usuarios autenticados.
     * Permite realizar operaciones CRUD sobre el servicio HTTP y acceder a la
     * mejora FTP.
     */
    private static void showMainMenu() {
        System.out.println("\n--- MENÚ PRINCIPAL ---");
        System.out.println("1. Consultar mis datos (GET)");
        System.out.println("2. Modificar nombre (PUT)");
        System.out.println("3. Eliminar mi cuenta (DELETE)");
        System.out.println("4. Descargar recurso FTP (MEJORA)");
        System.out.println("5. Cerrar Sesión (Logout)");
        System.out.println("6. Salir de la App (SALIR)");

        String opt = readInput("Selecciona opción: ");

        switch (opt) {
            case "1" -> sendRequest("GET", "user=" + currentUser);
            case "2" -> {
                String newName = "";
                while (newName.trim().isEmpty()) {
                    newName = readInput("Nuevo nombre de usuario: ");
                }
                if (sendRequest("PUT", "oldUser=" + currentUser + "&newUser=" + newName)) {
                    currentUser = newName;
                }
            }
            case "3" -> {
                if (sendRequest("DELETE", "user=" + currentUser))
                    currentUser = null;
            }
            case "4" -> FtpClientService.downloadFile();
            case "5" -> {
                System.out.println("Cerrando sesión del usuario...");
                currentUser = null;
            }
            case "6" -> exitApp();
            default -> System.out.println("[!] Opción no reconocida por el sistema.");
        }
    }

    /**
     * Captura la entrada del usuario por consola y verifica la palabra clave de
     * salida.
     * 
     * @param message Mensaje descriptivo para el usuario.
     * @return String Texto introducido por el usuario.
     */
    private static String readInput(String message) {
        System.out.print(message);
        String input = sc.nextLine();
        if (input.equalsIgnoreCase("SALIR"))
            exitApp();
        return input;
    }

    /**
     * Método centralizado para el envío de peticiones HTTP al servidor.
     * Implementa la configuración de cabeceras y el manejo de los diferentes
     * métodos.
     * 
     * @param method Verbo HTTP (GET, POST, PUT, DELETE).
     * @param data   Datos a enviar en el cuerpo o como parámetros de URL.
     * @return boolean True si la operación fue satisfactoria (2xx).
     */
    private static boolean sendRequest(String method, String data) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String url = BASE_URL;

            // Los métodos GET pasan los parámetros a través de la Query String.
            if (method.equalsIgnoreCase("GET"))
                url += "?" + data;

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/x-www-form-urlencoded");

            // Configuración dinámica según el método solicitado.
            switch (method.toUpperCase()) {
                case "POST" -> builder.POST(HttpRequest.BodyPublishers.ofString(data));
                case "PUT" -> builder.PUT(HttpRequest.BodyPublishers.ofString(data));
                case "DELETE" -> builder.method("DELETE", HttpRequest.BodyPublishers.ofString(data));
                default -> builder.GET();
            }

            // Envío síncrono de la petición y recepción de la respuesta.
            HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());

            System.out.println("\n[SERVIDOR]: " + response.body());

            // Códigos 200 (OK) y 201 (Created) indican éxito en la operación.
            return response.statusCode() == 200 || response.statusCode() == 201;

        } catch (Exception e) {
            System.err.println("[ERROR DE RED]: No se pudo contactar con el servidor. " + e.getMessage());
            return false;
        }
    }

    /**
     * Finaliza la ejecución de la aplicación de forma controlada.
     */
    private static void exitApp() {
        System.out.println("\nFinalizando conexión y saliendo del sistema...");
        System.exit(0);
    }
}
