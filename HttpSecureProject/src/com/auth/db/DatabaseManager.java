package com.auth.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Gestiona todas las operaciones CRUD y la conectividad con la base de datos
 * MySQL.
 * Implementa medidas de seguridad contra SQL Injection mediante el uso de
 * PreparedStatements.
 */
public class DatabaseManager {

    // Configuración de los parámetros de conexión.
    private static final String URL = "jdbc:mysql://localhost:3306/secure_auth?useSSL=false&serverTimezone=UTC";
    private static final String USER = "";
    private static final String PASSWORD = "";

    /**
     * Establece y devuelve una conexión activa con la base de datos MySQL.
     * 
     * @return Connection objeto de conexión configurado.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Asegura la carga del driver en el classpath.
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("[DB ERROR] Driver MySQL no encontrado: " + e.getMessage());
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Registra un nuevo usuario aplicando persistencia de las credenciales
     * hasheadas.
     * 
     * @param username Nombre único del usuario.
     * @param hash     Hash de la contraseña (generado previamente con SHA-256).
     * @param salt     Sal aleatoria utilizada para el hash.
     * @return boolean True si el registro fue exitoso, False en caso contrario.
     */
    public static boolean registerUser(String username, String hash, String salt) {
        String query = "INSERT INTO users (username, password_hash, salt) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, hash);
            pstmt.setString(3, salt);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[DB ERROR] Error en el registro de usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Modifica el identificador (username) de un registro existente.
     * 
     * @param oldName Nombre actual en la base de datos.
     * @param newName Nuevo nombre deseado.
     * @return boolean True si se modificó correctamente.
     */
    public static boolean updateUsername(String oldName, String newName) {
        String query = "UPDATE users SET username = ? WHERE username = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, newName);
            pstmt.setString(2, oldName);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Error al actualizar el nombre: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina de forma permanente la cuenta de un usuario de la base de datos.
     * 
     * @param username Nombre del usuario a eliminar.
     * @return boolean True si el borrado fue efectivo.
     */
    public static boolean deleteUser(String username) {
        String query = "DELETE FROM users WHERE username = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Error al borrar el usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Recupera las credenciales de seguridad (Hash y Salt) de un usuario
     * específico.
     * 
     * @param username Nombre del usuario a consultar.
     * @return UserCredentials Record con los datos de seguridad o null si no
     *         existe.
     */
    public static UserCredentials getCredentials(String username) {
        String query = "SELECT password_hash, salt FROM users WHERE username = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new UserCredentials(rs.getString("password_hash"), rs.getString("salt"));
                }
            }
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Error en consulta de credenciales: " + e.getMessage());
        }
        return null;
    }

    /**
     * Estructura de datos inmutable para el transporte de credenciales.
     */
    public record UserCredentials(String hash, String salt) {
    }
}
