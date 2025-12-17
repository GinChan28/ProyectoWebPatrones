--  Sistema de Gestiones Web para Taller Mecánico Automotriz

-- Sección de administración (ejecutar una vez en un entorno de desarrollo)
DROP DATABASE IF EXISTS taller_db;
DROP USER IF EXISTS 'taller_app'@'%';
DROP USER IF EXISTS 'taller_reportes'@'%';

-- Creación del esquema
CREATE DATABASE taller_db
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;
USE taller_db;

-- Creación de usuarios con contraseñas seguras (idealmente asignadas fuera del script)
create user 'taller_app'@'%' identified by 'Proyecto_G8';
create user 'taller_reportes'@'%' identified by 'Proyecto_G8';

-- Permisos
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, INDEX ON taller_db.* TO 'taller_app'@'%';
GRANT SELECT ON taller_db.* TO 'taller_reportes'@'%';
FLUSH PRIVILEGES;

-- Sección de Tablas --
-- Tabla de roles
CREATE TABLE rol (
  id_rol INT NOT NULL AUTO_INCREMENT,
  rol VARCHAR(20) NOT NULL UNIQUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_rol)
) ENGINE = InnoDB;

-- Tabla de usuarios (autenticación base del sistema)
CREATE TABLE usuario (
  id_usuario INT NOT NULL AUTO_INCREMENT,
  username VARCHAR(30) NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  nombre VARCHAR(50) NOT NULL,
  apellidos VARCHAR(80) NOT NULL,
  correo VARCHAR(75) NULL UNIQUE,
  telefono VARCHAR(25) NULL,
  ruta_imagen VARCHAR(1024),
  activo BOOLEAN DEFAULT TRUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_usuario),
  CHECK (correo IS NULL OR correo REGEXP '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$'),
  INDEX ndx_username (username)
) ENGINE = InnoDB;

-- Relación usuario-rol (muchos a muchos)
CREATE TABLE usuario_rol (
  id_usuario INT NOT NULL,
  id_rol INT NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_usuario, id_rol),
  CONSTRAINT fk_usuarioRol_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
  CONSTRAINT fk_usuarioRol_rol FOREIGN KEY (id_rol) REFERENCES rol(id_rol)
) ENGINE = InnoDB;

-- Roles base
INSERT INTO rol (rol) VALUES ('ADMIN'), ('MECANICO'), ('CLIENTE') ON DUPLICATE KEY UPDATE rol = VALUES(rol);

-- Cliente (perfil vinculado a usuario)
CREATE TABLE cliente (
  id_cliente INT NOT NULL AUTO_INCREMENT,
  id_usuario INT NOT NULL,
  nombre VARCHAR(100) NOT NULL,
  telefono VARCHAR(30),
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_cliente),
  CONSTRAINT fk_cliente_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
) ENGINE = InnoDB;

-- Mecánico
CREATE TABLE mecanico (
  id_mecanico INT NOT NULL AUTO_INCREMENT,
  id_usuario INT NOT NULL,
  nombre VARCHAR(100) NOT NULL,
  cedula VARCHAR(30),
  especialidad VARCHAR(100),
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_mecanico),
  CONSTRAINT fk_mecanico_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
) ENGINE = InnoDB;

-- Vehículo (pertenece a un cliente)
CREATE TABLE vehiculo (
  id_vehiculo INT NOT NULL AUTO_INCREMENT,
  id_cliente INT NOT NULL,
  placa VARCHAR(15) NOT NULL,
  marca VARCHAR(60) NOT NULL,
  modelo VARCHAR(60) NOT NULL,
  anno INT,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_vehiculo),
  UNIQUE (placa),
  CONSTRAINT fk_vehiculo_cliente FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
  INDEX ndx_placa (placa)
) ENGINE = InnoDB;

-- Catálogo de servicios (cambio de aceite, frenos, etc.)
CREATE TABLE servicio (
  id_servicio INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(100) NOT NULL,
  descripcion TEXT,
  precio_base DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  activo BOOLEAN NOT NULL DEFAULT TRUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_servicio),
  UNIQUE (nombre)
) ENGINE = InnoDB;

