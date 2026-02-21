public class Consumer implements Runnable {

    // Almacén compartido
    private Warehouse storage;
    // Total de jamones a consumir
    private int totalC;
    // ID del consumidor
    private int idC;

    public Consumer(Warehouse storage, int totalC, int idC) {
        this.storage = storage;
        this.totalC = totalC;
        this.idC = idC;
    }

    @Override
    public void run() {
        for (int i = 0; i < totalC; i++) {
            try {
                // Retirar un jamón del almacén
                storage.consume(idC);

                // Tiempo de procesamiento
                Thread.sleep(2000 + (int) (Math.random() * 1000));
            } catch (InterruptedException ie) {
                System.out.println("[Consumidor " + idC + "] Interrumpido.");
                Thread.currentThread().interrupt();
            }
        }

        storage.write("[Consumidor " + idC + "] Ha terminado de consumir.");
    }
}
