import java.io.*;

public class StudentStorageEngine {
    /**
     * 
     * @param valuesPrincipal Son el número de valores que se especifican en el
     *                        llamamiento, por lo que deberían ser 5 valores
     */
    public static void main(String[] valuesPrincipal) {
        // Asegurar que tenga 5 parámetros
        if (valuesPrincipal.length != 5) {
            System.out.println("Número de argumentos incorrecto");
            return;
        }

        try {
            // Asignar valores y dirección al archivo
            String name = valuesPrincipal[0];
            String surname = valuesPrincipal[1];
            String dni = valuesPrincipal[2];
            String date = valuesPrincipal[3];
            String mark = valuesPrincipal[4];

            String dirS = "src/alumnos/";
            File file = new File(dirS + dni + ".txt");

            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write("DNI,Nombre,Apellidos,FechaNacimiento,NotaMedia\n");
            bw.write(dni + "," + name + "," + surname + "," + date + "," + mark);

            System.out.println("Alumno almacenado correctamente: " + file.getAbsolutePath());
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