-- Citas (agendadas por cliente, asignables a mecánico)
CREATE TABLE cita (
  id_cita INT NOT NULL AUTO_INCREMENT,
  id_vehiculo INT NOT NULL,
  id_mecanico INT NULL, -- se asigna luego
  id_servicio INT NOT NULL,
  fecha DATE NOT NULL,
  hora TIME NOT NULL,
  estado ENUM('PENDIENTE','EN_PROGRESO','FINALIZADA','CANCELADA') NOT NULL DEFAULT 'PENDIENTE',
  comentario VARCHAR(255),
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_cita),
  CONSTRAINT fk_cita_vehiculo FOREIGN KEY (id_vehiculo) REFERENCES vehiculo(id_vehiculo),
  CONSTRAINT fk_cita_mecanico FOREIGN KEY (id_mecanico) REFERENCES mecanico(id_mecanico),
  CONSTRAINT fk_cita_servicio FOREIGN KEY (id_servicio) REFERENCES servicio(id_servicio),
  INDEX ndx_fecha (fecha),
  INDEX ndx_estado (estado)
) ENGINE = InnoDB;

-- Historial de estados 
CREATE TABLE cita_historial_estado (
  id_historial INT AUTO_INCREMENT,
  id_cita INT NOT NULL,
  estado_anterior ENUM('PENDIENTE','EN_PROGRESO','FINALIZADA','CANCELADA'),
  estado_nuevo ENUM('PENDIENTE','EN_PROGRESO','FINALIZADA','CANCELADA') NOT NULL,
  cambiado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_historial),
  CONSTRAINT fk_historial_cita FOREIGN KEY (id_cita) REFERENCES cita(id_cita),
  INDEX ndx_cita_hist (id_cita, cambiado_en)
) ENGINE = InnoDB;

-- Inventario de repuestos
CREATE TABLE repuesto (
  id_repuesto INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(120) NOT NULL UNIQUE,
  sku VARCHAR(60),
  existencias INT UNSIGNED NOT NULL DEFAULT 0,
  costo_unitario DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  activo BOOLEAN NOT NULL DEFAULT TRUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_repuesto),
  CHECK (existencias >= 0),
  CHECK (costo_unitario >= 0)
) ENGINE = InnoDB;

-- Consumo de repuestos por cita (trazabilidad de materiales)
CREATE TABLE consumo_repuesto (
  id_consumo INT AUTO_INCREMENT,
  id_cita INT NOT NULL,
  id_repuesto INT NOT NULL,
  cantidad INT NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_consumo),
  CONSTRAINT fk_consumo_cita FOREIGN KEY (id_cita) REFERENCES cita(id_cita),
  CONSTRAINT fk_consumo_repuesto FOREIGN KEY (id_repuesto) REFERENCES repuesto(id_repuesto),
  CHECK (cantidad > 0),
  UNIQUE (id_cita, id_repuesto) -- un repuesto por cita con su cantidad total
) ENGINE = InnoDB;

-- Archivos (URLs de Firebase Storage para evidencias/fotos)
CREATE TABLE archivo (
  id_archivo INT NOT NULL AUTO_INCREMENT,
  id_cita INT NULL,
  id_vehiculo INT NULL,
  tipo ENUM('FOTO_VEHICULO','EVIDENCIA_SERVICIO','OTRO') NOT NULL,
  url_publica VARCHAR(1024) NOT NULL,
  nombre_archivo VARCHAR(255),
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_archivo),
  CONSTRAINT fk_archivo_cita FOREIGN KEY (id_cita) REFERENCES cita(id_cita),
  CONSTRAINT fk_archivo_vehiculo FOREIGN KEY (id_vehiculo) REFERENCES vehiculo(id_vehiculo),
  INDEX ndx_tipo (tipo)
) ENGINE = InnoDB;

CREATE TABLE orden_compra (
    id_orden INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    fecha DATETIME,
    estado ENUM('CARRITO','PAGADA') NOT NULL,
    total DECIMAL(12,2) NOT NULL DEFAULT 0,

    CONSTRAINT fk_orden_cliente
        FOREIGN KEY (id_cliente)
        REFERENCES cliente(id_cliente)
);

