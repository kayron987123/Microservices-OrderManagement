-- Tabla de Productos
CREATE TABLE products
(
    id_product   INT PRIMARY KEY AUTO_INCREMENT,
    uuid_product BINARY(16) DEFAULT (UUID_TO_BIN(UUID())) UNIQUE NOT NULL,
    name         VARCHAR(200)   NOT NULL,
    price        DECIMAL(10, 2) NOT NULL,
    stock        INT            NOT NULL
);
