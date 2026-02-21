public class Principal {

    public static void main(String[] args) {
        // Pedir todos los parámetros
        int maxA = Program.askValue("Ingrese el numero maximo de jamones posibles en el almacen (SALIR para salir): ");
        int totalJ = Program.askValue("Ingrese el numero de jamones que quiere \"probar\" (SALIR para salir): ");
        int nProducers = Program.askValueP("Ingrese el numero de productores (SALIR para salir): ", totalJ);
        int nConsumers = Program.askValue("Ingrese el numero de consumidores (SALIR para salir): ");
        // Crear el programa
        Program program = new Program(maxA, totalJ, nProducers, nConsumers);
        System.out.println("\n----------    COMENZAMOS    ----------\n");
        // Iniciar el programa
        program.start();
    }
}