CREATE TABLE detalle_orden (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_orden INT NOT NULL,
    id_repuesto INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(12,2) NOT NULL,

    CONSTRAINT fk_detalle_orden
        FOREIGN KEY (id_orden)
        REFERENCES orden_compra(id_orden),

    CONSTRAINT fk_detalle_repuesto
        FOREIGN KEY (id_repuesto)
        REFERENCES repuesto(id_repuesto),

    UNIQUE (id_orden, id_repuesto)
);

CREATE TABLE carrito_item (
    id_item INT AUTO_INCREMENT PRIMARY KEY,
    id_orden INT NOT NULL,
    id_repuesto INT NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    precio_unitario DECIMAL(12,2) NOT NULL,

    CONSTRAINT fk_item_orden
        FOREIGN KEY (id_orden) REFERENCES orden_compra(id_orden),

    CONSTRAINT fk_item_repuesto
        FOREIGN KEY (id_repuesto) REFERENCES repuesto(id_repuesto),

    CONSTRAINT chk_cantidad CHECK (cantidad > 0),

    UNIQUE (id_orden, id_repuesto)
);



-- Perfiles de Admin y mecánico
INSERT INTO usuario (username, password, nombre, apellidos, correo, telefono, activo)
VALUES ('admin', '{noop}admin1234', 'Luis', 'Admin', 'admin@default.com', '8996-0055', TRUE)
ON DUPLICATE KEY UPDATE username=username;

INSERT INTO usuario_rol (id_usuario, id_rol)
SELECT u.id_usuario, r.id_rol
FROM usuario u JOIN rol r ON u.username='admin' AND r.rol='ADMIN'
WHERE NOT EXISTS (
  SELECT 1 FROM usuario_rol ur 
  WHERE ur.id_usuario=u.id_usuario AND ur.id_rol=r.id_rol
);
-- Mecanico
INSERT INTO usuario (username, password, nombre, apellidos, correo, telefono, activo)
VALUES ('mecanico', '{noop}meca1234', 'Miguel', 'Mecánico', 'mecanico@default.com', '7899-6010', TRUE),
('mecaTr', '{noop}meca2587', 'Juan', 'Tramado', 'tramado@taller.com', '00000001', true),
('mecaEsp', '{noop}meca7895', 'Diego', 'Especialista', 'especialista@taller.com', '00000002', true)
ON DUPLICATE KEY UPDATE username=username;

INSERT INTO usuario_rol (id_usuario, id_rol)
SELECT u.id_usuario, r.id_rol
FROM usuario u JOIN rol r ON u.username='mecanico' AND r.rol='MECANICO'
WHERE NOT EXISTS (
  SELECT 1 FROM usuario_rol ur 
  WHERE ur.id_usuario=u.id_usuario AND ur.id_rol=r.id_rol
);

INSERT INTO usuario_rol (id_usuario, id_rol)
SELECT u.id_usuario, r.id_rol
FROM usuario u JOIN rol r ON u.username='cliente' AND r.rol='CLIENTE'
WHERE NOT EXISTS (
      SELECT 1 FROM usuario_rol ur
      WHERE ur.id_usuario=u.id_usuario AND ur.id_rol=r.id_rol
  );


INSERT INTO mecanico (id_usuario, nombre, cedula, especialidad)
SELECT u.id_usuario, 'Mecánico General', '402680967', 'General'
FROM usuario u
WHERE u.username='mecanico'
  AND NOT EXISTS (SELECT 1 FROM mecanico m WHERE m.id_usuario=u.id_usuario);
  
INSERT INTO mecanico (id_usuario, nombre, cedula, especialidad)
SELECT u.id_usuario, 'Mecánico Tramado', '4026802584', 'Tramado'
FROM usuario u
WHERE u.username='mecaTr';

