import java.io.*;

public class StudentDevolutionEngine {
    /**
     * 
     * @param valuesPrincipal Son el número de valores que se especifican en el
     *                        llamamiento, por lo que deberían ser 7 valores
     */
    public static void main(String[] valuesPrincipal) {
        String dirS = "src/alumnos/";

        try {
            if (valuesPrincipal.length == 1) {
                // Buscar archivo por DNI
                String dni = valuesPrincipal[0];
                File file = new File(dirS + dni + ".txt");

                // Salir si no hay archivos
                if (!file.exists()) {
                    System.out.println("No se ha podido encontrar el alumno con DNI: " + dni);
                    return;
                }

                // Utilizar proceso
                BufferedReader br = new BufferedReader(new FileReader(file));
                // Para que me lea la segunda línea y no la primera
                br.readLine();
                String line = br.readLine();
                br.close();

                // Formatear e imprimir la información (Este código ha sido investigado para
                // cumplir con el ejercicio correctamente)
                printStudent(line);

            } else if (valuesPrincipal.length == 0) {
                // Obtener todos los alumnos
                File dir = new File(dirS);
                File[] files = dir.listFiles();

                // Salir si no hay archivos
                if (files == null || files.length == 0) {
                    System.out.println("No hay alumnos almacenados");
                    return;
                }

                // Recorrer archivos
                for (File file : files) {
                    // Utilizar proceso
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    br.readLine();
                    String line = br.readLine();
                    br.close();

                    // Formatear e imprimir la información
                    printStudent(line);
                }
            } else {
                System.out.println("Número de argumentos incorrecto");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printStudent(String line) {
        String[] data = line.split(",");
        System.out.println("============================");
        System.out.println("DNI: " + data[0]);
        System.out.println("Nombre y apellidos: " + data[2] + ", " + data[1]);
        System.out.println("Fecha de nacimiento: " + data[3]);
        System.out.println("Nota media: " + data[4]);
        System.out.println("============================");
    }
}
