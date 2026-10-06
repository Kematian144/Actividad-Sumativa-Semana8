-- =====================================================================
-- PROYECTO: SpeedFast - Sistema de Gestión de Pedidos y Entregas
-- ASIGNATURA: Desarrollo Orientado a Objetos II (Semana 8)
-- INSTITUCIÓN: Duoc UC
-- =====================================================================

-- 1. Creación de la base de datos
CREATE DATABASE IF NOT EXISTS speedfast_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE speedfast_db;

-- Desactivar temporalmente revisión de claves foráneas para recreación limpia
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS entregas;
DROP TABLE IF EXISTS pedidos;
DROP TABLE IF EXISTS repartidores;
DROP TABLE IF EXISTS clientes;
SET FOREIGN_KEY_CHECKS = 1;

-- 2. Tabla Repartidores (según pauta oficial)
CREATE TABLE repartidores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Tabla Pedidos (según pauta oficial)
CREATE TABLE pedidos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    direccion VARCHAR(100) NOT NULL,
    tipo ENUM('COMIDA', 'ENCOMIENDA', 'EXPRESS') NOT NULL,
    estado ENUM('PENDIENTE', 'EN_REPARTO', 'ENTREGADO') NOT NULL DEFAULT 'PENDIENTE'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Tabla Entregas (según pauta oficial con claves foráneas)
CREATE TABLE entregas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT NOT NULL,
    id_repartidor INT NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    FOREIGN KEY (id_pedido) REFERENCES pedidos(id),
    FOREIGN KEY (id_repartidor) REFERENCES repartidores(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Tabla Clientes (para soporte completo del requisito textual ClienteDAO)
CREATE TABLE clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    direccion VARCHAR(150) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================================
-- DATOS INICIALES DE PRUEBA (SEED DATA)
-- =====================================================================

-- Inserción de Repartidores
INSERT INTO repartidores (nombre) VALUES
('Carlos Mendoza'),
('Ana González'),
('Rodrigo Valenzuela'),
('Camila Fuentes'),
('Ignacio Morales');

-- Inserción de Pedidos
INSERT INTO pedidos (direccion, tipo, estado) VALUES
('Av. Providencia 1250, Depto 402', 'COMIDA', 'PENDIENTE'),
('Calle Huérfanos 835, Oficina 501', 'ENCOMIENDA', 'EN_REPARTO'),
('Av. Apoquindo 4500, Las Condes', 'EXPRESS', 'ENTREGADO'),
('Los Leones 2233, Providencia', 'COMIDA', 'EN_REPARTO'),
('Av. Libertador Bernardo O''Higgins 1058', 'ENCOMIENDA', 'PENDIENTE'),
('Av. Vicuña Mackenna 4860, San Joaquín', 'EXPRESS', 'PENDIENTE');

-- Inserción de Entregas
INSERT INTO entregas (id_pedido, id_repartidor, fecha, hora) VALUES
(2, 1, CURDATE(), '10:30:00'),
(3, 2, CURDATE(), '11:15:00'),
(4, 3, CURDATE(), '12:00:00');

-- Inserción de Clientes
INSERT INTO clientes (nombre, email, telefono, direccion) VALUES
('Juan Pérez', 'juan.perez@duocuc.cl', '+56911223344', 'Av. Providencia 1250'),
('María Soto', 'maria.soto@gmail.com', '+56922334455', 'Calle Huérfanos 835'),
('Pedro Ramírez', 'pedro.ramirez@empresa.cl', '+56933445566', 'Av. Apoquindo 4500'),
('Valentina Paz', 'v.paz@outlook.com', '+56944556677', 'Los Leones 2233');

-- Consulta de verificación
SELECT 'Repartidores creados:' AS info, COUNT(*) AS total FROM repartidores;
SELECT 'Pedidos creados:' AS info, COUNT(*) AS total FROM pedidos;
SELECT 'Entregas creadas:' AS info, COUNT(*) AS total FROM entregas;
SELECT 'Clientes creados:' AS info, COUNT(*) AS total FROM clientes;

-- 6. Usuario de conexión de la aplicación (soporte universal MariaDB / MySQL)
CREATE USER IF NOT EXISTS 'speedfast'@'localhost' IDENTIFIED BY 'speedfast123';
GRANT ALL PRIVILEGES ON speedfast_db.* TO 'speedfast'@'localhost';
FLUSH PRIVILEGES;

