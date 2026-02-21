package com.client;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Servicio especializado en la gestión de recursos mediante el protocolo FTP.
 * Utiliza la librería Apache Commons Net para interactuar con servidores
 * remotos.
 */
public class FtpClientService {

    /**
     * Realiza la conexión, autenticación y descarga de un archivo de un servidor
     * FTP público.
     * Implementa el modo pasivo para garantizar la compatibilidad con firewalls y
     * NAT.
     */
    public static void downloadFile() {
        FTPClient ftpClient = new FTPClient();

        // Configuración del servidor (Universidad de Berlín).
        String server = "ftp.fu-berlin.de";
        String user = "anonymous";
        String pass = "anonymous";

        try {
            // Configuración de tiempos de espera (mejor usabilidad).
            ftpClient.setConnectTimeout(20000);

            System.out.println("[FTP] Conectando a " + server + "...");
            ftpClient.connect(server);

            // Proceso de autenticación.
            if (ftpClient.login(user, pass)) {

                // IMPORTANTE: Modo pasivo para evitar bloqueos de red/firewall.
                ftpClient.enterLocalPassiveMode();

                // Definimos el tipo de transferencia como binario para integridad de datos.
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

                // Recurso remoto y destino local.
                String remoteFile = "README";
                File downloadFile = new File("descarga_ftp_log.txt");

                System.out.println("[FTP] Descargando recurso: " + remoteFile + "...");

                try (FileOutputStream fos = new FileOutputStream(downloadFile)) {
                    boolean success = ftpClient.retrieveFile(remoteFile, fos);
                    if (success) {
                        System.out.println("[FTP] ÉXITO: Archivo guardado localmente como 'descarga_ftp_log.txt'.");
                    } else {
                        System.out.println("[FTP] ERROR: El servidor no permitió la descarga del recurso.");
                    }
                }
            } else {
                System.out.println("[FTP] ERROR: Credenciales rechazadas por el servidor.");
            }

        } catch (IOException ex) {
            System.err.println("[FTP ERROR] Fallo en la comunicación: " + ex.getMessage());
        } finally {
            // Garantizamos el cierre de la conexión para liberar recursos del servidor.
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                    System.out.println("[FTP] Conexión cerrada correctamente.");
                }
            } catch (IOException ex) {
                System.err.println("[FTP ERROR] Error al cerrar conexión: " + ex.getMessage());
            }
        }
    }
}
