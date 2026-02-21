import java.util.LinkedList;
import java.util.Queue;
import java.io.*;

public class Warehouse {

    private String FILENAME = "registro_jamones.txt";
    // Cola donde se guardan los jamones producidos (FIFO)
    private Queue<Ham> buffer;

    // Capacidad máxima del almacén
    private final int max;
    // Contador de jamones creados
    private int contJ = 0;

    // Buffer para registrar la producción y consumo en un .txt
    private BufferedWriter bw;

    // Constructor con capacidad y la cola
    public Warehouse(int max) {
        this.max = max;
        this.buffer = new LinkedList<>();

        try {
            // Abrir el archivo "registro_jamones.txt" y añadir al final
            bw = new BufferedWriter(new FileWriter(FILENAME));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método sincronizado para imprimir en consola y escribir en el archivo
    public synchronized void write(String message) {
        // Imprime en consola
        System.out.println(message);

        // Escribe en el archivo
        try {
            if (bw != null) {
                bw.write(message);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método sincronizado: crea el siguiente jamón
    public synchronized void produceNextJamon(int idP) throws InterruptedException {
        // Espera si el almacén está lleno
        while (buffer.size() >= max) {
            write("[Almacen] Esta lleno. Productor " + idP + " espera...");
            wait();
        }

        // Crear y añadir el jamón en orden
        contJ++;
        Ham jamon = new Ham(contJ);
        buffer.add(jamon);

        write("[Productor " + idP + "] Producido " + jamon);
        write("[Almacen] Se ha almacenado " + jamon);
        write("[Almacen] Contenido actual: " + new LinkedList<>(buffer));

        notifyAll();
    }

    // Método sincronizado: el consumidor retira un jamón del almacén
    public synchronized Ham consume(int idC) throws InterruptedException {
        // Si el almacén está vacío, el consumidor espera
        while (buffer.isEmpty()) {
            write("[Almacen] Esta vacio. Consumidor " + idC + " espera...");
            wait();
        }

        // Retira el jamón más antiguo de la cola (Método de Queue)
        Ham jamon = buffer.poll();

        // Mensajes de terminal
        write("[Almacen] Se ha retirado " + jamon);
        write("[Consumidor " + idC + "] Consumido " + jamon);
        write("[Almacen] Contenido actual: " + new LinkedList<>(buffer));

        // Notifica a los productores de que hay espacio libre
        notifyAll();
        return jamon;
    }

    // Cierra el archivo al finalizar el programa
    public void closeWrite() {
        try {
            if (bw != null) {
                bw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
