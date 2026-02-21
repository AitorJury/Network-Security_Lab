import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Program {

    private static final Scanner read = new Scanner(System.in);
    // Constante de salida del programa
    private static final String EXIT = "SALIR";

    // Almacén compartido
    private Warehouse storage;

    // Capacidad del almacén, nº jamones, productores y consumidores
    private int maxA, totalJ, nProducers, nConsumers;

    // Constructor del Programa
    public Program(int maxA, int totalJ, int nProducers, int nConsumers) {
        this.maxA = maxA;
        this.totalJ = totalJ;
        this.nProducers = nProducers;
        this.nConsumers = nConsumers;
    }

    // Método que inicia el programa con los hilos
    public void start() {
        // Inicio del programa
        long start = System.currentTimeMillis();
        // Crear el almacén
        storage = new Warehouse(maxA);

        // Título
        storage.write("----------    JAMONSYNC    ----------\n- Tamano del Almacen: " + maxA + "\n- Numero de Jamones: "
                + totalJ + "\n- Numero de Productores: " + nProducers + "\n- Numero de Consumidores: " + nConsumers
                + "\n");

        // Lista de hilos
        List<Thread> threads = new ArrayList<>();

        // Jamones mínimo por productor
        int baseP = totalJ / nProducers;
        // Jamones sobrantes
        int sobraP = totalJ % nProducers;

        // Crear productores
        for (int i = 0; i < nProducers; i++) {
            int totalFJ = baseP;
            if (i < sobraP) {
                totalFJ += 1;
            }
            // Si hay un productor sin jamones, no aparece
            if (totalFJ <= 0)
                continue;

            Producer productor = new Producer(storage, totalFJ, i + 1);
            Thread threadP = new Thread(productor);
            threads.add(threadP);
            threadP.start();
        }

        // Jamones mínimo por consumidor
        int baseC = totalJ / nConsumers;
        // Jamones sobrantes
        int sobraC = totalJ % nConsumers;
        // Crear consumidores
        for (int i = 0; i < nConsumers; i++) {
            int totalFJ = baseC;
            if (i < sobraC) {
                totalFJ += 1;
            }
            // Si hay un productor sin jamones, no aparece
            if (totalFJ <= 0)
                continue;

            Consumer consumer = new Consumer(storage, totalFJ, i + 1);
            Thread threadC = new Thread(consumer);
            threads.add(threadC);
            threadC.start();
        }

        // Esperar a que todos los hilos terminen
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.out.println("Hilo interrumpido.");
                Thread.currentThread().interrupt();
            }
        }

        long end = System.currentTimeMillis();
        long duration = end - start;

        // Escribir tiempo de ejecución
        storage.write("Tiempo total de ejecucion: " + (duration / 1000) + "." + (duration % 1000) + " segundos");
        storage.closeWrite();

        System.out.println("Programa finalizado: todos los jamones producidos y consumidos...");
    }

    // Método para validar los parámetros
    public static int askValue(String message) {
        int value = 0;
        do {
            System.out.println(message);
            String input = read.nextLine().trim();

            if (input.equals(EXIT)) {
                System.out.println("Saliendo del programa...");
                System.exit(0);
            }

            if (!input.matches("\\d+")) {
                System.out.println("Debe ser un numero valido. No te inventes cosas...");
            } else {
                value = Integer.parseInt(input);
                if (value < 1) {
                    System.out.println("El valor debe ser 1 o mayor...");
                    value = 0;
                }
            }
        } while (value == 0);
        return value;
    }

    public static int askValueP(String message, int totalJ) {
        int value = 0;
        do {
            System.out.println(message);
            String input = read.nextLine().trim();

            if (input.equals(EXIT)) {
                System.out.println("Saliendo del programa...");
                System.exit(0);
            }

            if (!input.matches("\\d+")) {
                System.out.println("Debe ser un numero valido. No te inventes cosas...");
            } else {
                value = Integer.parseInt(input);
                if (value < 1) {
                    System.out.println("El valor debe ser 1 o mayor...");
                    value = 0;
                } else if (value > totalJ) {
                    System.out
                            .println("No puedes tener tantos productores. Despide a algunos y vuelve a intentarlo...");
                    value = 0;
                }
            }
        } while (value == 0);
        return value;
    }
}
