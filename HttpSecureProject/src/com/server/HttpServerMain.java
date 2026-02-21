package com.server;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Punto de entrada principal para el despliegue del servidor de autenticación.
 * Configura el entorno de red, los manejadores de contexto y la gestión de
 * hilos.
 */
public class HttpServerMain {

    // Puerto de escucha estandarizado para servicios web alternativos.
    private static final int PORT = 8080;

    /**
     * Inicializa y arranca el motor del servidor HTTP.
     * 
     * @param args Argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        try {
            // Instanciamos el servidor vinculado a la dirección local y el puerto definido.
            // El segundo parámetro (backlog) en 0 utiliza el valor por defecto del sistema.
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

            // Vinculamos el contexto "/auth" con nuestra lógica de negocio en AuthHandler.
            // Todas las peticiones a http://localhost:8080/auth serán procesadas allí.
            server.createContext("/auth", new AuthHandler());

            /**
             * MEJORA: Por defecto, HttpServer es monohilo. Al asignar un Executor
             * (CachedThreadPool),
             * permitimos que el servidor cree o reutilice hilos para manejar cada petición
             * de forma asíncrona. Esto evita que un cliente lento bloquee a los demás.
             */
            server.setExecutor(Executors.newCachedThreadPool());

            // Mensajes de estado para el administrador del sistema.
            System.out.println("====================================================");
            System.out.println("[SISTEMA] Servidor de Autenticación Segura iniciado");
            System.out.println("[ESTADO] Escuchando en el puerto: " + PORT);
            System.out.println("[URL] Punto de acceso: http://localhost:" + PORT + "/auth");
            System.out.println("====================================================");

            // Inicio del servicio.
            server.start();

            // Gancho de apagado (Shutdown Hook) para una finalización limpia.
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\n[SISTEMA] Deteniendo servidor y liberando recursos...");
                server.stop(0);
                System.out.println("[ESTADO] Servidor desconectado correctamente.");
            }));

        } catch (Exception e) {
            System.err.println("[ERROR CRÍTICO] Fallo al inicializar el servicio: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
