public class Producer implements Runnable {

    // Almacén compartido
    private Warehouse storage;
    // Total de jamones a producir
    private int totalP;
    // ID del productor
    private int idP;

    // Constructor del productor
    public Producer(Warehouse storage, int totalP, int idP) {
        this.storage = storage;
        this.totalP = totalP;
        this.idP = idP;
    }

    @Override
    public void run() {
        for (int i = 0; i < totalP; i++) {
            try {
                // Crear un jamón y añadirlo
                storage.produceNextJamon(idP);

                // Tiempo de producción
                Thread.sleep(2000 + (int) (Math.random() * 1000));
            } catch (InterruptedException ie) {
                System.out.println("[Productor " + idP + "] Interrumpido.");
                Thread.currentThread().interrupt();
            }
        }

        storage.write("[Productor " + idP + "] Ha terminado de producir.");
    }
}
