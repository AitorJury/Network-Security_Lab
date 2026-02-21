import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientApp {
    public static void main(String[] args) {
        // Recogemos errores de conexión, no hay otros errores planificados.
        try {
            // Creamos el túnel de mensajes entre cliente, servidor y abrimos un buffer que
            // lee los datos
            // del servidor y un printer que envía datos al servidor.
            Socket socket = new Socket("localhost", 5000);
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner read = new Scanner(System.in);

            // Identificamos al jugador.
            System.out.println("=======================================");
            System.out.println("             MASTERMIND                ");
            System.out.println("=======================================");

            String name = "";
            // Bucle de validación de nombre: solo letras y espacios, no vacío.
            while (name.trim().isEmpty() || !name.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
                System.out.print(" >> Introduce tu nombre: ");
                name = read.nextLine();
                if (name.trim().isEmpty() || !name.matches("[a-zA-Z ]+")) {
                    System.out.println(" [!] Error: El nombre solo puede contener letras y no puede estar vacío.");
                }
            }

            // Envía el nombre al servidor.
            pw.println(name);

            System.out.println("\n ¡Hola " + name + "! Adivina el código de 4 números (1-6).");
            System.out.println("---------------------------------------");

            // Bucle del juego (hasta que acierte o se desconecte).
            while (true) {
                // Pedimos intento.
                System.out.print(" >> Intento: ");
                String tried = read.nextLine();

                // Si el intento no tiene 4 caracteres y números del 1 al 6, avisamos de formato
                // inválido.
                if (tried.length() != 4 || !tried.matches("[1-6]+")) {
                    System.out.println(" [!] Error: Deben ser 4 números entre 1 y 6.");
                    continue;
                }

                // Mandamos el intento al servidor.
                pw.println(tried);
                // Recibimos la respuesta del servidor.
                String answer = br.readLine();
                // Separamos del mensaje recibido las partes divididas por una barra.
                String[] parts = answer.split("\\|");

                // Si el primer dato es el creado si se acierta, avisamos de que ha ganado.
                // Si el primer dato es "FEEDBACK", damos la pista para el acierto.
                if (parts[0].equals("WINNER")) {
                    System.out.println("\n***************************************");
                    System.out.println("   ¡VICTORIA! Lo lograste en " + parts[1] + " turnos");
                    System.out.println("***************************************");
                    // Para mostrar el ranking (Solo si lo tiene).
                    if (parts.length > 2) {
                        System.out.println(parts[2].replace("#", "\n"));
                    }
                    // Salimos del bucle.
                    break;
                } else {
                    System.out.println(" > " + parts[1]);
                    System.out.println("---------------------------------------");
                }
            }

            // Cerramos todo para que no haya problemas.
            socket.close();
            pw.close();
            br.close();
            read.close();
        } catch (IOException e) {
            System.err.println("\n [!] Error: No se pudo conectar con el servidor.");
        }
    }
}
