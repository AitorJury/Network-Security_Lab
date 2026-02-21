import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Principal {
    private static final String DIRS = "src/alumnos/";
    private static final String RESET = "REINICIAR";
    private static final String DATEVAL = "^\\d{2}-\\d{2}-\\d{4}$";

    public static void main(String[] args) {
        Scanner read = new Scanner(System.in);
        int option = 0;

        try {
            // Crear carpeta alumnos si no existe
            File dirF = new File(DIRS);
            if (!dirF.exists()) {
                dirF.mkdirs();
            }

            // Mostrar menú
            while (option != 6) {
                System.out.println("\n=== MENÚ PRINCIPAL ===");
                System.out.println("1. Buscar alumno por DNI");
                System.out.println("2. Insertar nuevo alumno");
                System.out.println("3. Obtener todos los alumnos");
                System.out.println("4. Eliminar alumno por DNI");
                System.out.println("5. Modificar alumno por DNI");
                System.out.println("6. Finalizar");
                System.out.print("Elige una opción:\n");
                option = read.nextInt();
                read.nextLine();
                System.out.println();

                // Elegir opción
                switch (option) {
                    // Buscar alumno
                    case 1:
                        searchStudent(read);
                        System.out.println();
                        break;

                    // Insertar alumno
                    case 2:
                        insertStudent(read);
                        System.out.println();
                        break;

                    // Obtener todos
                    case 3:
                        getAll();
                        System.out.println();
                        break;

                    // Eliminar alumno
                    case 4:
                        deleteStudent(read);
                        System.out.println();
                        break;

                    // Modificar alumno
                    case 5:
                        modifyStudent(read);
                        System.out.println();
                        break;

                    // Salir
                    case 6:
                        System.out.println("Finalizando programa...\n");
                        break;

                    // Opciones no válidas
                    default:
                        System.out.println("Opción no válida...\n");
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            read.close();
        }
    }

    // Buscar alumno
    private static void searchStudent(Scanner read) {
        try {
            // Pedir el DNI del alumno a buscar
            String dni = askDNI(read);
            if (dni.equals(RESET)) {
                return;
            }

            // Utilizar proceso
            ProcessBuilder pb = new ProcessBuilder("java", "-cp", "bin", "ProcesoDevolucion", dni);
            Process p = pb.start();
            InputStreamReader isr = new InputStreamReader(p.getInputStream());
            BufferedReader br = new BufferedReader(isr);

            // Leer líneas hasta que se terminen
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

            p.waitFor();
            br.close();
            isr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Insertar alumno
    private static void insertStudent(Scanner read) {
        try {
            String name, surname, dni, date, mark;

            // Pedir nombre
            while (true) {
                System.out.println("Nombre:");
                name = read.nextLine();
                if (name.equals(RESET))
                    return;
                if (!name.isEmpty()) {
                    // Validar que no contenga números ni caracteres especiales
                    if (!name.matches("^[A-Za-zÀ-ÿ\\s]+$")) {
                        System.out.println("El nombre no puede contener números ni caracteres especiales");
                    } else {
                        break;
                    }
                }
                System.out.println("El nombre no puede estar vacío");
            }

            // Pedir Apellido(s)
            while (true) {
                System.out.println("Apellido(s):");
                surname = read.nextLine();
                if (surname.equals(RESET))
                    return;
                if (!surname.isEmpty()) {
                    // Validar que no contenga números ni caracteres especiales
                    if (!surname.matches("^[A-Za-zÀ-ÿ\\s]+$")) {
                        System.out.println("Los apellidos no pueden contener números ni caracteres especiales");
                    } else {
                        break;
                    }
                }
                System.out.println("Los apellidos no pueden estar vacíos");
            }

            // Pedir DNI
            while (true) {
                System.out.println("DNI:");
                dni = read.nextLine();
                if (dni.equals(RESET))
                    return;
                // Validar formato 8 dígitos + letra mayúscula
                if (dni.isEmpty()) {
                    System.out.println("El DNI no puede estar vacío");
                    continue;
                }
                if (!dni.matches("^\\d{8}[A-Z]$")) {
                    System.out.println("Formato incorrecto...");
                    continue;
                }
                break;
            }

            // Pedir fecha de nacimiento
            date = askDate(read, 0);
            if (date.equals(RESET))
                return;

            // Pedir nota media (opcional)
            while (true) {
                System.out.println("Nota media (0 por defecto):");
                mark = read.nextLine();
                if (mark.equals(RESET))
                    return;
                if (mark.isEmpty()) {
                    mark = "0";
                    break;
                }
                // Validar formato: número positivo, entero o decimal
                if (!mark.matches("^\\d+(\\.\\d+)?$")) {
                    System.out.println("Formato inválido...");
                    continue;
                }
                break;

            }

            // Utilizar proceso
            ProcessBuilder pb = new ProcessBuilder("java", "-cp", "bin", "ProcesoAlmacenamiento", name, surname, dni,
                    date, mark);
            Process p = pb.start();
            InputStreamReader isr = new InputStreamReader(p.getInputStream());
            BufferedReader br = new BufferedReader(isr);

            // Leer líneas hasta que se terminen
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

            p.waitFor();
            br.close();
            isr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Obtener todos los alumnos
    private static void getAll() {
        try {
            // Utilizar proceso
            ProcessBuilder pb = new ProcessBuilder("java", "-cp", "bin", "ProcesoDevolucion");
            Process p = pb.start();
            InputStreamReader isr = new InputStreamReader(p.getInputStream());
            BufferedReader br = new BufferedReader(isr);

            // Leer líneas hasta que se terminen
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

            p.waitFor();
            br.close();
            isr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Eliminar alumno buscando por DNI
    private static void deleteStudent(Scanner read) {
        try {
            // Pedir el DNI del alumno a buscar
            String dni = askDNI(read);
            if (dni.equals(RESET))
                return;
            File file = new File(DIRS + dni + ".txt");

            // Eliminar si existe
            if (file.exists()) {
                if (file.delete()) {
                    System.out.println("Alumno con DNI " + dni + " borrado correctamente.");
                } else {
                    System.out.println("No se pudo borrar el alumno.");
                }
            } else {
                System.out.println("No existe ningún alumno con ese DNI.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Modificar alumno buscando por DNI
    private static void modifyStudent(Scanner read) {
        try {
            // Pedir el DNI del alumno a buscar
            String dni = askDNI(read);
            if (dni.equals(RESET))
                return;
            File file = new File(DIRS + dni + ".txt");
            // Salir si no existe
            if (!file.exists()) {
                System.out.println("No se ha encontrado el alumno con DNI: " + dni);
                return;
            }
            System.out.println("ATENCIÓN:\nSi no quieres cambiar algún dato, Enter directamente\n");

            // Leer datos actuales y añadirlos a listado
            BufferedReader br = new BufferedReader(new FileReader(file));
            String header = br.readLine();
            String line = br.readLine();
            br.close();
            String[] data = line.split(",");

            // Pedir nuevos datos (enter para mantener)
            System.out.println("Nombre actual: " + data[1] + ". Nuevo nombre:");
            String name = read.nextLine();
            if (name.isEmpty()) {
                name = data[1];
            }

            System.out.println("Apellido(s) actual(es): " + data[2] + ". Nuevo(s) apellido(s):");
            String surname = read.nextLine();
            if (surname.isEmpty()) {
                surname = data[2];
            }

            String date;
            System.out.println("Fecha de nacimiento actual: " + data[3]);
            date = askDate(read, 1);

            System.out.println("Nota media actual: " + data[4] + ". Nueva nota:");
            String mark = read.nextLine();
            if (mark.isEmpty()) {
                mark = data[4];
            }

            // Sobrescribir archivo con los nuevos datos
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(header + "\n");
            bw.write(dni + "," + name + "," + surname + "," + date + "," + mark);
            bw.close();

            System.out.println("Alumno modificado correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método de validación de fecha
    private static boolean validateDate(String date) {
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate.parse(date, dtf);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método de petición de DNI (mejora)
    private static String askDNI(Scanner read) {
        String dni;
        do {
            System.out.println("Introduce el DNI del alumno:");
            dni = read.nextLine();
            if (dni.isEmpty()) {
                System.out.println("Debe introducir un DNI");
                continue;
            }
            if (!dni.matches("^\\d{8}[A-Z]$")) {
                System.out.println("Formato incorrecto. 8 dígitos + letra mayúscula");
                continue;
            }
            break;
        } while (true);

        return dni;
    }

    private static String askDate(Scanner read, int value) {
        String date;
        while (true) {
            System.out.println("Fecha de nacimiento (dd-mm-yyyy):");
            date = read.nextLine();
            if (date.equals(RESET))
                break;

            if (date.isEmpty()) {
                if (value == 0) {
                    System.out.println("La fecha de nacimiento no puede estar vacía");
                } else {
                    break;
                }
            } else if (!date.matches(DATEVAL)) {
                System.out.println("Formato incorrecto: dd-mm-yyyy");
            } else if (!validateDate(date)) {
                System.out.println("La fecha introducida no es válida");
            } else {
                break;
            }
        }

        return date;
    }
}