INSERT INTO mecanico (id_usuario, nombre, cedula, especialidad)
SELECT u.id_usuario, 'Mecánico Especialista', '40268052', 'Frenos'
FROM usuario u
WHERE u.username='mecaEsp';


-- Servicios 
INSERT INTO servicio (nombre, descripcion, precio_base, activo) VALUES
('Cambio de aceite','Cambio de aceite y filtro',25000,TRUE),
('Revisión de frenos','Diagnóstico y cambio de pastillas',45000,TRUE),
('Alineación y balanceo','Servicio de alineación y balanceo',35000,TRUE)
ON DUPLICATE KEY UPDATE descripcion=VALUES(descripcion), precio_base=VALUES(precio_base), activo=VALUES(activo);



INSERT INTO usuario (username, password, nombre, apellidos, correo, telefono, activo)
VALUES
('carlosr', '{noop}1234', 'Carlos', 'Ramírez', 'carlos.ramirez@example.com', '88810001', 1),
('mariaf', '{noop}1234', 'María', 'Fernández', 'maria.fernandez@example.com', '88810002', 1),
('luisg', '{noop}1234', 'Luis', 'Gómez', 'luis.gomez@example.com', '88810003', 1),
('anar', '{noop}1234', 'Ana', 'Rodríguez', 'ana.rodriguez@example.com', '88810004', 1),
('jorges', '{noop}1234', 'Jorge', 'Salazar', 'jorge.salazar@example.com', '88810005', 1)
ON DUPLICATE KEY UPDATE username=username;



-- Cliente

INSERT INTO cliente (id_usuario, nombre, telefono) VALUES
(3, 'Carlos Ramírez', '88810001'),
(4, 'María Fernández', '88810002'),
(5, 'Luis Gómez', '88810003'),
(6, 'Ana Rodríguez', '88810004'),
(7, 'Jorge Salazar', '88810005');

INSERT INTO cliente (id_usuario, nombre, telefono)
SELECT u.id_usuario,
       CONCAT(u.nombre, ' ', u.apellidos),
       u.telefono
FROM usuario u
JOIN usuario_rol ur ON ur.id_usuario = u.id_usuario
JOIN rol r ON r.id_rol = ur.id_rol
LEFT JOIN cliente c ON c.id_usuario = u.id_usuario
WHERE r.rol = 'CLIENTE'
  AND c.id_cliente IS NULL;




INSERT INTO repuesto (nombre, sku, existencias, costo_unitario, activo)
VALUES
('Bujía', 'SKU-001', 50, 3500.00, true),
('Filtro de aceite', 'SKU-002', 20, 4500.00, true),
('Pastillas de freno', 'SKU-003', 15, 12000.00, true);

INSERT INTO usuario_rol (id_usuario, id_rol)
SELECT u.id_usuario, r.id_rol
FROM usuario u
JOIN rol r ON r.rol = 'CLIENTE'
WHERE u.username IN ('carlosr','mariaf','luisg','anar','jorges')
AND NOT EXISTS (
    SELECT 1
    FROM usuario_rol ur
    WHERE ur.id_usuario = u.id_usuario
      AND ur.id_rol = r.id_rol
);

INSERT INTO usuario_rol (id_usuario, id_rol)
SELECT u.id_usuario, r.id_rol
FROM usuario u
JOIN rol r ON r.rol = 'MECANICO'
WHERE u.username IN ('mecanico', 'mecaTr', 'mecaEsp')
AND NOT EXISTS (
    SELECT 1 
    FROM usuario_rol ur 
    WHERE ur.id_usuario = u.id_usuario
      AND ur.id_rol = r.id_rol
);


SET SQL_SAFE_UPDATES = 0;
UPDATE usuario
SET password = CONCAT('{noop}', REPLACE(password, '{noop}', ''))
WHERE password NOT LIKE '{noop}%';
SET SQL_SAFE_UPDATES = 1;


-- SELECT id_usuario, nombre FROM usuario;
-- SELECT id_usuario FROM usuario;
-- SELECT id_usuario, username FROM usuario;
-- SELECT id_usuario FROM usuario;


ALTER TABLE usuario DROP CHECK usuario_chk_1;
