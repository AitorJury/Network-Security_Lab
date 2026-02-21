import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    // Configuración del servidor: puerto, mejores resultados y fichero de ranking
    // para almacenar resultados.
    private static final int PORT = 5000;
    private static final Map<String, Integer> ranking = new ConcurrentHashMap<>();
    private static final String RANKING_FILE = "ranking.txt";

    // El servidor es un bucle inifinito para recibir cuando haga falta clientes.
    public static void main(String[] args) {
        // Recogemos el ranking antes de iniciar el juego.
        chargeRanking();
        System.out.println("Servidor Mastermind iniciado...");

        // Creamos el canal del servidor. Recogemos errores de conexión.
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                // Cliente nuevo = Hilo nuevo par ajugar (y lo arrancamos ya)
                Socket socketClient = serverSocket.accept();
                new Thread(new Client(socketClient)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Cada cliente necesita un controlador como jugador.
    private static class Client implements Runnable {

        // Abrimos el canal.
        private Socket socket;

        // Creamos el cliente con el canal establecido entre cliente-servidor.
        public Client(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            // Recogemos el error de cierre de conexión.
            try {
                // Para comunicación con el lado cliente.
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

                // Leemos el nombre del usuario y creamos un código al azar. Mostramos en el
                // lado servidor la solución.
                String userName = br.readLine();
                String code = generateCode();
                System.out.println("Nueva partida: " + userName + " | Código: " + code);

                // Número de intentos y si está realizado.
                int tries = 0;
                boolean success = false;

                // Nos mantenemos en el bucle hasta que haya terminado.
                while (!success) {
                    // Recibe el intento.
                    String tried = br.readLine();
                    // Cerramos si el cliente se va.
                    if (tried == null)
                        break;

                    // Aumentamos el contador de intentos y comparamos el código con el intento
                    // dado.
                    tries++;
                    String answer = evaluateTried(code, tried);

                    // Si el usuario ha acertado, actualizamos el ranking y salimos.
                    // Si el usuario no acierta, le damos una pista de cómo va.
                    if (answer.startsWith("4X")) {
                        success = true;
                        actualizateRanking(userName, tries);
                        pw.println("WINNER|" + tries + "|" + rankingTop());
                    } else {
                        // Mejora visual en el feedback
                        pw.println("FEEDBACK| Intentado: [" + tried + "] -> Pistas: " + answer);
                    }
                }

                // Cerramos todo.
                br.close();
                pw.close();
            } catch (IOException e) {
                System.out.println("Conexión finalizada.");
            }
        }
    }

    // Método para generar un código al azar.
    private static String generateCode() {
        // Utilizamos la clase Random, y le damos las pautas de que sean 4 dígitos del 1
        // al 6.
        Random random = new Random();
        String code = "";
        for (int i = 0; i < 4; i++)
            code += (random.nextInt(6) + 1);
        return code;
    }

    // Método para comparar el código con el intento.
    private static String evaluateTried(String code, String tried) {
        int x = 0, o = 0;
        // Creamos dos arrays para recoger
        boolean[] sV = new boolean[4], iV = new boolean[4];

        for (int i = 0; i < 4; i++) {
            if (code.charAt(i) == tried.charAt(i)) {
                x++;
                sV[i] = iV[i] = true;
            }
        }
        for (int i = 0; i < 4; i++) {
            if (!iV[i]) {
                for (int j = 0; j < 4; j++) {
                    if (!sV[j] && tried.charAt(i) == code.charAt(j)) {
                        o++;
                        sV[j] = true;
                        break;
                    }
                }
            }
        }
        return x + "X, " + o + "O";
    }

    // Mejora: Guardado de Ranking. Actualizamos el ranking (sincronizado para que
    // dos clientes no lo hagan a la vez) con los datos que se nos dan.
    private static synchronized void actualizateRanking(String name, int tries) {
        // Solo escribimos si no existe un dato con el mismo nombre y misma puntuación.
        if (!ranking.containsKey(name) || tries < ranking.get(name)) {
            ranking.put(name, tries);
            saveRanking();
        }
    }

    // Mejora: Escribir el Ranking en un archivo para permanencia.
    private static void saveRanking() {
        // Recogemos errores de conexión.
        try {
            // Direccionamos al archivo y escribimos lo que haya en el mapa.
            PrintWriter pw = new PrintWriter(new FileWriter(RANKING_FILE));
            for (Map.Entry<String, Integer> entry : ranking.entrySet()) {
                pw.println(entry.getKey() + ":" + entry.getValue());
            }

            // Cerramos la escritura.
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Mejora: Leemos al iniciar cada partida los valores del Ranking.
    private static void chargeRanking() {
        // Solo lee si existe el fichero.
        File file = new File(RANKING_FILE);
        if (!file.exists())
            return;

        // Recogemos errores que surjan, como que no existan valores en el ranking.
        try {
            // Direccionamos la lectura y leemos línea a línea los valores separados por
            // ':', y así creamos
            // el mapa de valores cliente-puntuación.
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                ranking.put(parts[0], Integer.parseInt(parts[1]));
            }

            // Cerramos lectura.
            br.close();
        } catch (Exception e) {
            System.out.println("No hay ranking previo.");
        }
    }

    // Mejora: Mostramos los 3 mejores juadores por el Ranking.
    private static String rankingTop() {
        String top = "# [ TABLA DE RÉCORDS ]#  --------------------";

        // Metemos todas las entradas del mapa en una lista para poder ordenarlas.
        List<Map.Entry<String, Integer>> list = new ArrayList<>(ranking.entrySet());

        if (list.isEmpty()) {
            return top + "# Aún no hay récords grabados.";
        }

        // Ordenamos la lista (de menos intentos a más intentos).
        list.sort(Map.Entry.comparingByValue());

        // Usamos un bucle para coger solo los 3 primeros jugadores.
        for (int i = 0; i < list.size(); i++) {
            // Si tenemos 3, salimos.
            if (i == 3) break;

            // Revisamos los valores del mapa y recogemos a los jugadores.
            Map.Entry<String, Integer> input = list.get(i);
            String name = input.getKey();
            int tries = input.getValue();

            // Formato de lista más limpio.
            top = top + "# " + (i + 1) + ". " + name + " (" + tries + " intentos)";
        }

        return top;
    }
}
