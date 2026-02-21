/**
 * Script de creación de la base de datos para el sistema de autenticación segura.
 */
-- 1. Creamos la base de datos.
CREATE DATABASE IF NOT EXISTS secure_auth;
USE secure_auth;

-- 2. Creamos la tabla de usuarios.
-- Guardamos username, el hash de la clave y el salt (la semilla aleatoria).
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    salt VARCHAR(255) NOT NULL
);
